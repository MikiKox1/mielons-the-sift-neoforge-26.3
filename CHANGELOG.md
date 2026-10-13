# Port 1.1.2 — Minecraft 26.3

**Release type:** Release

**Minecraft:** 26.3

**Loader:** NeoForge 26.3.0.64-beta or newer

**Port version:** 1.1.2

## Updated

- Updated the NeoForge 26.3 port from **Mielon's The Sift 1.1.1 to 1.1.2**, incorporating upstream gameplay fixes.

## Gameplay fixes

- **Ichor Regeneration:** Reworked contact-based Regeneration I detection and refresh across flowing Ichor, fluid-containing blocks and cauldrons, without replacing stronger independent effects.
- **Dark Sniffer / Sonic Sniff:** Changed per-wave hit tracking to entity UUIDs, preventing repeated pulls on the same target. Corrected Sonic Sniff-induced player velocity across save/rejoin.
- **Sift precipitation:** Corrected the weather renderer's handling of Ichor rain and snow in the appropriate biomes.

## Shader and sky rendering

- Reworked the animated **six-layer Sift sky** using smooth RGBA gradient textures and interpolated UVs instead of flat per-vertex shaderpack colors.
- Improved **Complementary Shaders** compatibility, reducing sky banding, polygon rings and jagged triangles.
- Added a dedicated **Photon Shaders** compatibility path for post-final sky compositing, depth-masked auroras and blending with Photon clouds and moonlight.
- Preloaded sky textures and GPU buffers before an active **RenderPass**, correcting a texture-upload crash observed with Photon on Minecraft 26.3.
- Separated native and shaderpack-specific sky rendering. **No changes to Photon or Complementary shaderpack ZIPs are required.**

## Requirements

- **Minecraft:** 26.3
- **NeoForge:** 26.3.0.64-beta or newer; metadata range `[26.3.0.64-beta,)` (no upper bound)
- **Java:** 25
- **GeckoLib:** 5.5.7 or compatible 5.x (required)
- **Easy Magic 26.3.0, LambDynamicLights, Iris and compatible Sodium:** optional

## Release assets

- `mielons-the-sift-1.1.2-mc26.3-neoforge-port.jar`

  **SHA-256:** `56724D4A0F7E42A8D21D3D85447A945FAAE93A04BFB11D8E485C1FF5914BFD19`
- `mielons-the-sift-1.1.2-mc26.3-neoforge-port-sources.jar`

  **SHA-256:** `11E4AF34FF3BB0087FC0EE2007C977CCEFC901786E47B5E87CD63CF3636D2A10`

**Validation:** Java 25 clean build, archive and metadata checks, regression test for the texture-upload crash, dedicated-server startup and shaderless client-menu launch passed. **The final in-world Photon/Complementary visual check after this RenderPass fix remains pending**; universal shaderpack compatibility is not claimed. Incompatible Sodium builds can fail independently on NeoForge 26.3.0.76.

**Original mod:** Mielon (MIT). **Unofficial NeoForge port:** MikiKox1. See `LICENSE`, `SOURCE_PROVENANCE.md` and `AI_DISCLOSURE.md`.

---

# Port 1.1.1

**Release type:** Release

**Minecraft:** 26.3

**Loader:** NeoForge 26.3.0.64-beta or newer

**Port version:** 1.1.1

## Ported

- Adapted Mielon's The Sift from Fabric to NeoForge 26.3, including registrations, networking, world generation, dimension logic, entities, and client rendering.
- Preserved the original `the_sift` mod ID, dimension content, mobs, items, and progression.

## Fixed

- Corrected Ichor fluid behavior, bucket interactions, and waterlogging.
- Corrected Ancient City portal activation and Rift return routing.
- Stabilized Singer dynamic lighting and Sonorous beam rendering.
- Corrected water-sensitive Sift plants, Echo Golem audio, and sky rendering compatibility.

## Added

- Optional Easy Magic 26.3.0 integration for Charoite enchanting, while keeping vanilla enchanting available without Easy Magic.
- Optional compatibility with LambDynamicLights and supported client rendering integrations.

## Requirements

- Minecraft 26.3, NeoForge 26.3.0.64-beta or newer, and Java 25.
- GeckoLib 5.5.7 or a compatible 5.x version is required.
- Easy Magic and LambDynamicLights are optional.

**Original mod:** Mielon (MIT). **NeoForge port:** MikiKox1.
