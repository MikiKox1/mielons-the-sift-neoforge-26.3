"""Offline verification of 10 /locate biome targets in saved Anvil FULL chunks.

Only reads the Prime2 disposable world after graceful server shutdown.
"""

from pathlib import Path
import json
from verify_saved_chunks_2612 import chunk_from_region, WORKTREE
from generate_verified_biome_chunks_2612 import LOCATED

REPORT = WORKTREE / 'logs/phase22-located-biome-full-chunks-2612.json'


def main():
    result = []
    failed = []
    for biome, (x, y, z) in LOCATED.items():
        chunk = chunk_from_region(x // 16, z // 16)
        # The Anvil palette covers all Y-sections of a column, including target Y.
        # Do not claim exact y-level parity without sampling biome palette indexes.
        found = biome in chunk['biomes']
        full = chunk['status'].lower() in ('minecraft:full', 'full')
        data_ok = chunk['data_version'] == 4790
        row = {'biome': biome, 'pos': [x, y, z], 'chunk': [x // 16, z // 16],
               'contains_target_biome': found, 'is_full': full, 'data_version': chunk['data_version'],
               'observed_biomes': chunk['biomes']}
        result.append(row)
        if not (found and full and data_ok):
            failed.append(biome)
        print('PASS' if found and full and data_ok else 'FAIL', biome,
              'FULL=' + str(full), 'DV=' + str(chunk['data_version']),
              'palette=' + ','.join(sorted(chunk['biomes'])))
    REPORT.parent.mkdir(parents=True, exist_ok=True)
    REPORT.write_text(json.dumps({'checked': len(result), 'passed': len(result) - len(failed),
                                  'failures': failed, 'rows': result,
                                  'limit': 'Any section palette match, not exact target quart-biome selection; manual biome visuals pending'},
                                 ensure_ascii=False, indent=2), encoding='utf-8')
    if failed:
        raise SystemExit('Missing target biome(s) in serialized columns: ' + ','.join(failed))
    print('All 10 independently located biome columns PASS; report:', REPORT)


if __name__ == '__main__':
    main()
