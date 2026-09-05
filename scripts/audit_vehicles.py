#!/usr/bin/env python3
import json
import sys
from datetime import date, datetime, timedelta, timezone
from pathlib import Path

DATA = Path("app/src/main/assets/vehicles.json")
REPORT = Path("vehicle-audit-report.md")
REQUIRED = ["id", "make", "model", "version", "year", "market", "currency"]
NUMERIC_NONNEGATIVE = [
    "price", "batteryKwh", "usableBatteryKwh", "wltpKm", "consumptionKwh100",
    "powerKw", "acKw", "dcKw", "charge10to80Min", "charge15to80Min",
    "acceleration0to100Sec", "trunkLiters", "weightKg"
]


def main() -> int:
    if not DATA.exists():
        print(f"ERROR: {DATA} not found")
        return 1

    try:
        payload = json.loads(DATA.read_text(encoding="utf-8"))
    except Exception as exc:
        print(f"ERROR: invalid JSON: {exc}")
        return 1

    vehicles = payload.get("vehicles")
    if not isinstance(vehicles, list) or not vehicles:
        print("ERROR: vehicles must be a non-empty list")
        return 1

    errors = []
    warnings = []
    ids = set()
    today = date.today()
    stale_before = today - timedelta(days=90)

    for i, vehicle in enumerate(vehicles, start=1):
        label = f"{vehicle.get('make', '?')} {vehicle.get('model', '?')} {vehicle.get('version', '')}".strip()
        for key in REQUIRED:
            if key not in vehicle:
                errors.append(f"#{i} {label}: missing required field '{key}'")

        vid = vehicle.get("id")
        if vid in ids:
            errors.append(f"#{i} {label}: duplicate id '{vid}'")
        ids.add(vid)

        for key in NUMERIC_NONNEGATIVE:
            value = vehicle.get(key)
            if value is not None and (not isinstance(value, (int, float)) or isinstance(value, bool) or value < 0):
                errors.append(f"#{i} {label}: invalid numeric value for {key}: {value!r}")

        nominal = vehicle.get("batteryKwh")
        usable = vehicle.get("usableBatteryKwh")
        if nominal is not None and usable is not None and usable > nominal * 1.03:
            warnings.append(f"{label}: usableBatteryKwh ({usable}) is greater than batteryKwh ({nominal})")

        wltp = vehicle.get("wltpKm")
        if wltp is not None and not 50 <= wltp <= 1200:
            warnings.append(f"{label}: unusual WLTP range: {wltp}")

        source = vehicle.get("source")
        updated = vehicle.get("lastUpdated")
        if not source:
            warnings.append(f"{label}: no source recorded")
        if not updated:
            warnings.append(f"{label}: no lastUpdated recorded")
        else:
            try:
                d = datetime.strptime(updated, "%Y-%m-%d").date()
                if d < stale_before:
                    warnings.append(f"{label}: data older than 90 days ({updated})")
            except ValueError:
                errors.append(f"{label}: invalid lastUpdated '{updated}' (expected YYYY-MM-DD)")

    report = [
        "# EV Calculator PRO — vehicle data audit",
        "",
        f"Generated: {datetime.now(timezone.utc).isoformat(timespec='seconds')}",
        f"Dataset version: {payload.get('datasetVersion', '?')}",
        f"Vehicles checked: {len(vehicles)}",
        "",
        f"## Errors ({len(errors)})",
        "",
    ]
    report += [f"- {x}" for x in errors] or ["- None"]
    report += ["", f"## Warnings ({len(warnings)})", ""]
    report += [f"- {x}" for x in warnings] or ["- None"]
    REPORT.write_text("\n".join(report) + "\n", encoding="utf-8")

    print(f"Checked {len(vehicles)} vehicles")
    print(f"Errors: {len(errors)}")
    print(f"Warnings: {len(warnings)}")
    print(f"Report: {REPORT}")

    if errors:
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
