"""Generate full chunks at coordinates *actually returned* by /locate biome.

Operates only in the previously created disposable Prime2 worldgen stage.
Never changes a player's world, inventory or character. The server is stopped
and all force-load tickets are released after the test.
"""

from pathlib import Path
import json
import sys
import time

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
STAGE = ROOT / 'run/server-26.1.2.117-port-2612-worldgen'
REPORT = ROOT / 'logs/phase22-located-biome-chunk-generation-2612.json'
sys.path.insert(0, str(HERE / '_qa_vendor'))
from mcrcon import MCRcon

LOCATED = {
    'the_sift:sift_wastes': (0, 96, 0),
    'the_sift:overgrown_clearing': (-192, 96, 64),
    'the_sift:overgrown_forest': (352, 96, -608),
    'the_sift:overgrown_forest_slopes': (-64, 96, 992),
    'the_sift:overgrown_slopes': (-384, 96, 160),
    'the_sift:siftslate_slopes': (-416, 96, 256),
    'the_sift:ichor_snowy_peaks': (-448, 96, 128),
    'the_sift:siftslate_peaks': (-448, 96, 224),
    'the_sift:overgrown_peaks': (-416, 96, 160),
    'the_sift:sift_deep_dark': (-416, 32, 160),
}


def main():
    config = dict(x.split('=', 1) for x in (STAGE / 'server.properties').read_text(encoding='utf-8').splitlines()
                  if '=' in x and not x.startswith('#'))
    required = {'server-ip': '127.0.0.1', 'server-port': '25574', 'rcon.port': '25584',
                'online-mode': 'true', 'enable-rcon': 'true', 'level-name': 'qa-natural-worldgen'}
    for field, expected in required.items():
        assert config.get(field) == expected, 'Unsafe or mismatched test-server setting: ' + field
    assert len(config.get('rcon.password', '')) >= 20

    point_pairs = sorted({(x, z) for x, _, z in LOCATED.values()})
    events = []
    with MCRcon('127.0.0.1', config['rcon.password'], port=25584, timeout=30) as client:
        client.socket.settimeout(30)
        try:
            prefix = 'execute in the_sift:the_sift run '
            for x, z in point_pairs:
                command = prefix + f'forceload add {x} {z}'
                response = client.command(command)
                events.append({'gate': 'load', 'x': x, 'z': z, 'response': response})
                assert 'Marked chunk' in response or 'already force loaded' in response.lower(), response
                print('load', x, z, response[:120].strip())
            time.sleep(3)
            first_save = client.command('save-all flush')
            events.append({'gate': 'save-after-load', 'response': first_save})
            assert 'Saved the game' in first_save, first_save
            for x, z in point_pairs:
                command = prefix + f'forceload remove {x} {z}'
                response = client.command(command)
                events.append({'gate': 'release', 'x': x, 'z': z, 'response': response})
                assert 'Unmarked chunk' in response, response
            second_save = client.command('save-all flush')
            events.append({'gate': 'save-after-release', 'response': second_save})
            assert 'Saved the game' in second_save, second_save
        finally:
            REPORT.parent.mkdir(parents=True, exist_ok=True)
            REPORT.write_text(json.dumps({
                'scope': 'existing disposable 26.1.2.117 world only',
                'point_source': 'phase20-locate-biomes-2612.json',
                'biomes': {k: list(v) for k, v in LOCATED.items()},
                'unique_columns': len(point_pairs), 'events': events,
            }, ensure_ascii=False, indent=2), encoding='utf-8')
            try:
                print('stop:', client.command('stop')[:150].strip())
            except (OSError, ConnectionError, TimeoutError):
                print('RCON connection closed during stop; verify server log')
    print('PASS requested 10 target biomes through', len(point_pairs), 'distinct chunk columns; report:', REPORT)


if __name__ == '__main__':
    main()
