#!/usr/bin/env python3
"""Focused regression for the MC26.3 The Sift sky RenderPass upload crash.

Run --source-only before patch (expected FAIL), again after patch (PASS),
then full mode after Java25 Gradle clean build to inspect actual packaged JAR.
No user profile files are opened or mutated by this script.
"""
from __future__ import annotations

import hashlib
import json
import re
import sys
import tomllib
from pathlib import Path
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parents[2]
SIFT = ROOT.parent.parent
RENDERER = ROOT / "src/client/java/mielon/thesift/client/render/SiftProceduralSkyRenderer.java"
MIXIN = ROOT / "src/client/java/mielon/thesift/client/mixin/SiftSkyRendererMixin.java"
OLD = SIFT / (
    "artifacts/release-candidates/Port-1.1.2-SkyFix-Dual-26.2-26.3/"
    "mielon's-the-sift-1.1.2-mc26.3-neoforge-port.jar"
)
NEW = ROOT / "build/libs/the_sift-1.1.2-neoforge.3.jar"
VANILLA_SRC = (SIFT / "work/neo-26.3-release-1.1.2/build/moddev/"
               "artifacts/minecraft-patched-26.3.0.76-sources.jar")
EXPECTED_OLD_SHA = "29626d6e2b59b536b2fee2b0d44a698df70f5a2297d9ace9954d796708b186f8"
SKY_PNG = "assets/the_sift/textures/sky/alpha2/"


def check(condition: bool, label: str) -> None:
    if not condition:
        raise AssertionError(label)
    print("PASS " + label)


def digest(blob: bytes) -> str:
    return hashlib.sha256(blob).hexdigest()


def source_contract() -> None:
    renderer = RENDERER.read_text(encoding="utf-8")
    mixin = MIXIN.read_text(encoding="utf-8")
    with ZipFile(VANILLA_SRC) as jar:
        vanilla = jar.read("net/minecraft/client/renderer/SkyRenderer.java").decode("utf-8")
    start = vanilla.index("public void render(GpuBufferSlice skyFog, SkyRenderState state)")
    inside = vanilla.index('createRenderPass(() -> "Sky"', start)
    check(start < inside, "SkyRenderer.render HEAD is before its actual RenderPass opens")

    check('method = "render", at = @At("HEAD")' in mixin
          and 'SiftProceduralSkyRenderer.prepareSkyResourcesBeforePass(' in mixin,
          "SkyRenderer render HEAD preloads Sift GPU resources outside any pass")
    check("renderAfterPhotonFinalPass()" in renderer
          and 'SiftShaderCompat.assignPipeline(TEXTURED_SKY_PIPELINE, "TEXTURED")' in renderer,
          "Photon final-pass and complementary sky routing preserved")

    prefix, actual_pass = renderer.split("private static void renderActual(", 1)
    check('prepareSkyResourcesBeforePass(' in prefix
          and 'getTexture(SKY_TEXTURES[' in prefix
          and 'getSampler(' in prefix,
          "first texture uploads and sampler allocation are in pre-pass preparation")
    check('ensureBuffers(' in prefix
          and '.getBuffer(indexCount)' in prefix,
          "first sky vertex/index GPU buffer uploads happen BEFORE RenderPass")
    pass_body = actual_pass.split("public static void close()", 1)[0]
    check("getTexture(" not in pass_body
          and "getSampler(" not in pass_body
          and "ensureBuffers(" not in pass_body,
          "no unsafe texture/buffer initialization inside an active RenderPass")
    check("PREPARED_SKY_TEXTURE_VIEWS[layer]" in pass_body
          and "PREPARED_SKY_SAMPLER" in pass_body,
          "active render pass only binds previously prepared GPU texture views")
    check("PREPARED_SKY_TEXTURE_VIEWS[i] = null;" in renderer
          and "PREPARED_SKY_SAMPLER = null;" in renderer,
          "cached views/sampler released from held references when sky renderer closes")
    check("1.1.2-neoforge.3" in (ROOT / "gradle.properties").read_text(encoding="utf-8"),
          "fixed MC26.3 artifact increments internal patch version")


def binary_contract() -> None:
    check(OLD.exists() and digest(OLD.read_bytes()) == EXPECTED_OLD_SHA,
          "previous user-crashing MC26.3 JAR preserved at recorded SHA256")
    check(NEW.is_file(), "patched MC26.3 JAR exists")
    with ZipFile(OLD) as before, ZipFile(NEW) as after:
        check(before.testzip() is None and after.testzip() is None, "both binaries ZIP CRC pass")
        names_before = {n for n in before.namelist() if not n.endswith("/")}
        names_after = {n for n in after.namelist() if not n.endswith("/")}
        check(names_before == names_after, "release hotfix adds/removes no resources or classes")
        changes = {n for n in names_before if before.read(n) != after.read(n)}
        changed_classes = sorted(n for n in changes if n.endswith(".class"))
        expected = [
            "mielon/thesift/client/mixin/SiftSkyRendererMixin.class",
            "mielon/thesift/client/render/SiftProceduralSkyRenderer.class",
        ]
        check(changed_classes == expected,
              "only the expected preloader/mixin bytecode classes changed")
        for name in sorted(changes):
            if name in changed_classes or name == "META-INF/neoforge.mods.toml":
                continue
            a, b = before.read(name), after.read(name)
            if name.endswith(".json"):
                try:
                    if json.loads(a) == json.loads(b):
                        continue
                except ValueError:
                    pass
            if a.replace(b"\r\n", b"\n") == b.replace(b"\r\n", b"\n"):
                continue
            raise AssertionError(f"unexpected other asset change: {name}")
        check(all(before.read(n) == after.read(n) for n in names_before
                  if n.startswith(SKY_PNG) and n.endswith(".png")),
              "the six user-accepted sky gradient images remain byte-identical")
        meta = tomllib.loads(after.read("META-INF/neoforge.mods.toml").decode("utf-8-sig"))
        deps = {d["modId"]: d for d in meta["dependencies"]["the_sift"]}
        check(meta["mods"][0]["modId"] == "the_sift"
              and meta["mods"][0]["version"] == "1.1.2-neoforge.3"
              and meta["license"] == "MIT",
              "fixed mod identity, internal version and original MIT attribution")
        check(deps["neoforge"]["versionRange"] == "[26.3.0.64-beta,)"
              and deps["minecraft"]["versionRange"] == "[26.3]"
              and deps["geckolib"]["versionRange"] == "[5.5.7,6)",
              "Minecraft26.3 and NeoForge original open minimum unchanged")
        check("LICENSE_the_sift" in names_after
              and "mielon/thesift/client/mixin/IrisPhotonFinalSkyMixin.class" in names_after,
              "attribution and optional Photon Iris mixin retained")
    print("HOTFIX_SHA256=" + digest(NEW.read_bytes()).upper())


def main() -> None:
    source_contract()
    if "--source-only" not in sys.argv:
        binary_contract()
    print("RENDERPASS_SKY_PRELOAD_REGRESSION=PASS")


if __name__ == "__main__":
    main()
