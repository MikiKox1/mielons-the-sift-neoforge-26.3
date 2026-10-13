# Mielon's The Sift

> **Unofficial NeoForge port of Mielon's The Sift for newer Minecraft versions.** Separate editions are maintained for Minecraft 26.3 and 26.2; Minecraft 26.1.2 is planned. Original mod and creative work: **Mielon**. NeoForge port maintained by **MikiKox1**, based on a [public source snapshot hosted by Arta48](https://github.com/Arta48/Mielons-The-Sift).
>
> The original MIT license and copyright notice are preserved in [LICENSE](LICENSE). Additional project records: [source and credits](SOURCE_PROVENANCE.md) and [development disclosure](AI_DISCLOSURE.md).

## Minecraft version support

**Port 1.1.2 is available in separate builds for Minecraft 26.3 and 26.2.** Minecraft 26.1.2 has not been released.

| Minecraft | Status | Source branch | Download |
| --- | --- | --- | --- |
| **26.3** | **Released — Port 1.1.2 26.3** | [`minecraft/26.3`](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/tree/minecraft/26.3) | [Port 1.1.2 26.3](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/releases/tag/v1.1.2-mc26.3-neoforge) |
| **26.2** | **Released — Port 1.1.2 26.2** | [`minecraft/26.2`](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/tree/minecraft/26.2) | [Port 1.1.2 26.2](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/releases/tag/v1.1.2-mc26.2-neoforge) |
| 26.1.2 | Planned — not available yet | `minecraft/26.1.2` (to be created when the port exists) | Not available |

**Each Minecraft version has a separate source branch and release artifacts. Do not use a JAR built for one Minecraft version on another.** See [version support and branch policy](VERSIONS.md).

## NeoForge port — Minecraft 26.3

- **Current 26.3 release:** **Port 1.1.2 26.3** — [GitHub release](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/releases/tag/v1.1.2-mc26.3-neoforge), [changelog](docs/releases/port-1.1.2-mc26.3.md), [previous Port 1.1.1](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/releases/tag/v1.1.1-neoforge.2-community.3-fix.1).
- **Minecraft:** 26.3; **NeoForge:** 26.3.0.64-beta or newer (tested on .76); **Java:** 25. No upper loader bound is declared; future compatibility is not guaranteed without testing.
- **Required:** GeckoLib 5.5.7 or a compatible 5.x version. No Fabric Loader or Fabric API is required.
- **Optional integration:** [Easy Magic 26.3.0](EASY_MAGIC_COMPAT.md) (with its own required Puzzles Lib dependency). Charoite works with Easy Magic's enchanting menu, its +20 level surcharge, improved enchantments, advancements and anvil restrictions. Without Easy Magic, the standard enchantment table remains supported.
- **Optional visuals:** LambDynamicLights and compatible Iris/Sodium integrations. Following the final sky and RenderPass fixes, Photon, Complementary and shaderless rendering were confirmed in-world by the user. See [combined 26.3 release notes](RELEASE-NOTES-1.1.2.md); not every shader/renderer combination is guaranteed.
- Existing worlds retain the original `the_sift` mod ID; back up worlds before changing from another build of the mod.

The NeoForge port includes fixes to Ichor fluid behavior, Ancient City / Rift return routes, Charoite enchanting, Singer lighting, Sift plant water removal, Sonorous beam visibility and Echo Golem audio. See [PORT_STATUS.md](PORT_STATUS.md) for the exact validation scope. The gameplay descriptions below are adapted from the original project.

**Versioning:** Public titles include their Minecraft target, e.g. `Port 1.1.2 26.3` and `Port 1.1.2 26.2`. Branches, tags and assets remain separate for each Minecraft version. Later changelogs describe only changes since the preceding public release within the same Minecraft version line. This is an unofficial port, not an original Mielon release.

**Source build:** Requires JDK 25; run `./gradlew build` (or `gradlew.bat build` on Windows).

## NeoForge port — Minecraft 26.2

