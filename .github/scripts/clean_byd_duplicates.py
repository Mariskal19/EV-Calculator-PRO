import json
from pathlib import Path

PATH = Path("app/src/main/assets/catalog_es_2024_2026.json")

def norm(value):
    return " ".join(str(value or "").strip().lower().split())

def completeness(v):
    # Prefer the record carrying the most actual technical information.
    return sum(1 for k, value in v.items() if value not in (None, "", []))

data = json.loads(PATH.read_text(encoding="utf-8"))
vehicles = data["vehicles"]
byd = [v for v in vehicles if norm(v.get("make")) == "byd"]

print(f"BYD antes: {len(byd)}")

# Only merge true duplicates: same market/year/model/version after normalization.
groups = {}
for idx, v in enumerate(vehicles):
    if norm(v.get("make")) != "byd":
        continue
    key = (
        norm(v.get("market")),
        norm(v.get("model")),
        int(v.get("year")) if str(v.get("year", "")).isdigit() else v.get("year"),
        norm(v.get("version")),
    )
    groups.setdefault(key, []).append((idx, v))

removed = []
keep_indexes = set(range(len(vehicles)))
for key, entries in groups.items():
    if len(entries) <= 1:
        continue
    # Keep the richest record. On ties, keep the first/original record.
    winner_idx, winner = max(entries, key=lambda item: completeness(item[1]))
    for idx, v in entries:
        if idx != winner_idx:
            keep_indexes.discard(idx)
            removed.append((v.get("model"), v.get("year"), v.get("version"), completeness(v), completeness(winner)))

vehicles[:] = [v for i, v in enumerate(vehicles) if i in keep_indexes]

# Canonicalize the model spelling for Sealion 7 so the three retained records are identical in name.
for v in vehicles:
    if norm(v.get("make")) == "byd" and norm(v.get("model")) == "sealion 7":
        v["model"] = "SEALION 7"

byd_after = [v for v in vehicles if norm(v.get("make")) == "byd"]
sealion = [v for v in byd_after if norm(v.get("model")) == "sealion 7"]

print(f"Duplicados BYD eliminados: {len(removed)}")
for item in removed:
    print("  -", item)
print(f"BYD después: {len(byd_after)}")
print("SEALION 7 después:", len(sealion))

assert len(sealion) == 3, f"Se esperaban exactamente 3 SEALION 7 y quedan {len(sealion)}"
assert {v.get("version") for v in sealion} == {"Comfort", "Design AWD", "Excellence AWD"}, [v.get("version") for v in sealion]
assert len({(norm(v.get("market")), norm(v.get("model")), v.get("year"), norm(v.get("version"))) for v in byd_after}) == len(byd_after)

PATH.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
print("Catálogo BYD limpiado correctamente.")
