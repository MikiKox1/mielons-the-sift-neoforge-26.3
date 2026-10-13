# The Sift 1.1.2 — experimental sky ALPHA2, MC26.2 / NeoForge

**Date:** 2026-10-13. **Deliverable:** local experiment only, **NOT a public release**.
**Original stage:** ALPHA1 commit \`d632a6fa87de037583a1686696449c6f22aa6bd4\`, binary SHA-256 \`6BCBD85FB607C421ECD522E0CACB0B8AEF59A38C0337859E1FB2BC2514985032\`. The original ALPHA1 Git branch and JAR are **unchanged**.
**Experiment branch:** \`experiment/26.2-sky-texture-alpha2\`, independent worktree \`work/neo-26.2-1.1.2-experiment-sky-alpha2/\`.

## Problem

The user passed all their manual MC26.2 gameplay test cases, but provided two screenshots showing severe sky faceting under **Photon** and visible concentric blue gradient bands under **Complementary**. Earlier ALPHA1 increased sky geometry detail but left RGB/alpha in a \`POSITION_COLOR\` vertex format. Iris routes it through \`BASIC\` / \`gbuffers_basic\`. Complementary's \`flat glColor\` disables smooth per-pixel interpolation; increasing polygon count alone cannot change that.

Original investigation: \`TheSift/reports/qa/20261013-MC26.2-IRIS-SKY-PHOTON-COMPLEMENTARY-INVESTIGATION.md\`.

## Experimental fix, owned entirely by The Sift

1. The \`tools/generate_sky_gradient_textures.py\` script **pre-bakes** the exact existing six static gradients (base day/night, far ribbons day/night, near ribbons day/night) into **RGBA8 PNG 2048×768**. The four ribbon maps preserve transparent alpha, all U=0/1 pixel columns match and every adjacent alpha sample changes by **at most 3/255**. Texture pixels are generated at build/preparation time using the workshop's existing NumPy/Pillow, not downloaded or regenerated each frame.
2. Only when \`SiftShaderCompat.isShaderPackInUse()\` is true does \`SiftProceduralSkyRenderer\` switch to a **single shared \`POSITION_TEX\` UV dome**, vanilla \`core/position_tex\` shaders, a LINEAR + clamp-to-edge texture sampler, and an additional \`the_sift:pipeline/procedural_sky_textured_alpha2\` pipeline explicitly mapped to **Iris \`TEXTURED\`**.
3. The original \`RenderPipelines.DEBUG_QUADS\` / \`POSITION_COLOR\` path is **unchanged** whenever a shaderpack is disabled or Iris is absent. All six existing animated transforms, day/night modulation, rain dimming, layering and camera transforms remain.
4. Source-generated texture resources are vanilla-managed \`SimpleTexture\` assets. Shaderpack resources are not edited. Buffer is GPU-released by \`SiftProceduralSkyRenderer.close()\`; \`TextureManager\` owns image reload on resource changes. No per-frame image upload/reallocation.
5. The previous ALPHA1 shader-mode mesh subdivisions are preserved for the experiment, making this a narrower change to color sampling rather than a geometry/performance redesign. The **single shared vertex buffer** avoids duplicating that mesh six times.

**Resources:** 6 × 2048 × 768 × 4 RGBA8 bytes ≈ **36 MiB unpacked texture pixels on GPU**, plus ≈3 MiB shared position/UV vertex buffer; actual total VRAM/CPU overhead and rendering FPS must be measured in-world. The packaged PNG files compress far smaller. No resource download or GUI shaderpack modification.

## Actual QA, not guessed

| Gate | Result | Evidence |
| --- | --- | --- |
| Official latest MC26.2 NeoForge build | **PASS** | Maven verification reported **26.2.0.90** latest on 2026-10-13, independent of provisional minimum \`[26.2.0.90,)\` |
| Java25 Gradle **clean build** | **PASS** | \`logs/the-sift-112-mc262-alpha2-textured-sky-cleanbuild-02.log\`, \`BUILD SUCCESSFUL\` |
| Six textures reproducible | **PASS** | \`tools/generate_sky_gradient_textures.py --verify\`; 6/6 exact PNG pixels/alpha/seams |
| JAR package and source parity | **PASS** | \`tools/tests/verify_sky_alpha2_experiment.py\`; all non-sky gameplay JAR entries are unchanged in content from ALPHA1, with 243 text/json files differing only by CRLF/LF formatting; no deleted entries; six PNGs added and only one compiled renderer class changed |
| Independent server bootstrap | **PASS** | \`run/server-26.2.0.90-sky-alpha2/logs/latest.log\`, \`Done (2.387s)!\`, \`the_sift:the_sift\`, **0 ERROR/FATAL** |
| Independent client title-screen without Iris | **PASS** | \`run/client-26.2.0.90-sky-alpha2\`, responsive window, GeckoLib 5 models/animations, **0 ERROR/FATAL**, clean window close and Gradle SUCCESS |
| Client with pinned official Iris/Sodium | **PASS menu bootstrap** | Iris **1.11.4** / Sodium **0.9.2** MC26.2 on isolated Prime2 only; \`logs/the-sift-alpha2-neo262-client-menu-iris-sodium-01.log\`, **0 ERROR/FATAL**, no fatal mixin error; Iris logged **\`Found okay program match ... procedural_sky_textured_alpha2: TEXTURED\`** |
| **Photon shaderpack in-world** | **PENDING** | No shaderpack loaded in automated client: Iris logged shaders disabled because no valid pack selected. Needs user screenshot |
| **Complementary shaderpack in-world** | **PENDING** | Same; especially no flat triangles/concentric rings and correct rainbow colors |
| Shader toggle/reload, day/night, rain and FPS/VRAM | **PENDING** | Must be tested on a new user test profile/world |
| Publication or live installation | **NOT PERFORMED** | Separate user approval required |

Known unrelated bare-client warnings: third-party Iris/Sodium deprecated \`logoFile\` metadata and optional existing \`Sampler0/Sampler1\` uniforms unused in other The Sift portal pipelines. Neither prevented client-menu bootstrap. They are **not evidence of actual shaderpack sky compatibility**.

## Next required user acceptance

Follow **\`SKY-ALPHA2-MANUAL-TESTS.md\`**. Compare the same viewpoint/seed/time with ALPHA1 and ALPHA2 in **Photon and Complementary**. Expected: visible rainbow sky preserved, no exaggerated triangles or rings, no black/invisible dome, sensible performance. If sky is still flawed, retain the ALPHA1 binary and report the exact pack version/settings, screenshot and log before redesigning.

**Do not merge ALPHA2 into the stable 26.2 port or release it to Prime 3 until shaderpack screenshots PASS.**
