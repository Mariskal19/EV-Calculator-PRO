#!/usr/bin/env python3
import json, re, sys
from datetime import date
from pathlib import Path
from urllib.request import urlopen, Request

CATALOG = Path('app/src/main/assets/catalog_es_2024_2026.json')
LATEST_API = 'https://api.github.com/repos/open-ev-data/open-ev-data-dataset/releases/latest'
TODAY = date.today().isoformat()
BRAND_ALIASES = {'ds':'ds automobiles','citroën':'citroen','skoda':'skoda','š koda':'skoda','mini':'mini','smart':'smart','range rover':'land rover'}

def norm(s):
    s = str(s or '').lower().strip()
    s = s.replace('é','e').replace('è','e').replace('ë','e').replace('ï','i').replace('ö','o').replace('ü','u').replace('š','s')
    s = re.sub(r'[^a-z0-9]+', ' ', s)
    return re.sub(r'\s+', ' ', s).strip()

def num(v):
    try: return float(v)
    except Exception: return None

def first(d, *keys):
    for k in keys:
        if isinstance(d, dict) and d.get(k) not in (None, ''): return d[k]
    return None

def flatten_sources(v):
    out=[]
    for s in v.get('sources',[]) if isinstance(v,dict) else []:
        if not isinstance(s,dict): continue
        name=first(s,'name','source_name','title'); url=first(s,'url','source_url')
        if name or url: out.append((name or 'OpenEV Data',url))
    return out

def candidate_fields(v):
    b=v.get('battery') or {}; p=v.get('powertrain') or {}; c=v.get('charging') or {}; ac=c.get('ac') or {}; dc=c.get('dc') or {}
    perf=v.get('performance') or {}; cap=v.get('capacity') or {}; w=v.get('weights') or {}
    battery=first(b,'pack_capacity_kwh_net','capacity_kwh'); power=first(p,'system_power_kw','power_kw'); drive=first(p,'drivetrain') or v.get('drive_type')
    accel=first(perf,'acceleration_0_100_kmh_s','acceleration_0_100_kmh') or v.get('acceleration_0_100_kmh')
    ac_kw=first(ac,'max_power_kw'); dc_kw=first(dc,'max_power_kw') or v.get('charging_speed_kw'); charge_time=None
    ct=c.get('charging_time')
    if isinstance(ct,dict):
        for row in ct.get('dc',[]):
            if isinstance(row,dict) and num(row.get('from_soc_percent'))==10 and num(row.get('to_soc_percent'))==80:
                charge_time=row.get('time_min'); break
    return {'battery':num(battery),'chemistry':first(b,'chemistry'),'power':num(power),'drive':str(drive or '').upper(),'ac':num(ac_kw),'dc':num(dc_kw),'charge_time':num(charge_time),'accel':num(accel),'trunk':num(first(cap,'cargo_l')),'weight':num(first(w,'curb_weight_kg')),'sources':flatten_sources(v)}

def load_source():
    req=Request(LATEST_API,headers={'User-Agent':'EV-Calculator-PRO catalog enrichment','Accept':'application/vnd.github+json'})
    with urlopen(req,timeout=60) as r: release=json.load(r)
    tag=release.get('tag_name'); assets=release.get('assets') or []
    if not tag: raise RuntimeError('Could not determine OpenEV latest release tag')
    asset=next((a for a in assets if a.get('name')==f'open-ev-data-{tag}.json'),None) or next((a for a in assets if a.get('name','').endswith('.json') and 'open-ev-data' in a.get('name','')),None)
    if asset is None: raise RuntimeError(f'No OpenEV JSON asset found for {tag}')
    req=Request(asset['browser_download_url'],headers={'User-Agent':'EV-Calculator-PRO catalog enrichment'})
    with urlopen(req,timeout=120) as r: data=json.load(r)
    vehicles=data.get('vehicles') if isinstance(data,dict) else data
    if not isinstance(vehicles,list): raise RuntimeError('OpenEV Data JSON has no vehicles array')
    print(f'Using OpenEV Data {tag}')
    return vehicles

