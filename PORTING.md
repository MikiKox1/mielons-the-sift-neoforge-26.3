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

The working tree additionally retains the two public upstream commits that are newer than the 1.1.1 publication date:

- `f57d2f6` — Russian translation
- `8eb1757` — multiplayer compatibility fix

The multiplayer commit's `ModItems` jukebox change was already present in the 1.1.1 release and was therefore not duplicated; only the missing post-release client compatibility pieces were applied.

## Current validated source baseline

The working branch intentionally layers the public post-release upstream commits on top of the reconstructed 1.1.1 behavior. This keeps the Russian translation and the October 1 multiplayer compatibility fix (`VanillaPaletteRemapper`, `IdMapperMixin`, `SiftServerDetectionMixin`) because the port targets the newest available upstream state rather than the September 30 release in isolation. This combined baseline builds successfully on Fabric and contains 320 class entries.

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

Only the three access transformations corresponding to the published 26.3 Fabric access widener are enabled at this stage. No gameplay/platform-layer source changes are included in the stage 3 commit.

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
