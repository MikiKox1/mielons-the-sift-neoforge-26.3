"""Regression checks for NeoForge compiler/minimum-loader policy.

Compatible compiler versions remain valid even when newer loader builds exist.
"""

from __future__ import annotations

from pathlib import Path
import re
import unittest


ROOT = Path(__file__).resolve().parents[2]
LOADER_RE = re.compile(r"^(\d+)\.(\d+)\.(\d+)\.(\d+)(?:-([A-Za-z0-9.-]+))?$")


def project_properties() -> dict[str, str]:
    result: dict[str, str] = {}
    for line in (ROOT / "gradle.properties").read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if line and not line.startswith("#") and "=" in line:
            key, value = line.split("=", 1)
            result[key.strip()] = value.strip()
    return result


def compatible_loader(compilation: str, minimum: str, minecraft: str) -> bool:
    compiled, lower = LOADER_RE.fullmatch(compilation), LOADER_RE.fullmatch(minimum)
    if not compiled or not lower:
        return False
    expected = minecraft.split(".")
    if len(expected) != 2 or not all(part.isdigit() for part in expected):
        return False
    mc_parts = tuple(int(v) for v in expected)
    for match in (compiled, lower):
        if (int(match[1]), int(match[2])) != mc_parts or int(match[3]) != 0:
            return False
    actual_build, minimum_build = int(compiled[4]), int(lower[4])
    if actual_build < minimum_build:
        return False
    if actual_build == minimum_build and compiled[5]:
        if not lower[5] or compiled[5] != lower[5]:
            return False
    return True


class NeoForgeCompilerPolicy(unittest.TestCase):
    def test_compiler_loader_satisfies_declared_minimum(self) -> None:
        props = project_properties()
        self.assertEqual(
            props["minecraft_version_range"], f"[{props['minecraft_version']}]"
        )
        self.assertTrue(
            compatible_loader(
                props["neo_version"],
                props["neo_min_version"],
                props["minecraft_version"],
            ),
            "The compiler loader must match the MC line and the declared minimum",
        )

    def test_older_compatible_compilation_is_valid(self) -> None:
        self.assertTrue(compatible_loader("26.3.0.76", "26.3.0.64-beta", "26.3"))
        self.assertTrue(compatible_loader("26.2.0.90", "26.2.0.90", "26.2"))
        self.assertFalse(compatible_loader("26.3.0.63", "26.3.0.64-beta", "26.3"))
        self.assertFalse(compatible_loader("26.2.0.89", "26.2.0.90", "26.2"))
        self.assertFalse(compatible_loader("26.2.0.99", "26.3.0.64-beta", "26.3"))
        self.assertFalse(compatible_loader("26.3.0.64-beta", "26.3.0.64", "26.3"))

    def test_gradle_checks_compatibility_not_latest(self) -> None:
        build = (ROOT / "build.gradle").read_text(encoding="utf-8")
        self.assertIn("tasks.register('verifyNeoForgeCompatibility')", build)
        self.assertIn("dependsOn tasks.named('verifyNeoForgeCompatibility')", build)
        self.assertNotIn("verifyLatestNeoForge", build)
        self.assertNotIn("maven-metadata.xml", build)

    def test_packaged_version_range_is_open_ended(self) -> None:
        template = (
            ROOT / "src/main/templates/META-INF/neoforge.mods.toml"
        ).read_text(encoding="utf-8")
        self.assertIn('versionRange="[${neo_min_version},)"', template)
        self.assertIn('versionRange="${minecraft_version_range}"', template)


if __name__ == "__main__":
    unittest.main()
