# Mielon's The Sift 1.1.2 — separate NeoForge 26.2 backport

**Checkpoint:** 2026-10-13. **Status:** EARLY ALPHA / BUILD FAIL, NOT PLAYABLE, NO JAR READY. User authorized an iterative backport in the style of the original The Sift port. This is a **different local worktree/branch** from the accepted 26.3 release.

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
| F3 — Main-game code migration | Blocks/compost, bonemeal, fluid tick, entities/motion, commands, ServerClock mixins | **PENDING — compile errors** |
| F4 — Client renderer API | 26.3 RenderPearl/OIT → 26.2 Blaze3D/Vulkan, shaders, GeckoLib, UI/cloud/rift/beam/sky | **PENDING — compile errors** |
| F5 — Networking/persistence | Portals/Rift, 1.1.2 Sonic Sniff, Ichor Regen, Charoite/Easy Magic, data reload, save/rejoin | **PENDING** |
| F6 — Runtime | \`clean build\` / ZIP + metadata / isolated dedicated server and client | **NOT PASS** — compiler currently fails |
| F7 — User manual QA | Same gameplay contract as accepted 26.3, mod-only and optional integrations | **PENDING** |
| F8 — Publication | User acceptance, independent source/JAR hashes, release and rollback | **HOLD**, not authorized yet |

## Observed build result — not hidden

First \`gradlew --no-daemon clean build\` against target NeoForge **26.2.0.90** generated the actual 26.2 patched Minecraft source, but failed during Java compilation. A second compilation after worldgen Java API edits also **failed**; compiler output is capped (100 errors per javac pass). Report: \`evidence/phase2-compile-issues.json\`; raw logs: \`logs/phase2-clean-build-262-first.log\` and \`logs/phase2-compile-262-feature-api.log\`.

Examples of real blockers:

- **Missing newer 26.3 classes:** \`Compostable\`, \`BonemealSource\`, \`ResolvableInt\`, \`Prediction\`, \`LinearInterpolationHandler\`, \`ServerClockManager.MoveResult\`.
- **Rendering:** 26.3 \`com.mojang.renderpearl.api.*\` and OIT are not in 26.2; must adapt precisely to Blaze3D 26.2, not suppress/disable effects blindly.
- **Mixin bytecode:** target methods, mutable locals and ordinals require direct comparison to mapped NeoForge 26.2 classes. Do not claim a mixin PASS based on class existence alone.

**User's fixed release rule:** Always use latest published NeoForge for target 26.2 when building/testing. The JAR must not have an upper NeoForge bound, and the minimum remains independent of build/test pin. Current \`.90\` minimum is provisional, not a claim that earlier \`.xx\` builds fail.

## Next concrete work

1. Verify the exact 26.2 patched vanilla methods for these missing classes; adapt **blocks/items + entity/ServerClock first**, preserving the known 1.1.2 behavior.
2. Port the **Blaze3D 26.2 rendering** from original Fabric 26.2 classes and our NeoForge 26.3 source, then rebuild and fix exact compile failures.
3. After a clean compile, validate JSON codecs on a disposable dedicated server and only then start a separate client. Do not use user worlds.

The original 26.3 release, its stage, source/main, live profiles and IntoTheMaelstrom have not been modified.
