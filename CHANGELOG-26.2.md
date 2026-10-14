# Changelog — Minecraft 26.2 (NeoForge)

Released versions are listed **newest first**.

## Port 1.1.2 — Minecraft 26.2

A distinct NeoForge implementation of Mielon's The Sift 1.1.2 for Minecraft 26.2.

- Adapted the original 26.2 dimension, **biomes, world generation, features, carvers, structure templates, entities, fluids, networking, menus and registries** to NeoForge.
- Preserved the original `the_sift` content, advancement progression, blocks, mobs, structures and loot.
- Adapted portal, Rift, Sonorous beam, Singer and animated-sky rendering to the Minecraft 26.2 graphics APIs.
- Updated **Ichor Regeneration I** contact and refresh behavior, including cauldrons and blocks containing Ichor.
- Updated **Dark Sniffer Sonic Sniff** to track targets by UUID, preventing repeated pulls within a wave and preserving affected player movement across save/rejoin.
- Corrected **Ichor rain and snow** rendering, including Ichor-Snowy Peaks.
- Preserved **Ancient City portals, Rift return travel, Charoite enchanting and anvil restrictions**; adapted willow growth, bonemeal, stripping and composting.
- Reworked the **six-layer animated Sift sky** with RGBA gradient textures and interpolated UVs, reducing color banding and geometric artifacts with **Complementary Shaders**.
- Added **Photon Shaders** depth-masked sky composition after Iris's final pass, without drawing sky layers over terrain or altering shaderpack ZIPs.
- Preserved the native shaderless sky, and added optional **Easy Magic 26.2.0** integration.

[Port 1.1.2 — Minecraft 26.2](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/releases/tag/v1.1.2-mc26.2-neoforge)

**Original mod:** Mielon (MIT). **Unofficial NeoForge port:** MikiKox1. See [README.md](README.md) for the current, version-specific dependencies and [SOURCE_PROVENANCE.md](SOURCE_PROVENANCE.md) for credits.
