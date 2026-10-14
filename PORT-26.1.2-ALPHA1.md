# The Sift 1.1.2 → NeoForge MC26.1.2 — Prime 2 ALPHA1 checkpoint

**Date:** 2026-10-14. **Worktree:** `work/neo-26.1.2-1.1.2-port`.
**Branch:** `port/26.1.2-1.1.2-phase1`. **Basis:** accepted MC26.2 commit `aa02718ae239c4439ba8f774dab7a2c6523e02fc`.

**Status:** fully independently compiling ALPHA, extensive static, server, client-menu and optional-mod bootstrap QA performed. **Not final/user-accepted. Not published, pushed or installed into live profiles.** No current Fabric 26.1.2 reference exists; we instead used the accepted MC26.2 port and official Mielon 1.1.2 NeoForge 1.21.1 for behavior comparison. Review the detailed feasibility and file-by-file matrices at `../../reports/research/THE-SIFT-112-MC26.1.2-DETAILED-RESEARCH-20261014.md`.

## Pinned toolchain

- Minecraft `26.1.2`, **NeoForge 26.1.2.117** latest Maven checked at build time, FML 11, **Java 25**, mandatory **GeckoLib 5.5.2**.
- Minecraft dependency: `[26.1.2]`; NeoForge minimum provisional `[26.1.2.117,)` **open at the top**, not conflated with future test updates. Older minimum compatibility not yet tested.
- Internal version `1.1.2-neoforge.1-alpha.1`. This is only a test JAR, not a stable public 1.1.2 release.

## Major backport code changes

- Replaced MC26.2-only vanilla names with MC26.1.2 actual `BlockEntityType`, `EntityType`, `PointedDripstoneBlock`, `Gui`; ported `BlubMoveControl` from generic to compatible superclass.
- Adapted Singer barter wool list to the **same sixteen items** (26.1.2 does not expose MC26.2 `Blocks.WOOL.asList`).
- Added a targeted vanilla `CauldronInteractions.addDefaultInteractions` Mixin invoker rather than approximating Ichor interaction tables.
- Migrated shaders/RenderPipeline from MC26.2 bind-group / PrimitiveTopology interfaces to target `RenderPipeline.Builder.withSampler`, `withVertexFormat`, legacy `RenderPass.drawIndexed` and target `DynamicUniforms.writeTransform`.
- Adapted portal loading GUI to MC26.1.2 `Minecraft.screen`/`Minecraft.setScreen` lifecycle.
- Migrated `SiftWeatherTextureMixin` from fragile variable ordinal to the actual `getPrecipitationAt(Level, BlockPos)` return value.
- For the Photon post-final sky, retained the accepted six-gradient approach, but changed depth masking to **forward-Z** (`clip.z=clip.w`). Actual in-world Photon/Complementary acceptance remains **PENDING**.
- All sky `TextureManager.getTexture` views and sampler are acquired **before** opening the target `RenderPass`, preventing lazy image uploads during the pass.
- Adapted four Willow sign blockstates to the 26.1.2 particle-only sign model convention; subsequent client boot reported **zero missing block model warnings**.
- Retained the 26.2 high-value gameplay mechanics, Ichor Regeneration I, Dark Sniffer Sonic Sniff UUID dedup/persistence, Charoite, portals/Rifts, weather, biome worldgen and GeckoLib animation resources without silent removals.

## Automated evidence matrix

| Gate | Evidence | Result |
| --- | --- | --- |
| Compile / full Gradle build | `gradlew --no-daemon clean build` with latest-NeoForge verification | PASS |
| JAR/metadata/worldgen/texture/mixin/static checks | `tools/tests/verify_backport2612.py` | **286/286 PASS** (before final packaging; rerun for exact JAR SHA) |
| Dedicated server startup and Sift dimension save | isolated MC26.1.2.117 + GeckoLib | PASS; 0 hard errors |
| Original NBT templates | `phase12-all16-structure-placement-2612.json` | **16/16 forced placement PASS**; not a test of *natural* structure placement |
| Biome source and locate | `phase20-locate-biomes-2612.json` | **10/10 biomes located** |
| Biomes in persisted full chunks | `phase22-located-biome-full-chunks-2612.json` | **10/10 target biome palettes**, nine independent columns, `minecraft:full`, `DataVersion4790` |
| Chunk generation/reload | `phase17-natural-worldgen-2612.json`, `phase18-saved-chunk-validation-2612.json`, `phase22-located-biome-chunk-generation-2612.json` | PASS on sampled fresh-world stage chunks and saves |
| Entity spawn/tick queries | `phase19-entity-tick-2612.json` | **5/5 Sift mobs** (Singer, Echo Golem, Dark Sniffer, Blub, Sifter) spawn and respond after server ticks |
| Optional Easy Magic + Puzzles Lib | `phase13-easymagic-smoke-report.json` | Server/mixin/portal/clock/save PASS; in-world enchantment behavior **PENDING** |
| Client no shaders | isolated `latest.log`/menu | PASS; 5 GeckoLib models + animations |
| Iris 1.11.4 + Sodium0.9.2 with Photon v1.3b | isolated client log | Bootstrap PASS, Photon identified and final Iris mixin applied; **visual PENDING** |
| Iris/Sodium with Complementary Unbound r5.9.3 | isolated client log | Bootstrap PASS; sky pipeline `TEXTURED` bound; **visual PENDING** |
| With Easy Magic/Puzzles Lib + LambDynamicLights4.11.2 | `phase16-client-fullstack-ldl-2612.log` | Full optional stack client/bootstrap PASS; gameplay **PENDING** |
| Real dimension portal/Rift travel, Ichor/Charoite effects, A01–A20 manual, performance, multiplayer | `MANUAL-ACCEPTANCE-26.1.2-ALPHA1.md` | **PENDING**, requires user game tests |

**Do not exaggerate claims:** `locate biome` and chunk palettes show that biomes can be generated, not that all natural features/structures have correct distribution; server console `summon` is not a model or AI acceptance test. Shaderpack bootstrap proves initialization, not correct Sift sky composition over world terrain. Build/CRC PASS does not prove multiplayer.

## Safety and reproducibility

- All tests run only inside disposable `run/*port-2612-*` directories. The original accepted releases **MC26.2** SHA-256 `9740DA26B007C909B60716C78C90691409E30DB7BFF3A6FEF9321462C58A4A7D` and **MC26.3 hotfix** `56724D4A0F7E42A8D21D3D85447A945FAAE93A04BFB11D8E485C1FF5914BFD19` are unchanged.
- `tools/tests/*.py` include read-only archive/NBT checks and loopback-only disposable world commands. Local `tools/tests/_qa_vendor` is ignored and does not ship in the source snapshot; RCON credentials are never logged or redistributed. Temporary RCON is disabled and its credential erased after checks.
- No mods from NeoPack, ModTest, any user save, shaderpack ZIP patch, GitHub push, CurseForge update or application install.
- Source license MIT, original author Mielon, community port MikiKox1 and credits for other asset contributors are retained.

## Next gate

Provide the user this isolated ALPHA artifact and `MANUAL-ACCEPTANCE-26.1.2-ALPHA1.md`. Begin with **A02/A06/A18** (dimension entry and shaderless/Photon/Complementary in-world Sift sky), because the forward-Z/Photon final depth behavior is not proven by menus. Then A03–A05 and A08–A15 core mechanics, followed by multiplayer and performance. Correct any observed regression **only in a new isolated checkpoint**, rebuild, reverify the complete manifest and retest affected gates. Do not publish until explicitly authorized after acceptance.
