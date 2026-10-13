# Port 1.1.2

**Release type:** Release (publication handed off to Prime 3)

**Minecraft:** 26.3

**Loader:** NeoForge 26.3.0.64-beta or newer, no upper NeoForge bound
**Internal mod version:** \`1.1.2-neoforge.1\`

## Updated from Port 1.1.1

- Adopted original Mielon's The Sift **1.1.2** bug fixes, preserving the earlier independent NeoForge compatibility repairs.
- **Ichor:** Regeneration I is driven by actual living-entity contact with Ichor, including Ichor cauldrons and fluid-bearing blocks. The effect refreshes as designed and handles leaving the fluid; flowing-fluid slowdown remains.
- **Dark Sniffer:** Sonic Sniff hit tracking uses entity UUIDs to avoid repeated hits in one wave. Player momentum caused by Sonic Sniff is marked and handled safely across save/rejoin, without wiping unrelated motion.
- **Sift weather:** changed precipitation rendering injection to the newer upstream approach for accurate rain versus Ichor snow in the intended biomes.
- Retained Ancient City portals/Rifts, Charoite and optional Easy Magic integration, Ichor buckets/waterlogging, Singer lighting and other stable Port 1.1.1 fixes.
- Omitted the redundant upstream nested \`textures.zip\` because it duplicates textures already contained in the mod.

## Build and acceptance

- Compiled on **NeoForge 26.3.0.76** / Java 25 / Gradle 9.2.1, metadata minimum **\`[26.3.0.64-beta,)\`** and Minecraft \`[26.3]\`.
- Independent source/ZIP/metadata checks and disposable dedicated-server bootstrap: PASS. User confirmed completion of the previously supplied in-world feature and Charoite/Easy Magic test checklists on 2026-10-13. Detailed results are recorded in the Prime 2 release handoff; those user tests are not misrepresented as independent automated measurements.
- An old unpatched Sodium \`0.9.3-alpha.1\` caused an unrelated client mixin crash on NeoForge \`26.3.0.76\`; the user addressed their test profile's renderer-mod mismatch before confirming functionality. A particular NeoPack-modified Sodium is NOT a runtime dependency of The Sift.

**Original author:** Mielon (MIT). **Independent NeoForge port:** MikiKox1. See \`LICENSE\`, \`SOURCE_PROVENANCE.md\`, and \`AI_DISCLOSURE.md\`.

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
