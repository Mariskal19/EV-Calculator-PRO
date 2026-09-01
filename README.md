# EV Calculator PRO v1.0.3.1

Aplicación Android para calcular la carga de un vehículo eléctrico: tiempo necesario, coste de carga y hora de inicio recomendada.

## Versión en desarrollo: 1.0.3.1

Esta versión parte de la versión estable **1.0.3 (52)** y añade una nueva pantalla de Configuración.

### Estado técnico

- Nombre de la aplicación: **EV Calculator PRO**.
- Identificador interno: `com.evcalculatorpro`.
- Versión visible: **1.0.3.1**.
- `versionCode`: **53**.
- La pantalla de carga se mantiene como **EV Charge Calculator**.
- `compileSdk`: 36.
- `targetSdk`: 36.
- `minSdk`: 23.
- Release optimizada con **R8** y reducción de recursos (`minifyEnabled` + `shrinkResources`).
- Sin permisos especiales declarados en el `AndroidManifest.xml`.
- Icono launcher configurado mediante adaptive icon.
- GitHub Actions genera APK Release y Android App Bundle (AAB) Release.
- GitHub Actions utiliza Java 17, Gradle 8.9, `actions/checkout@v5`, `actions/setup-java@v5` y `actions/upload-artifact@v6`.
- La firma Release requiere la upload key configurada mediante GitHub Secrets.

## Cambios de la versión 1.0.3.1

- Nueva pantalla **Configuración**, accesible desde el menú de la aplicación.
- Agrupación de las preferencias de **Idioma**, **Moneda** y **Tema**.
- El cambio de tema queda integrado en Configuración con tres opciones: Automático, Claro y Oscuro.
- El selector de idioma queda integrado en Configuración y mantiene los seis idiomas actuales.
- Nueva selección de moneda con EUR, USD, GBP, CHF, CAD y AUD.
- Las preferencias de configuración se guardan de forma persistente.
- La pantalla de Configuración utiliza un diseño tecnológico luminoso para el tema claro y una variante oscura para el tema oscuro.
- Se mantiene la solución que evita el tembleque al cambiar de idioma.
- No se modifica el diseño de las pantallas Principal, Charge y Electric salvo el acceso a Configuración desde el menú.

## Estado de estabilidad

**Versión estable de partida:** 1.0.3 (52)

**Commit estable de partida:** `0f9d29b92213d17545b08de2d9f0c518a85d3612`

**Nueva versión en pruebas:** 1.0.3.1 (53)

Esta versión **todavía no está declarada como estable**. Debe compilarse y probarse antes de fijar un nuevo punto de rollback.

### Puntos de rollback

- **01/09/2026 · 18:57 — estable funcional:** `0f9d29b92213d17545b08de2d9f0c518a85d3612`
- **01/09/2026 · 18:46 — estable anterior:** `85684e2d4bf6390f90ced9d7b0f4b59f18dc6fc3`
- **01/09/2026 — estable Google Play 1.0.3 (52):** ejecución GitHub Actions `33535755241`

## Google Play

La versión **1.0.3 (52)** es la versión que se ha enviado a Google Play Console y queda conservada como referencia. La versión 1.0.3.1 utiliza `versionCode 53` para evitar reutilizar el código 52.

## Archivos de entrega

- ZIP: `EV-Calculator-PRO-v1.0.3.1.zip`
- APK Release: `EV-Calculator-PRO-v1.0.3.1.apk`
- AAB Release: `EV-Calculator-PRO-v1.0.3.1.aab`
