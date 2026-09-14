#!/usr/bin/env python3
import json, re, subprocess, sys
from datetime import date
from pathlib import Path
from urllib.request import urlopen, Request

CATALOG = Path('app/src/main/assets/catalog_es_2024_2026.json')
SOURCE_URL = 'https://github.com/open-ev-data/open-ev-data-dataset/releases/latest/download/open-ev-data.json'
TODAY = date.today().isoformat()

ALIASES = {
    'citroen': 'citroen', 'ds': 'ds automobiles', 'cupra': 'cupra',
    'mercedes-benz': 'mercedes-benz', 'mercedes benz': 'mercedes-benz',
    'vw': 'volkswagen', 'volkswagen': 'volkswagen',
    'opel': 'opel', 'smart': 'smart', 'tesla': 'tesla',
}

def norm(s):
    s = str(s or '').lower().strip()
    s = s.replace('é','e').replace('è','e').replace('ë','e').replace('ï','i').replace('ö','o').replace('ü','u')
    s = re.sub(r'[^a-z0-9]+', ' ', s)
    return re.sub(r'\s+', ' ', s).strip()

def num(v):
    try:
        return float(v)
    except Exception:
        return None

def first(d, *keys):
    for k in keys:
        if isinstance(d, dict) and d.get(k) not in (None, ''):
            return d[k]
    return None

def flatten_sources(v):
    out = []
    for s in v.get('sources', []) if isinstance(v, dict) else []:
        if not isinstance(s, dict):
            continue
        name = first(s, 'name', 'source_name', 'title')
        url = first(s, 'url', 'source_url')
        if name or url:
            out.append((name or 'OpenEV Data', url))
    return out

def candidate_fields(v):
    b = v.get('battery') or {}
    p = v.get('powertrain') or {}
    c = v.get('charging') or {}
    ac = c.get('ac') or {}
    dc = c.get('dc') or {}
    perf = v.get('performance') or {}
    cap = v.get('capacity') or {}
    w = v.get('weights') or {}
    # Support older OpenEV shape too.
    battery = first(b, 'pack_capacity_kwh_net', 'capacity_kwh')
    power = first(p, 'system_power_kw', 'power_kw')
    drive = first(p, 'drivetrain')
    if drive is None:
        drive = v.get('drive_type')
    accel = first(perf, 'acceleration_0_100_kmh_s', 'acceleration_0_100_kmh')
    if accel is None:
        accel = v.get('acceleration_0_100_kmh')
    ac_kw = first(ac, 'max_power_kw')
    dc_kw = first(dc, 'max_power_kw')
    if dc_kw is None:
        dc_kw = v.get('charging_speed_kw')
    charge_time = None
    for row in (c.get('charging_time', {}).get('dc', []) if isinstance(c.get('charging_time'), dict) else []):
        if not isinstance(row, dict):
            continue
        if num(row.get('from_soc_percent')) == 10 and num(row.get('to_soc_percent')) == 80:
            charge_time = row.get('time_min')
            break
    return {
        'battery': num(battery),
        'chemistry': first(b, 'chemistry'),
        'power': num(power),
        'drive': str(drive or '').upper(),
        'ac': num(ac_kw), 'dc': num(dc_kw),
        'charge_time': num(charge_time), 'accel': num(accel),
        'trunk': num(first(cap, 'cargo_l')), 'weight': num(first(w, 'curb_weight_kg')),
        'sources': flatten_sources(v),
    }

def load_source():
    req = Request(SOURCE_URL, headers={'User-Agent': 'EV-Calculator-PRO catalog enrichment'})
    with urlopen(req, timeout=60) as r:
        data = json.load(r)
    vehicles = data.get('vehicles') if isinstance(data, dict) else data
    if not isinstance(vehicles, list):
        raise RuntimeError('OpenEV Data JSON has no vehicles array')
    return data, vehicles

