# The Sift — NeoForge Port 1.0.0

**Release type:** Release

**Minecraft:** 26.3

**Loader:** NeoForge 26.3.0.64-beta or newer

**Version:** 1.0.0

**File:** `the-sift-neoforge-port-1.0.0.jar`

### Ported

- Adapted Mielon's The Sift from Fabric to NeoForge 26.3, including mod registration, networking, world generation, dimension and entity logic, and client rendering.
- Preserved the original `the_sift` mod ID, dimension content, mobs, items, and progression.

### Fixed

- Adapted Ichor fluid interactions, bucket behavior, and waterlogging for NeoForge.
- Corrected Ancient City portal activation and Rift return routing.
- Stabilized Singer dynamic lighting and Sonorous beam rendering.
- Corrected water-sensitive Sift plants, Echo Golem audio, and sky rendering compatibility.

### Added

- Optional Easy Magic 26.3.0 integration for Charoite enchanting, with vanilla enchanting remaining available.
- Optional compatibility with LambDynamicLights and supported client rendering integrations.

### Requirements

- Minecraft 26.3, NeoForge 26.3.0.64-beta or newer, Java 25.
- GeckoLib 5.5.7 or a compatible 5.x version is required.
- Easy Magic and LambDynamicLights are optional.

**Original mod:** Mielon (MIT). **NeoForge port:** MikiKox1.

---

Future versions of this port use independent semantic versioning (`1.0.1`, `1.1.0`, and so on). Each future changelog records changes since the preceding public port release, not changes to the original Fabric mod.
