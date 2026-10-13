# The Sift — isolated upstream 1.1.2 NeoForge 26.3 stage

**CURRENT TARGET / LATEST-LOADER UPDATE (2026-10-13):** `neo_version=26.3.0.76` (compile/test), `neo_min_version=26.3.0.64-beta` (advertised minimum). JAR's actual `neoforge.mods.toml` contains **`versionRange="[26.3.0.64-beta,)"`**, OPEN above. The latest-loader gate in `build.gradle` checks published Maven `26.3.x` before builds and runtime dev tests; it **does not** force the minimum to equal the latest. Java 25, GeckoLib 5.5.7.

Latest **clean build/static/ZIP QA and dedicated server bootstrap PASS on NeoForge 26.3.0.76, FML 12.0.12**; runtime world `run/server-26.3.0.76/Sift112_NeoForge76_Disposable/` loaded with `the_sift:the_sift`; no ERROR/FATAL. After an earlier .76 boot the world was reloaded on the corrected `[.64-beta,)` JAR, reaching `Done (0.286s)!`. Client-menu smoke performed previously on .64-beta is **historical, not .76 client QA**. User gameplay/manual multiplayer still PENDING; no live install/publish/backports. Current stage artifact under `TheSift/artifacts/builds/TheSift-1.1.2-MC26.3-NeoForge-26.3.0.76-STAGEONLY.jar` SHA-256 `9742B48A30E44C7D6CB1317FD980561191E4C033CF00BD7C69FD7F4C9940931D`. Older results, paths and hashes below are historic; not the active candidate.

Test receipt and instructions: `TheSift/reports/qa/TheSift-112-NeoForge76-OPEN-RANGE-SERVER-20261013.md` and `TheSift/reports/qa/MANUAL-TESTS-112-NEOFORGE-263-LATEST-20261013.md`. Old .64-specific temporary run folders and old stage-only JAR were removed; their logs and receipt were archived in `TheSift/reports/qa/archive/obsolete-loader64-stage/`. The original public 1.1.1 release and `source/main` stay unchanged.

**Status (2026-10-13):** Source integration COMPLETE, static/build/client-menu/server-start smoke PASS; manual gameplay QA and user acceptance PENDING. This is **not** an accepted/public release. It must not be deployed to NeoPack, Steam, Modrinth, CurseForge or GitHub without separate approval.

**Base:** clean `main` at commit `0516b95a26a725e41502b5ce4ab7a84133b4fe4e`. Independent Git worktree on branch `port/26.3-upstream-1.1.2` (local only, no push). Original published 1.1.1 release and separate `source/` checkout remain unchanged.

**Target:** Minecraft 26.3; NeoForge 26.3.0.64-beta; Java 25; Gradle wrapper 9.2.1; GeckoLib 5.5.7. New mod version: `1.1.2-neoforge.1-stage.1`.

## Upstream changes implemented

- Contact-driven Ichor regeneration via `IchorRegeneration` and `LivingEntity.baseTick` mixin; includes waterlogged Ichor and Ichor cauldrons; retains existing NeoForge FluidType, bucket, flow, and motion slowdown.
- Dark Sniffer Sonic Sniff victim set keyed by UUID; single pull per sonic wave; player momentum tagging and explicit persisted `TheSiftSonicSniffMotion` reset on reload, without resetting ordinary player velocity.
- Weather precipitation mixin from upstream 1.1.2 uses `@ModifyVariable STORE` with `MixinExtras @Local MutableBlockPos`. The patched 26.3.0.64-beta client source confirms the target local and assignment in `WeatherEffectRenderer.extractRenderState`.
- Two required mixin entries added to the existing config, which retains all original entries and order; provenance and `the_sift` ID retained.
- Not copied: upstream's redundant nested `assets/the_sift/textures/textures.zip` (103 of 103 are byte-identical duplicates), Fabric metadata/classes or optional library JARs.
- Client/server dev runs are redirected exclusively to `run/stage-client` and `run/stage-server`, both marked `.prime2-disposable`; loopback server `127.0.0.1:25673`.

