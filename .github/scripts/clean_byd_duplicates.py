import json
import re
from pathlib import Path

PATH = Path("app/src/main/assets/catalog_es_2024_2026.json")
REPORT = Path("byd_cleanup_report.txt")

def norm(value):
    return " ".join(str(value or "").strip().lower().split())

def completeness(v):
    return sum(1 for value in v.values() if value not in (None, "", []))

def trim_key(version):
    s = norm(version)
    s = re.sub(r"\s+\d+(?:\.\d+)?\s*kw\b.*$", "", s)
    return s

data = json.loads(PATH.read_text(encoding="utf-8"))
vehicles = data["vehicles"]
byd = [v for v in vehicles if norm(v.get("make")) == "byd"]
lines = [f"BYD antes: {len(byd)}"]

groups = {}
for idx, v in enumerate(vehicles):
    if norm(v.get("make")) != "byd":
        continue
    year = int(v["year"]) if str(v.get("year", "")).isdigit() else v.get("year")
    key = (norm(v.get("model")), year, trim_key(v.get("version")))
    groups.setdefault(key, []).append((idx, v))

for key, entries in groups.items():
    if len(entries) > 1:
        lines.append(f"DUP KEY {key}: {len(entries)}")
        for idx, v in entries:
            lines.append(f"  {idx}: model={v.get('model')!r} year={v.get('year')!r} market={v.get('market')!r} version={v.get('version')!r} fields={completeness(v)}")

removed = []
keep_indexes = set(range(len(vehicles)))
for key, entries in groups.items():
    if len(entries) <= 1:
        continue
    winner_idx, winner = max(entries, key=lambda item: completeness(item[1]))
    for idx, v in entries:
        if idx != winner_idx:
            keep_indexes.discard(idx)
            removed.append((v.get("model"), v.get("year"), v.get("version"), winner.get("version")))

vehicles[:] = [v for i, v in enumerate(vehicles) if i in keep_indexes]
for v in vehicles:
    if norm(v.get("make")) == "byd" and norm(v.get("model")) == "sealion 7":
        v["model"] = "SEALION 7"

byd_after = [v for v in vehicles if norm(v.get("make")) == "byd"]
sealion = [v for v in byd_after if norm(v.get("model")) == "sealion 7"]
lines += [f"REMOVED: {len(removed)}", f"BYD después: {len(byd_after)}", f"SEALION 7 después: {len(sealion)}", f"SEALION versions: {[v.get('version') for v in sealion]}"]
print("\n".join(lines))
REPORT.write_text("\n".join(lines) + "\n", encoding="utf-8")

PATH.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
