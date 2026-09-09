import json
import re
from pathlib import Path

# Canonical Spanish catalog generator: MERCADO > MARCA > MODELO > AÑO > VERSIÓN > TÉCNICA.
ROOT = Path('app/src/main/assets')
SOURCES = [
    'vehicles.json',
    'vehicle_variants.json',
    'vehicle_market_additions.json',
    'vehicle_market_additions_pass4.json',
    'vehicle_market_additions_toyota_volkswagen.json',
    'vehicle_market_additions_tesla.json',
    'vehicle_market_additions_pass5.json',
    'vehicle_market_additions_pass6.json',
    'vehicle_market_additions_pass7.json',
    'research_mini_ES_2024_2026.json',
]
OUTPUT = ROOT / 'catalog_es_2024_2026.json'


def number(value):
    return value if isinstance(value, (int, float)) else 0


def normalized_version(vehicle):
    value = str(vehicle.get('version') or '').strip().lower()
    value = re.sub(r'^\d+(?:\.\d+)?\s*kwh\s*', '', value)
    return re.sub(r'\s+', ' ', value).strip()


def battery_key(vehicle):
    battery = number(vehicle.get('batteryKwh'))
    if battery > 0:
        return f'{battery:.1f}'
    match = re.match(r'^(\d+(?:\.\d+)?)\s*kwh\b', str(vehicle.get('version') or ''), re.I)
    return match.group(1) if match else '0'


def logical_key(vehicle):
    return '|'.join([
        str(vehicle.get('make') or ''),
        str(vehicle.get('model') or ''),
        str(vehicle.get('market') or ''),
        str(vehicle.get('year') or 0),
        battery_key(vehicle),
        normalized_version(vehicle),
    ]).strip().lower()


def trim_rank(vehicle):
    value = str(vehicle.get('version') or '').lower().replace('-', ' ').replace('_', ' ')
    rank = 300
    if any(x in value for x in ['standard', 'base', 'pure', 'core', 'essential', 'life', 'active', 'entry']):
        rank = 100
    if any(x in value for x in ['comfort', 'advance', 'advanced', 'evolution', 'plus', 'boost']):
        rank = max(rank, 200)
    if any(x in value for x in ['long range', 'extended', 'max', 'pro', 'premium', 'design', 'techno', 'earth']):
        rank = max(rank, 300)
    if any(x in value for x in ['awd', '4wd', 'dual motor', 'all wheel']):
        rank = max(rank, 400)
    if any(x in value for x in ['performance', 'gt', 'gts', 'rs ', 'sport', 'm performance', 'amg']) or value == 'rs':
        rank = 500
    return rank


def derive_consumption(vehicle):
    """Fill only missing WLTP consumption from battery energy and WLTP range.

    Existing researched consumption values are never overwritten. The derived value
    is rounded to one decimal and keeps the catalog self-contained for app runtime.
    """
    if vehicle.get('consumptionKwh100') not in (None, '', 0):
        return
    battery = number(vehicle.get('batteryKwh'))
    wltp = number(vehicle.get('wltpKm'))
    if battery > 0 and wltp > 0:
        vehicle['consumptionKwh100'] = round((battery / wltp) * 100, 1)


merged = {}
for source in SOURCES:
    path = ROOT / source
    if not path.exists():
        continue
    data = json.loads(path.read_text(encoding='utf-8'))
    for vehicle in data.get('vehicles', []):
        if vehicle.get('year') not in (2024, 2025, 2026):
            continue
        key = logical_key(vehicle)
        if key not in merged:
            merged[key] = dict(vehicle)
            continue
        existing = merged[key]
        for field in [
            'price', 'batteryKwh', 'usableBatteryKwh', 'batteryType', 'wltpKm',
            'consumptionKwh100', 'consumption', 'powerKw', 'drivetrain', 'acKw',
            'dcKw', 'charge10to80Min', 'acceleration0to100Sec', 'trunkLiters',
            'weightKg', 'photo', 'source', 'lastUpdated', 'arrivalYear'
        ]:
            if existing.get(field) in (None, '', 0) and vehicle.get(field) not in (None, '', 0):
                existing[field] = vehicle[field]

vehicles = list(merged.values())

# Remove slash-combined versions when all individual versions exist for the same
# make/model/market/year. This keeps one technical configuration per catalog entry.
remove_ids = set()
for vehicle in vehicles:
    version = str(vehicle.get('version') or '')
    if '/' not in version:
        continue
    parts = [part.strip().lower() for part in version.split('/') if part.strip()]
    complete = bool(parts)
    for part in parts:
        if not any(
            other is not vehicle
            and str(other.get('make', '')).lower() == str(vehicle.get('make', '')).lower()
            and str(other.get('model', '')).lower() == str(vehicle.get('model', '')).lower()
            and str(other.get('market', '')).lower() == str(vehicle.get('market', '')).lower()
            and other.get('year') == vehicle.get('year')
            and str(other.get('version', '')).strip().lower() == part
            for other in vehicles
        ):
            complete = False
            break
    if complete:
        remove_ids.add(id(vehicle))
vehicles = [vehicle for vehicle in vehicles if id(vehicle) not in remove_ids]

# Make the runtime catalog self-contained: every configuration with battery + WLTP
# has a usable consumption value for Compare Cars and other app calculations.
for vehicle in vehicles:
    derive_consumption(vehicle)


def order_key(vehicle):
    return (
        str(vehicle.get('market') or '').upper(),
        str(vehicle.get('make') or '').lower(),
        str(vehicle.get('model') or '').lower(),
        int(vehicle.get('year') or 0),
        trim_rank(vehicle),
        number(vehicle.get('batteryKwh')),
        number(vehicle.get('powerKw')),
        int(vehicle.get('wltpKm') or 0),
        str(vehicle.get('version') or '').lower(),
    )


vehicles.sort(key=order_key)
result = {
    'schemaVersion': 1,
    'datasetVersion': 'catalog-es-2024-2026-1.0',
    'marketDefault': 'ES',
    'catalogOrder': 'market>make>model>year>trim>technical',
    'vehicles': vehicles,
}
OUTPUT.write_text(json.dumps(result, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
print(f'Catalog generated: {len(vehicles)} vehicles')
