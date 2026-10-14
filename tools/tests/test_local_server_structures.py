"""Local-only QA: use RCON to exercise structure templates in a disposable world.

Never run against a user's Minecraft profile. Server settings must explicitly
match Prime 2's isolated run directory, loopback IP and dedicated port. Uses
the optional Python mcrcon library installed only inside tools/tests/_qa_vendor.
No credentials are printed or included in the audit file.
"""

import json
from pathlib import Path
import sys
import time

HERE = Path(__file__).resolve().parent
WORKTREE = HERE.parents[1]
SERVER_DIR = WORKTREE / "run/server-26.1.2.117-port-2612-test"
REPORT = WORKTREE / "logs/phase11-structure-placement-2612.json"
sys.path.insert(0, str(HERE / "_qa_vendor"))
from mcrcon import MCRcon


def config():
    raw = (SERVER_DIR / "server.properties").read_text(encoding="utf-8")
    mapping = dict(line.split("=", 1) for line in raw.splitlines() if "=" in line and not line.startswith("#"))
    for key, value in {
        "server-ip": "127.0.0.1", "server-port": "25572",
        "rcon.port": "25582", "enable-rcon": "true", "online-mode": "true"
    }.items():
        assert mapping.get(key) == value, f"Unsafe/unexpected test server setting: {key}"
    assert len(mapping.get("rcon.password", "")) >= 20, "Temporary RCON password missing"
    return mapping


def main():
    props = config()
    commands = [
        ("sift-dimension", "execute in the_sift:the_sift run time query time"),
        ("forceload-main", "execute in the_sift:the_sift run forceload add 0 0"),
        ("place-main-portal", "execute in the_sift:the_sift run place template the_sift:main_portal 0 150 0"),
        ("forceload-willow", "execute in the_sift:the_sift run forceload add 128 0"),
        ("place-willow", "execute in the_sift:the_sift run place template the_sift:overgrown_willow/small_01 128 150 0"),
        ("forceload-city", "execute in the_sift:the_sift run forceload add 256 0"),
        ("place-city", "execute in the_sift:the_sift run place template minecraft:ancient_city/city_center/city_center_1 256 150 0"),
        ("save", "save-all flush"),
        ("release-main", "execute in the_sift:the_sift run forceload remove 0 0"),
        ("release-willow", "execute in the_sift:the_sift run forceload remove 128 0"),
        ("release-city", "execute in the_sift:the_sift run forceload remove 256 0"),
    ]
    responses = []
    with MCRcon("127.0.0.1", props["rcon.password"], port=25582) as client:
        try:
            for gate, command in commands:
                response = client.command(command)
                responses.append({"gate": gate, "command": command, "response": response})
                print(gate + ":", response[:450])
        finally:
            REPORT.parent.mkdir(exist_ok=True, parents=True)
            REPORT.write_text(json.dumps({
                "target": "MC26.1.2 / NeoForge26.1.2.117",
                "source": "disposable loopback RCON 127.0.0.1:25582",
                "evidence": responses,
                "interpretation": "Console response and server log must be reviewed. Not shader/gameplay acceptance.",
            }, indent=2, ensure_ascii=False), encoding="utf-8")
            try:
                print("server-stop:", client.command("stop")[:200])
            except (OSError, ConnectionError, TimeoutError) as exc:
                print("server-stop connection closed:", type(exc).__name__)
    print("Evidence file:", REPORT)


if __name__ == "__main__":
    main()
