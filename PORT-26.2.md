# Mielon's The Sift 1.1.2 — separate NeoForge 26.2 backport

**Latest checkpoint:** 2026-10-13. **Status:** EARLY ALPHA / CLEAN BUILD + ISOLATED SERVER AND CLIENT MENU BOOT PASS / IN-WORLD QA PENDING / NOT RELEASED. User authorized an iterative backport in the style of the original The Sift port. This is a **different local worktree/branch** from the accepted 26.3 release. Read **\`PHASE4-QA-20261013.md\`** for current test results; earlier compiler errors below are historical.

## Source and scope

- **Branch:** \`port/26.2-upstream-1.1.2-phase1\`
- **Initial base:** \`6bd9e6435ebb69a8e807b04b29bc917cc4e10c70\`, our exact accepted 26.3 Port 1.1.2 source, MIT original attribution to Mielon.
- **Accepted 26.3 JAR:** SHA-256 \`D3BD9A6BF718E755E1DA42190FF726A48D3FE6708245ED879A00883434E27CEE\`.
- **Original source-of-truth for 26.2 data/API:** Mielon Fabric 1.1.2, Modrinth \`FjiiWlMl\`; exact JAR SHA-256 \`1013807C2DEA8F366F97347AB74CC895518799CAF0E9513FF19A660567D33886\`.
- Unchanged pins, resource comparison tables, original 26.2 data and selectively decompiled **61 worldgen classes / 30 Java files**: \`TheSift/backports/26.2-1.1.2-prep/\`.

## Toolchain

| Dimension | Current baseline |
| --- | --- |
| Minecraft | **26.2** |
| Latest NeoForge checked | **26.2.0.90** on 2026-10-13, official Maven |
| Staging minimum | **26.2.0.90**, provisional, NOT independently verified on older versions |
| NeoForge range syntax | \`[26.2.0.90,)\` **without upper bound**; lower bound may be relaxed only after tests |
| Java | 25 |
| Gradle / ModDevGradle | 9.2.1 / 2.0.148 |
| GeckoLib | 5.5.6 (published NeoForge MC26.2) |
| LambDynamicLights | 4.12.4, optional |
| Easy Magic | 26.2.0, optional, with Puzzles Lib 26.2.5 |

Both previously accepted and official original JARs remain unchanged. No world/profile or unrelated modpack was touched.

## Stage checklist

| Stage | Work | Result |
| --- | --- | --- |
| F0 — Input integrity | Two independent 1.1.2 JARs; source commit; 158 worldgen mapping; 16 NBT comparisons | **PASS** SHA, 158/158 resources, 16/16 NBT |
| F1 — NeoForge 26.2 bootstrap | Version pin, Gradle plugin, GeckoLib/LDL target, metadata ranges, separate branch | **PASS** for Gradle \`verifyLatestNeoForge\`; full compile not yet |
| F2A — Datapack/worldgen | Original MC26.2 \`configured_feature\`/\`configured_carver\`, 10 biomes, density/noise, structure placements | **STATIC PASS** 158/158 IDs+hashes |
| F2B — Resources | 78 advancements, 53 loot tables, one recipe, two vanilla advancements, 3 block models, 10 shader sources | **STATIC PASS** 147/147 exact original hashes |
| F2C — Worldgen Java | 15 \`Feature<NoneFeatureConfiguration>\` wrappers, custom \`Feature<SpringConfiguration>\`, 18 queued registry entries | **STATIC PASS**, integrated, compilation not independently isolated |
| F3 — Main-game code migration | Blocks/compost, bonemeal, structures, entities/motion, sign GUI, ServerClock mixins | **COMPILE PASS / static QA PASS; gameplay PENDING** |
| F4 — Client renderer API | 26.3 RenderPearl/OIT → 26.2 Blaze3D, shaders, GeckoLib, UI/cloud/rift/beam/sky | **COMPILE + client menu PASS; visual world QA PENDING** |
| F5 — Networking/persistence | Portals/Rift, 1.1.2 Sonic Sniff, Ichor Regen, Charoite/Easy Magic, data reload, save/rejoin | **SERVER + optional Easy Magic BOOT PASS; gameplay/rejoin PENDING** |
| F6 — Runtime | \`clean build\` / ZIP + metadata / isolated dedicated server and client | **PASS for build/ZIP/bootstrap**, **no gameplay claim** |
| F7 — User manual QA | Same gameplay contract as accepted 26.3, mod-only and optional integrations | **PENDING** |
| F8 — Publication | User acceptance, independent source/JAR hashes, release and rollback | **HOLD**, not authorized yet |

## Historical early build failures — subsequently resolved

First \`gradlew --no-daemon clean build\` against target NeoForge **26.2.0.90** generated the actual 26.2 patched Minecraft source, but failed during Java compilation. A second compilation after worldgen Java API edits also **failed**; compiler output is capped (100 errors per javac pass). Report: \`evidence/phase2-compile-issues.json\`; raw logs: \`logs/phase2-clean-build-262-first.log\` and \`logs/phase2-compile-262-feature-api.log\`.

Examples of real blockers:

- **Missing newer 26.3 classes:** \`Compostable\`, \`BonemealSource\`, \`ResolvableInt\`, \`Prediction\`, \`LinearInterpolationHandler\`, \`ServerClockManager.MoveResult\`.
- **Rendering:** 26.3 \`com.mojang.renderpearl.api.*\` and OIT are not in 26.2; must adapt precisely to Blaze3D 26.2, not suppress/disable effects blindly.
- **Mixin bytecode:** target methods, mutable locals and ordinals require direct comparison to mapped NeoForge 26.2 classes. Do not claim a mixin PASS based on class existence alone.

**Later verified:** After F4 migration and strict registry fixes, **final \`gradlew clean build\` PASS** on NeoForge 26.2.0.90. Disposable server reaches \`Done\` and loads \`the_sift:the_sift\`; separate client reaches a responsive window; each also booted independently **with optional Easy Magic26.2.0 + PuzzlesLib26.2.5** without hard errors. See \`PHASE4-QA-20261013.md\`.

**User's fixed release rule:** Always use latest published NeoForge for target 26.2 when building/testing. The JAR must not have an upper NeoForge bound, and the minimum remains independent of build/test pin. Current \`.90\` minimum is provisional, not a claim that earlier \`.xx\` builds fail.

## Prior next-step plan (completed in F4)

1. Done: verified target 26.2 patched vanilla methods and migrated blocks/items + entity/ServerClock without removing known Port1.1.2 behavior.
2. Done: migrated native Blaze3D 26.2 rendering from pinned original Fabric source, preserving independent shader mesh/portal fixes. Full compile/clean build PASS.
3. Done for bootstrap: validated registry loading on disposable dedicated server and separate client menu. **Pending:** in-world gameplay, multiplayer, worldgen coverage, renderer visuals and compatibility with optional Iris/Sodium.

The original 26.3 release, its stage, source/main, live profiles and IntoTheMaelstrom have not been modified.

## F3a — Source API adaptation (checkpoint on 2026-10-13)

**Completed as a separate, statically verified substage (not full compile):**

- Overgrown Willow Sapling changed to the actual 26.2 \`BonemealableBlock\` 4-/3-argument methods, preserving the 45% bonemeal chance and existing tree placement/recovery logic.
- Echo Golem soul handoff adapted from 26.3 \`Prediction.SERVER_ONLY\` to 26.2 \`Player.drop(item, false)\`, preserving the original delivery advancement and inventory flow.
- Siftite return entity uses 26.2 \`new InterpolationHandler(this, 3)\` with \`getInterpolation()\` and \`setInvulnerable(true)\`. The existing item conservation/delivery queue is kept unchanged.
- \`ServerClockManagerMixin\` uses MC26.2 boolean \`moveToTimeMarker\` return and \`getTotalTicks(clock)\` instead of 26.3 \`MoveResult\`/getInstance API; the time transition logic is unchanged, but exact runtime injection application still needs independent QA.
- Removed the newer 26.3 \`DataComponents.COMPOSTABLE\` API from \`ModBlocks\` / \`ModItems\`. All **16** previously accepted compostable item contributions (30/50/65%) are now in NeoForge 26.2 native \`data/neoforge/data_maps/item/compostables.json\`. This is the official NeoForge data map \`neoforge:compostables\`, source: https://github.com/neoforged/NeoForge/blob/26.2.x/src/main/java/net/neoforged/neoforge/registries/datamaps/builtin/NeoForgeDataMaps.java . Previous \`modifyDefaultComponents\` hooks are preserved as no-ops to avoid destabilizing event bootstrap.
- All six original Mielon MC26.2 affected core Java classes were inspected via pinned original Fabric 1.1.2 JAR, selectively decompiled (read-only) into \`backports/26.2-1.1.2-prep/research/original26.2-core-api-decompiled\`.

**New evidence:** \`tools/tests/verify_phase3_core_api_262.py\` + \`logs/phase3-core-api-262-static.log\` PASS. \`logs/phase3-core-api-262-compile-01.log\` still reports compiler failure (capped 100 per javac pass, 200 visible over two passes), now led by **client RenderPearl/OIT** incompatibilities. No \`error:\` lines for the six F3a adapted Java files appear within visible first 100 errors per pass; this is **not** proof that all Java files compile. Must continue with F3b/F4 and rebuild.

**F3/F4 status update:** all currently observed Java compiler issues were resolved by the following committed stage, and fresh \`clean build\` plus independent server/client bootstrap PASSED. Do not suppress mixin or compilation failures. **Full in-world gameplay, multiplayer and visual shader acceptance remain PENDING**. User guide: \`MANUAL-ACCEPTANCE-26.2-1.1.2.md\`.
