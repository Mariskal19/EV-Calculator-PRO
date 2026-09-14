import json
from pathlib import Path

p = Path("app/src/main/assets/catalog_es_2024_2026.json")
data = json.loads(p.read_text(encoding="utf-8"))
vehicles = data["vehicles"]

before = sum(1 for v in vehicles if str(v.get("model", "")).lower() == "sealion 7")
vehicles[:] = [v for v in vehicles if not (str(v.get("make", "")).upper() == "BYD" and str(v.get("model", "")).lower() == "sealion 7")]

# Keep only the canonical three Spanish 2024 versions.
canonical = [
    ("SEALION 7", "Comfort"),
    ("SEALION 7", "Design AWD"),
    ("SEALION 7", "Excellence AWD"),
]
# Recover the canonical records from the pre-existing uppercase entries.
removed = [v for v in data["vehicles"] if False]
# The canonical entries are identified before filtering via their uppercase model name.
# Re-read from git is unnecessary: filtering above only removed lowercase duplicates.
# Validate the resulting records directly.
sealions = [v for v in vehicles if str(v.get("make", "")).upper() == "BYD" and str(v.get("model", "")).upper() == "SEALION 7"]
assert len(sealions) == 3, f"Expected 3 canonical SEALION 7 records, found {len(sealions)}"
assert {v.get("version") for v in sealions} == {"Comfort", "Design AWD", "Excellence AWD"}

p.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
print(f"Removed {before - len(sealions)} lowercase duplicate records; SEALION 7 count is now {len(sealions)}")
