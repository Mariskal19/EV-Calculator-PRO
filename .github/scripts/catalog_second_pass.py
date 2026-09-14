#!/usr/bin/env python3
import json
import re
from datetime import date
from pathlib import Path
from urllib.request import Request, urlopen

CATALOG = Path('app/src/main/assets/catalog_es_2024_2026.json')
LATEST_API = 'https://api.github.com/repos/open-ev-data/open-ev-data-dataset/releases/latest'
TODAY = date.today().isoformat()
FIELDS = ('usableBatteryKwh','batteryChemistry','drivetrain','acKw','dcKw','charge10to80Min','acceleration0to100Sec','trunkLiters','weightKg')
ALIASES = {'citroën':'citroen','ds automobiles':'ds','ds':'ds','skoda':'skoda','š koda':'skoda','mini':'mini','smart':'smart','xpen g':'xpeng','xpeng':'xpeng'}

def norm(s):
    s = str(s or '').lower().strip()
    s = s.replace('é','e').replace('è','e').replace('ë','e').replace('ï','i').replace('ö','o').replace('ü','u').replace('š','s').replace('º','o')
    s = re.sub(r'[^a-z0-9]+',' ', s)
    return re.sub(r'\s+', ' ', s).strip()

def brand(s):
    n = norm(s)
    return ALIASES.get(n, n)

def num(v):
    try: return float(v)
    except Exception: return None

def first(d, *keys):
    for k in keys:
        if isinstance(d, dict) and d.get(k) not in (None, ''):
            return d[k]
    return None

def source_fields(v):
    b=v.get('battery') or {}; p=v.get('powertrain') or {}; c=v.get('charging') or {}; ac=c.get('ac') or {}; dc=c.get('dc') or {}; perf=v.get('performance') or {}; cap=v.get('capacity') or {}; w=v.get('weights') or {}
    charge=None
    ct=c.get('charging_time')
    if isinstance(ct,dict):
        for row in ct.get('dc',[]):
            if isinstance(row,dict) and num(row.get('from_soc_percent'))==10 and num(row.get('to_soc_percent'))==80:
                charge=num(row.get('time_min')); break
    return {
        'usableBatteryKwh': num(first(b,'pack_capacity_kwh_net','capacity_kwh')),
        'batteryChemistry': first(b,'chemistry'),
        'drivetrain': str(first(p,'drivetrain') or v.get('drive_type') or '').upper() or None,
        'acKw': num(first(ac,'max_power_kw')),
        'dcKw': num(first(dc,'max_power_kw') or v.get('charging_speed_kw')),
        'charge10to80Min': charge,
        'acceleration0to100Sec': num(first(perf,'acceleration_0_100_kmh_s','acceleration_0_100_kmh') or v.get('acceleration_0_100_kmh')),
        'trunkLiters': num(first(cap,'cargo_l')),
        'weightKg': num(first(w,'curb_weight_kg')),
    }

def load_openev():
    req=Request(LATEST_API,headers={'User-Agent':'EV-Calculator-PRO second catalog pass','Accept':'application/vnd.github+json'})
    with urlopen(req,timeout=60) as r: release=json.load(r)
    tag=release.get('tag_name')
    assets=release.get('assets') or []
    asset=next((a for a in assets if a.get('name')==f'open-ev-data-{tag}.json'),None) or next((a for a in assets if a.get('name','').endswith('.json') and 'open-ev-data' in a.get('name','')),None)
    if not asset: raise RuntimeError('OpenEV JSON release asset not found')
    req=Request(asset['browser_download_url'],headers={'User-Agent':'EV-Calculator-PRO second catalog pass'})
    with urlopen(req,timeout=120) as r: data=json.load(r)
    vehicles=data.get('vehicles') if isinstance(data,dict) else data
    if not isinstance(vehicles,list): raise RuntimeError('OpenEV vehicles array missing')
    print('OpenEV release:', tag, 'records:', len(vehicles))
    return vehicles

def signature(v):
    return (brand(v.get('make')), norm(v.get('model')), int(v.get('year') or 0), round(num(v.get('batteryKwh')) or -1,1), round(num(v.get('powerKw')) or -1,1), norm(v.get('drive') or v.get('drivetrain')))