def src_brand(src):
    if isinstance(src.get('make'),dict): return src['make'].get('name')
    return src.get('brand') or src.get('make')

def src_model(src):
    if isinstance(src.get('model'),dict): return src['model'].get('name')
    return src.get('model')

def score(target,src):
    tb=BRAND_ALIASES.get(norm(target.get('make')),norm(target.get('make'))); sb=BRAND_ALIASES.get(norm(src_brand(src)),norm(src_brand(src)))
    if tb!=sb: return -999
    tm=norm(target.get('model')); sm=norm(src_model(src)); tt=set(tm.split()); st=set(sm.split())
    if tm==sm: s=12
    elif len(tt)>=2 and tt.issubset(st): s=9
    elif len(st)>=2 and st.issubset(tt): s=8
    else: return -999
    if int(target.get('year',0))!=int(src.get('year',0) or 0): return -999
    c=candidate_fields(src); s+=10
    tv=num(target.get('powerKw'))
    if tv is not None and c['power'] is not None: s+=8 if abs(tv-c['power'])<=2 else -5
    tv=num(target.get('batteryKwh'))
    if tv is not None and c['battery'] is not None: s+=5 if abs(tv-c['battery'])<=2.5 else -3
    td=str(target.get('drive') or target.get('drivetrain') or '').upper()
    if td and c['drive']: s+=4 if td==c['drive'] else -4
    version=norm(target.get('version')); variant=norm(src.get('variant') or src.get('trim') or src.get('version'))
    if version and variant:
        overlap=len(set(re.findall(r'[a-z0-9]+',version)) & set(re.findall(r'[a-z0-9]+',variant)))
        s+=min(6,overlap)
    return s

def set_missing(v,key,value):
    if value in (None,'',0) or v.get(key) not in (None,'',0): return False
    v[key]=value; return True

def main():
    source_vehicles=load_source(); catalog=json.loads(CATALOG.read_text(encoding='utf-8')); vehicles=catalog.get('vehicles')
    if not isinstance(vehicles,list): raise RuntimeError('Catalog vehicles array missing')
    original_count=len(vehicles); changes=matched=ambiguous=0; by_brand={}; unmatched={}
    for v in vehicles:
        scored=sorted(((score(v,x),x) for x in source_vehicles if score(v,x)>=18),key=lambda z:z[0],reverse=True)
        if not scored:
            k=(v.get('make'),v.get('model')); unmatched[k]=unmatched.get(k,0)+1; continue
        if len(scored)>1 and scored[0][0]==scored[1][0]: ambiguous+=1; continue
        matched+=1; c=candidate_fields(scored[0][1]); added=0
        for key,value in {'usableBatteryKwh':c['battery'],'batteryChemistry':c['chemistry'],'drivetrain':c['drive'],'acKw':c['ac'],'dcKw':c['dc'],'charge10to80Min':c['charge_time'],'acceleration0to100Sec':c['accel'],'trunkLiters':c['trunk'],'weightKg':c['weight']}.items():
            if set_missing(v,key,value): changes+=1; added+=1
        if added:
            if not v.get('source') and c['sources']:
                name,url=c['sources'][0]; v['source']=f'OpenEV Data; {name}'+(f' ({url})' if url else '')
            if not v.get('lastUpdated'): v['lastUpdated']=TODAY
            by_brand[v.get('make','Unknown')]=by_brand.get(v.get('make','Unknown'),0)+added
    if len(vehicles)!=original_count: raise RuntimeError('Vehicle count changed unexpectedly')
    CATALOG.write_text(json.dumps(catalog,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    print(f'Catalog vehicles: {original_count}; matched: {matched}; ambiguous: {ambiguous}; missing fields filled: {changes}')
    for brand,count in sorted(by_brand.items()): print(f'  {brand}: +{count}')
    print(f'Unmatched model groups: {len(unmatched)}')
    for (brand,model),count in sorted(unmatched.items()): print(f'  UNMATCHED {brand} / {model}: {count}')

if __name__=='__main__':
    try: main()
    except Exception as exc: print(f'ERROR: {exc}',file=sys.stderr); sys.exit(1)
