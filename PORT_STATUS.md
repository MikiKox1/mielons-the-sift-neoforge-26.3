# NeoForge 26.3 community port: scope and validation

**Current public release:** **[Port 1.1.2 26.3](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/releases/tag/v1.1.2-mc26.3-neoforge)**. The final sky/RenderPass fix was confirmed working in-world by the user with Photon, Complementary and shaders off. See [CHANGELOG.md](CHANGELOG.md) and the single [RELEASE-NOTES-1.1.2.md](RELEASE-NOTES-1.1.2.md), which now includes the prior sky compatibility and hotfix reports. Older sections below retain historical test context.

## Port 1.1.2 — published 26.3 update (2026-10-14)

The original Fabric 1.1.2 Ichor-regeneration, Dark Sniffer Sonic Sniff and precipitation behavior was adapted to the independent NeoForge 26.3 port without replacing previously tested mod-specific fixes. The published binary uses internal mod version `1.1.2-neoforge.3`, native NeoForge metadata and `the_sift` IDs. The shader sky and RenderPass fixes are included. **Tested loader at release preparation:** NeoForge 26.3.0.76; metadata minimum remains `[26.3.0.64-beta,)` with no upper loader bound. GeckoLib 5.5.7 and Java 25 are required.

Source/build/static checks, dedicated-server boot and isolated client-menu tests are supported by Prime 2 QA records and passing GitHub Actions. The user also confirmed gameplay, Charoite/Easy Magic and, **after the final RenderPass texture-preload fix**, in-world Photon/Complementary/shaders-disabled sky operation. A separate Sodium `LevelExtractorMixin` issue on an incompatible renderer stack is not a The Sift dependency or an outstanding defect in this release.

**Publication status:** Published on GitHub by Prime 3, with binary SHA-256 `56724D4A0F7E42A8D21D3D85447A945FAAE93A04BFB11D8E485C1FF5914BFD19`. The previous public Port 1.1.1 release remains available for rollback. This documentation update did not modify any released JAR or active modpack. No independent FPS/MSPT benchmarking, exhaustive multiplayer stress test or formal proof of all future NeoForge/shaderpack compatibility is claimed.

## NeoForge loader compatibility fix (`1.1.1-neoforge.2-community.3-fix.1`)

**Fixed:** The published `community.3` loader metadata specified `neoforge@[26.3.0.64-beta]`, which matches only the exact .64-beta build. In an isolated NeoPack upgrade to NeoForge `26.3.0.69-beta`, this caused an actual FML dependency rejection before mod initialization. The published loader declaration now uses `neoforge@[26.3.0.64-beta,)`, with an inclusive minimum and no upper loader bound. The required Minecraft version remains restricted to exactly `26.3`.

**Compatibility evidence:** The original mod was gameplay-tested on `26.3.0.64-beta`. A separate `.69-beta` descriptor variant passed the FML dependency gate in isolated NeoPack boot tests, and successful operation on the newer NeoForge version was subsequently reported by the user during pack updating. The compilation toolchain remains pinned to NeoForge .64-beta to preserve the lower bound. This metadata permission is not a blanket gameplay guarantee for untested future loader builds.

**Release scope:** Loader minimum, mod version, documentation and a CI regression test only; no intentional changes to compiled gameplay classes, registry data, assets or optional Easy Magic/LambDynamicLights behavior. The previous stable GitHub release remains available for rollback.

## Stable release — Singer dynamic lighting (`1.1.1-neoforge.2-community.3`)

**Preview 2 regression:** Singer's dynamic light disappeared during gameplay. In that release, `SingerRenderer.shouldRender` only registered the light when the entity was culled, leaving the visible case entirely dependent on the `head_light` GeckoLib locator callback. In GeckoLib 5.5.7, locator callbacks run during deferred geometry rendering and may not be delivered when the locator is unavailable. They cannot serve as a guaranteed source-registration point. A regression test against the compiled preview-2 JAR reproduced a visible return path without calling the light bridge.

**Fixed:** Restored unconditional light registration in `shouldRender` before the visibility decision and set a stable light position at the model's resting head height (34/16 = 2.125 blocks). Removed the deferred locator listener and redundant render-state ticket, leaving one light-update path per render extraction. LambDynamicLights remains optional; the light stays near the head without tracking secondary head/antler motion.

**Gameplay acceptance:** Stable Singer illumination, including the disappearance of the previously reported flicker, was confirmed manually on NeoForge 26.3.0.64-beta. This confirmation applies to the tested installation and renderer configuration. Earlier preview releases remain available for rollback.

Technical investigation and validation evidence: [Singer lighting investigation](docs/singer-dynamic-light-investigation.md).

