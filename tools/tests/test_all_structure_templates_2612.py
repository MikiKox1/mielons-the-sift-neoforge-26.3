"""Test all 16 original structure templates on the isolated MC26.1.2 server.

The server must be configured for loopback-only RCON. This is a destructive
test *only within the disposable stage world*. No user worlds are accessed.
"""
from hashlib import sha256
import json
from pathlib import Path
from zipfile import ZipFile

from test_local_server_structures import MCRcon, WORKTREE, config

JAR = WORKTREE / "build/libs/the_sift-1.1.2-neoforge.1-alpha.1.jar"
REPORT = WORKTREE / "logs/phase12-all16-structure-placement-2612.json"


def main():
    props = config()
    with ZipFile(JAR) as jar:
        assert jar.testzip() is None
        names = sorted(n for n in jar.namelist()
                       if n.endswith(".nbt") and (n.startswith("data/the_sift/structure/")
                                                 or n.startswith("data/minecraft/structure/")))
    assert len(names) == 16, f"Expected 16 source templates; found {len(names)}"
    responses = []
    failures = []
    with MCRcon("127.0.0.1", props["rcon.password"], port=25582) as conn:
        try:
            time_response = conn.command("execute in the_sift:the_sift run time query time")
            responses.append({"gate": "sift-clock-query", "response": time_response})
            print("Sift clock:", time_response[:240])
            for index, filename in enumerate(names):
                namespace = filename.split("/")[1]
                path = filename.split("/structure/", 1)[1][:-4]
                resource = namespace + ":" + path
                x = index * 192
                prefix = "execute in the_sift:the_sift run "
                forced = conn.command(prefix + f"forceload add {x} 0")
                result = conn.command(prefix + f"place template {resource} {x} 150 0")
                ok = result.startswith("Loaded template")
                if not ok:
                    failures.append(resource)
                responses.append({
                    "resource": resource, "x": x, "y": 150, "z": 0,
                    "force": forced, "response": result, "passed": ok,
                })
                print(("PASS " if ok else "FAIL ") + resource + ": " + result[:270])
                conn.command(prefix + f"forceload remove {x} 0")
            saved = conn.command("save-all flush")
            responses.append({"gate": "save-all", "response": saved})
            print("Saving:", saved[:220])
        finally:
            REPORT.parent.mkdir(parents=True, exist_ok=True)
            REPORT.write_text(json.dumps({
                "minecraft": "26.1.2", "neoforge": "26.1.2.117",
                "jar_sha256": sha256(JAR.read_bytes()).hexdigest().upper(),
                "target": "isolated throwaway world only",
                "tested": len(names), "passed": len(names)-len(failures),
                "failed": failures, "responses": responses,
                "caution": "Template placement PASS does not establish worldgen biomes, shaders or multiplayer."
            }, ensure_ascii=False, indent=2), encoding="utf-8")
            try:
                conn.command("stop")
                print("Graceful server stop requested.")
            except (OSError, ConnectionError, TimeoutError):
                print("Server closed the loopback connection during stop.")
    print("Evidence:", REPORT)
    if failures:
        raise SystemExit(1)


if __name__ == "__main__":
    main()
