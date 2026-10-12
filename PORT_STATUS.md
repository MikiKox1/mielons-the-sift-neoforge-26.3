# NeoForge 26.3 community port: scope and validation

**Current stable release:** **Port 1.1.1**. Its consolidated changes relative to the original Fabric mod are listed in [CHANGELOG.md](CHANGELOG.md). The following sections are historical compatibility records, not public release notes.

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

**Shader-specific limitation:** smooth Sift sky with *Complementary Unbound 5.9.3* required a separate user-selected `TheSiftSmoothSky` shaderpack variant that corrected shader-side flat color interpolation. That third-party shaderpack is not included in this source repository. Standard rendering and other shaderpack combinations do not receive a blanket visual guarantee.

**Save-data limitation:** a legacy world that has already lost its original Ancient City return coordinates cannot retroactively recover the lost coordinates. The safe main-exit fallback prevents generating an unrelated Rift.

Additional records: [porting history](PORTING.md), [sources and credits](SOURCE_PROVENANCE.md), [optional Easy Magic integration](EASY_MAGIC_COMPAT.md), and [AI assistance disclosure](AI_DISCLOSURE.md).
