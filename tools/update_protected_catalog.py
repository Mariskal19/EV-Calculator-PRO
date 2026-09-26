#!/usr/bin/env python3
import json, os, re, sys, unicodedata
from pathlib import Path
from urllib.request import Request, urlopen

ROOT = Path("app/src/main/assets")
CATALOG = ROOT / "catalog_es_2024_2026.json"
SOURCES = [
    os.environ.get("CATALOG_SOURCE_1_URL", "").strip(),
    os.environ.get("CATALOG_SOURCE_2_URL", "").strip(),
]
OUT = ROOT / "catalog_remote_additions.json"

FIELDS = [
    "price","batteryKwh","usableBatteryKwh","batteryType","batteryChemistry",
    "wltpKm","consumptionKwh100","powerKw","drive","drivetrain","acKw","dcKw",
    "charge10to80Min","acceleration0to100Sec","trunkLiters","weightKg","photo",
    "source","lastUpdated","arrivalYear","currency"
]

def load_json(url):
    req = Request(url, headers={"User-Agent": "EV-Calculator-PRO-catalog-bot/1.0"})
    with urlopen(req, timeout=30) as r:
        return json.loads(r.read().decode("utf-8"))

def num(v):
    return v if isinstance(v, (int, float)) else 0

def norm(v):
    return re.sub(r"\s+", " ", str(v or "").strip().lower())

def battery_key(v):
    b = num(v.get("batteryKwh"))
    if b > 0:
        return f"{b:.1f}"
    m = re.match(r"^(\d+(?:\.\d+)?)\s*kwh\b", str(v.get("version") or ""), re.I)
    return m.group(1) if m else "0"

def key(v):
    return "|".join([
        norm(v.get("make")), norm(v.get("model")), norm(v.get("market") or "ES"),
        str(v.get("year") or 0), battery_key(v), norm(v.get("version"))
    ])

# Known naming variations between catalog sources. These aliases are deliberately
# conservative: they only collapse names that identify the same model family, while
# preserving body styles that are sold as distinct models (e.g. Q6 vs Q6 Sportback).
MODEL_ALIASES = {
    "audi": {
        "q6 suv e-tron": "q6 e-tron",
        "q6 e tron": "q6 e-tron",
    },
    "volkswagen": {
        "id buzz": "id.buzz",
        "id buzz long": "id.buzz",
    },
}

def canonical_text(value):
    text = str(value or "").strip().lower()
    text = unicodedata.normalize("NFKD", text)
    text = "".join(ch for ch in text if not unicodedata.combining(ch))
    text = re.sub(r"\s+", " ", text)
    return text

def model_key(v):
    """Build a conservative canonical model identity across source naming styles."""
    make = canonical_text(v.get("make"))
    model = canonical_text(v.get("model"))
    if make and model.startswith(make):
        model = model[len(make):].strip(" -_/")
    model = MODEL_ALIASES.get(make, {}).get(model, model)
    # Treat punctuation/spacing variants as identical: ID.3 == ID-3 == ID 3.
    return re.sub(r"[^a-z0-9]+", "", model)

def tech_duplicate(a, b):
    """Return True when a source record is very likely the same configuration already known."""
    if canonical_text(a.get("make")) != canonical_text(b.get("make")):
        return False
    if model_key(a) != model_key(b):
        return False
    if str(a.get("year") or 0) != str(b.get("year") or 0):
        return False
    if norm(a.get("market") or "ES") != norm(b.get("market") or "ES"):
        return False

    va, vb = norm(a.get("version")), norm(b.get("version"))
    if va and vb:
        # Same trim name, or one name is a commercial expansion of the other.
        if va == vb or va.startswith(vb + " ") or vb.startswith(va + " "):
            return True

    def close_num(field, tolerance):
        x, y = num(a.get(field)), num(b.get(field))
        return x > 0 and y > 0 and abs(x - y) <= tolerance

    battery_close = close_num("batteryKwh", 2.0)
    power_close = close_num("powerKw", 8.0)
    range_close = close_num("wltpKm", 12)

    da, db = canonical_text(a.get("drive") or a.get("drivetrain")), canonical_text(b.get("drive") or b.get("drivetrain"))
    drive_compatible = not da or not db or da == db

    # Conservative technical equivalence: never merge clearly different batteries,
    # motors, drivetrains or WLTP figures. A source with only a trim-name variation
    # is treated as the same configuration when the core specs also agree.
    return battery_close and power_close and range_close and drive_compatible

