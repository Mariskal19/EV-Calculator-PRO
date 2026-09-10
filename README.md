# EV Calculator PRO v1.0.4

Aplicación Android para calcular la carga de un vehículo eléctrico y comparar vehículos eléctricos mediante sus principales características.

## Versión actual

- Versión visible: **1.0.4**
- `versionCode`: **55**
- `applicationId`: `com.evcalculatorpro`
- `minSdk`: 23
- `targetSdk`: 35
- `compileSdk`: 36
- Release con R8 y reducción de recursos
- Firma Release mediante GitHub Secrets
- Java 17 + Gradle 8.9

## Catálogo español definitivo

El proyecto utiliza un único catálogo maestro:

`app/src/main/assets/catalog_es_2024_2026.json`

Criterio: **MERCADO → MARCA → MODELO → AÑO DE LLEGADA → VERSIONES**.

- Mercado: España (`ES`)
- Años: 2024, 2025 y 2026
- Catálogo cerrado el **10/09/2026**
- 611 configuraciones en el catálogo cerrado
- `consumptionKwh100` debe estar informado cuando existen batería y WLTP
- No se regeneran datos automáticamente
- GitHub Actions valida el catálogo existente; no añade ni modifica vehículos

## Comparar coches

La pantalla permite seleccionar 2 o 3 vehículos, buscar por marca/modelo/versión, conservar la selección y comparar batería/autonomía, prestaciones, carga, practicidad y precio. Los precios se muestran en la moneda seleccionada.

## Configuración

- Idioma, moneda y tema se guardan de forma persistente.
- Idiomas: español, inglés, francés, alemán, italiano y portugués.
- Monedas: EUR, USD, GBP, CHF, CAD y AUD.
- Los tipos de cambio se actualizan mediante referencias del BCE y se almacenan en caché para reutilización sin conexión.

## Automatización y calidad

La automatización de generación de catálogo anterior ha sido retirada de `main`. El catálogo definitivo se mantiene como dato versionado y las workflows actuales realizan únicamente validaciones.

### Copia definitiva del proyecto

La copia completa anterior a la auditoría final está preservada en la rama:

`definitivo-2026-09-10-2150`

Cierre de la copia: **10 de septiembre de 2026 · 21:50 (Europe/Madrid)**.

## Entrega Release

GitHub Actions genera y comprueba:

- APK Release
- AAB Release
- ZIP completo del proyecto

Los nombres de entrega se generan a partir de `versionName`.
