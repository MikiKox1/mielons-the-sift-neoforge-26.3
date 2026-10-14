"""Run a few command-level smoke checks on a *disposable* Easy Magic server.

Requires the isolated Gradle runServer launched with -PqaServerLabel=easymagic.
Never contacts game servers or modifies any user worlds. The per-run password
remains in a gitignored temporary server.properties file; it is not logged.
"""

import json
from pathlib import Path
import sys

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
SERVER_DIR = ROOT / 'run/server-26.1.2.117-port-2612-easymagic'
REPORT = ROOT / 'logs/phase13-easymagic-smoke-report.json'
sys.path.insert(0, str(HERE / '_qa_vendor'))
from mcrcon import MCRcon


def main():
    config_file = SERVER_DIR / 'server.properties'
    assert config_file.is_file(), 'Expected separate stage-world config'
    config = dict(line.split('=', 1) for line in config_file.read_text(encoding='utf-8').splitlines()
                  if '=' in line and not line.startswith('#'))
    expected = {
        'server-ip': '127.0.0.1', 'server-port': '25573',
        'rcon.port': '25583', 'enable-rcon': 'true', 'online-mode': 'true'
    }
    for key, value in expected.items():
        assert config.get(key) == value, f'Server config mismatch: {key}'
    assert len(config.get('rcon.password', '')) >= 20, 'Missing secret'
    commands = [
        ('clock', 'execute in the_sift:the_sift run time query time'),
        ('force-chunk', 'execute in the_sift:the_sift run forceload add 0 0'),
        ('sift-template', 'execute in the_sift:the_sift run place template the_sift:main_portal 0 150 0'),
        ('release-chunk', 'execute in the_sift:the_sift run forceload remove 0 0'),
        ('save', 'save-all flush')
    ]
    results = []
    with MCRcon('127.0.0.1', config['rcon.password'], port=25583) as server:
        try:
            for gate, command in commands:
                result = server.command(command)
                results.append({'gate': gate, 'response': result, 'command': command})
                print(gate, result[:300])
        finally:
            REPORT.parent.mkdir(parents=True, exist_ok=True)
            REPORT.write_text(json.dumps({
                'scope': 'Prime2 disposable Easy Magic+PuzzlesLib MC26.1.2 server',
                'tests': results, 'note': 'No GUI, enchantment or network peer interaction validated'
            }, indent=2, ensure_ascii=False), encoding='utf-8')
            try:
                server.command('stop')
                print('Graceful stop requested')
            except (OSError, ConnectionError, TimeoutError):
                print('Server closed RCON connection during stop')
    assert len(results) == len(commands)
    assert 'Clock the_sift:the_sift' in results[0]['response'], 'Sift clock failed'
    assert 'Marked chunk' in results[1]['response'], 'Force-load setup failed'
    assert 'Loaded template' in results[2]['response'], 'Portal template failed'
    assert 'Unmarked chunk' in results[3]['response'], 'Chunk release failed'
    assert 'Saved the game' in results[4]['response'], 'Save failed'
    print('Smoke check PASS; report:', REPORT)


if __name__ == '__main__':
    main()
