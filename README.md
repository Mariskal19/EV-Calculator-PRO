# EV Calculator PRO v1.0.3

Aplicación Android para calcular la carga de un vehículo eléctrico: tiempo necesario, coste de carga y hora de inicio recomendada.

## Versión actual: 1.0.3

Esta es la versión actual del proyecto en GitHub y la versión preparada para su subida a Google Play.

### Estado técnico

- Nombre de la aplicación: **EV Calculator PRO**.
- Identificador interno: `com.evcalculatorpro`.
- Versión: **1.0.3** (`versionCode 51`).
- La pantalla de carga se mantiene como **EV Charge Calculator**.
- `compileSdk`: 36.
- `targetSdk`: 36.
- `minSdk`: 23.
- Release optimizada con **R8** y reducción de recursos (`minifyEnabled` + `shrinkResources`).
- Sin permisos especiales declarados en el `AndroidManifest.xml`.
- Icono launcher configurado mediante adaptive icon.
- GitHub Actions genera APK Release y Android App Bundle (AAB) Release.
- GitHub Actions utiliza Java 17, Gradle 8.9, `actions/checkout@v5`, `actions/setup-java@v5` y `actions/upload-artifact@v6`.
- El workflow no modifica ni hace commits automáticos sobre el código fuente.
- El workflow está limitado a la rama `main`.
- La firma Release requiere la upload key configurada mediante GitHub Secrets.

## Cambios de la versión 1.0.3

- Corregida la actualización del idioma al volver a la pantalla principal después de cambiarlo desde **EV Charge Calculator** o **Electric Vs Combustion Calculator**.
- El idioma seleccionado se conserva entre pantallas mediante las preferencias de la aplicación.
- La actualización del idioma en la pantalla principal no reconstruye la interfaz (`recreate()`), evitando cambios de geometría o movimientos visuales.
- Mantiene los seis idiomas disponibles: español, inglés, francés, alemán, italiano y portugués.
- Diseño, posiciones, tamaños y comportamiento del scroll se mantienen sin cambios.

## Estado de estabilidad

**Versión estable:** 1.0.3

**Commit estable:** `0f9d29b92213d17545b08de2d9f0c518a85d3612`

**Fecha de estabilización:** 1 de septiembre de 2026, 18:57.

Esta versión fue probada manualmente y se verificó el cambio de idioma entre las pantallas Charge, Electric y Principal sin el efecto de tembleque observado anteriormente.

### Puntos de rollback

- **01/09/2026 · 18:57 — estable actual:** `0f9d29b92213d17545b08de2d9f0c518a85d3612`
- **01/09/2026 · 18:46 — estable anterior:** `85684e2d4bf6390f90ced9d7b0f4b59f18dc6fc3`

## Google Play

El proyecto queda preparado para generar el **Android App Bundle (AAB) Release de la versión 1.0.3** para Google Play Console. Antes de publicar hay que completar o revisar en Play Console la ficha de la aplicación, capturas de pantalla, política de privacidad, Data Safety, clasificación de contenido y los formularios requeridos.

## Archivos de entrega

- ZIP: `EV-Calculator-PRO-v1.0.3.zip`
- APK Release: `EV-Calculator-PRO-v1.0.3.apk`
- AAB Release: `EV-Calculator-PRO-v1.0.3.aab`
