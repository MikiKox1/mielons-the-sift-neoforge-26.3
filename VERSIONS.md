# Minecraft version support and branch policy

This repository contains the unofficial NeoForge port of Mielon's The Sift, maintained by MikiKox1. **Only the Minecraft 26.3 build is released today.** Minecraft 26.2 and 26.1.2 are future targets, not claims of working or tested builds.

## Release matrix

| Minecraft | Availability | Source | Binary |
| --- | --- | --- | --- |
| **26.3** | **Released: Port 1.1.1** | [`minecraft/26.3`](https://github.com/MikiKox1/mielons-the-sift-neoforge-26.3/tree/minecraft/26.3) | [Port 1.1.1 GitHub release](https://github.com/MikiKox1/mielons-the-sift-neoforge-26.3/releases/tag/v1.1.1-neoforge.2-community.3-fix.1) |
| 26.2 | Planned; no public build | `minecraft/26.2` will be added after the actual port is available | None |
| 26.1.2 | Planned; no public build | `minecraft/26.1.2` will be added after the actual port is available | None |

## What each branch means

- **`main`**: current GitHub landing/default branch. It remains the verified Minecraft **26.3** source until an explicit future release-management decision changes it. Do not treat `main` as a source tree that simultaneously supports all Minecraft versions.
- **`minecraft/26.3`**: dedicated branch for the released **26.3** port. Starts at the reviewed 26.3 publication baseline, with the same published code and documentation.
- **`minecraft/26.2`**: reserved branch name for a **future actual 26.2 port**. It is **not created** while only the 26.3 source exists.
- **`minecraft/26.1.2`**: reserved branch name for a **future actual 26.1.2 port**. It is **not created** while only the 26.3 source exists.

Every future version branch must contain its **own correctly targeted** `gradle.properties`, NeoForge metadata, dependencies, source adjustments, compatibility tests, CI and changelog. Do not simply copy or rename the 26.3 JAR and present it as a different Minecraft version. Share changes across branches only after reviewing and adapting them to the destination version.

## GitHub releases and tags

- The existing Minecraft 26.3 GitHub Release retains its public title **`Port 1.1.1`**, original tag **`v1.1.1-neoforge.2-community.3-fix.1`**, and unchanged uploaded JAR. CurseForge File ID **9124779** also remains unchanged.
- GitHub's `/releases/latest` is repository-wide, **not specific to a Minecraft version**. Use the exact release/tag links in the table above when linking to a given Minecraft line.
- When new versions are ready and explicitly approved for publication, each must have an unambiguous Minecraft target in its release description, tag and downloadable asset metadata/name. Never reuse or overwrite a published tag or binary from a different Minecraft line.
- Release notes for later releases within a line should describe changes since that line's previous public release, rather than experimental build history.
- CurseForge game-version metadata must match the actual tested binary. Publish on GitHub first, then CurseForge, only after the normal technical, user-gameplay, licensing and provenance checks pass.

## Status and provenance

This is an **unofficial** NeoForge port. Original mod: **Mielon** (MIT). Port maintainer: **MikiKox1**. Keep [LICENSE](LICENSE), [source and attribution](SOURCE_PROVENANCE.md) and [AI development disclosure](AI_DISCLOSURE.md). No claim is made that the planned 26.2 or 26.1.2 targets have been compiled, tested or released.

The repository URL currently retains its historical `-26.3` suffix. **Branch name, release tag and per-version download are the version identifiers**; the repository name alone must not be used to infer compatibility.
