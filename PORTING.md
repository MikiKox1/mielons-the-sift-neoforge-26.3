# The Sift — NeoForge 26.3 Port

This repository is the working tree for a NeoForge-only port of Mielon's The Sift to Minecraft 26.3.

## Upstream provenance

- Upstream repository: `https://github.com/Arta48/Mielons-The-Sift`
- Upstream baseline commit: `8eb175723efa810f45cff882df814fbcc62558dc` (`Fixed Multiplayer`)
- Upstream tag available publicly: `1.0.2`
- License: MIT

The public Git repository does **not** contain the exact source for the published Fabric 1.1.1 release. It has no `1.1.1` branch/tag and the published release does not ship a source JAR.

## Published 1.1.1 reference

- Modrinth version: `JqpmzEMO`
- File: `mielon's-the-sift-1.1.1-mc26.3-fabric.jar`
- CurseForge file: `9022781`
- SHA-256: `ACA3D635F976BF7113A73C72F4B78C4F88C231BF2AC0972BEECB9EB501EFF4B1`
- SHA-512: `87f39a38f9f83ded04d5e4b06962c7a1856892337b8b9522fb150f71b1fa8ccc62824cee1802b9c3a40364bb4c6402bf91718151b6deda60a09cfbb7178be3d6`

The exact release behavior was reconstructed from the official 1.1.1 artifact with Vineflower 1.12.0, using the public source as the authoritative reference for source-language constructs that decompilers cannot recover faithfully (notably Mixin `(Object) this` bridges and generic type witnesses).

Before beginning the loader port, the reconstructed Fabric 1.1.1 source was required to build successfully on the release toolchain recorded in its manifest (Gradle 9.6.0 / Fabric Loom 1.17.21 / Java 25). The rebuilt artifact contains exactly the same 317 class paths and the same 1104 gameplay/resource entries as the published 1.1.1 JAR. Re-decompiling the rebuilt JAR yields the same 216 top-level Java source paths; 196/216 are text-identical to the official-release decompilation and the remaining 20 were reviewed as compiler/decompiler-equivalent source-shape differences. Seven deterministic worldgen helpers were explicitly corrected to preserve the official release's double-precision `2^-53` arithmetic before this comparison.

The working tree additionally retains two public upstream changes that are not present in the published 1.1.1 artifact:

- `f57d2f6` — Russian translation
- `8eb1757` — multiplayer compatibility fix

`f57d2f6` predates the September 30 release chronologically, but its Russian translation is absent from the published 1.1.1 JAR and is therefore retained as an upstream overlay. The October 1 multiplayer commit is retained in full, including its safer delayed jukebox lookup for the Rift music disc.

## Current validated source baseline

The working branch intentionally layers the public upstream changes missing from the release artifact on top of the reconstructed 1.1.1 behavior. This keeps the Russian translation and the October 1 multiplayer compatibility fix (`VanillaPaletteRemapper`, `IdMapperMixin`, `SiftServerDetectionMixin`, plus the Rift jukebox lookup change) because the port targets the newest available upstream state rather than the September 30 release in isolation. This combined baseline builds successfully on Fabric and contains 320 class entries.

The project additionally preserves `LICENSE_the_sift` from upstream.

No Fabric build is intended to be published from this branch. Fabric is only the source-of-truth baseline for Minecraft 26.3 behavior.

## NeoForge target

- Minecraft: `26.3`
- NeoForge: `26.3.0.43-beta`
- Java: `25`
- GeckoLib: `5.5.7`
- Loader target: NeoForge only
- Build system target: NeoForge ModDevGradle

The NeoForge baseline is intentionally pinned while this port is being brought up. It must not be widened or upgraded without a compatibility audit.

### Stage 3 build bootstrap status

