#!/usr/bin/env python3
import json
import sys
from datetime import date, datetime, timedelta, timezone
from pathlib import Path

DATA_FILES = [Path("app/src/main/assets/vehicles.json"), Path("app/src/main/assets/vehicle_variants.json")]
REPORT = Path("vehicle-audit-report.md")
REQUIRED = ["id", "make", "model", "version", "year", "market", "currency"]
NUMERIC_NONNEGATIVE = [
    "price", "batteryKwh", "usableBatteryKwh", "wltpKm", "consumptionKwh100",
    "powerKw", "acKw", "dcKw", "charge10to80Min", "charge15to80Min",
    "acceleration0to100Sec", "trunkLiters", "weightKg"
]
FIELDS = [
    "price", "batteryKwh", "usableBatteryKwh", "batteryType", "wltpKm",
    "consumptionKwh100", "powerKw", "drivetrain", "acKw", "dcKw",
    "charge10to80Min", "acceleration0to100Sec", "trunkLiters", "weightKg",
    "source", "lastUpdated"
]


def load_all():
    all_vehicles = []
    versions = []
    for path in DATA_FILES:
        if not path.exists():
            raise FileNotFoundError(str(path))
        payload = json.loads(path.read_text(encoding="utf-8"))
        vehicles = payload.get("vehicles")
        if not isinstance(vehicles, list) or not vehicles:
            raise ValueError(f"{path}: vehicles must be a non-empty list")
        versions.append((path.name, payload.get("datasetVersion", "?"), len(vehicles)))
        all_vehicles.extend(vehicles)
    return all_vehicles, versions


def main() -> int:
    try:
        vehicles, versions = load_all()
    except Exception as exc:
        print(f"ERROR: {exc}")
        return 1

    errors, warnings, ids = [], [], set()
    today = date.today()
    stale_before = today - timedelta(days=90)
    complete_fields = 0
    total_fields = len(vehicles) * len(FIELDS)

    for i, vehicle in enumerate(vehicles, start=1):
        label = f"{vehicle.get('make', '?')} {vehicle.get('model', '?')} {vehicle.get('version', '')}".strip()
        for key in REQUIRED:
            if key not in vehicle:
                errors.append(f"#{i} {label}: missing required field '{key}'")
        vid = vehicle.get("id")
        if not vid:
            errors.append(f"#{i} {label}: empty id")
        elif vid in ids:
            errors.append(f"#{i} {label}: duplicate id '{vid}'")
        ids.add(vid)
        if vehicle.get("market") == "ES" and vehicle.get("currency") != "EUR":
            warnings.append(f"{label}: ES market should normally use EUR, found {vehicle.get('currency')!r}")
        for key in NUMERIC_NONNEGATIVE:
            value = vehicle.get(key)
            if value is not None and (not isinstance(value, (int, float)) or isinstance(value, bool) or value < 0):
                errors.append(f"#{i} {label}: invalid numeric value for {key}: {value!r}")
        nominal, usable = vehicle.get("batteryKwh"), vehicle.get("usableBatteryKwh")
        if nominal is not None and usable is not None:
            if usable > nominal * 1.03:
                errors.append(f"#{i} {label}: usableBatteryKwh ({usable}) exceeds batteryKwh ({nominal})")
            elif usable > nominal:
                warnings.append(f"{label}: usableBatteryKwh ({usable}) is slightly above batteryKwh ({nominal})")
        checks = [("wltpKm", 50, 1200, "WLTP range"), ("consumptionKwh100", 8, 35, "consumption"),
                  ("powerKw", 20, 800, "power"), ("dcKw", 20, 600, "DC charging power"),
                  ("charge10to80Min", 10, 180, "10–80% charge time"), ("acceleration0to100Sec", 2, 20, "0–100 km/h time")]
        for key, low, high, label2 in checks:
            value = vehicle.get(key)
            if value is not None and not low <= value <= high:
                warnings.append(f"{label}: unusual {label2}: {value}")
        missing = [key for key in FIELDS if vehicle.get(key) in (None, "")]
        complete_fields += len(FIELDS) - len(missing)
        if missing:
            warnings.append(f"{label}: missing {len(missing)} data fields ({', '.join(missing)})")
        source, updated = vehicle.get("source"), vehicle.get("lastUpdated")
        if not source:
            warnings.append(f"{label}: no source recorded")
        if not updated:
            warnings.append(f"{label}: no lastUpdated recorded")
        else:
            try:
                d = datetime.strptime(updated, "%Y-%m-%d").date()
                if d > today:
                    errors.append(f"#{i} {label}: lastUpdated is in the future ({updated})")
                elif d < stale_before:
                    warnings.append(f"{label}: data older than 90 days ({updated})")
            except ValueError:
                errors.append(f"{label}: invalid lastUpdated '{updated}' (expected YYYY-MM-DD)")

    completeness = complete_fields / total_fields * 100.0 if total_fields else 0.0
    report = ["# EV Calculator PRO — vehicle data audit", "", f"Generated: {datetime.now(timezone.utc).isoformat(timespec='seconds')}",
              "", "## Datasets", ""]
    report += [f"- {name}: dataset {version}, {count} entries" for name, version, count in versions]
    report += ["", f"Total vehicle/version entries checked: {len(vehicles)}", f"Data completeness: {completeness:.1f}% ({complete_fields}/{total_fields} populated fields)", "",
               "## Errors (data that must be corrected)", ""]
    report += [f"- {x}" for x in errors] or ["- None"]
    report += ["", "## Warnings / review needed", ""]
    report += [f"- {x}" for x in warnings] or ["- None"]
    REPORT.write_text("\n".join(report) + "\n", encoding="utf-8")
    print(f"Checked {len(vehicles)} vehicle/version entries")
    print(f"Data completeness: {completeness:.1f}%")
    print(f"Errors: {len(errors)}")
    print(f"Warnings: {len(warnings)}")
    if errors:
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
