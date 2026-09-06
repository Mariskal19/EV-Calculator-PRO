# EV Calculator PRO v1.0.4

Aplicación Android para calcular la carga de un vehículo eléctrico y comparar vehículos eléctricos mediante sus principales características.

## Versión actual: 1.0.4

Esta versión incorpora la nueva pantalla **Comparar coches**, preparada para seleccionar y comparar 2 o 3 vehículos, además de mejoras de configuración, moneda y calidad de datos.

### Estado técnico

- Nombre de la aplicación: **EV Calculator PRO**.
- Identificador interno: `com.evcalculatorpro`.
- Versión visible: **1.0.4**.
- `versionCode`: **55**.
- `compileSdk`: 36.
- `targetSdk`: 36.
- `minSdk`: 23.
- Release optimizada con **R8** y reducción de recursos.
- Firma Release mediante GitHub Secrets.
- GitHub Actions genera APK Release, AAB Release y ZIP del proyecto.
- Java 17 y Gradle 8.9.

## Comparar coches

La pantalla **Comparar coches** permite:

- Seleccionar 2 o 3 vehículos eléctricos.
- Buscar por marca, modelo o versión.
- Mantener la selección al salir y volver a la pantalla.
- Mostrar las características agrupadas por batería/autonomía, prestaciones, carga, practicidad y precio.
- Destacar visualmente el mejor valor de cada característica comparable.
- Mostrar los precios en la moneda seleccionada en Configuración.
- Mantener un diseño horizontal de comparación con desplazamiento independiente.

## Configuración y monedas

- Idioma, moneda y tema se gestionan desde **Configuración**.
- Las preferencias se guardan de forma persistente.
- Monedas disponibles: EUR, USD, GBP, CHF, CAD y AUD.
- Los tipos de cambio se actualizan mediante las referencias del BCE, se almacenan en caché y se reutilizan sin conexión.
- La conversión afecta a la presentación de precios sin alterar la comparación relativa de precios.

## Calidad y automatización de datos de vehículos

El proyecto incluye un control automatizado de calidad para `vehicles.json` mediante `scripts/audit_vehicles.py`.

El control revisa campos obligatorios, duplicados, valores numéricos, coherencia de batería, autonomía, consumo, potencia, carga, prestaciones, completitud, fuentes y fechas de actualización.

GitHub Actions ejecuta la auditoría:

- Semanalmente.
- Cuando cambia la base de datos de vehículos.
- Cuando cambia el propio script de auditoría.
- En las ramas `main` y `feature/automatizacion-coches`.

La automatización es actualmente un **control de calidad y frescura de datos**. No introduce automáticamente datos no verificados ni realiza scraping sin validación.

## Criterio de datos

Cuando se actualizan vehículos se prioriza:

1. Fuente oficial del fabricante.
2. Segunda fuente independiente, como EV Database.
3. Identificación exacta de versión, año y mercado.
4. No mezclar generaciones ni versiones.
5. No promediar valores discrepantes ni inventar datos.
6. Dejar un dato vacío cuando no pueda verificarse con suficiente seguridad.

## Estado de estabilidad

La versión estable anterior **1.0.3.2** se conserva como referencia y no se modifica.

**Punto de rollback de la versión 1.0.4:**

`38a090b43e2c0991cf38f4e3ec21ccc8c3a860b6`

Ese commit corresponde a la versión 1.0.4 antes de los trabajos posteriores de automatización y sirve como punto seguro de recuperación.

### Ramas de trabajo

- `main`: rama principal.
- `feature/comparar-coches-1.0.4`: desarrollo de la pantalla Comparar coches.
- `feature/automatizacion-coches`: automatización y controles de calidad de datos.

## Archivos de entrega

- ZIP: `EV-Calculator-PRO-v1.0.4-source.zip`
- APK Release: `EV-Calculator-PRO-v1.0.4.apk`
- AAB Release: `EV-Calculator-PRO-v1.0.4.aab`
