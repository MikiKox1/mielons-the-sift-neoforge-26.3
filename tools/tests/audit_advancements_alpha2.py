"""Read-only full advancement audit versus official Mielon 1.1.2 NeoForge 1.21.1.

The two location achievements use their original IMPOSSIBLE criteria and are
awarded only by the matching upstream server tick callbacks. Recipe additions
and target-Minecraft vanilla mob criteria are audited separately.
"""

from pathlib import Path
import json
import re
from zipfile import ZipFile

WORKTREE = Path(__file__).resolve().parents[2]
PROJECT = WORKTREE.parents[1]
ORIGINAL = PROJECT / "backports/26.1.2-1.1.2-prep/reference/mielon's-the-sift-1.1.2-mc1.21.1-neo.jar"
ROOT = WORKTREE / "src/main/resources/data"
JAVA = WORKTREE / "src/main/java/mielon/thesift/advancement/ModAdvancements.java"


def main():
    with ZipFile(ORIGINAL) as jar:
        original = {n.removeprefix('data/'): json.loads(jar.read(n).decode('utf-8-sig'))
                    for n in jar.namelist() if '/advancement/' in n and n.endswith('.json')}
    current = {p.relative_to(ROOT).as_posix(): json.loads(p.read_text(encoding='utf-8-sig'))
               for p in ROOT.rglob('advancement/**/*.json')}
    print('advancements current', len(current), 'original', len(original))
    assert len(current) == 100 and len(original) == 98
    assert set(original) <= set(current), 'Missing original advancement'
    additional = set(current) - set(original)
    assert additional == {
        'the_sift/advancement/recipes/overgrown_willow_shelf.json',
        'the_sift/advancement/recipes/siftite_spear_smithing.json',
    }, f'Unexpected new advancements: {additional}'

    differences = {}
    for name in sorted(original):
        a, b = original[name], current[name]
        changed = {key for key in a.keys() | b.keys() if a.get(key) != b.get(key)}
        if changed:
            differences[name] = sorted(changed)
    expected_display_diff = {
        'the_sift/advancement/the_sift/root.json',
        'the_sift/advancement/the_sift/food_chain.json',
        'the_sift/advancement/the_sift/like_father_and_son.json',
        'the_sift/advancement/the_sift/pitch_perfect.json',
    }
    expected_vanilla_mobs = {
        'minecraft/advancement/adventure/kill_a_mob.json',
        'minecraft/advancement/adventure/kill_all_mobs.json',
    }
    assert set(differences) == expected_display_diff | expected_vanilla_mobs, (
        'Unexplained change in advancement behavior: ' + str(differences)
    )
    for name in expected_display_diff:
        assert differences[name] == ['display'], (name, differences[name])

    target_paths = {
        'fallen_civilization': 'the_sift/advancement/story/fallen_civilization.json',
        'certified_sifter': 'the_sift/advancement/the_sift/certified_sifter.json',
    }
    for short_name, path in target_paths.items():
        adv = current[path]
        assert adv == original[path], f'{short_name}: original predicate semantics lost'
        assert adv['criteria'] and all(
            criterion['trigger'] == 'minecraft:impossible'
            for criterion in adv['criteria'].values()
        ), f'{short_name}: login auto-unlock regression'
    certified = current[target_paths['certified_sifter']]
    assert len(certified['criteria']) == 10
    assert len(certified['requirements']) == 10
    assert {criterion for req in certified['requirements'] for criterion in req} == set(certified['criteria'])

    java = JAVA.read_text(encoding='utf-8')
    assert 'ServerTickEvents.END_SERVER_TICK.register' in java
    assert '(server.getTickCount() & 15) != 0' in java
    assert 'getStructureWithPieceAt(player.blockPosition(), ancientCity.value()).isValid()' in java
    assert 'level.dimension().equals(TheSiftDimension.LEVEL_KEY)' in java
    assert 'level.getBiome(player.blockPosition()).unwrapKey()' in java
    assert 'awardCriterion(player, FALLEN_CIVILIZATION, "ancient_city")' in java
    assert 'awardCriterion(player, CERTIFIED_SIFTER, criterion)' in java
    for criterion in certified['criteria']:
        assert 'Identifier.fromNamespaceAndPath("the_sift", "' + criterion + '")' in java

    for name, advancement in current.items():
        criteria = advancement.get('criteria', {})
        assert criteria, f'{name}: criteria empty'
        for req in advancement.get('requirements', []):
            assert req and all(criterion in criteria for criterion in req), f'{name}: invalid requirements {req}'
        if name.startswith('the_sift/advancement/'):
            parent = advancement.get('parent')
            if parent and parent.startswith('the_sift:'):
                parent_path = parent.removeprefix('the_sift:')
                assert ('the_sift/advancement/' + parent_path + '.json') in current, (
                    f'{name}: missing advancement parent {parent}'
                )
    print('PASS: all 100 advancement JSON parse, all 98 official preserved except 6 explained target changes,')
    print('      2 new recipes, exact manual original Fallen Civilization + 10 Certified Sifter criteria,')
    print('      16-tick server callback and every parent/requirement valid.')
    print('ALPHA1 source behavior DIFFERED, but ALPHA2 behavior now follows official bytecode.')


if __name__ == '__main__':
    main()