- Gradle wrapper: `9.2.1`
- ModDevGradle: `2.0.147`
- NeoForge: exactly `26.3.0.43-beta`
- FML resolved by NeoForge: `12.0.8`
- Java toolchain/runtime used for validation: `25`
- GeckoLib: `geckolib-neoforge-26.3:5.5.7`
- LambDynamicLights API: compile-only `4.13.0+26.3`
- Architectury / Fabric API / Connector / Porting Lib: not present
- `generateModMetadata` and `processResources`: PASS
- `compileClasspath` resolution: PASS
- `compileJava`: intentionally RED at the stage boundary because source migration has not started. The remaining failures are the expected Fabric-loader/API call sites (18 source files) plus NeoForge-facing source adaptations such as `ArmorMaterials.makeDefense` access and vanilla `BlockEntityType` extension. These belong to stage 4 and later, not to build-system bootstrap.

The stage 3 commit contains only the three access transformations corresponding to the published 26.3 Fabric access widener. Stage 4 later adds one narrowly scoped `FireBlock#setFlammable` access transformation because NeoForge 26.3 has no equivalent flammability data map and the release registers explicit willow fire values.

### Stage 4 common/platform status

- The NeoForge entrypoint is native `@Mod` with an injected mod `IEventBus`.
- The eight static registry-owner classes now defer insertion through `ModRegistration` and no longer call `Registry.register` directly. Objects, `ResourceKey`s, `setId(...)` calls and 26.3 constructors remain unchanged wherever possible.
- Registry owners are initialized at their matching `.43-beta` `RegisterEvent`, rather than all at the first event. This preserves dependency order and, in particular, ensures `ModItems` receives the registered `SoundEvent` holder for the Siftite armor material instead of capturing a direct holder before the sound registry event completes.
- `ModRegistration` rejects registrations queued after their target registry event has completed, turning a silent late-registration loss into an immediate startup failure.
- Entity attributes use `EntityAttributeCreationEvent`; spawn placement uses the full `RegisterSpawnPlacementsEvent` overload with `Operation.REPLACE`, matching registration of new entity types.
- Fabric default-item-component callbacks were replaced by `ModifyDefaultComponentsEvent`, and vanilla sign/hanging-sign/shelf block-entity extensions use `BlockEntityTypeAddBlocksEvent`.
- The Fabric creative-tab builder and particle helper were replaced with their equivalent Minecraft 26.3 APIs. The private `ArmorMaterials.makeDefense(...)` helper was reconstructed as the same `3/6/8/3/19` `ArmorType` map.
- Willow stripping is represented through NeoForge's `TRANSFORMABLES` data map at `data/neoforge/data_maps/block/transformables.json`, with the same copied axis, axe damage and strip sound as Fabric's 26.3 `BlockTransformerHelper.registerStripping` implementation.
- Common/server Fabric callbacks are routed through a small `NeoEvents` adapter. The actual NeoForge game-bus listeners are installed during mod construction, while release-style callbacks can be attached later without registering listeners after the game bus starts.
- Fabric's two timing-sensitive callbacks with no exact NeoForge event are preserved with narrow mixins: block-break `AFTER` dispatches after a successful removal immediately before `Block.destroy`, and living `AFTER_DEATH` dispatches immediately before `broadcastEntityEvent`, matching the Fabric 26.3 injection points inspected from the release API implementation.
- `generateModMetadata` and `processResources`: PASS after the Stage 4 resource changes.
- A dedicated Stage 4 compile validation, containing all common/server sources while excluding the two intentionally unported networking classes and replacing only their outbound send surface with a non-executed stub, is `BUILD SUCCESSFUL` against NeoForge `26.3.0.43-beta`.
- Full `compileJava` now fails only in 12 files that belong to later stages: the two Fabric networking classes plus ten client/render compatibility files (including the known GeckoLib 5.5.7 render-state migration). No Stage 4 file appears in the compiler error set.
- No live NeoPack files have been modified or deployed.

### Stage 5 networking / Ichor status

