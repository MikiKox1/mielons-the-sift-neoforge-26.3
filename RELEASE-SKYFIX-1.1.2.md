# Mielon's The Sift — NeoForge MC26.3 Port 1.1.2 sky compatibility update

**Local release candidate for user's requested final MC26.3 control test.** NOT installed into live profiles and NOT pushed to GitHub.

**Exact binary filename (unchanged):** mielon's-the-sift-1.1.2-mc26.3-neoforge-port.jar
**Internal version:** 1.1.2-neoforge.2, superseding the prior 1.1.2-neoforge.1 binary while leaving the original artifact untouched.
**Target:** Minecraft Java **26.3**, Java **25**, GeckoLib **5.5.7+ below 6**.
**NeoForge build/test:** latest **26.3.0.76**, with original earliest supported **26.3.0.64-beta** retained as minimum in OPEN range **[26.3.0.64-beta,)**. Do not cap later NeoForge builds.

## Changes vs previously accepted Port 1.1.2 for MC26.3

- Backports the **user-confirmed working MC26.2 ALPHA4 shader-sky solution** into Minecraft26.3 RenderPearl graphics APIs. The Blaze3D MC26.2 binary is not reused on MC26.3.
- Six RGBA day/night sky gradient textures are byte-identical to MC26.2; Iris TEXTURED selects linearly filtered UV sampling to avoid Complementary flat-color triangle artefacts. Original shaderless sky remains.
- Photon v1.3b is detected dynamically and routed to an optional Iris final-pass callback, drawing the same six layers into the composited main target with depth masking (reversed Z, far depth=0, CompareOp.EQUAL, no depth writes) and 72% base-sky alpha for Photon moon and clouds.
- **MC26.3 RenderPearl shader syntax** uses #include, explicit SPIR-V input/output locations; verified with actual isolated client resource reload after correcting initial MC26.2 shader syntax. Client reports zero shader compiler errors.
- Original MC26.3 worldgen, NBT, gameplay, localization, textures/models, sounds and other classes unchanged. Original MIT license and credits retained.

## QA and acceptance boundary

- **Java25 clean build:** PASS against latest NeoForge26.3.0.76.
- **Independent binary integrity/resource conservation:** PASS, compared against pinned original MC26.3 JAR SHA256 D3BD9A6BF718E755E1DA42190FF726A48D3FE6708245ED879A00883434E27CEE. Only two existing renderer/compat classes changed, one optional Iris mixin plus six sky PNG/two overlay GLSL sources, mod version and mixin JSON.
- **Dedicated server:** PASS; the_sift:the_sift dimension loaded, Done (2.656s), 0 ERROR/FATAL.
- **Independent no-Iris client menu:** PASS; RenderPearl shader compiled, five GeckoLib models loaded, 0 ERROR/FATAL.
- **Optional Iris1.11.7 + Sodium 0.9.2 OR 0.9.3-alpha.1 on MC26.3:** **BLOCKED BY UNRELATED SODIUM** before The Sift finishes loading. Both public Sodium builds target an old LevelExtractorMixin method not available on MC26.3/NeoForge26.3.0.76; the resulting InvalidInjectionException belongs to Sodium, not The Sift. No fix was borrowed from NeoPack or bundled into the mod.
- **User's requested final MC26.3 shaderpack in-world test:** **PENDING**. Use a known-good MC26.3 Sodium/Iris pairing, replace the previous The Sift JAR (do not load both), and compare Photon, Complementary and no-shaders with the earlier accepted MC26.2 visuals. Avoid calling 26.3 in-world visual QA PASS until the user reports the result.

**Rollback:** previous Port 1.1.2 for MC26.3 is preserved at SHA256 D3BD9A6BF718E755E1DA42190FF726A48D3FE6708245ED879A00883434E27CEE in the historical release-candidate folder.

No live installation, external publish, GitHub push, NeoPack or ModTest changes by Prime 2. Both new JAR names and SHA checksums are in the separate dual-version handoff manifest.
