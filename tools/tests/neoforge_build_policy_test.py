"""Check NeoForge compiler compatibility without requiring latest releases."""
from __future__ import annotations
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[2]
NEO = re.compile(r"^(\d+)\.(\d+)\.(\d+)\.(\d+)(?:-([A-Za-z0-9.-]+))?$")


def properties() -> dict[str, str]:
    result = {}
    for line in (ROOT / "gradle.properties").read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if line and not line.startswith("#") and "=" in line:
            k, v = line.split("=", 1)
            result[k.strip()] = v.strip()
    return result


def compatible(compiler: str, minimum: str, minecraft: str) -> bool:
    a = NEO.fullmatch(compiler)
    b = NEO.fullmatch(minimum)
    if not a or not b:
        return False
    series = minecraft.split(".")
    if len(series) == 2:
        series.append("0")
    if len(series) != 3 or not all(c.isdecimal() for c in series):
        return False
    expected = tuple(int(v) for v in series)
    if tuple(int(a[i]) for i in (1, 2, 3)) != expected:
        return False
    if tuple(int(b[i]) for i in (1, 2, 3)) != expected:
        return False
    if int(a[4]) < int(b[4]):
        return False
    if int(a[4]) == int(b[4]) and a[5] and (not b[5] or a[5] != b[5]):
        return False
    return True


class Compatibility(unittest.TestCase):
    def test_actual_compiler_meets_minimum(self) -> None:
        p = properties()
        self.assertEqual(p["minecraft_version_range"], f"[{p['minecraft_version']}]")
        self.assertTrue(compatible(p["neo_version"], p["neo_min_version"], p["minecraft_version"]))

    def test_newer_maven_release_does_not_invalidate_valid_pin(self) -> None:
        self.assertTrue(compatible("26.1.2.117", "26.1.2.100", "26.1.2"))
        self.assertTrue(compatible("26.3.0.76", "26.3.0.64-beta", "26.3"))
        self.assertFalse(compatible("26.1.2.99", "26.1.2.100", "26.1.2"))
        self.assertFalse(compatible("26.1.2.117", "26.2.0.90", "26.2"))
        self.assertFalse(compatible("26.3.0.64-beta", "26.3.0.64", "26.3"))

    def test_gradle_uses_local_compatibility_not_maven_latest(self) -> None:
        code = (ROOT / "build.gradle").read_text(encoding="utf-8")
        self.assertIn("tasks.register('verifyNeoForgeCompatibility')", code)
        self.assertIn("dependsOn tasks.named('verifyNeoForgeCompatibility')", code)
        self.assertNotIn("verifyLatestNeoForge", code)
        self.assertNotIn("maven-metadata.xml", code)

    def test_dependency_minimum_is_open_ended(self) -> None:
        template = (ROOT / "src/main/templates/META-INF/neoforge.mods.toml").read_text(encoding="utf-8")
        self.assertIn('versionRange="[${neo_min_version},)"', template)
        self.assertIn('versionRange="${minecraft_version_range}"', template)


if __name__ == "__main__":
    unittest.main()