- Both play payloads now use NeoForge `RegisterPayloadHandlersEvent` / `PayloadRegistrar` with protocol version `1`; the existing `CustomPacketPayload.Type` ids and `RegistryFriendlyByteBuf` codecs are unchanged. Both registrations are optional, matching Fabric's advertised-channel behavior instead of turning absence into a NeoForge handshake rejection.
- `RiftLoadingPayload` is registered as play-to-client without a common-side client handler. Its handler is attached only on `Dist.CLIENT` through `RegisterClientPayloadHandlersEvent`, preventing common/dedicated-server code from loading client classes.
- Server-to-client Rift sends preserve the Fabric `canSend` guard through NeoForge's negotiated `player.connection.hasChannel(TYPE)` before `PacketDistributor.sendToPlayer(...)`.
- `SiftIntroReadyPayload` is play-to-server. NeoForge `.43-beta` payload handlers execute on the main thread by default, so the old Fabric `server.execute(...)` hop was intentionally removed. Client sends use the client-only `ClientPacketDistributor.sendToServer(...)` after checking the negotiated channel, which is required for safe optional-channel sends.
- Ichor now owns a registered `the_sift:ichor` `FluidType` in `NeoForgeRegistries.FLUID_TYPES`, and both source/flowing Ichor override `getFluidType()` to return it. This is mandatory on `.43-beta`; the vanilla fallback rejects mod fluids.
- The Ichor `FluidType` keeps NeoForge's water defaults for density/viscosity instead of importing the historical 1.21.1 Neo-only `1500/1500` values. Explicit properties preserve release behavior that Fabric got from `WaterFluid` plus `#minecraft:water`: water-like movement, swimming/drowning, extinguishing, boating, hydration, zero fall-distance carryover, custom fill/empty sounds, and light level 12. Source conversion remains disabled.
- Ichor terrain rendering now uses client-only `RegisterFluidModelsEvent` and the existing `FluidModel.Unbaked` still/flow/water-overlay materials with untinted `-1` color semantics. No legacy `FluidRenderingRegistry` or old `IClientFluidTypeExtensions` texture registration is used.
- The exact `.43-beta` custom-registry ordering was checked: vanilla `FLUID` registration occurs before modded registry events, so loading `ModFluids` during the fluid event safely queues `ICHOR_TYPE` before the later `neoforge:fluid_type` `RegisterEvent`.
- Stage 5 validation (`src/main/java` plus the complete client loading/network surface and Ichor fluid model registration): `BUILD SUCCESSFUL` on Java 25 / NeoForge `26.3.0.43-beta`.
- `generateModMetadata` and `processResources`: PASS after Stage 5.
- Full `compileJava` now has no Stage 5 errors. The remaining compiler failures are confined to eight later client/render files: the Fabric client entrypoints/shader loader compatibility and the GeckoLib 5.5.7 renderer-state migration.
- No live NeoPack files have been modified or deployed.

### Stage 6 common Mixins / gameplay status

