import json
import re
import unicodedata
from pathlib import Path

CATALOG = Path('app/src/main/assets/catalog_es_2024_2026.json')
SOURCE_ROOT = Path('/tmp/open-ev-data-dataset/src')
BRANDS = {
    'Abarth': 'abarth', 'Alfa Romeo': 'alfa_romeo', 'Alpine': 'alpine',
    'Audi': 'audi', 'BMW': 'bmw', 'BYD': 'byd', 'Citroën': 'citroen',
    'CUPRA': 'cupra', 'Dacia': 'dacia', 'DS': 'ds_automobiles',
    'DS Automobiles': 'ds_automobiles'
}

def norm(value):
    value = unicodedata.normalize('NFKD', str(value or '')).encode('ascii','ignore').decode()
    return re.sub(r'[^a-z0-9]+', '', value.lower())

def source_records(brand_dir):
    out=[]
    for p in brand_dir.rglob('*.json'):
        try: o=json.loads(p.read_text(encoding='utf-8'))
        except Exception: continue
        if isinstance(o,dict) and o.get('year') and o.get('model',{}).get('name') and o.get('trim',{}).get('name') and o.get('powertrain') and o.get('battery'):
            out.append(o)
    return out

def choose(v, candidates):
    scored=[]
    vm=norm(v.get('model')); vp=v.get('powerKw'); vb=v.get('batteryKwh'); vd=str(v.get('drive','')).lower()
    for s in candidates:
        if s.get('year') != v.get('year'): continue
        sm=norm(s.get('model',{}).get('name'))
        if not vm or not sm: continue
        score=100 if vm==sm else (75 if vm in sm or sm in vm else 0)
        if not score: continue
        pt=s.get('powertrain') or {}; bat=s.get('battery') or {}
        sp=pt.get('system_power_kw'); sg=bat.get('pack_capacity_kwh_gross'); sd=str(pt.get('drivetrain','')).lower()
        if vp is not None and sp is not None: score += 35 if abs(sp-vp)<0.01 else (10 if abs(sp-vp)<=10 else 0)
        if vb is not None and sg is not None: score += 30 if abs(sg-vb)<=1.5 else (8 if abs(sg-vb)<=4 else 0)
        if vd and sd and vd==sd: score += 20
        vt=norm(v.get('version')); st=norm(s.get('trim',{}).get('name'))
        if vt and st and (st in vt or vt in st): score += 15
        scored.append((score,s))
    if not scored: return None
    scored.sort(key=lambda x:x[0],reverse=True)
    return scored[0][1] if scored[0][0] >= 130 else None

def chem(value):
    x=str(value or '').lower()
    if 'lfp' in x: return 'LFP'
    if 'nmc' in x: return 'NMC'
    if 'nca' in x: return 'NCA'
    if 'li-ion' in x or 'liion' in x: return 'Li-ion'
    return value or None

def enrich(v,s):
    b=s.get('battery') or {}; pt=s.get('powertrain') or {}; ch=s.get('charging') or {}
    ac=ch.get('ac') or {}; dc=ch.get('dc') or {}; perf=s.get('performance') or {}
    weights=s.get('weights') or {}; cap=s.get('capacity') or {}
    values={
      'usableBatteryKwh':b.get('pack_capacity_kwh_net'),
      'batteryChemistry':chem(b.get('chemistry')),
      'batteryType':chem(b.get('chemistry')),
      'drivetrain':str(pt.get('drivetrain','')).upper() or None,
      'acKw':ac.get('max_power_kw'),'dcKw':dc.get('max_power_kw'),
      'acceleration0to100Sec':perf.get('acceleration_0_100_kmh_s'),
      'trunkLiters':cap.get('cargo_l'),'weightKg':weights.get('curb_weight_kg')}
    filled=[]
    for k,val in values.items():
        if val not in (None,'',0) and v.get(k) in (None,'',0): v[k]=val; filled.append(k)
    if filled:
        v.setdefault('source','OpenEV Data v1.24.0; source references from OpenEV dataset')
        v.setdefault('lastUpdated','2026-09-14')
    return filled

def main():
    data=json.loads(CATALOG.read_text(encoding='utf-8')); stats={}
    sources={brand:source_records(SOURCE_ROOT/slug) for brand,slug in BRANDS.items() if (SOURCE_ROOT/slug).exists()}
    for v in data.get('vehicles',[]):
        brand=v.get('make')
        if brand not in BRANDS: continue
        st=stats.setdefault(brand,{'vehicles':0,'matched':0,'filled':0,'fields':{}}); st['vehicles']+=1
        if v.get('drivetrain') in (None,'',0) and v.get('drive'):
            v['drivetrain']=str(v['drive']).upper(); st['filled']+=1; st['fields']['drivetrain']=st['fields'].get('drivetrain',0)+1
        s=choose(v,sources.get(brand,[]))
        if s:
            st['matched']+=1
            for f in enrich(v,s): st['filled']+=1; st['fields'][f]=st['fields'].get(f,0)+1
    CATALOG.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    print('BLOCK 1 ENRICHMENT SUMMARY')
    for b,s in stats.items(): print(f"{b}: vehicles={s['vehicles']} matched={s['matched']} filled={s['filled']} fields={s['fields']}")
    print('TOTAL_FILLED='+str(sum(s['filled'] for s in stats.values())))

if __name__=='__main__': main()
