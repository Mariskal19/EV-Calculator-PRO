# EV Calculator PRO v1.0.1

Aplicación Android para calcular la carga de un vehículo eléctrico: tiempo necesario, coste de carga y hora de inicio recomendada.

## Versión actual: 1.0.1

Esta es la versión actual del proyecto en GitHub y la base de referencia para la preparación de su publicación en Google Play.

### Estado técnico

- Nombre de la aplicación: **EV Calculator PRO**.
- Identificador interno: `com.evcalculatorpro`.
- La pantalla actual se mantiene como **EV Charge Calculator**.
- `compileSdk`: 36.
- `targetSdk`: 36.
- `minSdk`: 23.
- Release optimizada con **R8** y reducción de recursos (`minifyEnabled` + `shrinkResources`).
- Sin permisos especiales declarados en el `AndroidManifest.xml`.
- Icono launcher configurado mediante adaptive icon.
- GitHub Actions genera APK Release y Android App Bundle (AAB) Release.
- GitHub Actions utiliza Java 17, Gradle 8.9, `actions/checkout@v5`, `actions/setup-java@v5` y `actions/upload-artifact@v6`.
- El workflow ya no modifica ni hace commits automáticos sobre el código fuente.
- La firma Release requiere la upload key configurada mediante GitHub Secrets.

## Limpieza y optimización realizada

- Activada la optimización R8 en Release.
- Activada la reducción de recursos no utilizados.
- Centralizado el nombre de la aplicación en `strings.xml` y utilizado desde el manifest.
- Eliminado del workflow el paso que modificaba `MainActivity.java` automáticamente y hacía `git push`.
- Reducidos los permisos de GitHub Actions a `contents: read`.
- El workflow queda limitado a la rama `main`.
- La compilación Release no permite fallback a una firma debug.
- Configurada una upload key específica para la publicación de EV Calculator PRO mediante GitHub Secrets.

## Google Play

La base técnica queda preparada para la fase final de publicación. Antes de subir el primer AAB habrá que completar en Play Console la ficha de la aplicación, capturas de pantalla, política de privacidad, Data Safety, clasificación de contenido y demás formularios requeridos.

## Archivos de entrega

- ZIP: `EV-Calculator-PRO-v1.0.1.zip`
- APK Release: `EV-Calculator-PRO-v1.0.1.apk`
- AAB Release: `EV-Calculator-PRO-v1.0.1.aab`
