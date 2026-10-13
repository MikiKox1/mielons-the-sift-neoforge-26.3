# Manual A06 / A18 test: ALPHA2 textured sky vs Photon / Complementary

**Important:** The alpha2 experiment is NOT a public release. No changes to NeoPack, ModTest or your existing saves are needed. Create a **separate disposable MC26.2 test profile** with NeoForge 26.2.0.90, Java 25, GeckoLib 5.5.6, and **only** the ALPHA2 JAR (remove ALPHA1 from this profile). Iris 1.11.4 + Sodium 0.9.2 MC26.2 and Photon/Complementary are optional per-case additions; use versions actually compatible with 26.2.

Record a screenshot and FPS for each comparison at the **same world location, weather and time of day**. Your prior ALPHA1 Photon and Complementary screenshots are the before images.

| ID | Test | Expected | Status |
| --- | --- | --- | --- |
| SKY01 | No Iris, no shaders, create disposable The Sift world | Original vanilla six-layer rainbow sky, same clouds, portal and beacon/beam | PENDING |
| SKY02 | Iris + Sodium installed, shaderpack OFF | No change to old sky, no crash, no texture missing | PENDING |
| SKY03 | Iris + **Photon** ON; look directly toward zenith | No giant jagged magenta/cyan triangles, concentric polygon rims or black sky | PENDING |
| SKY04 | Iris + **Complementary** ON; look at same zenith | Smooth cyan/blue sky gradient instead of concentric flat-color steps | PENDING |
| SKY05 | Photon and Complementary day, night and near horizon | Rainbow/ribbons remain, no incorrect recoloring, horizon seam or severe alpha clipping | PENDING |
| SKY06 | Turn camera 360° including upper/lower dome | No vertical seam at U=0/U=1, no abrupt texel-colored wedges at poles | PENDING |
| SKY07 | Toggle shaders OFF/ON, F3+T reload resources, travel between Sift and Overworld | No white/missing textures, stale resources, crashes, corruption or vanilla-weather changes | PENDING |
| SKY08 | Compare FPS/VRAM with ALPHA1 using same viewpoint | No major stutters or unacceptable sustained FPS regression; note first-render texture upload | PENDING |
| SKY09 | Ichor rain and snowy-peaks snow under both shaderpacks | Weather/sky alpha blend stays legible; prior working mechanics unchanged | PENDING |
| SKY10 | Rift/Ancient City portal, Sonorous beam, Singer light | Textured-sky pipeline does not hide beams/particles or leak into other dimensions | PENDING |

**If a test fails:** provide its ID, exact shaderpack version, Iris + Sodium versions, screenshot (especially your same two prior viewpoints), coordinates, dimension, in-game weather and \`logs/latest.log\` from the new standalone test profile. Do not modify Photon or Complementary to fix a failure.

**Only tests of real loaded shaderpacks count as A06/A18 acceptance.** Iris finding the TEXTURED pipeline at the main menu is helpful technical evidence but does NOT prove visual correction.
