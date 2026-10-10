# Singer lighting regression: investigation and resolution

Status: 2026-10-10, Minecraft 26.3 / NeoForge 26.3.0.64-beta. Version `1.1.1-neoforge.2-community.3` is **visually accepted for the tested setup** and promoted to stable release.

## Observations

1. Community Preview 1 (`community.1`) visibly emitted Singer light, although the user reported it flickering. Video: Radeon ReLive `unknown_2026.10.10-12.35.mp4` (8.88 s, 2560×1440, ~60 fps).
2. The user installed Community Preview 2 (`community.2`) and reported **no Singer dynamic light at all**, although the entity and other mod mechanics worked. Its source only called `SingerHeadLightBridge.update` from `shouldRender` if `super.shouldRender` returned `false`. When visible, the code relied on the `preRenderPass` GeckoLib locator listener.
3. The corresponding NeoPack client log recorded LambDynamicLights initialization and The Sift 26.3 load without a Singer-related exception. Absence of an exception does not establish that a deferred locator callback was delivered.

## Verified integration facts

- The Sift's bridge is a no-op until the optional LambDynamicLights initializer installs a handler. That handler calls `DynamicLightBehaviorManager.add` on the first `update` for a Singer; later updates move the existing light source.
- In the exact GeckoLib 5.5.7 NeoForge binary, `GeoRenderer.performRenderPass` invokes `preRenderPass` but the locator listener is supplied later during deferred model geometry rendering. `RenderPassInfo.addLocatorPositionListener(name, ...)` first calls `BakedGeoModel.getLocator(name).ifPresent` and therefore silently registers nothing if the named locator is absent. When a callback is actually executed on the normal entity path, `DataTickets.POSITION` is populated; the precise reason it was ineffective in the user's Preview 2 runtime has not been demonstrated by an instrumented render.
- `SingerRenderer.shouldRender` was already known to execute with a visible Singer on Community Preview 1. An analysis of Preview 2's compiled method found a path returning at bytecode offset 40 without any bridge update. The new `tools/tests/singer_light_registration_test.py` reproduces this as a **FAIL** on Preview 2 and **PASS** on Preview 3. This test inspects the compiled Java control flow, rather than assuming a successful Gradle build proves the light is registered.
- The Singer entity has a 4-block bounding height, so Preview 1's approximate source at 85% height stood at 3.4 blocks above the entity origin. Its model has a `head_light` resting Y of 34/16 = 2.125. The two writers *could* have competed if the locator callback was delivered; the original flicker has **not** been conclusively attributed to this cause.

Reference APIs: [GeckoLib 5 RenderPassInfo](https://wiki.geckolib.com/docs/geckolib5/concepts/rendering/renderpassinfo/) and [LambDynamicLights](https://github.com/LambdAurora/LambDynamicLights).

## Released fix

Restored unconditional `SingerHeadLightBridge.update` before the `shouldRender` visibility decision, with one stable source at the body-relative head height of 2.125 blocks. Removed the deferred locator writer, restoring light registration from Preview 1 and eliminating competing-position writes. Exact head animation tracking was intentionally replaced with stable head-height placement. Dynamic lights remain optional, and gameplay mechanics are unchanged.

Verification to date:

- Java 25 / NeoForge 26.3.0.64-beta: `gradlew clean build` — PASS.
- Easy Magic 26.3.0 (optional) binary contract tests — 4/4 PASS.
- Singer binary regression contract — Preview 2 FAIL; Preview 3 PASS.
- Production JAR ZIP/delta — 1,588 identical entry paths relative to Preview 2, with only `SingerRenderer.class` and `META-INF/neoforge.mods.toml` content changed.
- **Visual acceptance:** Stable Singer lighting with no observed flicker confirmed in the tested NeoForge 26.3.0.64-beta installation. Prior previews remain available, including the known-regression warning on Preview 2.

## Regression checks for future versions

Future renderer changes should be tested with a stationary view of an animating Singer for approximately 10 seconds, camera rotation through the visibility boundary, and movement around surrounding illuminated blocks. Before/after recordings should use matching shader and dynamic-light settings. A shader-disabled comparison can distinguish Iris/shader presentation artifacts from the dynamic light source.

Any future recurrence should be investigated with `SingerHeadLightBridge.update` invocation counts, Lamb's `DynamicLightBehavior.hasChanged` frequency, and chunk-rebuild cadence. A renderer-independent client `EntityTickEvent.Post` registration path is available as a follow-up architectural option if measurements justify it.
