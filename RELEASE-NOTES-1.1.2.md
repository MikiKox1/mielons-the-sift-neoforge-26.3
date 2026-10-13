# Port 1.1.2 — Minecraft 26.3

**Status:** Published GitHub release · **Minecraft:** 26.3 · **Loader:** NeoForge · **Java:** 25

**Internal mod version:** `1.1.2-neoforge.3` · **Original mod:** Mielon's The Sift 1.1.2

This is the single, consolidated release record for the 26.3 gameplay update, shader compatibility work and final RenderPass crash correction. Superseded development-stage details remain recoverable from Git history.

## Upstream 1.1.2 gameplay fixes

- **Ichor Regeneration:** Updated contact-based Regeneration I detection and refresh for flowing Ichor, Ichor-containing blocks and cauldrons, without overriding stronger independent effects.
- **Dark Sniffer / Sonic Sniff:** Entity UUIDs prevent repeated hits from the same wave; Sonic Sniff-induced player velocity is handled across saving and rejoining.
- **Sift precipitation:** Corrected Ichor rain and snow rendering in the appropriate biomes.
- The earlier NeoForge-specific portal/Rift, fluid/bucket, Charoite/Easy Magic, Singer lighting, Sonorous beam, vegetation and sound fixes remain included.

## Animated sky and shader compatibility

- The six original day/night Sift sky layers use pre-baked, smooth RGBA gradient textures and interpolated UV sampling instead of faceted shaderpack vertex colors.
- **Complementary Shaders:** An Iris TEXTURED rendering route avoids the previously visible triangular sky facets, concentric bands and jagged color transitions.
- **Photon Shaders:** An optional Iris final-pass callback blends the colored sky after Photon compositing, with depth masking so it does not render over terrain. The reverse-Z path uses far-depth comparison (`CompareOp.EQUAL`) with depth writes disabled; the base sky uses 72% alpha so Photon clouds and moonlight remain visible.
- **Shaders off:** The original native sky rendering path remains available.
- Photon and Complementary shaderpack ZIPs do not need edits, and no shaderpacks or optional renderers are bundled into the mod.

## RenderPass texture-upload fix

An earlier 26.3 shader-sky candidate could call `TextureManager.getTexture` lazily while a RenderPearl RenderPass was open, triggering `IllegalStateException: Close the existing render pass before performing additional commands` during a Photon render. This was a The Sift rendering bug, distinct from the unrelated Sodium `LevelExtractorMixin` compatibility issue.

The final Port 1.1.2 build preloads six sky textures, GPU sampler, vertex buffers and index buffers before the rendering pass opens. Drawing then binds already prepared views without a texture upload inside the open pass. Prepared views are refreshed after resource changes and released on renderer shutdown. The shader paths, world generation and gameplay content are preserved.

## Verification

| Check | Status |
| --- | --- |
| Java 25 clean build and source regression checks | PASS |
| JAR structure, metadata and sky resources | PASS |
| Dedicated server with The Sift dimension loaded | PASS |
| Isolated shaderless client menu, models and animations | PASS |
| **Photon in-world after final RenderPass fix** | **PASS — confirmed by user** |
| **Complementary in-world after final RenderPass fix** | **PASS — confirmed by user** |
| **Shaders disabled in-world after final RenderPass fix** | **PASS — confirmed by user** |

Following the final correction, the user confirmed that everything worked in the in-world Photon, Complementary and shaderless control tests. This confirmation is specific to the tested setup; it does not establish compatibility with every future shader, Sodium/Iris pairing, or NeoForge build.

Some public Sodium builds have an independent `LevelExtractorMixin` incompatibility with NeoForge 26.3.0.76. Sodium, Iris, shaderpacks, LambDynamicLights and Easy Magic remain optional; no NeoPack-specific modified renderer is packaged with The Sift.

## Requirements

- **Minecraft:** 26.3
- **NeoForge:** 26.3.0.64-beta or newer (open-ended metadata range `[26.3.0.64-beta,)`; tested on 26.3.0.76)
- **Java:** 25
- **GeckoLib:** 5.5.7 or a compatible 5.x (required)
- **Easy Magic 26.3.0:** optional; its Puzzles Lib dependency is separate
- **LambDynamicLights, Iris and compatible Sodium:** optional

## Release assets

- **Mod:** `mielons-the-sift-1.1.2-mc26.3-neoforge-port.jar` — SHA-256 `56724D4A0F7E42A8D21D3D85447A945FAAE93A04BFB11D8E485C1FF5914BFD19`
- **Sources:** `mielons-the-sift-1.1.2-mc26.3-neoforge-port-sources.jar` — SHA-256 `11E4AF34FF3BB0087FC0EE2007C977CCEFC901786E47B5E87CD63CF3636D2A10`
- **GitHub release:** https://github.com/MikiKox1/mielons-the-sift-neoforge-port/releases/tag/v1.1.2-mc26.3-neoforge
- **Older 26.3 Port 1.1.1:** https://github.com/MikiKox1/mielons-the-sift-neoforge-port/releases/tag/v1.1.1-neoforge.2-community.3-fix.1

## Attribution

**Original mod:** Mielon (MIT). **Unofficial NeoForge port:** MikiKox1. The original `the_sift` mod ID, assets and credits are retained. See `LICENSE`, `SOURCE_PROVENANCE.md`, and `AI_DISCLOSURE.md`. Previous development-stage investigations remain accessible in Git history; no published binary was modified by this documentation consolidation.