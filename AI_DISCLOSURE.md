# AI assistance disclosure

This branch contains an **AI-assisted community port** of Mielon's The Sift from Fabric to NeoForge for **Minecraft 26.2**. The Minecraft 26.3 port is maintained on a separate branch.

During the NeoForge adaptations, OpenAI language models were used extensively to help investigate the released upstream implementation and Minecraft/NeoForge APIs, propose and edit Java code and mixins, troubleshoot compatibility, prepare tests and review build results. This includes Minecraft 26.2 world generation, rendering and optional Easy Magic/Charoite compatibility. The port should therefore **not** be presented as entirely human-written code.

Human oversight determined the port target, behavior requirements and acceptance criteria. Java 25 builds, static/archive checks, dedicated-server and client startup checks are described in [the Minecraft 26.2 release notes](RELEASE-NOTES-1.1.2.md). The user manually accepted the tested Photon, Complementary and shaderless sky behavior. This does not constitute an independent code audit or guarantee every modpack and shader combination.

**Authorship and attribution:** Mielon is the creator of the original mod. The original mod's work, identity and credits must not be attributed to the AI or to the port maintainer. This repository retains Mielon's 2026 MIT copyright notice and preserves the existing credits for external contributors and assets. Fork changes are separately identified in Git history and the port documentation.

**Project relationship:** Independently maintained as an unofficial community port. Original mod published by Mielon on [Modrinth](https://modrinth.com/mod/mielons-the-sift) and [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mielons-the-sift). Port source based on a [public snapshot hosted by Arta48](https://github.com/Arta48/Mielons-The-Sift). Original creator and other contributors remain credited in [SOURCE_PROVENANCE.md](SOURCE_PROVENANCE.md).
