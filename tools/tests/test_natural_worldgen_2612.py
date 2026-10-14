"""Bounded fresh-chunk generation for The Sift MC26.1.2.

Uses a separate disposable Gradle run directory and world. Tests real chunk
loading/generation on several coordinates, live Sift clock and save. This does
NOT claim all biome proportions, vanilla structure frequency, visual sky or
gameplay, and never accesses a user's world or character.
"""

import json
from pathlib import Path
import sys

HERE = Path(__file__).resolve().parent
WORKTREE = HERE.parents[1]
SERVER = WORKTREE / 'run/server-26.1.2.117-port-2612-worldgen'
REPORT = WORKTREE / 'logs/phase17-natural-worldgen-2612.json'
sys.path.insert(0, str(HERE / '_qa_vendor'))
from mcrcon import MCRcon


def load_config():
    text = (SERVER / 'server.properties').read_text(encoding='utf-8')
    data = dict(line.split('=', 1) for line in text.splitlines() if '=' in line and not line.startswith('#'))
    for field, expected in (
        ('server-ip', '127.0.0.1'), ('server-port', '25574'),
        ('rcon.port', '25584'), ('enable-rcon', 'true'),
        ('online-mode', 'true'), ('level-name', 'qa-natural-worldgen'),
    ):
        assert data.get(field) == expected, f'Unsafe or incorrect environment: {field}'
    assert len(data.get('rcon.password', '')) >= 20
    return data


def main():
    config = load_config()
    prefix = 'execute in the_sift:the_sift run '
    tests = [('clock', prefix + 'time query time')]
    points = [(0, 0), (256, 256), (-256, -256), (512, -384), (-512, 640)]
    for x, z in points:
        tests.append((f'generate-{x}-{z}', prefix + f'forceload add {x} {z}'))
        tests.append((f'release-{x}-{z}', prefix + f'forceload remove {x} {z}'))
    tests.append(('save', 'save-all flush'))
    response_log = []
    with MCRcon('127.0.0.1', config['rcon.password'], port=25584) as client:
        try:
            for label, command in tests:
                response = client.command(command)
                response_log.append({'gate': label, 'command': command, 'response': response})
                print(label + ':', response[:250])
        finally:
            REPORT.parent.mkdir(exist_ok=True, parents=True)
            REPORT.write_text(json.dumps({
                'scope': 'new independent MC26.1.2 NeoForge26.1.2.117 world only',
                'points': points, 'tests': response_log,
                'claims_excluded': ['manual biome visuals', 'all biome distribution',
                                    'automatic portal occurrence', 'shaders', 'multiplayer'],
            }, indent=2, ensure_ascii=False), encoding='utf-8')
            try:
                client.command('stop')
                print('Dedicated server: graceful stop requested')
            except (ConnectionError, OSError, TimeoutError):
                print('RCON closed during graceful stop')

    assert len(response_log) == len(tests)
    assert 'Clock the_sift:the_sift' in response_log[0]['response'], 'Sift clock missing'
    for result in response_log[1:-1]:
        expected = 'Marked chunk' if result['gate'].startswith('generate-') else 'Unmarked chunk'
        assert expected in result['response'], f'Failed to handle fresh chunk: {result["gate"]}: {result["response"]}'
    assert 'Saved the game' in response_log[-1]['response']
    print('PASS: 5 fresh locations, Sift clock, 5 forceload releases, save; report', REPORT)


if __name__ == '__main__':
    main()
