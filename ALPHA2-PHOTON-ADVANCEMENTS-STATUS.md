# The Sift 1.1.2 — Minecraft 26.1.2 NeoForge ALPHA2 (test candidate)

**Date:** 2026-10-14. **Status: stage-only/unpublished; A18 and advancement in-world user retests PENDING.**

## Source provenance and safe scope

- Base: user-tested ALPHA1 source commit `602fa8b97e200fa1b3e20e81eb741739dc2a32be`.
- Branch: `fix/26.1.2-alpha2-photon-adv`, isolated worktree `work/neo-26.1.2-1.1.2-alpha2-photon-adv-fix`.
- Minecraft `26.1.2`, NeoForge **26.1.2.117** (latest verified), Java **25**, GeckoLib **5.5.2**.
- Internal mod version `1.1.2-neoforge.1-alpha.2`. NeoForge minimum `[26.1.2.117,)` **without upper bound**.
- ALPHA1, all original Mielon files, official MC26.2 and MC26.3 releases, NeoPack, ModTest configuration/mods/save, and Photon/Complementary shaderpacks remain unmodified.

## Bugs reproduced and examined

**Advancement criteria.** Read-only `ModTest/logs/latest.log`: on 2026-10-14 `19:58:07`, player joined Overworld. At `19:58:08`, `Fallen Civilization` and `Certified Sifter` both awarded. Actual Rift entry into The Sift was **19:58:27**. Original Mielon 1.1.2 NeoForge MC1.21.1 JAR has `minecraft:impossible` for all 11 affected criteria and awards them from `ModAdvancements.updateLocationAdvancements` on an `END_SERVER_TICK` callback every 16 ticks: Ancient City structure piece for Fallen Civilization; 10 individually visited Sift biomes for Certified Sifter. The MC26.1.2/26.2 port had accidentally dropped the callback and used `minecraft:location` criteria with an incorrectly nested `minecraft:location` key instead of vanilla 26.1.2's valid `location`. Predicates were effectively unguarded.

**Photon.** User screenshot shows huge patchwork of triangles in the night sky on Photon v1.3b. ModTest log at `20:21:11` selected Photon, and at `20:21:18` selected `Photon post-final depth-masked UV sky`, with zero fatal client errors. MC26.1.2 is **forward-Z** (clear depth 1.0), whereas the accepted MC26.2 path used reversed-Z (clear depth 0.0). Strict `DepthStencilState(CompareOp.EQUAL, false)` can expose per-triangle rounding when the clip position is `z=w`. This is a **working GPU depth hypothesis**, not user-verified correction. ModTest also contained C2ME WARN and a separate Complementary unknown uniform `BIOME_SULFUR_CAVES`; there is no evidence either caused Photon's mesh triangles.

## Changes versus ALPHA1

1. **Restore authoritative advancement behavior.** Both affected advancement JSON resources restored byte-for-byte from verified original Mielon NeoForge MC1.21.1 JAR. The Java tick callback, mapping of 10 biomes, dimension guard, spectator guard, and Ancient City structure piece check were restored to the target mappings, granting one manually tracked `minecraft:impossible` criterion at a time. This is not a global advancement reset and does not revoke existing save progress.
2. **Photon forward-Z mask precision.** In `assets/the_sift/shaders/core/photon_sky_overlay.fsh`, set `gl_FragDepth = 1.0;` explicitly on every fragment. Keep `CompareOp.EQUAL` and depth writes **disabled** so opaque world geometry occludes the sky. Retain `clip.z=clip.w`, unchanged six RGBA baked gradients, unchanged post-Iris `finalizeLevelRendering` hook, and vanilla/Complementary render paths.
3. **No other gameplay changes**: 158/158 worldgen definitions, 16/16 structures NBT, 6/6 baked gradient PNG, fluid/entity/balancing/network code unchanged. The two extra bytewise changes to `.vsh` and mixins JSON are newline formatting only (text and JSON semantics identical).

## Automated verification

| Gate | Result | Evidence |
| --- | --- | --- |
| `compileJava` | **PASS** | `logs/alpha2-advancement-compile.log` |
| `gradlew clean build`, NeoForge latest version check | **PASS** | `logs/alpha2-final-clean-build.log` |
| Full 100 advancement JSON/parents/requirements audit vs 98 original, 2 new recipes | **PASS** | `tools/tests/audit_advancements_alpha2.py` |
| ALPHA2 resource/metadata/JAR CRC parity against frozen ALPHA1 | **PASS** | `tools/tests/verify_alpha2_regressions.py` |
| Dedicated server `Done`, custom dimension load, 0 ERROR/FATAL | **PASS startup** | `logs/alpha2-server-adv-smoke.log` |
| Isolated Iris1.11.4+Sodium0.9.2+Photon v1.3b | **PASS bootstrap** | `logs/alpha2-photon-isolated-client.log` and isolated client latest.log |
| Isolated Iris1.11.4+Sodium0.9.2+Complementary Unbound r5.9.3 | **PASS bootstrap** | `logs/alpha2-complementary-isolated-client.log` and isolated client latest.log |
| User A01–A17 and A19–A20 on ALPHA1 | **PASS as reported by user** | Original user acceptance; changes scoped to shader+advancements |
| User in-world Photon sky A18 and advancement awarding on new ALPHA2 | **PENDING — critical acceptance** | See `ALPHA2-MANUAL-RETEST.md` |

No source/JAR change can guarantee correct visual results without user in-world retesting. Do not call Photon fixed or publish stable 1.1.2 until A18 passes with actual imagery.

## ModTest read-only warnings

- C2ME alpha contains deprecated FML loader metadata and occasional optional missing mixin targets (not evidence of The Sift defects).
- Sodium reports `AMD_GAME_OPTIMIZATION_BROKEN` driver workaround and detects RTSS injection.
- Complementary `BIOME_SULFUR_CAVES` custom uniform unresolved at shader reload; not correlated with the user Photon-only defect.
- Client shows warnings for unused `Sampler0`/`Sampler1` in portal shader, without ERROR/FATAL. Gameplay A05/A07 has user PASS.
- No ModTest files were modified during diagnosis.

**If ALPHA2 Photon night sky remains broken:** retain stage unchanged, investigate the depth texture presented by Iris's final pass and the shaderpack post-final frame state in a new ALPHA3. Do not change or distribute the shaderpack zip.
