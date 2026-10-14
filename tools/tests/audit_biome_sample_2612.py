"""Read-only sample of already saved biome palettes across Sift Anvil regions.

The sample is not a frequency test for all 10 registered biomes: coordinates,
seed and region availability influence results. No world data is modified.
"""

from collections import Counter
import gzip
import io
import json
from pathlib import Path
import struct
import zlib

import nbtlib

ROOT = Path(__file__).resolve().parents[2]
REGION_DIR = ROOT / 'run/server-26.1.2.117-port-2612-worldgen/qa-natural-worldgen/dimensions/the_sift/the_sift/region'
REPORT = ROOT / 'logs/phase18-all-saved-biomes-2612.json'
MAX_CHUNKS = 8192


def chunks_from_region(path):
    data = path.read_bytes()
    if len(data) < 8192:
        return
    for index in range(1024):
        entry = data[index * 4:index * 4 + 4]
        sector = int.from_bytes(entry[:3], 'big')
        count = entry[3]
        if sector == 0 or count == 0:
            continue
        start = sector * 4096
        length = struct.unpack('>I', data[start:start + 4])[0]
        if length < 2 or length > count * 4096 - 4:
            continue
        compression = data[start + 4]
        payload = data[start + 5:start + 4 + length]
        if compression == 2:
            nbt = zlib.decompress(payload)
        elif compression == 1:
            nbt = gzip.decompress(payload)
        elif compression == 3:
            nbt = payload
        else:
            continue
        try:
            compound = nbtlib.File.parse(io.BytesIO(nbt))
            biomes = set()
            for section in compound.get('sections', []):
                biomes.update(str(x) for x in section.get('biomes', {}).get('palette', []))
        except Exception as error:
            yield {'region': path.name, 'error': str(error)[:180]}
        else:
            yield {
                'region': path.name, 'x': int(compound.get('xPos', 0)),
                'z': int(compound.get('zPos', 0)),
                'biomes': sorted(biomes), 'status': str(compound.get('Status', 'unknown')),
            }


def main():
    assert REGION_DIR.is_dir()
    sample = []
    for region in sorted(REGION_DIR.glob('r.*.*.mca')):
        for chunk in chunks_from_region(region):
            sample.append(chunk)
            if len(sample) >= MAX_CHUNKS:
                break
        if len(sample) >= MAX_CHUNKS:
            break
    counts = Counter(biome for chunk in sample for biome in chunk.get('biomes', ()))
    statuses = Counter(chunk.get('status', '?') for chunk in sample)
    full = [chunk for chunk in sample if 'full' in chunk.get('status', '').lower()]
    full_biomes = Counter(biome for chunk in full for biome in chunk.get('biomes', ()))
    failures = [chunk for chunk in sample if 'error' in chunk]
    report = {
        'scope': f'all available region files (max {MAX_CHUNKS} persisted chunks)',
        'sampled_chunks': len(sample),
        'unique_biomes': dict(counts),
        'chunk_statuses': dict(statuses),
        'full_chunks': len(full),
        'full_chunk_biomes': dict(full_biomes),
        'parse_errors': failures[:25],
        'warning': 'Not a population/distribution guarantee; manual 10-biome validation remains pending.',
    }
    REPORT.parent.mkdir(parents=True, exist_ok=True)
    REPORT.write_text(json.dumps(report, indent=2, ensure_ascii=False), encoding='utf-8')
    print('chunk samples:', len(sample), 'errors:', len(failures))
    print('distinct biomes:', len(counts))
    print('chunk statuses:', dict(statuses))
    print('FULL chunks:', len(full), 'FULL biomes:', dict(full_biomes))
    for biome, count in counts.most_common():
        print(' ', biome, count, 'sampled chunks containing biome')
    print('report:', REPORT)
    assert sample and not failures, 'No saved biome chunks or NBT parsing errors'


if __name__ == '__main__':
    main()
