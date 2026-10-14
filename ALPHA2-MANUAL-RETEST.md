# ALPHA2 — required targeted in-world retests (user-controlled)

**Use the new ALPHA2 stage JAR on Minecraft 26.1.2 / NeoForge26.1.2.117 / Java25 / GeckoLib5.5.2.** Keep the exact user-tested ALPHA1 JAR separately as rollback. Do not load a 26.2/26.3 world in 26.1.2.

## Photon A18 — not yet accepted

1. Use the known compatible **Iris1.11.4** + **Sodium0.9.2**, official **Photon v1.3b**, with no shaderpack ZIP edits.
2. Visit `the_sift:the_sift`, preferably the same location/time as the A18 screenshot. Look upward near zenith at night; inspect whether the giant blue/purple geometric mosaic disappears.
3. Confirm the colorful Sift ribbons/aurora and diffuse sky remain present (not black or replaced by ordinary overworld sky). View the same horizon with structures/portal/terrain in foreground: there must be **no colored sky geometry over blocks**.
4. Capture a screenshot with shaderpack + world coordinates + time; optionally repeat during day, and reload Photon from Iris settings once to verify resources.
5. Repeat with **Complementary Unbound r5.9.3** and once with shaders **OFF**, making sure neither previous PASS regresses.

**Automatic client-menu bootstrap is NOT a visual PASS.** If the giant polygons persist, label A18 FAIL and attach a screenshot and `latest.log`.

## Fallen Civilization — must NOT grant on login

1. Prefer a fresh throwaway world/new player progression after swapping ALPHA1 for ALPHA2. Before going to Ancient City, verify **Fallen Civilization remains locked** after standing in an ordinary Overworld location for at least several seconds.
2. Locate an actual Ancient City structure and stand within its generated piece. `Fallen Civilization` should then unlock once.

## Certified Sifter — ten distinct biomes

1. On the same fresh test progression, `Certified Sifter` should **not** unlock upon joining Overworld or on entering the Sift dimension.
2. Visit ten biomes: Sift Wastes, Sift Deep Dark, Ichor Snowy Peaks, Siftslate Slopes, Siftslate Peaks, Overgrown Slopes, Overgrown Peaks, Overgrown Forest Slopes, Overgrown Forest, Overgrown Clearing. Each visited biome should grant one criterion; the challenge unlocks **only after all ten**.
3. Save/rejoin partway through, verify existing individual criterion progress persists and additional visits continue accumulating.

**Existing ALPHA1 saves:** previous incorrect advancement awards are persisted by Minecraft. Upgrading to ALPHA2 **will not automatically revoke them**. To redo tests in an existing test save *only if you choose to*, use in-game:
`/advancement revoke @s only the_sift:story/fallen_civilization`
and
`/advancement revoke @s only the_sift:the_sift/certified_sifter`.
Those commands intentionally alter player advancement history. Do **not** apply them to valued worlds merely for QA; prefer a fresh throwaway world.

All other ALPHA1 A01–A17, A19–A20 received user PASS; retest only if the new isolated build introduces an unexpected regression.
