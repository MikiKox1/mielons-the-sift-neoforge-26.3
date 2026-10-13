# The Sift 1.1.2 — Minecraft 26.3 RenderPass GPU texture preload hotfix

Date: 2026-10-14. Local hotfix release candidate only, no live game or GitHub publication.

## Evidence from user ModTest (read only)

Source: C:\Users\miki1\AppData\Roaming\ModrinthApp\profiles\ModTest\crash-reports\crash-2026-10-14_00.24.34-client.txt.

The first error is:

    Description: Uploading texture
    java.lang.IllegalStateException: Close the existing render pass before performing additional commands
      com.mojang.renderpearl.frontend.FrontendCommandEncoder.writeToTexture:410
      net.minecraft.client.renderer.texture.TextureManager.getTexture:98
      mielon.thesift.client.render.SiftProceduralSkyRenderer.renderActual:219
      mielon.thesift.client.render.SiftProceduralSkyRenderer.renderAfterPhotonFinalPass:165
      IrisRenderingPipeline.finalizeLevelRendering

ROOT CAUSE: The Sift 1.1.2-neoforge.2 calls lazy TextureManager.getTexture for a new sky PNG while the RenderPearl RenderPass is OPEN. TextureManager uploads pixels through CommandEncoder.writeToTexture, which requires the current render pass to have been closed. This is an actual port bug, NOT the independent Sodium LevelExtractorMixin issue.

## Minimal fix

- Branch: fix/1.1.2-neoforge.3-sky-texture-preload, created from immutable 26.3 release commit 34bf83c03571075b81e06e3728377e6fe6d8f938.
- During SkyRenderer.render HEAD, before its own createRenderPass: eagerly load the six sky textures and sampler, and pre-create the appropriate vertex/index GPU buffers.
- During vanilla or Photon final RenderPass: ONLY bind already-prepared views/sampler and perform the draws; no lazy texture upload or buffer creation.
- Refresh views each sky frame, including after shader toggles and resource reloads. Clear held references on renderer close.
- Preserve the exact six textures, Photon final depth mask, Complementary Iris TEXTURED route and shaderless fallback, all worldgen, loot, entities, animations, localization and saves.
- Internal mod version becomes 1.1.2-neoforge.3. External JAR name must remain mielon's-the-sift-1.1.2-mc26.3-neoforge-port.jar.
- NeoForge original open-ended minimum range [26.3.0.64-beta,) unchanged; built on latest 26.3.0.76, Java25, GeckoLib5.5.7.

## Independent QA results

| Gate | Result |
| --- | --- |
| First actual user exception/stack diagnosed | PASS |
| Source regression BEFORE change | Expected FAIL/RED |
| Source regression AFTER change | PASS/GREEN |
| Java25 full clean build | PASS |
| Final JAR ZIP CRC/diff/metadata six sky textures | PASS |
| Only two intended compiled classes changed | PASS |
| Isolated dedicated server plus Sift dimension | PASS, Done (2.744s), 0 hard errors |
| Isolated client without Iris | PASS, responsive menu, GeckoLib 5 models and animations, 0 hard errors |
| Photon and Complementary user-world render after fix | PENDING user test |

Exact old (crashing) 26.3 JAR SHA256: 29626D6E2B59B536B2FEE2B0D44A698DF70F5A2297D9ACE9954D796708B186F8. That binary and the older MC26.2 JAR remain untouched.

## Manual control test

Close Minecraft. In the existing ModTest profile, manually replace ONLY the old The Sift 26.3 JAR with this hotfix JAR (same name, do not load two copies). Keep the known-working Sodium/Iris pairing. Open a test world, enter The Sift, observe Photon sky and terrain, repeat with Complementary and shaders disabled, reload resources via F3+T, then inspect the latest log. Screenshots determine actual in-world PASS; no assistant controls your game.

Original source and old binary are preserved for rollback; no external publishing or edits to user profile by Prime2.
