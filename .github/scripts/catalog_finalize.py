#!/usr/bin/env python3
import json
import re
from collections import defaultdict
from pathlib import Path

CATALOG = Path('app/src/main/assets/catalog_es_2024_2026.json')
TODAY = '2026-09-14'

def norm(value):
    s = str(value or '').strip().lower()
    s = s.replace('á','a').replace('é','e').replace('í','i').replace('ó','o').replace('ú','u')
    s = re.sub(r'[^a-z0-9]+', ' ', s)
    return re.sub(r'\s+', ' ', s).strip()

def main():
    data = json.loads(CATALOG.read_text(encoding='utf-8'))
    vehicles = data['vehicles']
    start = len(vehicles)

    # ATTO 3 EVO Spain 2026: exactly Design and Excellence.
    atto = [v for v in vehicles if norm(v.get('make')) == 'byd' and norm(v.get('model')) == 'atto 3 evo' and int(v.get('year') or 0) == 2026]
    groups = {'Design': [], 'Excellence': []}
    for v in atto:
        ver = norm(v.get('version'))
        if ver.startswith('design'):
            groups['Design'].append(v)
        elif ver.startswith('excellence'):
            groups['Excellence'].append(v)
        else:
            raise RuntimeError(f'Unexpected ATTO 3 EVO version: {v.get("version")}')
    if not all(groups.values()):
        raise RuntimeError(f'Missing ATTO 3 EVO version: { {k: len(v) for k,v in groups.items()} }')
    atto_specs = {
        'Design': {'batteryKwh':74.8,'batteryType':'LFP','batteryChemistry':'LFP','powerKw':230,'drive':'RWD','drivetrain':'RWD','wltpKm':510,'consumptionKwh100':16.4,'acKw':11,'dcKw':220,'charge10to80Min':25,'acceleration0to100Sec':5.5,'trunkLiters':490,'weightKg':1880},
        'Excellence': {'batteryKwh':74.8,'batteryType':'LFP','batteryChemistry':'LFP','powerKw':330,'drive':'AWD','drivetrain':'AWD','wltpKm':470,'consumptionKwh100':17.8,'acKw':11,'dcKw':220,'charge10to80Min':25,'acceleration0to100Sec':3.9,'trunkLiters':490,'weightKg':1990},
    }
    for label, records in groups.items():
        keep = next((v for v in records if str(v.get('version','')).strip().lower() == label.lower()), records[0])
        for other in records:
            if other is not keep:
                for k, val in other.items():
                    if keep.get(k) in (None, '') and val not in (None, ''):
                        keep[k] = val
                vehicles.remove(other)
        keep['version'] = label
        keep.update(atto_specs[label])
        keep['source'] = 'BYD España; ficha técnica ATTO 3 EVO 2026'
        keep['lastUpdated'] = TODAY
    print('ATTO 3 EVO: 4 -> 2 records')

    # BYD SEALION 7 Spain 2024: exactly Comfort, Design AWD, Excellence AWD.
    sealion = [v for v in vehicles if norm(v.get('make')) == 'byd' and norm(v.get('model')) == 'sealion 7' and int(v.get('year') or 0) == 2024]
    if len(sealion) != 3:
        raise RuntimeError(f'Expected 3 SEALION 7 records, found {len(sealion)}')
    for v in sealion:
        ver = norm(v.get('version'))
        if ver.startswith('comfort'):
            v.update({'batteryKwh':82.5,'usableBatteryKwh':82.5,'batteryType':'LFP','batteryChemistry':'LFP','powerKw':230,'drive':'RWD','drivetrain':'RWD','wltpKm':482,'consumptionKwh100':17.1,'acKw':11,'dcKw':150,'charge10to80Min':32,'acceleration0to100Sec':6.7,'trunkLiters':520})
        elif ver.startswith('design awd'):
            v.update({'batteryKwh':82.5,'usableBatteryKwh':82.5,'batteryType':'LFP','batteryChemistry':'LFP','powerKw':390,'drive':'AWD','drivetrain':'AWD','wltpKm':456,'consumptionKwh100':18.1,'acKw':11,'dcKw':150,'charge10to80Min':32,'acceleration0to100Sec':4.5,'trunkLiters':520})
        elif ver.startswith('excellence awd'):
            v.update({'batteryKwh':91.3,'usableBatteryKwh':91.3,'batteryType':'LFP','batteryChemistry':'LFP','powerKw':390,'drive':'AWD','drivetrain':'AWD','wltpKm':502,'consumptionKwh100':18.2,'acKw':11,'dcKw':230,'charge10to80Min':24,'acceleration0to100Sec':4.5,'trunkLiters':520})
        else:
            raise RuntimeError(f'Unexpected SEALION 7 version: {v.get("version")}')
        v['source'] = 'BYD España / BYD Europe — SEALION 7 2024'
        v['lastUpdated'] = TODAY
    print('SEALION 7: verified 3 records; Excellence = 91.3 kWh')

    # Ford Explorer 2024: Ford Spain specifies RWD 79 kWh and AWD 77 kWh.
    groups = defaultdict(list)
    for v in vehicles:
        key = (norm(v.get('make')), norm(v.get('model')), int(v.get('year') or 0), norm(v.get('version')))
        groups[key].append(v)
    ford_removed = 0
    for key, records in list(groups.items()):
        make, model, year, version = key
        if make != 'ford' or model != 'explorer' or year != 2024 or len(records) <= 1:
            continue
        if 'rango extendido awd' in version:
            desired = 77.0; drive = 'AWD'
        elif 'rango extendido rwd' in version:
            desired = 79.0; drive = 'RWD'
        else:
            raise RuntimeError(f'Unexpected Ford Explorer duplicate: {key}')
        valid = [v for v in records if float(v.get('batteryKwh') or -1) == desired]
        if len(valid) != 1:
            raise RuntimeError(f'Ford Explorer duplicate lacks exactly one verified record: {key} -> {[v.get("batteryKwh") for v in records]}')
        keep = valid[0]
        keep.update({'batteryKwh':desired,'usableBatteryKwh':desired,'drive':drive,'drivetrain':drive,'batteryType':keep.get('batteryType') or 'Li-ion','source':'Ford España — Explorer eléctrico 2024','lastUpdated':TODAY})
        for other in records:
            if other is keep:
                continue
            for k, val in other.items():
                if keep.get(k) in (None, '') and val not in (None, ''):
                    keep[k] = val
            vehicles.remove(other)
            ford_removed += 1
    if ford_removed != 2:
        raise RuntimeError(f'Expected 2 Ford Explorer duplicate removals, got {ford_removed}')
    print('Ford Explorer: corrected AWD=77 kWh, RWD=79 kWh; removed 2 erroneous duplicates')

    # Final audit: no exact commercial duplicates and no duplicate IDs.
    commercial = defaultdict(list)
    for v in vehicles:
        key = (norm(v.get('make')), norm(v.get('model')), int(v.get('year') or 0), norm(v.get('version')))
        commercial[key].append(v)
    duplicates = {k: vals for k, vals in commercial.items() if len(vals) > 1}
    if duplicates:
        raise RuntimeError(f'Exact commercial duplicates remain: {list(duplicates)}')

    ids = defaultdict(list)
    for v in vehicles:
        if v.get('id'):
            ids[v['id']].append(v)
    duplicate_ids = {k: vals for k, vals in ids.items() if len(vals) > 1}
    if duplicate_ids:
        raise RuntimeError(f'Duplicate IDs remain: {list(duplicate_ids)}')

    if len(vehicles) != 607:
        raise RuntimeError(f'Unexpected final catalog count: {len(vehicles)}; expected 607 after verified deduplication')

    data['vehicles'] = vehicles
    CATALOG.write_text(json.dumps(data, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    print(f'FINAL AUDIT OK: {start} -> {len(vehicles)} vehicles; commercial duplicates=0; duplicate IDs=0')

if __name__ == '__main__':
    main()
