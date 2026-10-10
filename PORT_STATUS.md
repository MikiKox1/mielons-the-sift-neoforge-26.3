# NeoForge 26.3 community port: scope and validation

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

The new community branch `1.1.1-neoforge.2-community.1` subsequently **passed `gradlew clean build`** against the actual **26.3.0.64-beta** development dependencies using JDK 25. Its JAR passed ZIP integrity and metadata checks (original MIT license, compiled optional Easy Magic mixin, required resources, no Fabric metadata and no mandatory Easy Magic dependency), and the exact Easy Magic 26.3.0 binary contract passed **4/4** locally. This newly named JAR has **not yet been separately tested in gameplay or on a dedicated server**; the historical NeoPack testing above describes the equivalent preceding fixes, not an additional acceptance run for this artifact.

**Shader-specific limitation:** smooth Sift sky with *Complementary Unbound 5.9.3* required a separate user-selected `TheSiftSmoothSky` shaderpack variant that corrected shader-side flat color interpolation. That third-party shaderpack is not included in this source repository. Standard rendering and other shaderpack combinations do not receive a blanket visual guarantee.

**Save-data limitation:** a legacy world that has already lost its original Ancient City return coordinates cannot retroactively recover the lost coordinates. The safe main-exit fallback prevents generating an unrelated Rift.

For development history see [PORTING.md](PORTING.md). For optional enchanting compatibility see [EASY_MAGIC_COMPAT.md](EASY_MAGIC_COMPAT.md). For AI involvement and provenance see [AI_DISCLOSURE.md](AI_DISCLOSURE.md).