**Validation:** JDK 25 / NeoForge 26.3.0.64-beta Gradle `clean build` PASS; Easy Magic 26.3.0 binary compatibility 4/4 PASS; compiled control-flow regression test FAIL on Preview 2 and PASS on Preview 3; manual Singer lighting test PASS. JAR comparison against Preview 2 yielded identical paths for all 1,588 entries and precisely two changed contents: `SingerRenderer.class` and `META-INF/neoforge.mods.toml`.

Release metadata corrected: NeoForge FML uses `logoFile` for the mod icon, and the project URL points to the community repository. Only metadata is changed relative to the gameplay-accepted Preview 3 JAR; all compiled gameplay classes and asset contents remain unchanged.

## Community preview 2 — superseded; light regression

An AMD Radeon ReLive recording from 2026-10-10 exposed visible light flicker around a moving Singer while LambDynamicLights was active. Previously, `SingerRenderer.shouldRender` moved its light to a body-relative estimate at 85% of the 4-block bounding-box height (3.4 blocks) on every render pass, immediately before the animated `head_light` locator moved the same source to a different world position. The Singer model's head locator has a resting height of 34 pixels (2.125 blocks); the competing updates could rapidly move the light by about 1.275 blocks vertically even when the entity itself had not changed position.

**Failed attempt in `1.1.1-neoforge.2-community.2`:** The renderer was changed to use the animated head locator as the sole visible-render light source, with a fallback only when the entity was culled. This unintentionally removed reliable source registration for visible Singers. Superseded by the restored single-source implementation in `community.3`.

**Historical verification:** Java 25 `clean build` against NeoForge 26.3.0.64-beta PASS, exact Easy Magic contract 4/4 PASS, and JAR delta against Preview 1 restricted to `SingerRenderer.class` and version metadata. The static checks did not prevent the gameplay regression.

## Target

Minecraft 26.3, NeoForge 26.3.0.64-beta minimum, Java 25, GeckoLib 5.5.7. This fork continues Mielon's The Sift 1.1.1 behavior with the original `the_sift` mod ID. The previous loader port was developed and initially compiled against NeoForge 26.3.0.43-beta. The build still targets .64-beta while the required loader range permits newer NeoForge builds for the same Minecraft version.

## Changes from the upstream source

- Native NeoForge registration, events, networking, fluids, world generation, rendering and client lifecycle in place of Fabric integration.
- Working Ichor source/flow/waterlogging, pickup and bucket rules.
- Optional Easy Magic 26.3.0 Charoite enchantment compatibility, plus preserved vanilla enchanting.
- Ancient City main portal and Rift return-route safeguards, including safe fallback when legacy return coordinates are missing.
- Singer dynamic-light updates, Sonorous beam visibility, Sift plant fluid wash-away tags, Echo Golem pickup sound and procedural sky rendering improvements.
- Existing optional integrations with Iris, Sodium and LambDynamicLights.

## Tested baseline and limits

The earlier NeoPack-specific NeoForge 26.3.0.43-beta candidate `the_sift-1.1.1-neoforge.1-NeoPackFix-r3.jar` passed a Gradle build, 49/49 targeted automated checks, and dedicated-server startup. Eight repair groups were confirmed in gameplay, including Easy Magic Charoite, Ichor, portal/Rift routing, lighting, vegetation, beams, audio, and shader-assisted sky.

An equivalent-code local r4 JAR, changing only the loader version requirement, was subsequently started with NeoForge 26.3.0.64-beta in isolated server and client title-screen smoke tests.

Community Preview 1 (`1.1.1-neoforge.2-community.1`) passed `gradlew clean build` against NeoForge **26.3.0.64-beta** using JDK 25. ZIP integrity, mod metadata, and Easy Magic 26.3.0 binary checks (4/4) also passed. Gameplay on `.64-beta` was confirmed functional with one Singer lighting flicker, later resolved in `community.3`. Verification is specific to the tested installation and does not cover every shader or modpack combination.

**Historical 1.1.1 shader limitation (superseded by Port 1.1.2):** Earlier smooth-sky testing with *Complementary Unbound 5.9.3* relied on a user-selected `TheSiftSmoothSky` shaderpack variant to correct flat-color interpolation. **Port 1.1.2 fixes this in the mod's own texture-backed renderer**; the final Photon, Complementary and shaderless sky checks were confirmed working in-world. No modified shaderpack ZIP is included or required for those tested setups, and untested combinations are not guaranteed.

**Save-data limitation:** a legacy world that has already lost its original Ancient City return coordinates cannot retroactively recover the lost coordinates. The safe main-exit fallback prevents generating an unrelated Rift.

Additional records: [porting history](PORTING.md), [sources and credits](SOURCE_PROVENANCE.md), [optional Easy Magic integration](EASY_MAGIC_COMPAT.md), and [AI assistance disclosure](AI_DISCLOSURE.md).
