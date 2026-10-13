# The Sift 1.1.2 — F0/F1/F2 QA checkpoint, MC 26.2

> **HISTORICAL EARLY PHASE QA.** Initial compiler FAIL results below have been superseded by the later **\`PHASE4-QA-20261013.md\`** full clean build, isolated server/client and optional Easy Magic bootstrap PASS. Keep this file as exact early-phase evidence rather than silently rewriting test history. In-world gameplay remains PENDING.

**Date:** 2026-10-13. **Branch:** \`port/26.2-upstream-1.1.2-phase1\`.
**Phase state:** static migration prepared, **NOT COMPILE PASS**, **NOT RUNTIME PASS**, **NOT A JAR**.

| Gate | Result | Evidence |
| --- | --- | --- |
| Input SHA-256 / ZIP CRC | PASS | \`backports/26.2-1.1.2-prep/research/BINARY-AUDIT-112.json\`, \`tools/tests/verify_backport262_phase1.py\` |
| Original dimension worldgen | PASS static | 158 JSONs with original Fabric26.2 values, \`evidence/phase2-fabric-worldgen-262.json\` |
| Original structures NBT | PASS static | 16 byte-identical to both 26.2 Fabric and our 26.3 NeoForge JAR; no rewrite |
| Version-sensitive pack assets | PASS static | 147 files, \`evidence/phase2-pack-262.json\`; extra NeoForge tags retained |
| Worldgen feature API signatures | PASS source/static only | 15 normal features, one spring, 18 registrations, \`evidence/phase2-feature-api-262.json\` |
| NeoForge 26.2.0.90 latest verification | PASS | \`logs/phase1-verify-neoforge-262.log\`, official Maven |
| Java25 Gradle compile | **FAIL (known blockers)** | \`logs/phase2-compile-262-feature-api.log\`, truncated error list; exact \`evidence/phase2-compile-issues.json\` |
| Dedicated server, client | PENDING | Must first achieve clean build |
| Worldgen function in-game, Charoite, Ichor, portals, Dark Sniffer | PENDING | Static registry presence is not gameplay validation |
| Save/rejoin, multiplayer, optional integrations, FPS/MSPT | PENDING | No runtime claims borrowed from 26.3 |

**Important:** one scenario differs in balance between our 26.3 resources and original 26.2: \`data/the_sift/recipe/sifter_meat_smoking.json\` was 200 ticks in 26.3 and is 100 ticks in original Fabric 1.1.2 for 26.2. Restored original target-specific behavior intentionally; requires manual conservation/balance review later.

**Independence:** all changes confined to \`TheSift/work/neo-26.2-upstream-1.1.2\` and project \`backports/26.2-1.1.2-prep\`, no publication, user saves or mods profile touched.

## F3a checkpoint — Core API, source-only PASS

- Relevant classes matched to original **Fabric 1.1.2 MC26.2** and patched target Minecraft/NeoForge sources (no untested version assumptions).
- Bonemeal/Willow, Echo Golem soul drop, Siftite return interpolation and ServerClock mixin adapted without changing intended gameplay.
- Official 26.2 NeoForge compostable data map carries **16 items** and original probabilities.
- Static QA \`tools/tests/verify_phase3_core_api_262.py\`: **PASS**, no source warnings.
- \`compileJava\` in \`logs/phase3-core-api-262-compile-01.log\`: **FAIL**, primary blockers in client renderer \`com.mojang.renderpearl.api.*\`, absent in MC26.2. Unknown errors may remain past javac cap.
- **No executable JAR, server/client/bootstrap or gameplay PASS.** Further rendering migration required.
