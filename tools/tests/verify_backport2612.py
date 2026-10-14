"""Static integrity checks for the isolated Minecraft 26.1.2 The Sift port.

No other project's files are edited. The accepted MC26.2 source and released
MC26.2/MC26.3 binary artifacts are treated as immutable references.
"""

from collections import Counter
from hashlib import sha256
from pathlib import Path
import json
import re
import zipfile

WORKTREE = Path(__file__).resolve().parents[2]
PROJECT = WORKTREE.parents[1]
SOURCE = WORKTREE / "src/main/resources"
BASELINE = PROJECT / "work/neo-26.2-1.1.2-release-sky-fix/src/main/resources"
JAR = WORKTREE / "build/libs/the_sift-1.1.2-neoforge.1-alpha.1.jar"
CHECKS = []


def require(condition, label):
    CHECKS.append((label, bool(condition)))
    if not condition:
        print("FAIL", label)


def digest(path):
    return sha256(path.read_bytes()).hexdigest().upper()


def main():
    require(JAR.exists(), "Built 26.1.2 dev-stage JAR exists")
    with zipfile.ZipFile(JAR) as jar:
        require(jar.testzip() is None, "JAR CRC")
        names = set(jar.namelist())
        require("META-INF/neoforge.mods.toml" in names, "JAR NeoForge metadata")
        metadata = jar.read("META-INF/neoforge.mods.toml").decode("utf-8")
        for phrase in (
            'modId="the_sift"', 'version="1.1.2-neoforge.1-alpha.1"',
            'loaderVersion="[11,)"', 'versionRange="[26.1.2]"',
            'versionRange="[26.1.2.117,)"', 'versionRange="[5.5.2,6)"'
        ):
            require(phrase in metadata, f"metadata {phrase}")
        require(metadata.count('versionRange="[26.1.2]"') == 1,
                "Minecraft dependency restricted precisely to 26.1.2")

        counts = Counter()
        for area, ext, expected in [
            ("data/the_sift/worldgen/", ".json", 158),
            ("data/the_sift/structure/", ".nbt", 13),
            ("data/minecraft/structure/ancient_city/", ".nbt", 3),
        ]:
            paths = sorted(n for n in names if n.startswith(area) and n.endswith(ext))
            require(len(paths) == expected, f"{area} count {expected}")
            for entry in paths:
                rel = Path(*entry.split("/"))
                original = BASELINE / rel
                require(original.is_file() and jar.read(entry) == original.read_bytes(),
                        f"MC26.2 behavioral/data parity {entry}")
                if ext == ".json":
                    json.loads(jar.read(entry).decode("utf-8-sig"))
                counts[area] += 1

        for state, model in [
            ("overgrown_willow_sign", "overgrown_willow_sign"),
            ("overgrown_willow_wall_sign", "overgrown_willow_sign"),
            ("overgrown_willow_hanging_sign", "overgrown_willow_hanging_sign"),
            ("overgrown_willow_wall_hanging_sign", "overgrown_willow_hanging_sign"),
        ]:
            asset = f"assets/the_sift/blockstates/{state}.json"
            require(asset in names, f"blockstate {state}")
            payload = json.loads(jar.read(asset))
            require(payload == {"variants": {"": {"model": f"the_sift:block/{model}"}}},
                    f"MC26.1.2 particle-only sign state {state}")
        for model in ("overgrown_willow_sign", "overgrown_willow_hanging_sign"):
            entry = f"assets/the_sift/models/block/{model}.json"
            require(entry in names, f"target sign particle model {entry}")
            payload = json.loads(jar.read(entry))
            require(isinstance(payload.get("textures", {}).get("particle"), str), f"sign particle texture {entry}")

        shader_path = "assets/the_sift/shaders/core/photon_sky_overlay.vsh"
        require(shader_path in names, "Photon sky overlay shader packaged")
        shader = jar.read(shader_path).decode("utf-8")
        require("clip.z = clip.w;" in shader, "26.1.2 forward-Z Photon sky mask")
        require('assets/the_sift/textures/sky/alpha2/base_day.png' in names, "accepted baked daytime sky preserved")
        gradient_count = sum(n.startswith("assets/the_sift/textures/sky/alpha2/") and n.endswith(".png") for n in names)
        require(gradient_count == 6, "all six baked sky gradients")
        # The MC26.3 hotfix taught that texture uploads inside an open
        # RenderPass are unsafe. Verify MC26.1.2 explicitly prepares the
        # texture views before opening its own legacy Blaze3D pass.
        renderer = (WORKTREE / "src/client/java/mielon/thesift/client/render/SiftProceduralSkyRenderer.java").read_text(encoding="utf-8")
        first_pass = renderer.find("try (RenderPass pass")
        texture_resolve = renderer.find("getTexture(SKY_TEXTURES[i])")
        require(first_pass > texture_resolve >= 0
                and "getTexture(SKY_TEXTURES[" not in renderer[first_pass:renderer.find("public static void close()", first_pass)],
                "sky texture resources resolved before active RenderPass")

        mixin_entries = set()
        for name, side, package in (
            ("the_sift.mixins.json", "mixins", "mielon/thesift/mixin"),
            ("the_sift.client.mixins.json", "client", "mielon/thesift/client/mixin"),
        ):
            require(name in names, f"config {name}")
            config = json.loads(jar.read(name))
            require(config.get("required") is True and config["injectors"]["defaultRequire"] == 1,
                    f"required mixin enforcement {name}")
            for entry in config[side]:
                cls = f'{package}/{entry.replace(".", "/")}.class'
                require(cls in names, f"configured mixin bytecode {entry}")
                mixin_entries.add((name, entry))
        require(("the_sift.mixins.json", "CauldronInteractionsInvoker") in mixin_entries,
                "MC26.1.2 package-private cauldron invoker registered")
        require(("the_sift.client.mixins.json", "SiftSkyRendererMixin") in mixin_entries,
                "SkyRenderer corrected mixin packaged")
        require(("the_sift.client.mixins.json", "SiftWeatherTextureMixin") in mixin_entries,
                "Target weather return-value mixin packaged")

    for name, relative, expected in (
        ("accepted 26.2 release", "artifacts/release-candidates/Port-1.1.2-SkyFix-Dual-26.2-26.3/mielon's-the-sift-1.1.2-mc26.2-neoforge-port.jar",
         "9740DA26B007C909B60716C78C90691409E30DB7BFF3A6FEF9321462C58A4A7D"),
        ("accepted 26.3 hotfix", "artifacts/release-candidates/Port-1.1.2-NeoForge-26.3-RenderPass-Preload-Hotfix/mielon's-the-sift-1.1.2-mc26.3-neoforge-port.jar",
         "56724D4A0F7E42A8D21D3D85447A945FAAE93A04BFB11D8E485C1FF5914BFD19"),
    ):
        artifact = PROJECT / relative
        require(artifact.is_file() and digest(artifact) == expected, f"untouched {name}")
    failure = [x for x in CHECKS if not x[1]]
    print(f"Checks: {len(CHECKS)} / {len(CHECKS)} total, PASS={len(CHECKS)-len(failure)} FAIL={len(failure)}")
    print("STAGE_SHA256:", digest(JAR))
    print("Source basis", WORKTREE)
    if failure:
        raise SystemExit(1)


if __name__ == "__main__":
    main()
