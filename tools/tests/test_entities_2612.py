"""Bounded server-side spawn/tick/save smoke of five The Sift mobs.

Operates exclusively on a disposable Prime 2 server bound to localhost; never
uses a user profile or save. This proves registration, basic spawn and several
server ticks, NOT AI balancing, animation, riding, drops or multiplayer sync.
"""

from pathlib import Path
import json
import os
import sys
import time

HERE = Path(__file__).resolve().parent
WORKTREE = HERE.parents[1]
TEST_LABEL = os.environ.get('PRIME2_QA_ENTITY_LABEL', 'entities')
PORTS = {'entities': (25575, 25585), 'entities2': (25576, 25586)}
assert TEST_LABEL in PORTS, 'Unapproved isolated world label'
SERVER_PORT, RCON_PORT = PORTS[TEST_LABEL]
SERVER = WORKTREE / f'run/server-26.1.2.117-port-2612-{TEST_LABEL}'
REPORT = WORKTREE / 'logs/phase19-entity-tick-2612.json'
sys.path.insert(0, str(HERE / '_qa_vendor'))
from mcrcon import MCRcon

MOBS = ('singer', 'echo_golem', 'dark_sniffer', 'blub', 'sifter')


def main():
    file = SERVER / 'server.properties'
    props = dict(line.split('=', 1) for line in file.read_text(encoding='utf-8').splitlines()
                 if '=' in line and not line.startswith('#'))
    for key, expected in {
        'server-ip': '127.0.0.1', 'server-port': str(SERVER_PORT),
        'rcon.port': str(RCON_PORT), 'enable-rcon': 'true',
        'online-mode': 'true', 'level-name': f'qa-{TEST_LABEL}'
    }.items():
        assert props.get(key) == expected, f'Unsafe/incorrect isolated config: {key}'
    assert len(props.get('rcon.password', '')) >= 20
    events = []
    prefix = 'execute in the_sift:the_sift run '
    with MCRcon('127.0.0.1', props['rcon.password'], port=RCON_PORT) as server:
        def run(label, command):
            response = server.command(command)
            events.append({'gate': label, 'command': command, 'response': response})
            print(label, response[:290])
            return response

        try:
            run('clock', prefix + 'time query time')
            setup = run('forceload', prefix + 'forceload add 0 0')
            assert 'Marked chunk' in setup, 'Sift chunk unavailable'
            for i, mob in enumerate(MOBS):
                response = run('summon-' + mob,
                               prefix + f'summon the_sift:{mob} {2 + i * 2} 150 4 '
                               + '{NoGravity:1b,PersistenceRequired:1b}')
                assert 'Summoned new' in response, f'Cannot summon {mob}'
            time.sleep(5)
            for mob in MOBS:
                result = run('entity-after-ticks-' + mob,
                             prefix + f'data get entity @e[type=the_sift:{mob},limit=1] UUID')
                # /data get entity ... UUID prints the integer UUID array,
                # but does not print the literal word "UUID" in its response.
                assert 'has the following entity data' in result and 'No entity' not in result, f'{mob} not alive after ticks'
            for mob in MOBS:
                run('despawn-test-' + mob,
                    prefix + f'kill @e[type=the_sift:{mob}]')
            run('release-chunk', prefix + 'forceload remove 0 0')
            result = run('save', 'save-all flush')
            assert 'Saved the game' in result
        finally:
            REPORT.parent.mkdir(parents=True, exist_ok=True)
            REPORT.write_text(json.dumps({
                'mc': '26.1.2', 'neoforge': '26.1.2.117',
                'world': 'new disposable qa-entities', 'mobs': list(MOBS),
                'events': events,
                'limitations': 'No player actions, model visuals, loot, combat, mounts, navigation or multiplayer tested.'
            }, indent=2, ensure_ascii=False), encoding='utf-8')
            try:
                server.command('stop')
                print('Graceful server stop requested')
            except (OSError, ConnectionError, TimeoutError):
                print('Connection closed during stop')
    print('PASS 5/5 isolated mob summons and after-tick queries; report:', REPORT)


if __name__ == '__main__':
    main()
