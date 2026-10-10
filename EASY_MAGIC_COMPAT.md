# Optional Easy Magic compatibility

The Sift remains playable without Easy Magic. Its standard enchanting-table support and Charoite behavior are available without the optional mod.

When [Easy Magic](https://modrinth.com/mod/easy-magic) **26.3.0** for NeoForge 26.3 is installed, the port integrates Charoite with Easy Magic's custom three-slot enchantment menu:

- Charoite joins the `easymagic:enchanting_catalysts` item tag additively (`replace: false`), preserving lapis lazuli.
- Enchanting with Charoite adds 20 levels to non-zero costs and increases applicable enchantment levels by one.
- Charoite enchantment advancement and the original restriction on combining Charoite-enchanted items in an anvil still apply.
- Easy Magic's separate reroll slot and reroll action remain under Easy Magic's control.

The implementation uses an optional `@Pseudo` mixin whose target class is referenced by name, with no Java import or required loader dependency on Easy Magic. The data tag is inert without Easy Magic. **Puzzles Lib is required by Easy Magic itself, not by The Sift.**

The integration was checked against the exact **Easy Magic 26.3.0 NeoForge** binary and its three-slot/enchantment recalculation implementation. The historical compatibility contract passed 4/4 checks, and separate dedicated-server startup tests succeeded both with Easy Magic + Puzzles Lib and without them. The NeoPack user also manually confirmed working Charoite enchantment behavior.

This only establishes the tested 26.3.0 integration. If a future Easy Magic version changes its internal menu structure or method names, its optional mixin may need revising. Test upgrades against both installed and absent configurations.

## Reproducing the binary contract check

Place the original, unmodified `easymagic-v26.3.0-mc26.3.x+neoforge.jar` under `run/mods/`, or set the `THE_SIFT_EASY_MAGIC_JAR` environment variable to its location. Run with Python 3 and JDK 25's `javap` available in `PATH` or `JAVA_HOME`:

```sh
python tools/tests/easymagic_compat_test.py
```

The test verifies the exact published Easy Magic SHA-256, its menu/slot contract and the absence of a hard Java class import from The Sift. It does not replace in-game validation of enchanting or runtime launch tests with Easy Magic absent.
