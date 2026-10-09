# EV Calculator PRO

Aplicación Android para calcular tiempos, energía y costes de carga de vehículos eléctricos, consultar el catálogo y comparar coches.

## Versión del código

Estado de la rama `main` comprobado el **09/10/2026**:

- **Versión de desarrollo:** `1.0.5`
- **versionCode:** `59`
- **applicationId:** `com.evcalculatorpro`
- **minSdk:** 23
- **targetSdk / compileSdk:** 36 / 36
- **Java:** 17
- **Build Release:** APK y AAB, con R8 y reducción de recursos.
- **CI:** GitHub Actions; Gradle 9.6.0 en el workflow de build.

La versión indicada aquí es la que declara el código de `main`. No significa que esa versión esté publicada en Google Play. El último estado de Play Console registrado en este repositorio era **1.0.4 / versionCode 56 publicada**; comprobar Play Console antes de afirmar el estado de una publicación posterior.

## Funciones principales

- Calculadora de carga y estimación de energía/tiempo.
- Cálculo de costes.
- Catálogo de vehículos eléctricos.
- Comparativa de hasta tres vehículos.
- Búsqueda de coches similares y acceso al detalle del vehículo.
- Idiomas, monedas y tema claro/oscuro.
- Catálogo local protegido y adiciones remotas validadas.

La navegación principal es **Cargar → Coste → Coches → Más**.

## Catálogo de vehículos

Archivo base:

`app/src/main/assets/catalog_es_2024_2026.json`

- Mercado principal: España (`ES`).
- Años del conjunto base: 2024–2026.
- Último estado auditado documentado: **682 configuraciones**, sin duplicados lógicos ni IDs duplicados.
- Las adiciones remotas se guardan por separado en `app/src/main/assets/catalog_remote_additions.json`.
- Los datos protegidos no se sobrescriben con información externa.
- Las nuevas configuraciones se validan y se comparan por clave lógica antes de incorporarse.

## Buscar coches similares

El algoritmo vigente en `main` combina:

- **60 % competencia comercial**.
- **40 % distancia técnica direccional**.

Antes del ranking se aplican filtros obligatorios de año, marca, carrocería, clase física y zona de precio. SUV y crossover son compatibles entre sí; una carrocería incompatible no puede compensarse con una puntuación técnica alta. La distancia técnica penaliza al candidato cuando es peor que el vehículo de referencia, y el ranking utiliza la precisión interna completa antes de redondear el porcentaje mostrado. Se conserva como máximo un representante por marca.

Los pesos y reglas se mantienen en el código de `SimilarCarsActivity.java`; no hay excepciones de ranking específicas para Tesla, XPeng, Opel ni otras marcas.

## Actualización automática del catálogo

El workflow `.github/workflows/catalog-auto-update.yml` está programado para ejecutarse diariamente a las **15:00 (Europe/Madrid)** y también permite ejecución manual. Consulta las fuentes configuradas, valida posibles incorporaciones y solo crea un commit si hay cambios válidos. No necesita cron-job.org.

## Build y entregas

El workflow **Build EV Calculator PRO** compila la versión Release y prepara:

- APK Release.
- AAB Release.
- ZIP del proyecto.

Los nombres de los artefactos se generan a partir de `versionName`; las etiquetas de build incluyen la versión y el identificador de compilación.

## Privacidad

Política de privacidad: https://mariskal19.github.io/EV-Calculator-PRO-Privacy/

## Desarrollo

- Repositorio: https://github.com/Mariskal19/EV-Calculator-PRO
- Rama activa: `main`.
- Las ramas/checkpoints `stable` son puntos de recuperación y no deben modificarse sin autorización expresa.
- El documento operativo con el historial, decisiones y checkpoints es [`EV_CALCULATOR_PRO_MASTER.md`](EV_CALCULATOR_PRO_MASTER.md).
