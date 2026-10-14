# Minecraft version support and branch policy

This repository contains the unofficial NeoForge port of Mielon's The Sift, maintained by MikiKox1. **Separate Port 1.1.2 builds are available for Minecraft 26.3 and 26.2.** Minecraft 26.1.2 remains planned.

## Release matrix

| Minecraft | Availability | Source branch | Verified release |
| --- | --- | --- | --- |
| **26.3** | **Released: Port 1.1.2 26.3** | [`minecraft/26.3`](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/tree/minecraft/26.3) | [Port 1.1.2 26.3](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/releases/tag/v1.1.2-mc26.3-neoforge) |
| **26.2** | **Released: Port 1.1.2 26.2** | [`minecraft/26.2`](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/tree/minecraft/26.2) | [Port 1.1.2 26.2](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/releases/tag/v1.1.2-mc26.2-neoforge) |
| 26.1.2 | Planned — no public binary | `minecraft/26.1.2` (only after port development) | Not available |

## Branch policy

- **`main`**: GitHub default/landing branch, following the maintained Minecraft **26.3** port and version-support documentation.
- **`minecraft/26.3`**: source implementation for Minecraft **26.3**, NeoForge **26.3.0.64-beta** or newer, Java 25 and required GeckoLib 5.5.7-compatible 5.x.
- **`minecraft/26.2`**: distinct source implementation for Minecraft **26.2**, NeoForge **26.2.0.90** or newer, Java 25 and required GeckoLib 5.5.6-compatible 5.x.
- **`minecraft/26.1.2`**: reserved for a future actual 26.1.2 port, not yet implemented or published.

The two supported Minecraft versions have **different source code, dependencies, version ranges, Git tags and release files**. No 26.3 JAR is advertised as compatible with 26.2 or vice versa. Changes should be reviewed and adapted rather than blindly copied between branches.

## Public releases

- **Port 1.1.2 26.3:** tag `v1.1.2-mc26.3-neoforge`; game binary and sources JAR explicitly include `mc26.3` in their names. Notes: [26.3 release notes](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/blob/minecraft/26.3/RELEASE-NOTES-1.1.2.md).
- **Port 1.1.2 26.2:** tag `v1.1.2-mc26.2-neoforge`; game binary and sources JAR explicitly include `mc26.2` in their names. Notes: [26.2 release notes](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/blob/minecraft/26.2/RELEASE-NOTES-1.1.2.md).
- **Previous 26.3 Port 1.1.1:** unchanged at [its existing tag](https://github.com/MikiKox1/mielons-the-sift-neoforge-port/releases/tag/v1.1.1-neoforge.2-community.3-fix.1). Its historical GitHub release and CurseForge File ID 9124779 remain untouched.

GitHub `/releases/latest` is repository-wide, **not version-specific**. For a specific Minecraft version, follow its exact release link above. Each update should list changes relative to the previous public release **on that Minecraft version line**.

## Status, testing and provenance

For Minecraft 26.2, the user accepted Photon, Complementary and shaders-disabled sky rendering in-world. For Minecraft 26.3, the user also confirmed those three render paths working after the final RenderPass texture-preload correction. Compatibility with every possible future shader or renderer combination is not guaranteed.

This is an **unofficial** NeoForge port. **Mielon** created the original mod (MIT); the NeoForge port is maintained by **MikiKox1**. Keep [LICENSE](LICENSE), [SOURCE_PROVENANCE.md](SOURCE_PROVENANCE.md) and [AI_DISCLOSURE.md](AI_DISCLOSURE.md). All third-party assets retain their original attribution; do not claim new permissions that were not obtained.
