# The Sift 1.1.2 — Photon ALPHA4 post-final sky experiment

Date: 2026-10-13. Minecraft 26.2 / NeoForge 26.2.0.90. STAGE ONLY, NOT RELEASED.

## Diagnosis

User ALPHA2: Complementary and shaderless PASS, Photon FAIL (sky layers drawn over terrain).
User ALPHA3: Photon no longer overlays terrain, but The Sift's rainbow sky is entirely missing. Other modes still PASS.

Original Photon v1.3b Modrinth version gUv7fBPN source ZIP was pinned unchanged with SHA256 120897768EAA8CB1F14A3E98F88DFF009AE3204EE3B96904A4FE8586EDFC1EAD. Its gbuffers_textured draws lit translucent world material to attachment 13. ALPHA3 unmapped early native draw was overwritten when Iris later performed its final-pass composition.

## ALPHA4 mod-side experiment

ALPHA4 is a separate branch/worktree: work/neo-26.2-1.1.2-experiment-photon-alpha4 and experiment/26.2-photon-postfinal-alpha4. Based on unchanged ALPHA3 commit f8f166a35bd8767754b1c151769fe18af399553c.

1. Photon only: save the exact sky pose and modelview when SkyRenderer requests the sky, but do not draw it early.
2. An optional client mixin into IrisRenderingPipeline.finalizeLevelRendering() at TAIL draws the original six sky textures after Photon composites its final picture to the main Minecraft framebuffer.
3. Target-only depth mask: Minecraft 26.2 GlDevice uses GL_ZERO_TO_ONE and reversed Z, confirmed via source ARBClipControl.glClipControl(36001, 37727). The mod-owned vertex shader emits far-plane clip Z=0; the registered native pipeline compares EQUAL against existing depth and writes no depth. This SHOULD keep terrain visible in front of auroral ribbons.
4. The base sky layer blends with Photon at 72% rather than replacing all its clouds and moon. The four aurora/ribbon layers retain their existing opacity and animation.
5. Complementary and other non-Photon packs use ALPHA2 Iris TEXTURED unchanged. With shaderpacks off, retain the existing POSITION_COLOR renderer.
6. No shaderpack source is modified, and none is bundled into the The Sift JAR. ALPHA1, ALPHA2, ALPHA3 and the accepted 26.3 port remain frozen.

## Independent results

- Java25 / newest NeoForge 26.2.0.90 clean build: PASS.
- All mod resources/6 sky gradient maps retained: PASS by static archive comparison.
- Target Minecraft26.2 ZERO_TO_ONE/reversed Z checked from patched vanilla GlDevice and DepthStencilState: PASS static.
- No-Iris isolated client menu: PASS, 0 ERROR/FATAL, GeckoLib 5 models loaded.
- Iris1.11.4 + Sodium0.9.2 + official Photon v1.3b isolated menu: PASS, Photon actively loaded, 0 ERROR/FATAL.
- Dedicated isolated server: PASS, Done (3.068s), Sift dimension registered, 0 ERROR/FATAL.
- Photon sky visible and not overlaying terrain in-world: PENDING USER SCREENSHOT.
- Complementary and no-shader regression in-world: PENDING ALPHA4 USER CHECK.

Limitations: Client menu does not execute the in-world Iris final-pass callback. The mixin and GPU depth-mask are source/binary verified, but visual success is NOT established until the user tests an in-world frame. If the sky is still absent or blocks are overlaid, do not publish or merge ALPHA4. Send screenshot plus render-path log, then re-evaluate the framebuffer/depth approach.

Next: see SKY-PHOTON-ALPHA4-TESTS.md. Stage-only binary and SHA256 manifest will be located in TheSift/artifacts/builds/experimental-sky-alpha4-photon-262/.