- **Current 26.2 release:** **Port 1.1.2 26.2** — [GitHub release](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/releases/tag/v1.1.2-mc26.2-neoforge), [full changelog](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/blob/minecraft/26.2/docs/releases/port-1.1.2-mc26.2.md), [separate 26.2 source branch](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/tree/minecraft/26.2).
- **Minecraft:** 26.2; **NeoForge:** 26.2.0.90 or newer; **Java:** 25. This is a separately compiled port, not a renamed 26.3 JAR.
- **Required:** GeckoLib 5.5.6 or compatible 5.x. The original `the_sift` mod ID, world data and progression remain supported.
- **Optional integration:** Easy Magic 26.2.0 (requiring its own Puzzles Lib), LambDynamicLights and compatible Iris/Sodium and shaderpacks.
- **Graphics:** The six-layer Sift sky was adapted to 26.2 Blaze3D. Photon, Complementary and shaderless sky rendering were accepted in the user's in-world tests; shaderpack ZIPs do not need modification.
- **Compatibility:** Ichor regeneration, Sonic Sniff, weather, Ancient City portals/Rifts, willow growth and Charoite behavior are covered by the 26.2 release. Always use the version-specific JAR and back up existing worlds before upgrading.

## How to open the Ancient City Portal?

Portal activation requires **Note Blocks placed on Sonorous Deepslate in Note mode**.

Each of these Note Blocks has **8 different Sift notes** instead of the normal Note Block sounds.

1.  **Right-click a Note Block** to change its note.
2.  The notes cycle from **1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 → 1**.
3.  Set the Note Blocks to the required notes.
4.  **Left-click the Note Blocks** to play them in this exact order:

**1 → 3 → 7 → 6 → 5 → 2 → 4 → 8**

The physical placement of the notes does not need to match this order. Only the **order in which the notes are played** matters.

After the correct sequence is played, the Sonorous Deepslate will automatically replay the melody, the beams will activate, and the nearby Sift Portal will begin opening.

> Right-clicking changes a Note Block's note. Left-clicking plays it.

**The Rift has opened!**

Track down the forgotten portal hidden in an Ancient City, awaken it with sound, and enter **The Sift** - a new dimension filled with siftslate, living sculk, Ichor, overgrown forests, strange creatures and unstable Rifts. 

Inspired by Minecraft Dungeons II, The Sift is **not** an exact replica. The dimension reflects Mielon's interpretation of the environments shown in the trailers.

## The Sift

* Added an entirely new dimension with **10 biomes**, unique terrain generation, its own animated sky and completely independent weather.
* Features **Soul Canyons, Sniffer Caves, Ichor Lakes and springs, monoliths, cliff arches, sculk formations and abandoned portals**.
* The Sift uses **Siftslate** as its main stone.
* Coordinates match the Overworld at a **1:1 scale**.
* Beds work normally.
* Night shifts the dimension toward deep blue tones without becoming as dark as an Overworld night.
* Rain uses **Ichor-colored particles**, while Ichor-Snowy Peaks can accumulate up to 2 layers of **Ichor Snow**.

## Opening the Ancient City Portal

Ancient City centers now contain **8 Sonorous Deepslate blocks** in front of the Reinforced Deepslate frame.

1. Play a **Goat Horn** in the center of an Ancient City.
2. A **Singer** appears and awakens the Sonorous Deepslate by singing his song.
3. Place Note Blocks on the awakened blocks.
4. Play the Singer song on the Note Blocks.
5. The Reinforced Deepslate frame opens into The Sift.

## New Mobs

### Singer

A mysterious creature connected to the Ancient City portal and Soul Blocks. Singers can receive Soul Blocks collected by Echo Golems and react happily when given one.

### Echo Golem

A rare solitary creature that searches **Soul Canyons** for Soul Blocks and carries them back to the surface.

An **Echo Shard** permanently bonds an Echo Golem to a player, causing it to deliver Soul Blocks to that player instead of a Singer.

### Blub

A small rabbit-like neutral mob commonly found in overgrown biomes.

* Attracted to players holding Soul Blocks
* Has a **1 in 3 chance** of being tamed using one
* Tamed Blubs can follow, sit and defend their owner
* Their ears droop farther backwards as they lose health

### Sifter

A fast predator found throughout The Sift, especially in Overgrown Forests. Sifters can spawn alone or in groups of up to 5 and drop **Raw Sifter Meat**.

### Dark Sniffer

Sniffers digging into Sculk can become infected and transform into a Dark Sniffer after **30 seconds**.

Dark Sniffer texture was created by ImpSteve.

Dark Sniffers are hostile, can dig up **Sculkflower Seeds**, and use **Sonic Sniff** to pull distant targets toward them.

Wardens do not attack Dark Sniffers, and when they are completely calm, they can occasionally approach and pet one.

## Ichor

