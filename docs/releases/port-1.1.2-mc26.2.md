# Port 1.1.2 — Minecraft 26.2

**Release type:** Release

**Minecraft:** 26.2

**Loader:** NeoForge 26.2.0.90 or newer

**Port version:** 1.1.2

## Ported

- **Mielon's The Sift 1.1.2** is now available for **Minecraft 26.2 / NeoForge**, based on the original Fabric 26.2 release and the existing independent NeoForge implementation.
- Mod and entity registration, biome and dimension generation, features, carvers, structure templates, fluids, networking, menus and client rendering were adapted to the 26.2 APIs.
- The original `the_sift` mod ID, world content, creatures, blocks, items, progression and structure data are preserved.
- Portals, Rifts, sky/clouds, Sonorous beams and Singer effects were adapted to Minecraft 26.2 Blaze3D rendering.

## Upstream 1.1.2 fixes

- **Ichor Regeneration:** Improved contact detection and Regeneration I refresh for Ichor, including cauldrons and fluid-containing blocks. Fluid slowdown, buckets and waterlogging are retained.
- **Dark Sniffer / Sonic Sniff:** Targets are tracked by entity UUID to prevent repeated hits within a wave. Sonic Sniff-induced player movement is handled across save/rejoin.
- **Sift weather:** Corrected Ichor rain and snow rendering, including Ichor Snowy Peaks.

## Gameplay and compatibility

- Preserved Ancient City portal activation and Rift return travel.
- Adapted willow growth, bonemeal behavior, wood stripping and compostables to NeoForge 26.2.
- Retained Charoite enchanting costs, eligible enchantment upgrades and anvil restrictions.
- Corrected version-specific registration, mixins, shaders and client rendering.

## Shader compatibility

- Rebuilt the **six-layer animated Sift sky** using pre-baked RGBA gradients and interpolated texture coordinates.
- Improved **Complementary Shaders** rendering by removing prominent triangular facets, color banding and concentric sky artifacts.
- Added a dedicated **Photon Shaders** path for depth-masked sky compositing after Iris's final pass, without drawing over terrain.
- Preserved the colorful shaderless sky without modifying Photon or Complementary shaderpack ZIPs.

## Optional integrations

- **Easy Magic 26.2.0:** Optional Charoite support alongside normal lapis enchanting and Easy Magic rerolls. Easy Magic requires **Puzzles Lib** separately.
- **LambDynamicLights**, compatible Iris/Sodium renderers and shaderpacks remain optional. No shaderpack is required to play The Sift.

## Requirements

- **Minecraft:** 26.2
- **NeoForge:** 26.2.0.90 or newer; metadata range `[26.2.0.90,)` (no upper version bound)
- **Java:** 25
- **GeckoLib:** 5.5.6 or a compatible 5.x release (required)
- **Easy Magic, Puzzles Lib, LambDynamicLights, Iris and compatible Sodium:** optional where applicable

## Release assets

- `mielons-the-sift-1.1.2-mc26.2-neoforge-port.jar`

  **SHA-256:** `9740DA26B007C909B60716C78C90691409E30DB7BFF3A6FEF9321462C58A4A7D`
- `mielons-the-sift-1.1.2-mc26.2-neoforge-port-sources.jar`

  **SHA-256:** `43EB70A75A8EAA5309EDAD4A703E01DB7A8A63469A6E0547587F2BDC16B9A918`

**Validation:** Java 25 clean build, archive/metadata checks, isolated dedicated server and Iris/Photon client menu passed. The MC26.2 ALPHA4 sky was accepted by the user in-world; universal shaderpack compatibility and new multiplayer stress tests are not claimed.

**Original mod:** Mielon (MIT). **Unofficial NeoForge port:** MikiKox1. See `LICENSE`, `SOURCE_PROVENANCE.md` and `AI_DISCLOSURE.md`.
