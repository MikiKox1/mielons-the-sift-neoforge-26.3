"""Probe availability of selected Sift biomes via target Minecraft's own command.

This deliberately uses a disposable Minecraft 26.1.2 / NeoForge 26.1.2.117
world already created by Prime2. It does not visit user saves, move a player's
character, or claim that an observed target biome has the right appearance.
"""

import json
from pathlib import Path
import socket
import sys

HERE = Path(__file__).resolve().parent
WORKTREE = HERE.parents[1]
STAGE = WORKTREE / 'run/server-26.1.2.117-port-2612-worldgen'
REPORT = WORKTREE / 'logs/phase20-locate-biomes-2612.json'
sys.path.insert(0, str(HERE / '_qa_vendor'))
from mcrcon import MCRcon

BIOMES = (
    'the_sift:sift_wastes',
    'the_sift:overgrown_clearing',
    'the_sift:overgrown_forest',
    'the_sift:overgrown_forest_slopes',
    'the_sift:overgrown_slopes',
    'the_sift:siftslate_slopes',
    'the_sift:ichor_snowy_peaks',
    'the_sift:siftslate_peaks',
    'the_sift:overgrown_peaks',
    'the_sift:sift_deep_dark',
)


def main():
    settings = dict(line.split('=', 1) for line in (STAGE / 'server.properties').read_text(encoding='utf-8').splitlines()
                    if '=' in line and not line.startswith('#'))
    for key, value in {'server-ip': '127.0.0.1', 'server-port': '25574',
                       'rcon.port': '25584', 'enable-rcon': 'true',
                       'online-mode': 'true', 'level-name': 'qa-natural-worldgen'}.items():
        assert settings.get(key) == value, f'Wrong isolated QA configuration: {key}'
    assert len(settings.get('rcon.password', '')) >= 20
    reports = []
    try:
        with MCRcon('127.0.0.1', settings['rcon.password'], port=25584, timeout=30) as client:
            client.socket.settimeout(30)
            try:
                for biome in BIOMES:
                    command = f'execute in the_sift:the_sift positioned 0 96 0 run locate biome {biome}'
                    try:
                        response = client.command(command)
                        reports.append({'biome': biome, 'command': command, 'response': response})
                        print(biome, ':', response[:450])
                    except (OSError, socket.timeout) as error:
                        reports.append({'biome': biome, 'command': command, 'failure': type(error).__name__})
                        print('ERROR', biome, type(error).__name__)
                        break
            finally:
                try:
                    print('save:', client.command('save-all flush')[:240])
                    print('stop:', client.command('stop')[:240])
                except (OSError, socket.timeout):
                    print('Connection closed before stop confirmation — inspect server state')
    finally:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(json.dumps({
            'server': 'disposable 26.1.2.117 on 127.0.0.1:25574',
            'start': '0 96 0, Sift dimension',
            'results': reports,
            'limits': 'Locate tests biome availability, not foliage/ore density, natural structures, sky or visual correctness',
        }, ensure_ascii=False, indent=2), encoding='utf-8')
    print('Report:', REPORT)
    assert len(reports) == len(BIOMES) and not any('failure' in item for item in reports)
    assert all('located' in item['response'].lower() or 'at [' in item['response'].lower()
               for item in reports), 'Some biomes were not located; inspect exact responses'


if __name__ == '__main__':
    main()
