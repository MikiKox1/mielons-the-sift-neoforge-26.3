"""Verify persisted FULL chunks and Sift biome palettes in disposable Anvil files.

Strictly offline/read-only; does not create, modify, load or lock a world.
Reads only the dedicated stage world previously created with qaServerLabel=worldgen.
"""

from collections import Counter
import json
from pathlib import Path
import struct
import zlib
import gzip
import io

import nbtlib

WORKTREE = Path(__file__).resolve().parents[2]
REGIONS = WORKTREE / 'run/server-26.1.2.117-port-2612-worldgen/qa-natural-worldgen/dimensions/the_sift/the_sift/region'
REPORT = WORKTREE / 'logs/phase18-saved-chunk-validation-2612.json'
POINTS = ((0, 0), (16, 16), (-16, -16), (32, -24), (-32, 40))


def chunk_from_region(chunk_x, chunk_z):
    path = REGIONS / f'r.{chunk_x // 32}.{chunk_z // 32}.mca'
    assert path.is_file(), f'Missing region for {chunk_x},{chunk_z}: {path}'
    data = path.read_bytes()
    assert len(data) >= 8192, f'Bad region header: {path}'
    index = (chunk_x & 31) + ((chunk_z & 31) << 5)
    slot = data[4 * index:4 * index + 4]
    start = int.from_bytes(slot[:3], 'big') * 4096
    sectors = slot[3]
    assert start >= 8192 and sectors > 0, f'Chunk not saved: {chunk_x},{chunk_z}'
    length = struct.unpack('>I', data[start:start + 4])[0]
    assert 2 <= length <= sectors * 4096 - 4, f'Invalid chunk length {chunk_x},{chunk_z}'
    compression = data[start + 4]
    payload = data[start + 5:start + 4 + length]
    if compression == 2:
        uncompressed = zlib.decompress(payload)
    elif compression == 1:
        uncompressed = gzip.decompress(payload)
    elif compression == 3:
        uncompressed = payload
    else:
        raise AssertionError(f'Unsupported Anvil compression {compression}: {path}')
    chunk = nbtlib.File.parse(io.BytesIO(uncompressed))
    x, z = int(chunk['xPos']), int(chunk['zPos'])
    assert (x, z) == (chunk_x, chunk_z), f'Wrong chunk data location {(x,z)} != {(chunk_x,chunk_z)}'
    biome_names = Counter()
    for section in chunk.get('sections', []):
        for item in section.get('biomes', {}).get('palette', []):
            biome_names[str(item)] += 1
    return {
        'chunk_x': x, 'chunk_z': z, 'source': str(path),
        'data_version': int(chunk.get('DataVersion', 0)),
        'status': str(chunk.get('Status', '?')),
        'sections': len(chunk.get('sections', [])),
        'biomes': dict(biome_names),
        'compressed_payload_bytes': len(payload),
    }


def main():
    assert REGIONS.is_dir(), 'Only read the known disposable Sift region directory'
    checked = [chunk_from_region(x, z) for x, z in POINTS]
    for row in checked:
        print(row['chunk_x'], row['chunk_z'], 'DV', row['data_version'],
              'status', row['status'], 'biomes', ', '.join(sorted(row['biomes']))[:180])
    failures = []
    for row in checked:
        if row['data_version'] <= 0:
            failures.append(f'Bad DataVersion at {row["chunk_x"]},{row["chunk_z"]}')
        if 'full' not in row['status'].lower():
            failures.append(f'Chunk not FULL at {row["chunk_x"]},{row["chunk_z"]}')
        if not any(name.startswith('the_sift:') for name in row['biomes']):
            failures.append(f'No Sift biome palette at {row["chunk_x"]},{row["chunk_z"]}')
    REPORT.parent.mkdir(parents=True, exist_ok=True)
    REPORT.write_text(json.dumps({
        'source': 'disposable MC26.1.2 NeoForge26.1.2.117 world after graceful save/stop',
        'status': 'PASS' if not failures else 'FAIL', 'chunks': checked, 'failures': failures,
        'limitations': 'Sampled 5 chunk columns only. Not a visual, structural distribution or performance proof.',
    }, indent=2, ensure_ascii=False), encoding='utf-8')
    print('RESULT:', 'PASS' if not failures else 'FAIL', 'sampled', len(checked), 'saved chunk columns')
    print('report', REPORT)
    if failures:
        print('Failures:', failures)
        raise SystemExit(1)


if __name__ == '__main__':
    main()