- Every common/server mixin target was re-audited against the published Fabric 1.1.1 behavior and the patched Minecraft classes shipped by NeoForge `26.3.0.43-beta`. Existing release mixins were kept when NeoForge has no exact timing/behavior replacement rather than being rewritten merely for loader symmetry.
- NeoForge patches `LiquidBlock#neighborChanged` to use `FluidInteractionRegistry` instead of the vanilla/Fabric `shouldSpreadLiquid` path. A native lava-type interaction now restores Ichor horizontal-contact solidification for later neighbor updates while preserving the Fabric priority rule that real water wins when both water and Ichor touch the same lava block. The original `shouldSpreadLiquid` mixin remains for placement-time contact, because `.43-beta` still uses it from `LiquidBlock#onPlace`.
- Direct Ichor/lava conversions that still require mixins now route their replacement states through NeoForge `EventHooks.fireFluidPlaceBlockEvent(...)`, matching the platform hook used by patched lava interactions while retaining the same default Obsidian/Cobbled Siftslate/Siftslate results.
- Ichor cauldrons are registered through `RegisterCauldronFluidContentEvent`, and the custom `IchorBucketItem` explicitly exposes `Capabilities.Fluid.ITEM`. NeoForge's fallback bucket provider intentionally handles only exact `BucketItem` instances, so the subclass requires this registration for native fluid-transfer interop.
- The obsolete Fabric weather packet redirect was removed. `.43-beta` already broadcasts weather packets with `PlayerList.broadcastAll(packet, dimension)`, so retaining the one-argument redirect would fail mixin application under `defaultRequire = 1`. Per-dimension weather data and Ichor snow precipitation hooks remain in place because NeoForge does not replace those release behaviors.
- Fabric's `ServerLivingEntityEvents.AFTER_DEATH` parity is complete for players as well as ordinary living entities: `ServerPlayer` has its own `die` override, so a dedicated `ServerPlayer.die @TAIL` mixin mirrors Fabric Entity Events and does not double-dispatch through `LivingEntity.die`.
- The Charoite anvil restriction now applies at `AnvilMenu#createResult` TAIL on NeoForge. This preserves the same final empty output / cost `1`, while allowing `.43-beta`'s wrapped `CommonHooks.onAnvilUpdate(...)` / `AnvilUpdateEvent` to execute first instead of being skipped by a cancellable HEAD injection.
- `IchorBlockPlacementMixin` now wraps the actual `BlockItem#placeBlock(updatedPlaceContext, state)` invocation and uses that invocation context. This fixes an upstream 1.1.1 placement bug where scaffolding could inspect the original clicked position rather than the moved placement target, causing Ichor loss or duplication.
- The custom Sonorous/Horn `NoteBlockMixin` intentionally remains release-first: its target methods are valid on `.43-beta`, but its custom eight-sound paths bypass NeoForge `NoteBlockEvent.Change/Play`. Mapping arbitrary external note/instrument mutations onto The Sift's eight custom sounds is not well-defined, so no speculative compatibility behavior was added.
- Two additional inherited Fabric issues were confirmed but deliberately left unchanged during the loader port: a weakly-consistent persistence race in `SiftLandmarkTracker` and the absence of an explicit shutdown flush in `RiftDirectory`. They are upstream gameplay follow-ups, not NeoForge regressions.
- Forced Stage 6 common/server compilation (`--rerun-tasks`) plus `processResources`: `BUILD SUCCESSFUL` on Java 25 / NeoForge `26.3.0.43-beta`.
- Full `compileJava` still fails only in the same eight Stage 7 client/render files, with zero errors in Stage 6 files.
- No live NeoPack files have been modified or deployed.

## Porting rules

1. Preserve Minecraft 26.3 gameplay/data/render behavior from the reconstructed 1.1.1 source.
2. Use the historical NeoForge 1.21.1 build only as a loader-integration reference, never as the source for 26.3 vanilla-facing code.
3. Prefer current NeoForge 26.3 APIs verified against `26.3.0.43-beta` over old examples.
4. Change one subsystem at a time and compile/test before moving to the next.
5. Do not deploy into the live NeoPack profile until the staged NeoForge build passes static and runtime bootstrap validation.
6. Keep Fabric API, Architectury, Sinytra Connector, Forgified Fabric API and Porting Lib out of the final NeoForge-only runtime unless a later, evidence-backed blocker makes one necessary.

## Planned migration order

1. NeoForge ModDevGradle metadata/build + access transformers.
2. Registry lifecycle.
3. `@Mod` entrypoint, lifecycle/events, attributes and spawn placement.
4. Networking.
5. Ichor/FluidType and client fluid model integration.
6. Common Mixins and gameplay integration.
7. Client registration, rendering, Sodium/Iris compatibility and optional dynamic lights.
8. Staged runtime validation, then NeoPack integration.
