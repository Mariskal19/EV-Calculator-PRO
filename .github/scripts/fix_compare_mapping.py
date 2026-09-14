from pathlib import Path
import re

path = Path("app/src/main/java/com/evchargecalculator/CompararCochesActivity.java")
s = path.read_text(encoding="utf-8")

old = '''        Vehicle(JSONObject o){make=o.optString("make",o.optString("brand",""));model=o.optString("model","");version=o.optString("version",o.optString("trim",""));batteryType=o.optString("batteryType","");drivetrain=o.optString("drivetrain","");market=o.optString("market",o.optString("mercado","ES")).toUpperCase(Locale.ROOT);year=o.optInt("year",o.optInt("modelYear",0));price=o.optDouble("price",0);batteryKwh=o.optDouble("batteryKwh",o.optDouble("battery_capacity_kwh",0));usableBatteryKwh=o.optDouble("usableBatteryKwh",0);wltpKm=o.optDouble("wltpKm",o.optDouble("rangeKm",0));consumption=o.optDouble("consumption",0);if(consumption<=0)consumption=o.optDouble("consumptionKwh100",0);powerKw=o.optDouble("powerKw",0);acKw=o.optDouble("acKw",0);dcKw=o.optDouble("dcKw",0);chargeMin=o.optDouble("chargeMin",0);acc=o.optDouble("acc",o.optDouble("acceleration",0));trunk=o.optDouble("trunk",o.optDouble("trunkLiters",0));weight=o.optDouble("weight",0);id=o.optString("id","").trim();if(id.isEmpty()){String key=(make+"|"+model+"|"+market+"|"+year+"|"+String.format(Locale.US,"%.1f",batteryKwh)+"|"+version).trim().toLowerCase(Locale.ROOT);id="catalog-"+Integer.toHexString(key.hashCode());}}'''

new = '''        Vehicle(JSONObject o){
            make=o.optString("make",o.optString("brand",""));
            model=o.optString("model","");
            version=o.optString("version",o.optString("trim",""));
            batteryType=o.optString("batteryChemistry",o.optString("batteryType",o.optString("chemistry","")));
            drivetrain=o.optString("drivetrain",o.optString("drive",o.optString("drivetrainType","")));
            market=o.optString("market",o.optString("mercado","ES")).toUpperCase(Locale.ROOT);
            year=o.optInt("year",o.optInt("modelYear",0));
            price=o.optDouble("price",0);
            batteryKwh=o.optDouble("batteryKwh",o.optDouble("battery_capacity_kwh",0));
            usableBatteryKwh=o.optDouble("usableBatteryKwh",o.optDouble("netBatteryKwh",o.optDouble("batteryNetKwh",0)));
            wltpKm=o.optDouble("wltpKm",o.optDouble("rangeKm",0));
            consumption=o.optDouble("consumption",0);
            if(consumption<=0)consumption=o.optDouble("consumptionKwh100",0);
            powerKw=o.optDouble("powerKw",0);
            if(powerKw<=0)powerKw=o.optDouble("power_kW",0);
            acKw=o.optDouble("acKw",0);
            if(acKw<=0)acKw=o.optDouble("acChargeKw",0);
            dcKw=o.optDouble("dcKw",0);
            if(dcKw<=0)dcKw=o.optDouble("dcChargeKw",0);
            chargeMin=o.optDouble("charge10to80Min",o.optDouble("chargeMin",o.optDouble("chargeTime10to80Min",0)));
            acc=o.optDouble("acceleration0to100Sec",o.optDouble("acc",o.optDouble("acceleration",0)));
            trunk=o.optDouble("trunkLiters",o.optDouble("trunk",0));
            weight=o.optDouble("weightKg",o.optDouble("weight",0));
            id=o.optString("id","").trim();
            if(id.isEmpty()){String key=(make+"|"+model+"|"+market+"|"+year+"|"+String.format(Locale.US,"%.1f",batteryKwh)+"|"+version).trim().toLowerCase(Locale.ROOT);id="catalog-"+Integer.toHexString(key.hashCode());}
        }'''

if old not in s:
    raise SystemExit("Vehicle constructor pattern not found")
s = s.replace(old, new, 1)
path.write_text(s, encoding="utf-8")
print("Compare Vehicle mapping updated for current catalog field names and legacy fallbacks.")
