"""Inspect the exact NeoForge 26.1.2.117 mapped Minecraft source archive.

Read only. This helper never modifies source/references or runs Minecraft.
Examples: python tools/inspect_mc_2612.py SkyRenderer render 8
"""
import sys
import zipfile
from pathlib import Path

BASE = Path(__file__).resolve().parents[1]
ARCHIVE = BASE / 'build/moddev/artifacts/minecraft-patched-26.1.2.117-sources.jar'

def main():
    needle = sys.argv[1].lower() if len(sys.argv) > 1 else 'SkyRenderer'
    search = sys.argv[2].lower() if len(sys.argv) > 2 else ''
    radius = int(sys.argv[3]) if len(sys.argv) > 3 else 3
    with zipfile.ZipFile(ARCHIVE) as jar:
        names = [n for n in jar.namelist() if n.lower().endswith('.java') and needle in n.lower()]
        if not names:
            print('NOT_FOUND',needle)
        for name in names[:8]:
            rows = jar.read(name).decode('utf-8-sig', errors='replace').splitlines()
            print('===== CLASS',name,'lines',len(rows),'=====')
            if not search:
                indexes = list(range(min(120,len(rows))))
            else:
                found=[i for i,s in enumerate(rows) if search in s.lower()]
                indexes=sorted(set(j for i in found for j in range(max(0,i-radius),min(len(rows),i+radius+1))))
                print('matches',len(found))
            for i in indexes[:550]:
                print(f'{i+1:5d} {rows[i]}')

if __name__ == '__main__':
    main()
