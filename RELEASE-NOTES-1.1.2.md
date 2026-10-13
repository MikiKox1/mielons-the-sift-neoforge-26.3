# Port 1.1.2 — Mielon's The Sift for NeoForge 26.3

**Official community-port release candidate**, prepared 2026-10-13 for review and GitHub publication by Prime 3. Original mod by **Mielon** (MIT); port maintained by **MikiKox1**.

## What's new since Port 1.1.1

- Implemented the original upstream **1.1.2** fixes for **Ichor**: contact-based Regeneration I refresh, including Ichor cauldrons, fluid-bearing blocks, and the effect remaining briefly after leaving Ichor.
- Fixed **Dark Sniffer Sonic Sniff**: UUID-based hit tracking per wave; safe player movement persistence across save/rejoin after Sonic Sniff.
- Updated Sift precipitation rendering injection, preserving **Ichor Snow** in its snowy-peaks biome and Ichor rain elsewhere.
- Preserved our NeoForge-specific fixes for **Charoite/Easy Magic**, Ichor fluids/buckets, portal/Rift return positions, Singer dynamic lighting, Sonorous beams, plants and audio.
- Preserved mod ID \`the_sift\`, worldgen, models, original assets and compatible saved-world names.

## Requirements

- **Minecraft:** Java Edition **26.3**.
- **Loader:** NeoForge **26.3.0.64-beta or newer**. The required NeoForge version range inside the JAR is **\`[26.3.0.64-beta,)\`** with **no upper bound**.
- **Java:** 25.
- **Required:** GeckoLib **5.5.7 or compatible 5.x**.
- **Optional:** Easy Magic 26.3.0 (requires its own Puzzles Lib), LambDynamicLights, and supported Iris/Sodium combinations.
- **Compiled/tested loader:** NeoForge **26.3.0.76**, latest official Minecraft 26.3 release checked on 2026-10-13. No claim of tested operation on every possible future loader.

## Quality assurance and caveat

- Clean build, JAR archive/metadata regression checks and independent dedicated-server smoke tests passed.
- The user explicitly confirmed completion of the gameplay, Charoite, Easy Magic, portals/Ichor/Dark Sniffer and weather checks without objections on 2026-10-13, after correcting a test-profile Sodium mismatch.
- A separate client crash in ModTest was traced specifically to **unpatched Sodium 0.9.3-alpha.1** on NeoForge 26.3.0.76 (\`LevelExtractorMixin\` missing injection target). This is a Sodium/loader mismatch, not evidence of a The Sift crash. Use a compatible renderer stack; The Sift has **no dependency** on a particular custom Sodium build.
- As always, back up worlds before changing mod versions. Independent FPS/MSPT benchmarks, large multiplayer stress tests and blanket future-loader compatibility are not claimed.

## Attribution

**Original creator:** Mielon. **NeoForge port:** MikiKox1. **Public source snapshot:** Arta48. Additional attribution remains in the original project/port credits (including ImpSteve — Dark Sniffer texture; Fuzja Jądrowa — Rift music; StrawberrySushi_ — Korean translation; Echo_Dev — Wellspring remix). This is a community port, not an official Mielon-authored NeoForge 26.3 release.

**License:** MIT, original copyright (c) 2026 Mielon, notice included in source and release JAR.

**Requested release asset name:** \`mielon's-the-sift-1.1.2-mc26.3-neoforge-port.jar\`.
