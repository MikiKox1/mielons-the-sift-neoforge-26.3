# The Sift Port 1.1.2 — official community release for Minecraft 26.1.2

**Status:** user-accepted stable build of the *unofficial NeoForge community port*; original author Mielon, port maintainer MikiKox1. This is not an official release published by Mielon. External GitHub/CurseForge publication remains an independent user-authorized action.

## Provenance

- Branch: `release/1.1.2-neoforge.1-mc26.1.2`; tag: `v1.1.2-neoforge.1-mc26.1.2` (unique to this Minecraft line).
- Original upstream The Sift **1.1.2**, Mielon (MIT); official NeoForge Minecraft 1.21.1 was used as reference. This standalone port derives from accepted local MC26.2 source `aa02718ae239c4439ba8f774dab7a2c6523e02fc`, then MC26.1.2 ALPHA1 `602fa8b97e200fa1b3e20e81eb741739dc2a32be` and user-accepted ALPHA2 `12b1790b88158de346828c31565539de46c93cbf`.
- The only differences relative to accepted ALPHA2 are **stable version/description, an independently validated lower NeoForge minimum and release documentation**. No worldgen, structural NBT, sky gradient, entity, advancement, fluid or networking behavior is altered.
- Preserve Mielon's MIT license, contributor credits for original resource authors, and disclosure of AI-assisted community porting. Do not claim the community backport is Mielon's own update.

## Exact compatibility

| Item | Stable requirement |
| --- | --- |
| Minecraft | **26.1.2 only** (`[26.1.2]`) |
| NeoForge | **26.1.2.100 or newer** (`[26.1.2.100,)`), no upper bound; **26.1.2.117** used for final build and complete user gameplay acceptance |
| Java | **25** |
| GeckoLib | **5.5.2** or compatible 5.x (`[5.5.2,6)`), required |
| Optional shaders | Iris **1.11.4** + Sodium **0.9.2** on MC26.1.2, Photon v1.3b / Complementary Unbound r5.9.3 |
| Optional integrations | Easy Magic **26.1.0** + Puzzles Lib **26.1.16**, LambDynamicLights **4.11.2+26.1.2** |

## Why NeoForge 26.1.2.100 is the minimum

The minimum was lowered from the original provisional `.117` only after independent evidence:

1. The official Maven repository lists 26.1.2.100, .105, .110, .114 and .117, all depending on **FancyModLoader 11.0.15**.
2. Official `universal.jar` libraries for these five builds contain all **35 types imported by The Sift**. Between .100 and .117, **34/35** have identical public/protected binary declarations; the only extra .117 method is `EventHooks.onEquipmentDropChance`, unused by The Sift.
3. Exact accepted ALPHA2 source compiled with **NeoForge 26.1.2.100**: isolated `clean build` PASS, using actual mapped game classes/patches from .100 rather than just manifest tweaks.
4. Separate **NeoForge .100 dedicated server**: Sift dimension+clock, original portal NBT placement, saved world and graceful stop, all 7/7 PASS, 0 ERROR/FATAL.
5. Separate **NeoForge .100 client boots**: shaderless, Iris/Sodium + Photon v1.3b, and Iris/Sodium + Complementary r5.9.3 all loaded without ERROR/FATAL and loaded GeckoLib's five models/animations.
6. Complete A01–A20 including shader visuals and original achievement progression passed on **.117**, per user. This does not prove all gameplay on .100; .100 underwent build + targeted loader/bootstrap/world tests.

**Intentionally not claiming** support for NeoForge below .100: those earlier builds were not tested. The open upper bound means later 26.1.2 patch releases are not artificially rejected; it does not guarantee untested releases are bug-free.

## Release QA

- Minecraft 26.1.2 source build on the latest verified NeoForge .117, exact 26.1.2-only mod metadata, ZIP and source archive checksum parity.
- All 100 advancement JSON verified: original 98 resources (except six documented version-specific target differences) + two recipes; Fallen Civilization and Certified Sifter use original `minecraft:impossible` criteria and a 16-tick server player-position tracker.
- Worldgen **158 definitions**, original **16 NBT structures**, **six** RGBA gradient sky layers; parity with user-accepted ALPHA2.
- Original user **A01–A20 PASS** on ALPHA2; MC26.1.2 A18 Photon now accepted, no shaderpack ZIP modifications.
- Clean source commit/tag, exact JAR/source SHA-256, rollback to ALPHA2 archived unchanged and MC26.2/26.3 maintained independently.

## Publication guidance

- External release label: **Port 1.1.2 — Minecraft 26.1.2**. Upload the specific release JAR ending `mc26.1.2-neoforge-port.jar`, NOT the ALPHA1/ALPHA2 stage JARs or a MC26.2/26.3 binary.
- Include a Minecraft-specific tag `v1.1.2-neoforge.1-mc26.1.2`, source archive, checksum, GeckoLib requirement, NeoForge minimum .100, Minecraft 26.1.2 and license/credits.
- Public changelogs: English, impersonal, **only actual changes introduced by this NeoForge MC26.1.2 backport**. Do not include sections about unchanged functionality, private crash history or test framework internals.
- Existing public repository `MikiKox1/mielons-the-sift-neoforge-26.3` has MC version-separated branches under Prime3 conventions; do not make MC26.1.2 code appear as a 26.2/26.3 port. Push/publish only through an explicitly authorized authenticated channel.
