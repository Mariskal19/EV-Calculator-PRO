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
- El catálogo empaquetado es la base auditada y protegida: las actualizaciones remotas nunca modifican ni eliminan sus registros.
- Un catálogo incremental remoto puede añadir únicamente configuraciones nuevas que superen la validación automática.
- La app descarga ese incremento cuando tiene conexión y conserva una copia local para seguir funcionando sin conexión.
- Las nuevas configuraciones remotas no requieren publicar una nueva versión en Google Play.

## Comparar coches

La pantalla permite seleccionar 2 o 3 vehículos, buscar por marca/modelo/versión, conservar la selección y comparar batería/autonomía, prestaciones, carga, practicidad y precio. Los precios se muestran en la moneda seleccionada.

- Al volver desde Configuración, la pantalla refresca automáticamente idioma, moneda y tema si han cambiado.

## Configuración

- Idioma, moneda y tema se guardan de forma persistente.
- Idiomas: español, inglés, francés, alemán, italiano y portugués.
- Monedas: EUR, USD, GBP, CHF, CAD y AUD.
- Los tipos de cambio se actualizan mediante referencias del BCE y se almacenan en caché para reutilización sin conexión.

## Automatización del catálogo

El catálogo está automatizado mediante una combinación de **GitHub Actions + cron-job.org** porque el planificador nativo de GitHub Actions dejó de ejecutar de forma fiable los eventos `schedule`, aunque los lanzamientos manuales y por `push` seguían funcionando.

### Arquitectura actual

1. **07:00 Europe/Madrid — cron-job.org**
   - Cron-job.org realiza una petición HTTP `POST` al endpoint de GitHub Actions:
     `https://api.github.com/repos/Mariskal19/EV-Calculator-PRO/actions/workflows/catalog-auto-update.yml/dispatches`
   - Envía `{"ref":"main"}`.
   - La autenticación se realiza mediante un **Fine-grained GitHub PAT** almacenado únicamente en cron-job.org.
   - El token está limitado al repositorio `Mariskal19/EV-Calculator-PRO` y a **Actions: Read and write**.
   - El token **no** está guardado en el código ni en el repositorio.

2. **GitHub Actions — `catalog-auto-update.yml`**
   - Se ejecuta mediante `workflow_dispatch`.
   - También conserva un disparador nativo `schedule` como mecanismo adicional:
     - **08:18 Europe/Madrid todos los días**.
   - El `schedule` nativo se mantiene configurado aunque actualmente no se considera el mecanismo principal debido a los problemas observados con el planificador de GitHub.

3. **Actualización del catálogo**
   - El workflow ejecuta:
     `python3 tools/update_protected_catalog.py`
   - Fuente externa principal:
     `https://gaia-charge.github.io/evdb/v1/vehicles.json`
   - Puede existir una segunda fuente mediante la variable de repositorio `CATALOG_SOURCE_2_URL`.
   - El catálogo protegido/base nunca se sobrescribe.
   - Las configuraciones remotas se validan antes de incorporarse.
   - La protección por clave lógica evita duplicados.
   - Las exclusiones persistentes se conservan para no volver a incorporar configuraciones descartadas.
   - Si no hay cambios, no se crea ningún commit.
   - Si hay nuevas configuraciones válidas, se actualiza `app/src/main/assets/catalog_remote_additions.json` y GitHub Actions realiza el commit.

### Resultado de la primera ejecución automática verificada

La ejecución de prueba mediante cron-job.org quedó confirmada con:

- GitHub Actions run: **36771311142**
- Resultado: **éxito**
- Catálogo protegido: **676 registros intactos**
- Configuraciones remotas existentes: **36**
- Reglas de exclusión persistentes: **37**
- Nuevas configuraciones remotas validadas añadidas: **0**
- Cambios en el catálogo: **ninguno**
- Commit de catálogo: **no realizado**

Esto significa que la automatización funcionó correctamente de principio a fin y que en esa ejecución no se añadieron duplicados ni nuevas configuraciones. Las **37 reglas de exclusión persistentes no deben interpretarse como 37 duplicados nuevos**: son reglas acumuladas para configuraciones que el sistema debe mantener fuera del catálogo remoto.

### Horarios de mantenimiento

- **07:00** — mecanismo principal y probado: cron-job.org → `workflow_dispatch`.
- **08:18** — mecanismo nativo adicional de GitHub Actions, mantenido configurado.

Si en el futuro se confirma que el planificador nativo vuelve a funcionar de forma estable, se podrá valorar eliminar uno de los dos mecanismos para evitar ejecuciones duplicadas.

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

> Última verificación de build: refresco de configuración aplicado también a Charge Calculator y Electric Vs Combustion.
