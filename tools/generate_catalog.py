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
    'vehicle_market_additions_pass8.json',
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