def flatten_gaia(item):
    """Convert Gaia EVDB summary or full records into our catalog shape."""
    if not isinstance(item, dict):
        return None
    battery = item.get("battery") or {}
    charging = item.get("charging") or {}
    performance = item.get("performance") or {}
    efficiency = item.get("efficiency") or {}
    rng = item.get("range") or {}
    weight = item.get("weight") or {}
    cargo = item.get("cargo") or {}
    markets = item.get("markets") or []
    es = next((m for m in markets if str(m.get("market_code", "")).upper() == "ES"), None)
    if markets and es is None:
        return None
    return {
        "gaiaId": item.get("id"),
        "make": item.get("brand") or item.get("make"),
        "model": item.get("model_name") or item.get("model"),
        "version": item.get("variant_name") or item.get("name"),
        "year": item.get("model_year") or item.get("year"),
        "market": "ES",
        "currency": (es or {}).get("currency") or "EUR",
        "price": (es or {}).get("price_base"),
        "batteryKwh": battery.get("total_kwh") or item.get("battery_capacity_kwh") or item.get("battery_total_kwh"),
        "usableBatteryKwh": battery.get("usable_kwh") or item.get("battery_usable_kwh"),
        "batteryType": battery.get("chemistry") or item.get("battery_chemistry"),
        "batteryChemistry": battery.get("chemistry") or item.get("battery_chemistry"),
        "wltpKm": rng.get("wltp_km") or item.get("range_wltp_km"),
        "consumptionKwh100": efficiency.get("wltp_kwh_per_100km") or item.get("consumption_wltp_kwh_100km"),
        "powerKw": performance.get("total_power_kw") or item.get("total_power_kw") or item.get("power_kw"),
        "drive": performance.get("drive_type") or item.get("drive_type"),
        "drivetrain": performance.get("drive_type") or item.get("drive_type"),
        "acKw": charging.get("ac_max_kw") or item.get("ac_charge_power_kw") or item.get("ac_onboard_charger_kw"),
        "dcKw": charging.get("dc_max_kw") or item.get("dc_charge_power_kw"),
        "charge10to80Min": charging.get("time_10_to_80_min") or item.get("dc_charge_time_10_80_min"),
        "acceleration0to100Sec": performance.get("acceleration_0_100_sec") or item.get("acceleration_0_100_sec"),
        "trunkLiters": cargo.get("trunk_capacity_liters") or item.get("trunk_capacity_liters"),
        "weightKg": weight.get("curb_weight_kg") or item.get("weight_curb_kg"),
        "source": "Gaia Charge EVDB",
        "arrivalYear": item.get("model_year") or item.get("year")
    }

def valid(v):
    if not isinstance(v, dict): return False
    if v.get("year") not in (2024, 2025, 2026): return False
    if not v.get("make") or not v.get("model") or not v.get("version"): return False
    if num(v.get("batteryKwh")) <= 0 or num(v.get("wltpKm")) <= 0 or num(v.get("powerKw")) <= 0: return False
    if num(v.get("consumptionKwh100")) <= 0: return False
    c = num(v.get("consumptionKwh100"))
    if c < 8 or c > 35 or num(v.get("wltpKm")) > 1000: return False
    if num(v.get("usableBatteryKwh")) > num(v.get("batteryKwh")) + 0.5: return False
    if num(v.get("dcKw")) and num(v.get("acKw")) and num(v.get("dcKw")) < num(v.get("acKw")): return False
    return True

if not CATALOG.exists():
    raise SystemExit("Protected catalog not found")

catalog = json.loads(CATALOG.read_text(encoding="utf-8"))
existing = catalog.get("vehicles", [])

