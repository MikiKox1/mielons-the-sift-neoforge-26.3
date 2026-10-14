"""Independent ALPHA2 regression of a frozen ALPHA1 package.

ALPHA1 remains immutable. This script does not modify any original, world or
profile; it compares binary JAR entries and verifies exact intended deltas:
one GPU shader fix, two advancement JSON, one server advancement callback.
"""

from pathlib import Path
import hashlib
import json
from zipfile import ZipFile

WORKTREE = Path(__file__).resolve().parents[2]
ROOT = WORKTREE.parents[1]
ORIGINAL = ROOT / 'artifacts/release-candidates/Port-1.1.2-NeoForge-26.1.2-ALPHA1'
ALPHA1 = ORIGINAL / "mielon's-the-sift-1.1.2-mc26.1.2-neoforge-port-ALPHA1-STAGEONLY.jar"
ALPHA2 = WORKTREE / 'build/libs/the_sift-1.1.2-neoforge.1-alpha.2.jar'
ALPHA1_SHA = 'AF61B94C81921E7912BAA076F41D3814571A2397B1B0C47896D1E38038C92153'
CHANGED_CONTENT = {
    'data/the_sift/advancement/story/fallen_civilization.json',
    'data/the_sift/advancement/the_sift/certified_sifter.json',
    'assets/the_sift/shaders/core/photon_sky_overlay.fsh',
}
PACKAGING_ONLY_EOL = {
    'assets/the_sift/shaders/core/photon_sky_overlay.vsh',
    'the_sift.mixins.json',
}


def sha(b):
    return hashlib.sha256(b).hexdigest().upper()


def main():
    assert ALPHA1.is_file() and sha(ALPHA1.read_bytes()) == ALPHA1_SHA, 'Frozen ALPHA1 changed'
    assert ALPHA2.is_file(), 'ALPHA2 JAR not built'
    with ZipFile(ALPHA1) as old, ZipFile(ALPHA2) as new:
        assert old.testzip() is None and new.testzip() is None
        entries_old = {n for n in old.namelist() if n.endswith(('.json', '.nbt', '.png', '.mcmeta', '.vsh', '.fsh', '.glsl', '.ogg', '.lang', '.txt'))}
        entries_new = {n for n in new.namelist() if n.endswith(('.json', '.nbt', '.png', '.mcmeta', '.vsh', '.fsh', '.glsl', '.ogg', '.lang', '.txt'))}
        assert entries_old == entries_new, (
            'New/removed data or shaders: ' + str((entries_old ^ entries_new))
        )
        actual_changes = {n for n in entries_old if old.read(n) != new.read(n)}
        assert actual_changes == CHANGED_CONTENT | PACKAGING_ONLY_EOL, (
            'Unexpected asset/data drift: ' + str(actual_changes)
        )
        for entry in PACKAGING_ONLY_EOL:
            before = old.read(entry).decode('utf-8-sig').replace('\r\n', '\n')
            after = new.read(entry).decode('utf-8-sig').replace('\r\n', '\n')
            assert before == after, f'Unintended substantive change to {entry}'
        assert json.loads(old.read('the_sift.mixins.json')) == json.loads(new.read('the_sift.mixins.json'))
        meta = new.read('META-INF/neoforge.mods.toml').decode('utf8')
        for needle in ('version="1.1.2-neoforge.1-alpha.2"',
                       'versionRange="[26.1.2]"',
                       'versionRange="[26.1.2.117,)"',
                       'versionRange="[5.5.2,6)"'):
            assert needle in meta, 'Metadata missing: ' + needle
        shader = new.read('assets/the_sift/shaders/core/photon_sky_overlay.fsh').decode('utf8')
        assert shader.count('gl_FragDepth = 1.0;') == 1
        renderer = (WORKTREE / 'src/client/java/mielon/thesift/client/render/SiftProceduralSkyRenderer.java').read_text(encoding='utf8')
        assert 'new DepthStencilState(CompareOp.EQUAL, false)' in renderer
        assert 'photonPostFinal ? PHOTON_POST_FINAL_SKY_PIPELINE : TEXTURED_SKY_PIPELINE' in renderer
        assert 'if (SiftShaderCompat.isShaderPackInUse() && SiftShaderCompat.isPhotonShaderPackSelected())' in renderer
        for target in [
            'data/the_sift/advancement/story/fallen_civilization.json',
            'data/the_sift/advancement/the_sift/certified_sifter.json'
        ]:
            obj = json.loads(new.read(target))
            assert all(x['trigger'] == 'minecraft:impossible' for x in obj['criteria'].values())
        encoded = new.read('mielon/thesift/advancement/ModAdvancements.class')
        assert b'certified_sifter' in encoded and b'fallen_civilization' in encoded
        assert len(encoded) > 300, 'Advancement callback class unexpectedly truncated'
        worldgen = [n for n in entries_new if n.startswith('data/the_sift/worldgen/') and n.endswith('.json')]
        assert len(worldgen) == 158
        templates = [n for n in entries_new if n.endswith('.nbt') and n.startswith(('data/the_sift/structure/', 'data/minecraft/structure/'))]
        assert len(templates) == 16
        textures = [n for n in entries_new if n.startswith('assets/the_sift/textures/sky/alpha2/') and n.endswith('.png')]
        assert len(textures) == 6
        print('Data/asset byte parity:', len(entries_new) - len(actual_changes), 'identical entries')
        print('Only newline-format changes:', ', '.join(sorted(PACKAGING_ONLY_EOL)))
        print('Exactly intended semantic changes:', ', '.join(sorted(CHANGED_CONTENT)))
        print('Worldgen 158/158, structures 16/16, sky textures 6/6 unchanged')
        print('ALPHA1 SHA256:', ALPHA1_SHA)
        print('ALPHA2 SHA256:', sha(ALPHA2.read_bytes()))
        print('ZIP CRC and metadata PASS')


if __name__ == '__main__':
    main()
