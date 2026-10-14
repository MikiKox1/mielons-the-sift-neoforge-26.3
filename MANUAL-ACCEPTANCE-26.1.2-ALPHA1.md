# The Sift 1.1.2 / NeoForge Minecraft 26.1.2 — ALPHA1 manual acceptance

**Local, unpublished test build.** These are tests for the user to perform, **not claimed results**. Do not use any existing MC26.2/26.3 worlds or the NeoPack/ModTest profiles. Use a new disposable MC26.1.2 profile/world and keep this JAR separate from the already finished 26.2/26.3 builds. Only the user controls the character.

## Runtime requirements

- Minecraft **26.1.2**, **Java 25**, NeoForge **26.1.2.117** (latest tested; provisional minimum `[26.1.2.117,)`, **no upper bound**), mandatory GeckoLib **5.5.2**.
- Optional **Iris 1.11.4+26.1-neoforge** paired with **Sodium mc26.1.2-0.9.2-neoforge**; separate tests with **Photon v1.3b** and **Complementary Unbound r5.9.3**. Do not change shaderpack ZIPs.
- Optional **Easy Magic 26.1.0 + Puzzles Lib 26.1.16**, or **LambDynamicLights 4.11.2+26.1.2**. Each combination has only a **client/loader bootstrap PASS** so far; gameplay is unverified.
- **ALPHA1** only. Never distribute as a final release before these gates pass and the user approves the exact binary.

## A01–A20 manual matrix

| Test | Procedure | Evidence required | Status |
| --- | --- | --- | --- |
| A01 | New MC26.1.2 profile with only The Sift and GeckoLib | Menu boots, mods list, no errors | PENDING manual; automated menu PASS |
| A02 | Create a fresh world, travel to `the_sift:the_sift` | Enter, stay, return without crash | PENDING |
| A03 | Explore all 10 Sift biomes; note X/Z and terrain shapes | Biome name/screens, siftslate/overgrown variants, peaks, ores, plants, no missing terrain | PENDING; automatic 10/10 locate + chunk NBT PASS |
| A04 | Find naturally generated structures and abandoned portals | Natural placement, orientation, no holes or empty templates | PENDING; forced placement 16/16 PASS |
| A05 | Ancient City portal, Rift and return trip, then rejoin | Stable destination/return, no black screen, no duplicate portals | PENDING |
| A06 | Day/night sky with no shaders, then with each shaderpack | Original colorful Sift sky and moving ribbons; no void, clipped triangles, rings, false world overlay | PENDING |
| A07 | Singer, Sonorous beam, ambient effects | Visible beams/sounds/glow and correct particle transparency | PENDING |
| A08 | Ichor bucket, flow, cauldron, waterlogging, HUD air | Real fluid interactions, no bucket/cauldron item duplication, Ichor breath display | PENDING |
| A09 | Ichor healing including cauldron | Regen I (101-tick refresh behavior), no stronger-effect overwrite, stop refresh after leaving | PENDING |
| A10 | Weather including Ichor Snowy Peaks | Rain/ichor snow in intended biomes, Overworld weather unchanged | PENDING |
| A11 | Dark Sniffer Sonic Sniff | One displacement per wave/entity, correct velocity and effects | PENDING |
| A12 | Quit/rejoin after Sonic Sniff and dimension travel | No retained unintended velocity, saved state restored | PENDING |
| A13 | Charoite enchantment vs lapis | +20 required levels (not consumed levels), +1 eligible enchantment level, ordinary lapis unchanged | PENDING |
| A14 | Charoite anvil both slot orders | No illicit combinations; rename-only scenarios allowed | PENDING |
| A15 | Easy Magic with Puzzles Lib, rerolls and enchant table | Correct Charoite costs/bonuses, no double effects, no reroll or item dupe | PENDING |
| A16 | Willow tree/sapling, bonemeal, logs, composting | Growth, strip actions, sign/shelf/chest textures and recipes, no item duplication | PENDING |
| A17 | Singer / Echo Golem / Blub / Sifter / Dark Sniffer | Model/animation/AI/drops/sounds, no inventory loss or stuck entities | PENDING; five server summon/tick tests PASS |
| A18 | Iris/Sodium: Photon then Complementary in the Sift dimension | **Sift sky visible under Photon**, no giant translucent planes, world terrain stays in foreground; shaderpack switching/reload works | PENDING; both shaderpacks menu bootstrap PASS |
| A19 | Local dedicated server, two matching clients | Login, dimension travel, Rift/audio/payload sync, Ichor and Charoite action consistency | PENDING |
| A20 | Save/reopen world, change weather/time, measure performance | Zero hard runtime errors; compare FPS/MSPT/memory, stable cleanup on exit | PENDING; isolated server save/reopen PASS |

**Especially important:** A06 and A18 need **actual screenshots in the Sift dimension**, not merely log statements such as `Using shaderpack`. MC26.1.2 uses forward-depth OpenGL whereas the accepted MC26.2 Photon solution used reversed-Z. The new pipeline and optional Iris mixin compile and load, but final visual correctness **cannot** be deduced without in-world screenshots.

**Report each test:** ID, result PASS/FAIL, selected JAR filename and SHA-256, exact MC/NeoForge/GeckoLib and optional versions, world seed and coordinates, screenshot or relevant `latest.log` excerpt. On the first FAIL, include `crash-reports` and stop changing that disposable world until the cause is clear.

No live profile, game save, shaderpack, CurseForge page, GitHub repository or NeoPack integration was modified by Prime 2.
