# Photon ALPHA4 — manual in-world tests

Use a separate disposable Minecraft 26.2 profile with NeoForge 26.2.0.90, Java25, GeckoLib5.5.6 and only the ALPHA4 JAR. Iris1.11.4 and Sodium0.9.2 are the versions used in isolated bootstrap testing. Use original Photon v1.3b and the Complementary version that passed in ALPHA2. Do NOT install Alpha1/2/3/4 simultaneously or modify the Photon ZIP.

| Test | Expected | State |
| --- | --- | --- |
| P1 Photon night, same direction as last screenshot | The Sift colorful moving aurora/rainbow becomes visible above terrain, not plain blue Photon sky | PENDING |
| P2 Photon looking at terrain, horizon and moon | No opaque/transparent sky sheets in front of rock pillars, correct distant mountains, moon/clouds still partly visible | PENDING |
| P3 latest.log after entering The Sift with Photon | The Sift ALPHA4 active sky render path: Photon post-final depth-masked UV sky (experimental) | PENDING |
| P4 Complementary same world and settings | Still smooth as ALPHA2, no rings, no missing sky | PENDING |
| P5 Shaderpacks disabled | Original The Sift sky still visible and unchanged | PENDING |
| P6 Day/night, rain, shaderpack toggle, F3+T | No missing textures, crash, z-fighting or stale sky buffers | PENDING |
| P7 FPS/VRAM at same coordinates | No unacceptable FPS or memory regression | PENDING |

If Photon still shows ordinary sky, send a screenshot AND the single render-path log line above. If the line is absent, the optional final-pass hook did not execute. If line present but sky invisible, framebuffer/depth compositing is still wrong. If terrain becomes covered, depth masking needs correction. Do not modify any live modpack or shaderpack to hide the issue.

This is NOT a final release or guaranteed Photon fix. The frozen ALPHA2 remains the good baseline for Complementary.
