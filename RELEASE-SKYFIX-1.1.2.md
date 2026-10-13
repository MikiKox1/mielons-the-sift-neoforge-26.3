# Mielon's The Sift — NeoForge MC26.2 Port 1.1.2

**Standalone release candidate for user handoff** — not installed to any user profile and not pushed to GitHub.

**Exact binary filename:** mielon's-the-sift-1.1.2-mc26.2-neoforge-port.jar
**Internal version:** 1.1.2-neoforge.1
**Runtime target:** Minecraft Java **26.2**, Java **25**, GeckoLib **5.5.6+ below 6**.
**NeoForge tested:** **26.2.0.90**, with minimum **[26.2.0.90,)** and **NO upper version bound**.

## Included

- Complete The Sift Port 1.1.2 functionality from the separately verified MC26.2 backport.
- Six animated original day/night gradient layers now use pre-baked, seamless RGBA textures and Iris TEXTURED UV interpolation for Complementary and other compatible shaderpacks.
- **Photon v1.3b:** a separate mod-owned depth-masked sky overlay drawn AFTER Iris final pass, preserving landscape depth, Photon clouds, stars and moon. Does not modify Photon ZIP or require a separate resource pack.
- Shaders OFF: original position-color sky renderer unchanged. No gameplay/worldgen balance changes.
- Earlier fixes for Ichor Regeneration, Dark Sniffer Sonic Sniff, Charoite/enchanting/anvil, Easy Magic integration, Rift/portals and Echo Golem/Singer preserved.

## QA

- **User manual MC26.2:** user reported ALPHA4 sky works perfectly and previously completed the A01–A20 gameplay checklist. Changes from accepted ALPHA4 in this release are only internal version, description, isolated Gradle run folders and diagnostic log wording.
- **Java25 clean build:** PASS against latest NeoForge 26.2.0.90.
- **Dedicated server:** PASS, registered/loaded dimension the_sift:the_sift, Done (2.048s), 0 ERROR/FATAL.
- **Iris1.11.4 + Sodium0.9.2 + Photon1.3b isolated client menu:** PASS, detected Photon, five GeckoLib models/animations, 0 ERROR/FATAL.
- **Binary QA:** see TheSift/reports/release-1.1.2/verify_dual_release.py and release hash manifests. 26.2 ALPHA4 game resources/worldgen unchanged. Iris/Sodium/Photon are NOT bundled.

**Not claimed:** universal shaderpack compatibility or new multiplayer FPS tests after release labeling. Source and binary SHA hashes are recorded in the handoff manifest.

**Rollback:** accepted MC26.2 ALPHA4 remains in TheSift/artifacts/builds/experimental-sky-alpha4-photon-262/, SHA256 28D682DADF1757F92A73C659247F4033E04A6E3AC4D996911ED057D46BDED5C5.
