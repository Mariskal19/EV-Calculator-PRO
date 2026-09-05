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
    fields_for_completeness = [
        "price", "batteryKwh", "usableBatteryKwh", "batteryType", "wltpKm",
        "consumptionKwh100", "powerKw", "drivetrain", "acKw", "dcKw",
        "charge10to80Min", "acceleration0to100Sec", "trunkLiters", "weightKg",
        "source", "lastUpdated"
    ]

    complete_fields = 0
    total_fields = len(vehicles) * len(fields_for_completeness)

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

        market = vehicle.get("market")
        currency = vehicle.get("currency")
        if market == "ES" and currency != "EUR":
            warnings.append(f"{label}: ES market should normally use EUR, found {currency!r}")

        for key in NUMERIC_NONNEGATIVE:
            value = vehicle.get(key)
            if value is not None and (not isinstance(value, (int, float)) or isinstance(value, bool) or value < 0):
                errors.append(f"#{i} {label}: invalid numeric value for {key}: {value!r}")

        nominal = vehicle.get("batteryKwh")
        usable = vehicle.get("usableBatteryKwh")
        if nominal is not None and usable is not None:
            if usable > nominal * 1.03:
                errors.append(f"#{i} {label}: usableBatteryKwh ({usable}) exceeds batteryKwh ({nominal})")
            elif usable > nominal:
                warnings.append(f"{label}: usableBatteryKwh ({usable}) is slightly above batteryKwh ({nominal})")

        wltp = vehicle.get("wltpKm")
        if wltp is not None and not 50 <= wltp <= 1200:
            warnings.append(f"{label}: unusual WLTP range: {wltp}")

        consumption = vehicle.get("consumptionKwh100")
        if consumption is not None and not 8 <= consumption <= 35:
            warnings.append(f"{label}: unusual consumption: {consumption} kWh/100 km")

        power = vehicle.get("powerKw")
        if power is not None and not 20 <= power <= 800:
            warnings.append(f"{label}: unusual power: {power} kW")

        dc = vehicle.get("dcKw")
        if dc is not None and not 20 <= dc <= 600:
            warnings.append(f"{label}: unusual DC charging power: {dc} kW")

        charge = vehicle.get("charge10to80Min")
        if charge is not None and not 10 <= charge <= 180:
            warnings.append(f"{label}: unusual 10–80% charge time: {charge} min")

        accel = vehicle.get("acceleration0to100Sec")
        if accel is not None and not 2 <= accel <= 20:
            warnings.append(f"{label}: unusual 0–100 km/h time: {accel} s")

        missing = [key for key in fields_for_completeness if vehicle.get(key) in (None, "")]
        complete_fields += len(fields_for_completeness) - len(missing)
        if missing:
            warnings.append(f"{label}: missing {len(missing)} data fields ({', '.join(missing)})")

        source = vehicle.get("source")
        updated = vehicle.get("lastUpdated")
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

    completeness = (complete_fields / total_fields * 100.0) if total_fields else 0.0

    report = [
        "# EV Calculator PRO — vehicle data audit",
        "",
        f"Generated: {datetime.now(timezone.utc).isoformat(timespec='seconds')}",
        f"Dataset version: {payload.get('datasetVersion', '?')}",
        f"Vehicles checked: {len(vehicles)}",
        f"Data completeness: {completeness:.1f}% ({complete_fields}/{total_fields} populated fields)",
        "",
        "## Errors (data that must be corrected)",
        "",
    ]
    report += [f"- {x}" for x in errors] or ["- None"]
    report += ["", "## Warnings / review needed", ""]
    report += [f"- {x}" for x in warnings] or ["- None"]
    REPORT.write_text("\n".join(report) + "\n", encoding="utf-8")

    print(f"Checked {len(vehicles)} vehicles")
    print(f"Data completeness: {completeness:.1f}%")
    print(f"Errors: {len(errors)}")
    print(f"Warnings: {len(warnings)}")
    print(f"Report: {REPORT}")

    if errors:
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
