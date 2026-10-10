# NeoForge 26.3 community port: scope and validation

## Community preview 3 — restore Singer lighting reliably (candidate)

**Regression in preview 2:** the user reported that Singer's dynamic light disappeared completely. In that release `SingerRenderer.shouldRender` only registered the light when the entity was culled, leaving the visible case entirely dependent on the `head_light` GeckoLib locator callback. In GeckoLib 5.5.7, locator callbacks run during deferred geometry rendering and may not be delivered when the locator is unavailable. They cannot serve as a guaranteed source-registration point. A regression test against the compiled preview-2 JAR reproduced a visible return path without calling the light bridge.

Preview 3 restores the known-working unconditional light update from `shouldRender`, before the visibility result, and fixes its position at the model's resting head height (34/16 = 2.125 blocks). The deferred locator listener and its redundant render-state ticket are removed, so Singer has one update path per render extraction and no competing 3.4-vs-2.125-block movements. LambDynamicLights remains optional. Its light stays near the head without tracking secondary head/antler animation.

This is a staged candidate awaiting the user's focused visual acceptance. The live NeoPack profile and world are not automatically modified; prior releases remain available for rollback.

Technical research and a focused visual retest procedure: [Singer lighting investigation](docs/singer-dynamic-light-investigation.md).

Validation: JDK 25 / NeoForge 26.3.0.64-beta Gradle `clean build` PASS; Easy Magic 26.3.0 binary compatibility 4/4 PASS; new compiled control-flow regression test correctly fails on Preview 2 (visible Singer can return without registering light) and passes on Preview 3. JAR comparison against the installed Preview 2 yielded identical paths for all 1,588 entries and precisely two changed contents: `SingerRenderer.class` and `META-INF/neoforge.mods.toml`. This is static/build verification; lighting stability still needs an in-game test.

## Community preview 2 — Singer light stability (superseded; regression)

An AMD Radeon ReLive recording from 2026-10-10 exposed visible light flicker around a moving Singer while LambDynamicLights was active. Previously, `SingerRenderer.shouldRender` moved its light to a body-relative estimate at 85% of the 4-block bounding-box height (3.4 blocks) on every render pass, immediately before the animated `head_light` locator moved the same source to a different world position. The Singer model's head locator has a resting height of 34 pixels (2.125 blocks); the competing updates could rapidly move the light by about 1.275 blocks vertically even when the entity itself had not changed position.

The corrected renderer uses the animated head locator as the sole visible-render position. It updates the fallback only when the entity is actually culled, at the model's resting head height (2.125 blocks), preserving lighting outside the frustum. The LambDynamicLights dependency remains optional. This fix is implemented for `1.1.1-neoforge.2-community.2`, with the previous community preview preserved as a rollback release. An in-game visual retest by the user is required to confirm the flicker is resolved on their exact renderer/shader setup.

Validation for this candidate: Java 25 `clean build` against NeoForge 26.3.0.64-beta **PASS**, exact Easy Magic contract **4/4 PASS**, and JAR entry comparison against community preview 1 **PASS**: of 1,588 entries, only `SingerRenderer.class` and the mod version metadata differ.

## Target

Minecraft 26.3, NeoForge 26.3.0.64-beta, Java 25, GeckoLib 5.5.7. This fork continues Mielon's The Sift 1.1.1 behavior with the original `the_sift` mod ID. The previous loader port was developed and initially compiled against NeoForge 26.3.0.43-beta. The branch targets .64-beta for public reproducible builds.

## Changes from the upstream source

- Native NeoForge registration, events, networking, fluids, world generation, rendering and client lifecycle in place of Fabric integration.
- Working Ichor source/flow/waterlogging, pickup and bucket rules.
- Optional Easy Magic 26.3.0 Charoite enchantment compatibility, plus preserved vanilla enchanting.
- Ancient City main portal and Rift return-route safeguards, including safe fallback when legacy return coordinates are missing.
- Singer dynamic-light updates, Sonorous beam visibility, Sift plant fluid wash-away tags, Echo Golem pickup sound and procedural sky rendering improvements.
- Existing optional integrations with Iris, Sodium and LambDynamicLights.

## Tested baseline and limits

The earlier NeoPack-specific NeoForge 26.3.0.43-beta candidate `the_sift-1.1.1-neoforge.1-NeoPackFix-r3.jar` passed a Gradle build, 49/49 targeted automated checks, and dedicated-server startup. The user confirmed eight repair groups in gameplay, including the Easy Magic Charoite path, Ichor, portal/Rift routing, lighting, vegetation, beam, audio and shader-assisted sky.

An equivalent-code local r4 JAR, changing only the loader version requirement, was subsequently started with NeoForge 26.3.0.64-beta in isolated server and client title-screen smoke tests.

The new community branch `1.1.1-neoforge.2-community.1` subsequently **passed `gradlew clean build`** against the actual **26.3.0.64-beta** development dependencies using JDK 25. Its JAR passed ZIP integrity and metadata checks (original MIT license, compiled optional Easy Magic mixin, required resources, no Fabric metadata and no mandatory Easy Magic dependency), and the exact Easy Magic 26.3.0 binary contract passed **4/4** locally. The user subsequently reported that their gameplay on `.64-beta` was working overall and supplied the Radeon ReLive clip showing the Singer light flicker. This is user-reported acceptance with one known visual issue, not universal modpack or shader compatibility certification.

**Shader-specific limitation:** smooth Sift sky with *Complementary Unbound 5.9.3* required a separate user-selected `TheSiftSmoothSky` shaderpack variant that corrected shader-side flat color interpolation. That third-party shaderpack is not included in this source repository. Standard rendering and other shaderpack combinations do not receive a blanket visual guarantee.

**Save-data limitation:** a legacy world that has already lost its original Ancient City return coordinates cannot retroactively recover the lost coordinates. The safe main-exit fallback prevents generating an unrelated Rift.

For development history see [PORTING.md](PORTING.md). For source-origin limitations see [SOURCE_PROVENANCE.md](SOURCE_PROVENANCE.md). For optional enchanting compatibility see [EASY_MAGIC_COMPAT.md](EASY_MAGIC_COMPAT.md). For AI involvement see [AI_DISCLOSURE.md](AI_DISCLOSURE.md).