def score(target, src):
    s = 0
    tmake, smake = norm(target.get('make')), norm(src.get('brand') or src.get('make', {}).get('name') if isinstance(src.get('make'), dict) else src.get('make'))
    if tmake != smake:
        return -999
    if norm(target.get('model')) != norm(src.get('model', {}).get('name') if isinstance(src.get('model'), dict) else src.get('model')):
        return -999
    if int(target.get('year', 0)) != int(src.get('year', 0) or 0):
        return -999
    s += 10
    t = candidate_fields(target); c = candidate_fields(src)
    tv = num(target.get('powerKw'))
    if tv is not None and c['power'] is not None:
        s += 7 if abs(tv-c['power']) <= 2 else -4
    tv = num(target.get('batteryKwh'))
    if tv is not None and c['battery'] is not None:
        s += 5 if abs(tv-c['battery']) <= 2.5 else -2
    td = str(target.get('drive') or target.get('drivetrain') or '').upper()
    if td and c['drive']:
        s += 4 if td == c['drive'] else -3
    version = norm(target.get('version'))
    variant = norm(src.get('variant') or src.get('trim') or src.get('version'))
    if version and variant:
        vtoks = set(re.findall(r'[a-z0-9]+', variant))
        ttoks = set(re.findall(r'[a-z0-9]+', version))
        s += min(5, len(vtoks & ttoks))
    return s

def set_missing(v, key, value):
    if value in (None, '', 0):
        return False
    if v.get(key) not in (None, '', 0):
        return False
    v[key] = value
    return True

def main():
    data, source_vehicles = load_source()
    catalog = json.loads(CATALOG.read_text(encoding='utf-8'))
    vehicles = catalog.get('vehicles')
    if not isinstance(vehicles, list):
        raise RuntimeError('Catalog vehicles array missing')
    original_count = len(vehicles)
    changes = 0
    matched = 0
    ambiguous = 0
    by_brand = {}

    for v in vehicles:
        brand = v.get('make', 'Unknown')
        candidates = [x for x in source_vehicles if score(v, x) >= 10]
        scored = sorted(((score(v, x), x) for x in candidates), key=lambda z: z[0], reverse=True)
        if not scored:
            continue
        best_score, best = scored[0]
        if len(scored) > 1 and scored[0][0] == scored[1][0]:
            ambiguous += 1
            continue
        matched += 1
        c = candidate_fields(best)
        added = 0
        mappings = {
            'usableBatteryKwh': c['battery'],
            'batteryChemistry': c['chemistry'],
            'drivetrain': c['drive'],
            'acKw': c['ac'],
            'dcKw': c['dc'],
            'charge10to80Min': c['charge_time'],
            'acceleration0to100Sec': c['accel'],
            'trunkLiters': c['trunk'],
            'weightKg': c['weight'],
        }
        for key, value in mappings.items():
            if set_missing(v, key, value):
                changes += 1; added += 1
        if added:
            if not v.get('source'):
                srcs = c['sources']
                if srcs:
                    name, url = srcs[0]
                    v['source'] = f'OpenEV Data; {name}' + (f' ({url})' if url else '')
            if not v.get('lastUpdated'):
                v['lastUpdated'] = TODAY
            by_brand[brand] = by_brand.get(brand, 0) + added

    if len(vehicles) != original_count:
        raise RuntimeError('Vehicle count changed unexpectedly')
    catalog['vehicles'] = vehicles
    CATALOG.write_text(json.dumps(catalog, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    print(f'OpenEV source vehicles: {len(source_vehicles)}')
    print(f'Catalog vehicles: {original_count}')
    print(f'Matched unambiguously: {matched}; ambiguous: {ambiguous}')
    print(f'Missing fields filled: {changes}')
    for brand, count in sorted(by_brand.items()):
        print(f'  {brand}: +{count}')

if __name__ == '__main__':
    try:
        main()
    except Exception as exc:
        print(f'ERROR: {exc}', file=sys.stderr)
        sys.exit(1)
