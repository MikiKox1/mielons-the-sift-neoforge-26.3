# Manual acceptance plan — The Sift 1.1.2 / NeoForge Minecraft26.2

**Do not use NeoPack or ModTest.** Stage-only alpha build of an independent mod port. Only the user operates the character in-game; Prime 2 does not install or change live launcher profiles. Use a disposable **separate Minecraft 26.2 / NeoForge 26.2.0.90** profile, **Java 25**, **GeckoLib 5.5.6**, this The Sift alpha JAR and optionally Easy Magic26.2.0 + Puzzles Lib26.2.5 in a second test configuration. This is a manually executed acceptance list, **NOT** results claiming PASS.

## Tests to record

| ID | Scenario | Expected | Status |
| --- | --- | --- | --- |
| A01 | Empty mods baseline: NeoForge + The Sift + GeckoLib | Menu loads, no FATAL/ERROR in log | PENDING |
| A02 | New creative world, enter \`the_sift:the_sift\` | Correct dimension, no crash on chunk generation | PENDING |
| A03 | Siftslate cliffs, ores, 10 biome types incl. snowy peaks | Original 26.2 worldgen shapes/placements; no missing features or chunk corruption | PENDING |
| A04 | Abandoned portal and other template structures | Correct orientation, generation, 16 NBT templates, no air-only structures | PENDING |
| A05 | Ancient City portal + Rift and return coordinates | Enter/leave and return to expected location, no recursion/black-screen | PENDING |
| A06 | Skybox/clouds daytime and nighttime | Colorful procedural dome, animating bands, no black void, ring artifacts or large FPS collapse | PENDING |
| A07 | Sonorous beam, Singer glow, particles/sounds | Beam visible at height; color, translucency, sound and glow correct | PENDING |
| A08 | Ichor bucket, flowing fluid, underwater air HUD | Real fluid behavior, air bubbles use Ichor sprite; no incorrect vanilla replacement elsewhere | PENDING |
| A09 | Ichor Regeneration I | Contact refresh incl. cauldrons, exit decays, stronger regen not overwritten | PENDING |
| A10 | Ichor rain and Ichor Snowy Peaks snow | Rain outside snowy peaks, snow inside, textures valid, Overworld weather unchanged | PENDING |
| A11 | Dark Sniffer Sonic Sniff | One hit per entity per wave; velocity safe; multiple entities | PENDING |
| A12 | Save / exit / rejoin after Sonic Sniff | No persistent incorrect movement; entity/world state saved | PENDING |
| A13 | Charoite enchanting vs lapis | +20 requirement (not 20 XP levels consumed), +1 eligible enchant level, normal lapis unaffected | PENDING |
| A14 | Charoite anvil rules | Combinations blocked both slot orders; rename allowed | PENDING |
| A15 | Easy Magic optional: table + rerolls | Charoite properly recognized, no double bonus or duplication, normal Easy Magic reroll/menu persistence | PENDING |
| A16 | Willow sapling, wooden logs and 16 compostables | Growth and bonemeal, axes strip both logs, composter probabilities reasonable, no inventory dupe | PENDING |
| A17 | Sifter + Echo Golem inventory return and Singer events | No items vanish/duplicate, server/client sync, gameplay effects and drops consistent | PENDING |
| A18 | Rendering with compatible optional Iris/Sodium | No mixin crash, shaderpack visuals, Rift glow/cloud/Sonorous beam pass | PENDING |
| A19 | Local multiplayer: same 26.2 version on dedicated server and two clients | Login, dimension travel, audio, item effects, Charoite and network sync | PENDING |
| A20 | Exit/reload, monitor logs and performance | Saves load, reasonable TPS/FPS; no recurring ERROR/FATAL or severe performance regression | PENDING |

**Report format:** test ID, version/build filename and SHA, expected vs observed, world seed/coords, log excerpt or screenshot, relevant installed optional mods. Prefer freshly created worlds and back them up before any changes.

**Known limitation:** This alpha has completed build/ZIP, server bootstrap and client menu tests only. Do **not** treat user acceptance of the earlier 26.3 Port1.1.2 as validation of this separate 26.2 backport. In-world tests and resource-pack visuals are especially important.