Added **Ichor**, a restorative fluid found throughout The Sift.

* Grants **Regeneration I** to living entities touching it
* Slightly slows movement
* Does not create infinite sources
* Can be collected with Buckets and Glass Bottles
* Ichor Bottles grant Regeneration I for 3 seconds
* Can be poured in cauldrons
* Pointed Dripstone can slowly fill cauldrons with Ichor
* Added Ichor Snow, Ichor Snow Blocks and Ichor Snowballs

## Rifts

Added temporary **Sift Rifts** connecting The Sift and the Overworld.

* Remain open for **5 minutes**
* Can transport players, mobs, vehicles, projectiles and items
* Automatically create or reuse a matching exit
* Natural Rifts can begin appearing after spending several minutes inside The Sift

## Siftite

Added **Siftite**, a new endgame material used to upgrade Netherite equipment.

Includes:

* Sword
* Pickaxe
* Axe
* Shovel
* Hoe
* Spear
* Full armor set

Siftite tools are **20% faster than Netherite**, have increased durability and slightly higher base damage. Siftite equipment is fire resistant, while the armor retains Netherite-level defense and toughness with increased knockback resistance.

Siftite equipment is upgraded in a Smithing Table using **Netherite equipment + Siftite Ingot + Siftite Upgrade Smithing Template**.

### Mini Rift Recovery

Siftite equipment is protected from normal inventory loss on death.

After respawning, a **Mini Rift** appears beside the player and returns their lost Siftite items. If the inventory is full, remaining items orbit the player until enough space becomes available.

## Charoite

Added **Charoite**, a new enchanting material found in The Sift.

Charoite can replace Lapis Lazuli in an Enchanting Table:

* Adds **20 levels** to the required XP
* Increases every rolled enchantment that can normally exceed level I by **1 level**
* The increased level is visible in the enchantment preview

Items enchanted using Charoite cannot later be combined with another item in an Anvil, although they can still be renamed.

## New Blocks and Plants

Added a complete **Overgrown Willow** wood family, including:

* Logs and stripped variants
* Planks
* Stairs and slabs
* Fences and gates
* Doors and trapdoors
* Pressure plates and buttons
* Shelves
* Signs and hanging signs
* Boats and chest boats
* Foliage, saplings and vines

Also added many new Sift plants and sculk variants, including:

* Healthy Sculk
* Healthy Sculk Sprouts
* Dry Healthy Sculk
* Dry Healthy Sculk Sprouts
* Dry Healthy Sculk Growth
* Siftslate Growth
* Overgrown Chard
* Overgrown Fronds
* Overgrown Stalks
* Overgrown Lotus
* Overgrown Hanging Roots
* Siftslate Hanging Roots
* Siftslate Stalks
* Sunburst Plant
* Whisperbloom
* Sculkflower

Several plants can also be crafted into dyes.

## Ores

Added Siftslate variants of:

* Coal Ore
* Diamond Ore
* Emerald Ore
* Charoite Ore
* Siftite Ore

## Abandoned Portals

Abandoned portal structures can generate throughout many Sift biomes.

Their loot may contain Sift vegetation and building materials, Charoite, Emeralds, Diamonds and rare Siftite Nuggets.

They also have:

* **50% chance** to contain the **“Rift” Music Disc**
* **40% chance** to contain a **Siftite Upgrade Smithing Template**

## Music and Sound

* Added the **“Rift” Music Disc by Fuzja Jądrowa**
* Added **8 unique Sonorous Deepslate notes**
* Added new sounds for Sift Portal and Rifts, and Singer

## Advancements

Added **20 advancements**, including a story path beginning in the Overworld and a dedicated **The Sift advancement tab** covering exploration, mobs, Siftite, Charoite, Rifts, Ichor and other new mechanics.

## Requirements

| Minecraft | NeoForge minimum | Required GeckoLib | Java |
| --- | --- | --- | --- |
| **26.3** | 26.3.0.64-beta | 5.5.7 or compatible 5.x | 25 |
| **26.2** | 26.2.0.90 | 5.5.6 or compatible 5.x | 25 |

The mod supports client and dedicated server. **Easy Magic** (26.3.0 for MC26.3; 26.2.0 for MC26.2), **LambDynamicLights**, and compatible **Iris/Sodium** combinations are optional. Easy Magic requires Puzzles Lib separately. The two Minecraft-specific JARs are not interchangeable.

