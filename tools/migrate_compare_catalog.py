from pathlib import Path
import re

path = Path('app/src/main/java/com/evchargecalculator/CompararCochesActivity.java')
text = path.read_text(encoding='utf-8')
pattern = re.compile(r'    private void loadVehicles\(\) \{.*?\n    \}\n\n    private void loadAssetVehicles', re.S)
replacement = '''    private void loadVehicles() {
        vehicles.clear();
        try (InputStream in = getAssets().open("catalog_es_2024_2026.json");
             BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }
            JSONArray a = new JSONObject(sb.toString()).optJSONArray("vehicles");
            if (a == null) {
                throw new IllegalStateException("catalog_es_2024_2026.json: vehicles array missing");
            }
            for (int i = 0; i < a.length(); i++) {
                JSONObject o = a.optJSONObject(i);
                if (o != null) {
                    vehicles.add(new Vehicle(o));
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("No se ha podido cargar el catálogo español", e);
        }
        if (vehicles.isEmpty()) {
            throw new IllegalStateException("El catálogo español está vacío");
        }
    }

    private void loadAssetVehicles'''
updated, count = pattern.subn(replacement, text, count=1)
if count != 1:
    raise SystemExit('No se encontró el método loadVehicles esperado')
path.write_text(updated, encoding='utf-8')
print('CompararCochesActivity now reads only catalog_es_2024_2026.json')
