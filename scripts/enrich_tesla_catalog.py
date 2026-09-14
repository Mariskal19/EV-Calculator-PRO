import json
from pathlib import Path

CATALOG = Path('app/src/main/assets/catalog_es_2024_2026.json')
TODAY = '2026-09-14'


def specs(v):
    make = v.get('make')
    model = v.get('model')
    version = v.get('version', '')
    year = v.get('year')
    if make != 'Tesla' or model not in {'Model 3', 'Model Y'}:
        return None

    # Only fill fields that are absent. Existing catalog values are preserved.
    s = {}
    if model == 'Model 3':
        if 'Performance' in version:
            s = dict(usableBatteryKwh=75, batteryType='Li-ion', acKw=11, dcKw=250,
                     charge10to80Min=27, acceleration0to100Sec=3.1,
                     trunkLiters=425, weightKg=1851 if year == 2024 else 1929)
        elif 'Premium Gran autonomía 366 kW AWD' in version:
            s = dict(usableBatteryKwh=75, batteryType='Li-ion', acKw=11, dcKw=250,
                     charge10to80Min=27, acceleration0to100Sec=4.4,
                     trunkLiters=425, weightKg=1899)
        elif 'Gran autonomía 208 kW AWD' in version:
            s = dict(usableBatteryKwh=75, batteryType='Li-ion', acKw=11, dcKw=250,
                     charge10to80Min=27, acceleration0to100Sec=4.4,
                     trunkLiters=425, weightKg=1899)
        elif 'Premium Gran autonomía 235 kW RWD' in version:
            s = dict(usableBatteryKwh=75, batteryType='Li-ion', acKw=11, dcKw=250,
                     charge10to80Min=35, acceleration0to100Sec=5.2,
                     trunkLiters=425, weightKg=1822)
        elif 'Gran autonomía 224 kW RWD' in version:
            s = dict(usableBatteryKwh=75, batteryType='Li-ion', acKw=11, dcKw=250,
                     charge10to80Min=35, acceleration0to100Sec=5.2,
                     trunkLiters=425, weightKg=1810)
        elif 'Tracción trasera 208 kW RWD 60 kWh' in version:
            s = dict(usableBatteryKwh=57, batteryType='LFP', acKw=11, dcKw=170,
                     charge10to80Min=27, acceleration0to100Sec=6.1,
                     trunkLiters=425, weightKg=1836 if year >= 2025 else 1836)

    elif model == 'Model Y':
        new_y = ('Premium ' in version or 'Launch Series' in version or
                 'Standard Gran autonomía' in version or 'Standard 220 kW' in version or
                 'Performance 461 kW' in version or 'Tracción trasera 227 kW' in version)
        if 'Premium Gran autonomía 7 plazas' in version:
            s = dict(usableBatteryKwh=75, batteryType='Li-ion', acKw=11, dcKw=250,
                     charge10to80Min=27, acceleration0to100Sec=4.8,
                     trunkLiters=709, weightKg=None)
        elif 'Performance 461 kW AWD' in version:
            s = dict(usableBatteryKwh=75, batteryType='Li-ion', acKw=11, dcKw=250,
                     charge10to80Min=27, acceleration0to100Sec=3.5,
                     trunkLiters=709, weightKg=2108)
        elif 'Premium Gran autonomía 378 kW AWD' in version or 'Launch Series Gran autonomía 378 kW AWD' in version:
            s = dict(usableBatteryKwh=75, batteryType='Li-ion', acKw=11, dcKw=250,
                     charge10to80Min=27, acceleration0to100Sec=4.8,
                     trunkLiters=709, weightKg=2072)
        elif 'Premium Gran autonomía 220 kW RWD' in version:
            s = dict(usableBatteryKwh=75, batteryType='Li-ion', acKw=11, dcKw=250,
                     charge10to80Min=27, acceleration0to100Sec=5.6,
                     trunkLiters=709, weightKg=1976)
        elif 'Standard Gran autonomía 220 kW RWD' in version:
            s = dict(usableBatteryKwh=75, batteryType='Li-ion', acKw=11, dcKw=260,
                     charge10to80Min=27, acceleration0to100Sec=5.6,
                     trunkLiters=709, weightKg=None)
        elif 'Standard 220 kW RWD 61 kWh' in version:
            s = dict(usableBatteryKwh=60, batteryType='Li-ion', acKw=11, dcKw=175,
                     charge10to80Min=27, acceleration0to100Sec=7.2,
                     trunkLiters=709, weightKg=None)
        elif 'Tracción trasera 227 kW RWD 61 kWh' in version:
            s = dict(usableBatteryKwh=60, batteryType='Li-ion', acKw=11, dcKw=175,
                     charge10to80Min=27, acceleration0to100Sec=7.2,
                     trunkLiters=709, weightKg=None)
        elif 'Gran autonomía 255 kW RWD 75 kWh' in version:
            s = dict(usableBatteryKwh=75, batteryType='Li-ion', acKw=11, dcKw=250,
                     charge10to80Min=27, acceleration0to100Sec=5.9,
                     trunkLiters=753, weightKg=2034)
        elif 'Gran autonomía 378 kW AWD 75 kWh' in version:
            s = dict(usableBatteryKwh=75, batteryType='Li-ion', acKw=11, dcKw=250,
                     charge10to80Min=27, acceleration0to100Sec=5.0,
                     trunkLiters=753, weightKg=None)
        elif 'Performance 393 kW AWD 75 kWh' in version:
            s = dict(usableBatteryKwh=75, batteryType='Li-ion', acKw=11, dcKw=250,
                     charge10to80Min=27, acceleration0to100Sec=3.7,
                     trunkLiters=753, weightKg=2072)
        elif 'Tracción trasera 220 kW RWD 60 kWh' in version:
            s = dict(usableBatteryKwh=60, batteryType='LFP', acKw=11, dcKw=170,
                     charge10to80Min=27, acceleration0to100Sec=6.9,
                     trunkLiters=753, weightKg=None)

    if not s:
        return None
    s['drivetrain'] = v.get('drive') or s.get('drivetrain')
    s['source'] = 'Tesla España + KM77; ficha técnica de la versión/periodo'
    s['lastUpdated'] = TODAY
    return s


data = json.loads(CATALOG.read_text(encoding='utf-8'))
changed = 0
for v in data.get('vehicles', []):
    s = specs(v)
    if not s:
        continue
    for key, value in s.items():
        if key == 'drivetrain' and value is None:
            continue
        if key not in v:
            v[key] = value
            changed += 1

CATALOG.write_text(json.dumps(data, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
print(f'Tesla enrichment: {changed} missing fields filled')
