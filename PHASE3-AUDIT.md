# The Sift 1.1.2 → MC26.2 — F3a independent core API audit

> **HISTORICAL F3a EVIDENCE.** Earlier Java compile FAIL was later resolved as part of F4. For current clean-build, server and client gate results, see **\`PHASE4-QA-20261013.md\`**. Do not treat the old compiler log as current status.

**Date:** 2026-10-13
**Status:** SOURCE/STATIC PASS; full Java compile remains **FAIL**.

## Evidence-first source mapping

| Layer | Concrete 26.3 → 26.2 API | Grounding | Result |
| --- | --- | --- | --- |
| Willow bonemeal | Removed \`BonemealSource\` argument from three methods | \`BonemealableBlock.java\` inside actual NeoForge 26.2.0.90 patched Minecraft source JAR + official Fabric 1.1.2 | STATIC PASS |
| Echo Golem | \`Player.drop(soul, false, Prediction.SERVER_ONLY)\` → \`drop(soul, false)\` | Official Fabric 1.1.2 source bytecode decomp | STATIC PASS |
| Siftite return entity | \`LinearInterpolationHandler.create\` → owned \`InterpolationHandler\`, \`getInterpolation\` override; \`setInvulnerable\` | Patched MC26.2 \`Entity.java\` + official Fabric source | STATIC PASS |
| World clock mixin | Boolean \`moveToTimeMarker\` and \`getTotalTicks(clock)\` | Patched MC26.2 \`ServerClockManager.java\` + original Fabric source | STATIC PASS; Mixin injection not runtime verified |
| Compostable items | Removed unsupported 26.3 component API, added 16 entries in \`neoforge:compostables\` data map | NeoForge 26.2 \`ComposterBlock.java\`, \`NeoForgeDataMaps.java\` and official docs | STATIC PASS; datapack load not runtime verified |

**Original reference classes:** \`TheSift/backports/26.2-1.1.2-prep/reference/selected-original-26.2-core-api-classes.jar\` (6 class files). Decompilation/inspection only: \`backports/26.2-1.1.2-prep/research/original26.2-core-api-decompiled/\` and matching log. Original JAR and accepted 26.3 source unchanged.

**QA script:** \`tools/tests/verify_phase3_core_api_262.py\` PASS. Full source errors:
\`logs/phase3-core-api-262-compile-01.log\`. Only the first 100 javac errors are reported per pass, so absence of a particular Java file from the log is not a full-compile guarantee.

**Blocker for next phase:** Blaze3D/Vulkan 26.2 render backport. The 26.3 RenderPearl/OIT APIs no longer exist in this target. Rebuild with the full class set once F4 is migrated. Avoid copying unrelated mod JARs and never use NeoPack shims or profile data.

**Acceptance boundary:** A successful Java compile, once achieved, still requires JAR ZIP/metadata, standalone dedicated-server load, client render, worldgen 158/158 and user in-world QA of Ichor, weather, portal/Rift, Dark Sniffer Sonic Sniff, Charoite and Easy Magic.