def source_score(v,s):
    if brand(v.get('make')) != brand(s.get('make') or s.get('brand')): return -999
    if int(v.get('year') or 0) != int(s.get('year') or 0): return -999
    vm=set(norm(v.get('model')).split()); sm=set(norm((s.get('model') if not isinstance(s.get('model'),dict) else s['model'].get('name')).split())
    if not vm or not sm or not (vm.issubset(sm) or sm.issubset(vm) or vm==sm): return -999
    score=20
    sf=source_fields(s)
    sb=num(s.get('batteryKwh') or (s.get('battery') or {}).get('pack_capacity_kwh_gross') or (s.get('battery') or {}).get('capacity_kwh'))
    if sb is not None and v.get('batteryKwh') is not None:
        score += 8 if abs(float(v['batteryKwh'])-sb)<=2.5 else -8
    sp=sf.get('drivetrain')
    vd=norm(v.get('drive') or v.get('drivetrain'))
    if sp and vd: score += 5 if sp==vd.upper() else -5
    power=num(v.get('powerKw'))
    rawp=first(s.get('powertrain') or {},'system_power_kw','power_kw')
    if power is not None and rawp is not None: score += 6 if abs(power-num(rawp))<=3 else -4
    overlap=len(set(norm(v.get('version')).split()) & set(norm(s.get('variant') or s.get('trim') or s.get('version')).split()))
    return score + min(overlap,8)

def unique_peer_values(vehicles, target, key):
    sig=signature(target)
    vals=[]
    for other in vehicles:
        if other is target or signature(other)!=sig: continue
        value=other.get(key)
        if value not in (None,'',0):
            vals.append(value)
    unique=[]
    for value in vals:
        if value not in unique: unique.append(value)
    return unique[0] if len(unique)==1 else None

def main():
    catalog=json.loads(CATALOG.read_text(encoding='utf-8'))
    vehicles=catalog.get('vehicles')
    if not isinstance(vehicles,list): raise RuntimeError('Catalog vehicles array missing')
    original=len(vehicles)
    openev=load_openev()
    changes_peer=changes_source=0
    by_field={k:0 for k in FIELDS}

    # Pass A: use unambiguous data already present in the same catalog. This is
    # deliberately conservative: only identical technical signatures and one
    # unique peer value are propagated.
    for v in vehicles:
        for key in FIELDS:
            if v.get(key) in (None,'',0):
                value=unique_peer_values(vehicles,v,key)
                if value not in (None,'',0):
                    v[key]=value; changes_peer+=1; by_field[key]+=1

    # Pass B: re-query the latest OpenEV release with relaxed model matching.
    for v in vehicles:
        candidates=[]
        for s in openev:
            sc=source_score(v,s)
            if sc>=20: candidates.append((sc,s))
        candidates.sort(key=lambda x:x[0],reverse=True)
        if not candidates: continue
        if len(candidates)>1 and candidates[0][0]==candidates[1][0]: continue
        sf=source_fields(candidates[0][1])
        added=False
        for key in FIELDS:
            if v.get(key) in (None,'',0) and sf.get(key) not in (None,'',0):
                v[key]=sf[key]; changes_source+=1; by_field[key]+=1; added=True
        if added:
            if not v.get('source'): v['source']='OpenEV Data — second enrichment pass'
            v['lastUpdated']=TODAY

    if len(vehicles)!=original: raise RuntimeError('Vehicle count changed')
    CATALOG.write_text(json.dumps(catalog,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    print('Catalog vehicles:', original)
    print('Filled from unambiguous catalog peers:', changes_peer)
    print('Filled from latest OpenEV:', changes_source)
    print('Total second-pass fields:', changes_peer+changes_source)
    for key in FIELDS: print(f'  {key}: +{by_field[key]}')
    remaining={k:sum(1 for v in vehicles if v.get(k) in (None,'',0)) for k in FIELDS}
    print('Remaining blanks:', remaining)

if __name__=='__main__':
    main()
