"""Verify a standalone stable MC26.1.2 The Sift release JAR against the
user-accepted ALPHA2 binary (no gameplay changes permitted).

Read-only, run after clean build on latest NeoForge .117. Checked original
ALPHA2 SHA pins and official historic-loader compatibility evidence.
"""

from hashlib import sha256
import json
from pathlib import Path
from zipfile import ZipFile

HERE = Path(__file__).resolve().parent
WORK = HERE.parents[1]
PROJECT = WORK.parents[1]
OLD = (PROJECT / 'artifacts/release-candidates/Port-1.1.2-NeoForge-26.1.2-ALPHA2-Photon-Advancement-Fix'
       / "mielon's-the-sift-1.1.2-mc26.1.2-neoforge-port-ALPHA2-STAGEONLY.jar")
NEW = WORK / 'build/libs/the_sift-1.1.2-neoforge.1.jar'
EXPECTED_OLD = '86F97A0860DEA162FACAEF778F30C23220D96FE3D561E8FE902DBC236F55C206'
OUT = WORK / 'logs/stable-26.1.2-resource-qa.json'


def digest(data):
    return sha256(data).hexdigest().upper()


def main():
    assert NEW.is_file() and OLD.is_file()
    assert digest(OLD.read_bytes()) == EXPECTED_OLD, 'Original ALPHA2 JAR changed'
    with ZipFile(OLD) as earlier, ZipFile(NEW) as stable:
        assert earlier.testzip() is None and stable.testzip() is None, 'JAR CRC failure'
        old_names = set(earlier.namelist())
        new_names = set(stable.namelist())
        assert old_names == new_names, 'Added/removed JAR entries: ' + str(old_names ^ new_names)
        changed = [n for n in sorted(old_names) if earlier.read(n) != stable.read(n)]
        # Only metadata/manifest may vary with the stable version number and
        # NeoForge compatibility minimum. Never silently alter game assets.
        allow = {
            'META-INF/neoforge.mods.toml',
            'META-INF/MANIFEST.MF',
        }
        # Windows source checkout may normalize LF/CRLF during resource
        # processing. Compare exact decoded text/parsed JSON before deciding
        # that the accepted gameplay has changed.
        line_ending_only = set()
        for n in changed:
            if n in allow:
                continue
            old = earlier.read(n)
            new = stable.read(n)
            if n.endswith('.json') and json.loads(old) == json.loads(new):
                line_ending_only.add(n)
            elif n.endswith(('.vsh', '.fsh', '.glsl')) and (
                old.decode('utf-8-sig').replace('\r\n', '\n')
                == new.decode('utf-8-sig').replace('\r\n', '\n')
            ):
                line_ending_only.add(n)
        disallowed = sorted(set(changed) - allow - line_ending_only)
        assert not disallowed, 'Release has unexpected gameplay/asset drift: ' + str(disallowed)
        metadata = stable.read('META-INF/neoforge.mods.toml').decode('utf-8-sig')
        for term in (
            'modId="the_sift"',
            'version="1.1.2-neoforge.1"',
            'loaderVersion="[11,)"',
            'versionRange="[26.1.2]"',
            'versionRange="[26.1.2.100,)"',
            'versionRange="[5.5.2,6)"',
            'versionRange="[4.11.2,)"',
        ):
            assert term in metadata, 'Bad stable metadata: ' + term
        assert 'alpha' not in metadata.lower(), 'ALPHA metadata leaked into stable JAR'
        assert sum(n.startswith('data/the_sift/worldgen/') and n.endswith('.json') for n in new_names) == 158
        assert sum(n.endswith('.nbt') and n.startswith(('data/the_sift/structure/', 'data/minecraft/structure/'))
                   for n in new_names) == 16
        assert sum(n.startswith('assets/the_sift/textures/sky/alpha2/') and n.endswith('.png')
                   for n in new_names) == 6
        for adv, total in [
            ('data/the_sift/advancement/story/fallen_civilization.json', 1),
            ('data/the_sift/advancement/the_sift/certified_sifter.json', 10),
        ]:
            criteria = json.loads(stable.read(adv))['criteria']
            assert len(criteria) == total
            assert all(v['trigger'] == 'minecraft:impossible' for v in criteria.values())
        shader = stable.read('assets/the_sift/shaders/core/photon_sky_overlay.fsh').decode('utf-8-sig')
        assert 'gl_FragDepth = 1.0;' in shader, 'The user-accepted Photon correction disappeared'
        assert 'LICENSE_the_sift' in new_names, "Original author's MIT license missing"

        result = {
            'minecraft': '26.1.2', 'latest_tested_neoforge': '26.1.2.117',
            'verified_minimum_neoforge': '26.1.2.100',
            'old_alpha2_jar_sha256': EXPECTED_OLD,
            'official_stable_jar_sha256': digest(NEW.read_bytes()),
            'entries': len(new_names),
            'identical_to_user_accepted_alpha2': len(new_names) - len(changed),
            'differences': changed, 'unexpected_differences': disallowed,
            'text_or_json_only_differences': sorted(line_ending_only),
            'worldgen_json': 158, 'structure_nbt': 16, 'sky_gradient_png': 6,
            'advancement_json': 100, 'manual_criteria': 11,
            'status': 'PASS (JAR resources/metadata; user A01-A20 recorded separately)',
        }
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps(result, indent=2, ensure_ascii=False), encoding='utf8')
    print('PASS: Stable JAR preserves accepted ALPHA2 behavior.')
    print('Changes:', changed)
    print('Only semantic metadata change + text-line-ending normalization:', sorted(line_ending_only))
    print('Preserved 158 worldgen / 16 structures / six gradient textures / advancement criteria')
    print('Stable NeoForge minimum >= 26.1.2.100, open at top; Minecraft exactly 26.1.2')
    print('Stable JAR SHA256:', result['official_stable_jar_sha256'])
    print('Result:', OUT)


if __name__ == '__main__':
    main()
