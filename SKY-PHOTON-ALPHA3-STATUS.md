# The Sift 1.1.2 → Minecraft 26.2: Photon sky ALPHA3 experiment

**Date:** 2026-10-13. **Status:** SEPARATE EXPERIMENT — not an official release, no user in-world screenshot validation.

## Why ALPHA3 exists

The user reported that the **ALPHA2 textured sky works beautifully in Complementary and without shaders**, but the same build renders large semitransparent sky-coloured layers through the terrain with **Photon v1.3b** (screenshot supplied in conversation).

Unlike Complementary, Photon v1.3b defines:

- \`shaders/world0/gbuffers_textured.fsh\` → \`/program/gbuffers_all_translucent.fsh\`
- \`gbuffers_all_translucent.fsh\` writes to **\`RENDERTARGETS: 13\`**, reconstructs \`material_from(...)\`, diffuse lighting and depth/fog as if the surface were translucent world geometry.

Our ALPHA2 routes six independently animated translucent sky layers through Iris \`IrisProgram.TEXTURED\`. Thus Photon handles them as lit translucent surfaces, breaking sky/world compositing. This failure is **confirmed by exact Photon shader sources**, not conjecture about the screenshot.

**Official Photon reference, untouched:** Modrinth \`gUv7fBPN\`, \`photon_v1.3b.zip\`, SHA-256 \`120897768EAA8CB1F14A3E98F88DFF009AE3204EE3B96904A4FE8586EDFC1EAD\`, saved under \`TheSift/backports/26.2-1.1.2-prep/reference/optional-shaders-photon-v1.3b/\`. The shaderpack is owned by SixthSurge, licensed separately; **never bundled, patched, republished or installed into a live profile by Prime 2**.

## Smallest independent experimental change

Source worktree \`TheSift/work/neo-26.2-1.1.2-experiment-photon-alpha3/\`, branch \`experiment/26.2-photon-native-uv-alpha3\`, based on the frozen ALPHA2 source commit \`8bf0a1b05d9f216f01135dca1e76ed5058e8a7c1\`.

1. The six ALPHA2 RGBA textures, shared UV dome, original animation, time/weather blending and GPU resource handling remain byte-for-byte/code unchanged.
2. Adds a **separately registered, native Blaze3D \`POSITION_TEX\` sky pipeline** (\`the_sift:pipeline/photon_native_sky_alpha3\`). It is intentionally **not assigned** to a shaderpack program via \`IrisApi.assignPipeline\`. This tests whether we can composite our existing sky geometry with its native \`core/position_tex\` shader and normal RGBA blending, instead of sending it to Photon's gbuffer 13.
3. The shaderpack selection check is guarded and optional: \`SiftShaderCompat\` uses reflective \`net.irisshaders.iris.Iris.getCurrentPackName()\` solely to match selected shaderpack names beginning with \`photon\`. This method is present in the actually pinned Iris1.11.4 for MC26.2. Iris does not currently expose it through its public API, so this is an **experimental compatibility integration**, gracefully returning false if an Iris update removes the symbol.
4. With **shaderpack OFF**, the ALPHA2/ALPHA1 vanilla \`POSITION_COLOR\` path remains. With **all non-Photon shaderpacks**, including Complementary, the same ALPHA2 \`IrisProgram.TEXTURED\` sky pipeline remains. **Only Photon** selects the experimental unmapped native alternative.
5. The mod logs the actual pack name during client setup and a **one-time in-world sky render-path selection**. This allows users to distinguish a failed path match from failed rendering.

## Observed QA (do not conflate separate gates)

| Gate | Evidence | Status |
| --- | --- | --- |
| Java25, latest official NeoForge MC26.2 \`26.2.0.90\`, clean build | \`logs/alpha3-photon-262-final-cleanbuild-01.log\` | **PASS** |
| Package integrity, protected ALPHA2 JAR and Photon source SHA | \`tools/tests/verify_photon_native_alpha3.py\` | **PASS** |
| All gameplay/worldgen/resources unchanged vs ALPHA2 | Binary archive compares all entries, exactly two renderer/compat class changes + version TOML | **PASS static** |
| Original ALPHA2 shader-less and Complementary branches retained | Two-pipeline branching static contract | **PASS static**, visual retest pending |
| Photon v1.3b Iris pack detected via exact runtime | Disposable \`run/client-26.2.0.90-photon-alpha3/logs/latest.log\`: \`Photon-specific native sky candidate: true\` | **PASS at client startup** |
| Photon v1.3b client menu/GeckoLib startup | Separate disposable client with official Iris1.11.4 + Sodium0.9.2 + unmodified Photon ZIP, 0 ERROR/FATAL | **PASS menu only** |
| Dedicated server bootstrap and The Sift dimension | \`run/server-26.2.0.90-photon-alpha3/logs/latest.log\`, \`Done (2.251s)!\`, 0 ERROR/FATAL | **PASS boot only** |
| Photon sky renders correctly and no sky pieces cover ground | Needs player-operated in-world test and screenshot | **PENDING** |
| Complementary sky remains beautiful / shaders OFF unchanged | User-accepted in ALPHA2; needs recheck on ALPHA3 | **PENDING ALPHA3 visual** |
| Mod FPS/VRAM & shader toggle/reload | Needs new manual test results | **PENDING** |

**Known risk:** An unmapped native pipeline may be ignored, composited incorrectly or produce a black sky under a particular Iris version. Client-menu startup cannot prove the in-world result. If Photon still breaks, **do not merge or publish this experiment**; fall back to frozen ALPHA2 while exploring an explicit mod-owned framebuffer composite rather than modifying Photon or degrading Complementary.

The \`isShaderPackInUse()\` callback can return false during early FML setup even when the selected pack is already loaded. ALPHA3 correctly separates **shaderpack name identification** from the renderer's own shader-active check. The startup log verified Photon detection as \`true\` after this correction.

**Scope:** No live ModTest, NeoPack, original 26.3 port, frozen ALPHA1/ALPHA2 worktrees or photon_v1.3b.zip bytes were modified. No external repository release/push, no player-world actions. All work stays inside Prime 2.
