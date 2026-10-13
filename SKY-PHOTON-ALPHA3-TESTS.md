# ALPHA3 — fast manual comparison (Photon / Complementary / no shaders)

Use **an entirely new disposable Minecraft 26.2 test profile**, with **NeoForge 26.2.0.90, Java25, GeckoLib5.5.6**, only the ALPHA3 JAR, and official Iris1.11.4 + Sodium0.9.2 for MC26.2. Never keep ALPHA1/ALPHA2/ALPHA3 in one \`mods\` directory. Use compatible Photon **v1.3b** and your previously working Complementary settings. Do **not** modify either shaderpack.

| Test | Expected | Status |
| --- | --- | --- |
| P1. Photon v1.3b in the same sky viewpoint as your screenshot | No massive translucent blue/teal ribbons in front of mountains or blocks; visible sky gradient with correct layered colors | PENDING |
| P2. Check \`latest.log\` after entering The Sift under Photon | \`The Sift ALPHA3 active sky render path: Photon native POSITION_TEX (experimental)\` occurs once | PENDING |
| P3. Complementary with the same viewpoint/time | ALPHA2 smooth sky, no rings, no exaggerated triangles or missing bands | PENDING |
| P4. Shaders disabled, same scene | Original rainbow gradient and cloud/sky behavior as ALPHA1/2 | PENDING |
| P5. Day → night, rain and snow, 360° view | Day/night transitions preserved; no dark wedges, seams, ghosted terrain or z-fighting | PENDING |
| P6. Turn Photon on/off, F3+T, dimension travel | No missing resource textures, runtime exceptions or permanent renderer state | PENDING |
| P7. FPS comparison on same seed/coords | Comparable smoothness to ALPHA2; no severe CPU spikes or VRAM growth | PENDING |

If P1 fails or the sky is black/invisible, **send the new screenshot and the \`The Sift ALPHA3 active sky render path\` line from \`latest.log\`**; do not test by modifying Photon's files. This is a staged experiment and the previous ALPHA2 remains available for instant rollback.
