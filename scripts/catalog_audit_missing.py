import json
from collections import Counter, defaultdict
from pathlib import Path
p=Path('app/src/main/assets/catalog_es_2024_2026.json')
data=json.loads(p.read_text(encoding='utf-8'))['vehicles']
fields=['usableBatteryKwh','batteryChemistry','batteryType','drivetrain','acKw','dcKw','charge10to80Min','acceleration0to100Sec','trunkLiters','weightKg']
by=defaultdict(lambda: [0,Counter(),[]])
for v in data:
    b=v.get('make','?'); by[b][0]+=1
    for f in fields:
        if v.get(f) in (None,'',0): by[b][1][f]+=1
for b in sorted(by):
    n,c,_=by[b]
    print(b,n,'; '.join(f'{f}={c[f]}' for f in fields if c[f]))
print('BRANDS',len(by))