## Actual QA gates

| Gate | Result | Evidence / limitation |
| --- | --- | --- |
| Java 25 `gradlew --offline --no-daemon clean build` | **PASS** | `logs/the-sift-112-neoforge-263-clean-build-01.log`; `test NO-SOURCE`, so compile is not functional testing |
| Static package, ZIP CRC and source invariants | **PASS** | `tools/tests/upstream_112_stage_test.py` and `logs/the-sift-112-static-regression-01.log`; new 4 class entries, modified 5 classes, no deleted entries, no changed gameplay JSON/NBT/textures |
| Dedicated server bootstrap | **PASS** | `run/stage-server/logs/latest.log`; reached `Done (2.080s)!`, `The Sift initialized`, saved disposable overworld + `the_sift:the_sift`; no hard error/fatal; server subsequently terminated via tool Ctrl+C (not a gameplay/reload PASS) |
| Client title-screen/bootstrap | **PASS** | `logs/the-sift-112-neoforge-263-client-smoke-01.log`; isolated client opened, resources/atlases/OpenAL loaded, GeckoLib 5 models + 5 animations; mixin logs show the three new/changed mixins applied; clean window close, `Stopping!`, `BUILD SUCCESSFUL in 1m 39s` |
| Mixins baseline | **PASS for class-apply during bootstrap** | `IchorRegenerationMixin`, `SonicSniffMotionMixin`, `SiftWeatherTextureMixin` each logged as mixing; no `MixinApplyError` or `InjectionError` during boot. Execution during weather/gameplay not measured |
| Ichor regen + cauldrons, potions, mob performance | **PENDING gameplay** | Needs disposable in-world tests incl. crossing fluids, 3 cauldron levels, strengthen/potion precedence and save/reload |
| Dark Sniffer attack + player save/rejoin | **PENDING gameplay** | Needs sonic-wave UUID uniqueness, motion marker persistence and multiplayer tests |
| Weather Snowy Peaks and other biomes, shaders | **PENDING gameplay** | Mixin loads, but title-menu smoke does not call live precipitation extraction |
| Easy Magic, LambDynamicLights, Iris, Sodium optional integrations | **PENDING on 1.1.2** | Base work retained, optional mods absent in bare smoke, so their class-not-found warnings were expected |
| In-world portal/Rift, worldgen, 13 NBT templates, dimensions, multiplayer | **PENDING regression** | No player world entered |
| FPS/MSPT/performance comparison | **PENDING** | No measurements |
| Publication / live install | **HOLD** | Requires separate user authorization |

The bare-client/server run reported the inherited `logoFile` FML deprecation warning (not fatal). Optional Easy Magic and Iris/Sodium classes were absent in the disposable runtime and caused expected optional `@Pseudo` warnings, not errors.

## Deliverables and rollbacks

Copies under `TheSift/artifacts/builds/`:

- `TheSift-1.1.2-MC26.3-NeoForge-STAGEONLY.jar` — 14 047 568 B, SHA-256 `1A1D4F8021C3264BEA064654D37943209804167D31E8AF22926A68977DF2FFD0`.
- `TheSift-1.1.2-MC26.3-NeoForge-STAGEONLY-sources.jar` — 13 604 601 B, SHA-256 `D7BBE5C3E910705EF297411795E67C56FE0B7FB066324ACCB3E8810FDB2366AE`.
- `TheSift-112-MC26.3-STAGEONLY-MANIFEST-20261013.json` — release gate ledger with hashes and PENDING tests.

Original official Fabric 1.1.1/1.1.2 binaries remain unchanged in `TheSift/reference/upstream-modrinth/`, and original 1.1.1 NeoForge GitHub JAR remains in `TheSift/releases/`. Full comparison: `TheSift/reports/research/UPSTREAM-1.1.2-VS-1.1.1-NEOFORGE-26.3-PREP-20261013.md`.

**Next authorized step** should be user-driven in-world acceptance of the 1.1.2 isolated stage. Do NOT immediately create 26.2 or 26.1.2 backports merely because compilation and menu boot passed.