# The packaged catalog is the immutable audited base.
# Remote additions are tracked separately so this workflow can never rewrite or delete it.
if OUT.exists():
    remote_root = json.loads(OUT.read_text(encoding="utf-8"))
else:
    remote_root = {"version": 1, "updatedAt": None, "vehicles": []}
remote_existing = remote_root.get("vehicles", [])
existing_keys = {key(v) for v in existing}

# Remote additions are only the delta over the protected catalog.
# Purge entries that became protected later, and de-duplicate the remote list itself.
cleaned_remote = []
for rv in remote_existing:
    if any(tech_duplicate(rv, pv) for pv in existing):
        continue
    if any(tech_duplicate(rv, prev) for prev in cleaned_remote):
        continue
    cleaned_remote.append(rv)
remote_existing = cleaned_remote
existing_keys.update(key(v) for v in remote_existing)
candidates = {}

def known_equivalent(v):
    return any(tech_duplicate(v, x) for x in list(existing) + list(remote_existing))

for url in SOURCES:
    if not url:
        continue
    data = load_json(url)
    raw_items = data.get("results", data.get("vehicles", [])) if isinstance(data, dict) else []
    is_gaia = "gaia-charge.github.io/evdb/" in url
    known_identities = {
        "|".join([norm(x.get("make")), norm(x.get("model")),
                  str(x.get("year")), norm(x.get("version")),
                  str(x.get("usableBatteryKwh") or "")])
        for x in list(existing) + list(remote_existing)
    }
    for raw in raw_items:
        # Gaia's vehicles.json is a summary endpoint. Its full vehicle endpoint
        # contains the remaining fields needed for safe automatic insertion.
        if is_gaia and isinstance(raw, dict):
            summary = flatten_gaia(raw)
            if not summary or summary.get("year") not in (2024, 2025, 2026):
                continue
            if not summary.get("make") or not summary.get("model") or not summary.get("version"):
                continue
            summary_identity = "|".join([
                norm(summary.get("make")), norm(summary.get("model")),
                str(summary.get("year")), norm(summary.get("version")),
                str(summary.get("usableBatteryKwh") or "")
            ])
            if summary_identity in known_identities:
                continue
            gaia_id = raw.get("id")
            if not gaia_id:
                continue
            try:
                detail_url = "https://gaia-charge.github.io/evdb/v1/vehicles/" + str(gaia_id) + ".json"
                v = flatten_gaia(load_json(detail_url))
            except Exception:
                continue
        else:
            v = flatten_gaia(raw) if "results" in data else dict(raw)
        if not v:
            continue
        v.setdefault("market", data.get("market", "ES") if isinstance(data, dict) else "ES")
        if not valid(v):
            continue
        k = key(v)
        if k in existing_keys or known_equivalent(v):
            continue
        if k not in candidates:
            candidates[k] = v
        else:
            # A candidate appearing in both sources must agree on core technical data.
            other = candidates[k]
            core = ["batteryKwh","usableBatteryKwh","wltpKm","consumptionKwh100","powerKw","drive","drivetrain","acKw","dcKw"]
            if any(norm(other.get(f)) != norm(v.get(f)) for f in core if other.get(f) not in (None,"",0) and v.get(f) not in (None,"",0)):
                candidates.pop(k, None)

added = []
for k, v in candidates.items():
    if k in existing_keys or not valid(v):
        continue
    v["auditDate"] = v.get("auditDate") or "AUTO"
    v["lastUpdated"] = v.get("lastUpdated") or "AUTO"
    v["source"] = v.get("source") or "Automatic source JSON"
    remote_existing.append(v)
    existing_keys.add(k)
    added.append(v)

if added:
    from datetime import datetime, timezone
    remote_root["version"] = int(remote_root.get("version") or 1)
    remote_root["updatedAt"] = datetime.now(timezone.utc).isoformat()
    remote_root["vehicles"] = remote_existing
    OUT.write_text(json.dumps(remote_root, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

print(f"Protected base catalog: {len(existing)} records untouched")
print(f"Existing remote additions: {len(remote_existing)-len(added)}")
print(f"Automatically added remotely: {len(added)} new validated configurations")
