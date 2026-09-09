import json
import re
from pathlib import Path

# Canonical Spanish catalog generator: MERCADO > MARCA > MODELO > AÑO > VERSIÓN > TÉCNICA.
ROOT = Path('app/src/main/assets')
SOURCES = [
    'vehicles.json', 'vehicle_variants.json', 'vehicle_market_additions.json',
    'vehicle_market_additions_pass4.json', 'vehicle_market_additions_toyota_volkswagen.json',
    'vehicle_market_additions_tesla.json', 'vehicle_market_additions_pass5.json',
    'vehicle_market_additions_pass6.json', 'vehicle_market_additions_pass7.json',
    'vehicle_market_additions_pass8.json', 'vehicle_market_additions_pass9.json',
    'vehicle_market_additions_pass10.json', 'vehicle_market_additions_pass11.json',
    'vehicle_market_additions_pass12.json', 'vehicle_market_additions_pass13.json',
    'vehicle_market_additions_pass14.json', 'vehicle_market_additions_pass15.json',
    'vehicle_market_additions_pass16.json', 'vehicle_market_additions_pass17.json',
    'vehicle_market_additions_pass18.json', 'vehicle_market_additions_pass19.json',
    'vehicle_market_additions_pass20.json', 'vehicle_market_additions_pass21.json',
    'vehicle_market_additions_pass22.json', 'research_mini_ES_2024_2026.json',
]
OUTPUT = ROOT / 'catalog_es_2024_2026.json'

def number(value): return value if isinstance(value, (int, float)) else 0

def normalized_version(vehicle):
    value = str(vehicle.get('version') or '').strip().lower()
    value = re.sub(r'^\d+(?:\.\d+)?\s*kwh\s*', '', value)
    return re.sub(r'\s+', ' ', value).strip()

def battery_key(vehicle):
    battery = number(vehicle.get('batteryKwh'))
    if battery > 0: return f'{battery:.1f}'
    match = re.match(r'^(\d+(?:\.\d+)?)\s*kwh\b', str(vehicle.get('version') or ''), re.I)
    return match.group(1) if match else '0'

def logical_key(vehicle):
    return '|'.join([str(vehicle.get('make') or ''), str(vehicle.get('model') or ''), str(vehicle.get('market') or ''), str(vehicle.get('year') or 0), battery_key(vehicle), normalized_version(vehicle)]).strip().lower()

def trim_rank(vehicle):
    value = str(vehicle.get('version') or '').lower().replace('-', ' ').replace('_', ' ')
    rank = 300
    if any(x in value for x in ['standard', 'base', 'pure', 'core', 'essential', 'life', 'active', 'entry']): rank = 100
    if any(x in value for x in ['comfort', 'advance', 'advanced', 'evolution', 'plus', 'boost']): rank = max(rank, 200)
    if any(x in value for x in ['long range', 'extended', 'max', 'pro', 'premium', 'design', 'techno', 'earth']): rank = max(rank, 300)
    if any(x in value for x in ['awd', '4wd', 'dual motor', 'all wheel']): rank = max(rank, 400)
    if any(x in value for x in ['performance', 'gt', 'gts', 'rs ', 'sport', 'm performance', 'amg']) or value == 'rs': rank = 500
    return rank

def derive_consumption(vehicle):
    if vehicle.get('consumptionKwh100') not in (None, '', 0): return
    battery = number(vehicle.get('batteryKwh')); wltp = number(vehicle.get('wltpKm'))
    if battery > 0 and wltp > 0: vehicle['consumptionKwh100'] = round((battery / wltp) * 100, 1)

merged = {}
for source_name in SOURCES:
    path = ROOT / source_name
    if not path.exists(): continue
    data = json.loads(path.read_text(encoding='utf-8'))
    vehicles = data.get('vehicles', []) if isinstance(data, dict) else []
    for vehicle in vehicles:
        if not isinstance(vehicle, dict) or vehicle.get('year') not in (2024, 2025, 2026): continue
        vehicle = dict(vehicle); vehicle.setdefault('market', data.get('market', 'ES')); derive_consumption(vehicle)
        key = logical_key(vehicle)
        if key not in merged: merged[key] = vehicle
        else:
            current = merged[key]
            for field, value in vehicle.items():
                if current.get(field) in (None, '', 0) and value not in (None, '', 0): current[field] = value
            derive_consumption(current)

vehicles = list(merged.values())
individual_keys = set()
for v in vehicles:
    version = str(v.get('version') or '')
    if '/' not in version: individual_keys.add((str(v.get('make') or '').lower(), str(v.get('model') or '').lower(), int(v.get('year') or 0), normalized_version(v)))
filtered = []
for v in vehicles:
    version = str(v.get('version') or '')
    if '/' in version:
        parts = [p.strip() for p in version.split('/') if p.strip()]
        if len(parts) > 1 and all((str(v.get('make') or '').lower(), str(v.get('model') or '').lower(), int(v.get('year') or 0), re.sub(r'\s+', ' ', p.lower())) in individual_keys for p in parts): continue
    filtered.append(v)
vehicles = filtered
vehicles.sort(key=lambda v: (str(v.get('market') or '').upper(), str(v.get('make') or '').lower(), str(v.get('model') or '').lower(), int(v.get('year') or 0), trim_rank(v), number(v.get('batteryKwh')), number(v.get('powerKw')), int(v.get('wltpKm') or 0), str(v.get('version') or '').lower()))
for v in vehicles: derive_consumption(v)
output = {'schemaVersion':1,'datasetVersion':'catalog-es-2024-2026-1.0','marketDefault':'ES','catalogOrder':'market>make>model>year>trim>technical','vehicles':vehicles}
OUTPUT.write_text(json.dumps(output, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
print(f'Generated {OUTPUT}: {len(vehicles)} vehicles')
