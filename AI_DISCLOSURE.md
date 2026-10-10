# AI assistance disclosure

This repository contains an **AI-assisted community port** of Mielon's The Sift from Fabric to NeoForge for Minecraft 26.3.

During this port, OpenAI language models were used extensively to help investigate the released upstream implementation and current Minecraft/NeoForge APIs, propose and edit Java code and mixins, troubleshoot compatibility, prepare test and audit scripts, and draft technical documentation. This includes work on the optional Easy Magic/Charoite compatibility layer and later NeoPack compatibility repairs. The port should therefore **not** be presented as entirely human-written code.

Human oversight determined the port target and behavior requirements, guided debugging and reviewed changes. The maintainer ran build/static/runtime checks, and the NeoPack user manually tested and accepted eight targeted gameplay repair categories on the earlier NeoForge 26.3.0.43-beta build. Those results do not constitute an independent code audit or full compatibility guarantee for all modpacks and shaders. See [PORT_STATUS.md](PORT_STATUS.md) for exact scope.

**Authorship and attribution:** Mielon is the creator of the original mod. The original mod's work, identity and credits must not be attributed to the AI or to the port maintainer. This repository retains Mielon's 2026 MIT copyright notice and preserves the existing credits for external contributors and assets. Fork changes are separately identified in Git history and the port documentation.

**Relationship to upstream:** This fork is independently maintained and is neither published nor endorsed as an official release by Mielon. The upstream project is [Arta48/Mielons-The-Sift](https://github.com/Arta48/Mielons-The-Sift).
