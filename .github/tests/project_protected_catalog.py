#!/usr/bin/env python3
"""Project the protected catalog JSON to a temporary TSV for the Java matcher smoke test."""
import json
import sys
from pathlib import Path

source = Path(sys.argv[1])
destination = Path(sys.argv[2])
root = json.loads(source.read_text(encoding="utf-8"))
vehicles = root.get("vehicles", [])
if not isinstance(vehicles, list):
    raise SystemExit("Protected catalog has no vehicles array")

def text(value):
    return str(value or "").replace("\t", " ").replace("\r", " ").replace("\n", " ").strip()

def number(*values):
    for value in values:
        try:
            n = float(value)
            if n > 0 and n < float("inf"):
                return str(n)
        except (TypeError, ValueError):
            pass
    return "0"

rows = []
for v in vehicles:
    if not isinstance(v, dict):
        continue
    make = text(v.get("make", v.get("brand", "")))
    model = text(v.get("model", ""))
    market = text(v.get("market", "ES")).upper()
    year = text(v.get("year", v.get("modelYear", "0")))
    version = text(v.get("version", v.get("trim", "")))
    battery = number(v.get("batteryKwh"), v.get("battery_capacity_kwh"))
    logical_key = "|".join((make, model, market, year, battery, version)).lower()
    row = [
        logical_key, make, model,
        text(v.get("bodyStyle", v.get("body_style", ""))),
        text(v.get("drivetrain", v.get("drive", ""))),
        number(v.get("price")),
        number(v.get("wltpKm"), v.get("rangeKm")),
        number(v.get("trunkLiters"), v.get("trunk")),
        battery,
        number(v.get("powerKw"), v.get("power_kW")),
        number(v.get("consumption"), v.get("consumptionKwh100")),
    ]
    rows.append("\t".join(row))

destination.write_text("\n".join(rows) + "\n", encoding="utf-8")
print(f"Projected {len(rows)} protected catalog records to temporary TSV")
