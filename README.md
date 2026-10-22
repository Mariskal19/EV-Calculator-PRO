# EV Calculator PRO

Aplicación Android para calcular tiempos, energía y costes de carga de vehículos eléctricos, consultar el catálogo y comparar coches.

## Versión del código

Estado de la rama `main` comprobado el **22/10/2026**:

- **Versión de desarrollo:** `1.0.5.1`
- **versionCode:** `61`
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

La navegación principal dispone de cinco accesos: **Cargar → Coste → Buscar → Comparar → Más**.

## Deslizador de batería

Corrección validada por el usuario en dispositivo el **13/10/2026**:

- Se conserva la animación de ampliación del pulgar al tocar y de retorno al soltar.
- Al finalizar el gesto, se mantiene el último porcentaje calculado durante el arrastre; no se recalcula la posición en `ACTION_UP`, evitando un pequeño salto del valor.
- Commit de la corrección: [ed59f75](https://github.com/Mariskal19/EV-Calculator-PRO/commit/ed59f75cdd1ee5d28c4b0ea203767bdff1c91f84).

## Catálogo de vehículos

Archivo base:

`app/src/main/assets/catalog_es_2024_2026.json`

- Mercado principal: España (`ES`).
- Años del conjunto base: 2024–2026.
- Estado actualizado en `main` el 20/10/2026: **688 configuraciones** (`catalog-es-2024-2026-1.12`), sin duplicados lógicos ni IDs duplicados.
- Las adiciones remotas se guardan por separado en `app/src/main/assets/catalog_remote_additions.json`.
- Los datos protegidos no se sobrescriben con información externa.
- Las nuevas configuraciones se validan y se comparan por clave lógica antes de incorporarse.
- Auditoría Zeekr 20/10/2026: 13 registros; corregidas dimensiones, química de batería y aceleraciones; añadidas las versiones 2026 Zeekr 001 Privilege AWD y Zeekr X Core RWD. Auditoría Lynk & Co 21/10/2026: revisados los tres registros del 02 eléctrico, corregidos batería utilizable, peso, carga DC 10–80 % y tracción donde estaba respaldado; no se añadieron híbridos. El detalle y las fuentes están en `EV_CALCULATOR_PRO_MASTER.md`.


### Reconciliación histórica de recuentos (20/10/2026)

El historial de `catalog_es_2024_2026.json` explica la evolución del catálogo:

- **682 registros**: estado certificado el 01/10/2026.
- **686 registros**: antes de la auditoría Zeekr. El aumento neto de cuatro corresponde a Renault Twingo E-Tech 2026 techno, XPeng G6 2026 RWD Standard Range, Renault Mégane E-Tech 2026 Techno LFP 67 kWh y Renault Scénic E-Tech 2026 Techno Gran Autonomía NMC 89 kWh.
- En el mismo periodo se dieron de alta las fichas Renault 5 EV40 y EV52 de 2025 y se retiraron las equivalentes etiquetadas 2024; seis altas y dos bajas dan un neto de +4.
- **688 registros**: estado actual de `main`, tras añadir Zeekr 001 Privilege AWD 2026 y Zeekr X Core RWD 2026. La auditoría registró 13 Zeekr, 0 duplicados lógicos y 0 IDs duplicados.

No se deben eliminar las cuatro incorporaciones ni restaurar las fichas Renault 5 de 2024 para forzar el recuento histórico. El detalle, los commits comparados y las fuentes están en [EV_CALCULATOR_PRO_MASTER.md](EV_CALCULATOR_PRO_MASTER.md) y en el [historial del catálogo](https://github.com/Mariskal19/EV-Calculator-PRO/commits/main/app/src/main/assets/catalog_es_2024_2026.json).

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


## Checkpoint antes del asesor conversacional (22/10/2026)

Antes de cualquier cambio de código para el asesor se creó y verificó una rama de respaldo completa del repositorio versionado:

- **Rama de respaldo:** [`backup/pre-asesor-ia-2026-10-22`](https://github.com/Mariskal19/EV-Calculator-PRO/tree/backup/pre-asesor-ia-2026-10-22)
- **Commit exacto del checkpoint:** [`a2021ffa2bae1846f4de7f777a2c937f6713434f`](https://github.com/Mariskal19/EV-Calculator-PRO/commit/a2021ffa2bae1846f4de7f777a2c937f6713434f)
- La rama de respaldo se creó desde el HEAD de `main` y se verificó que apunta al mismo SHA. Conserva el árbol versionado completo de ese punto, incluidos código, recursos, catálogos y documentación.
- La rama `main` estaba al día en el momento del checkpoint; su último commit entonces documentaba la auditoría Lynk & Co. La versión declarada por el código sigue siendo `1.0.5.1` / `versionCode 61`; esto no confirma su publicación en Google Play.
- La auditoría del asesor hasta este punto ha sido de solo lectura. No se ha cambiado código, catálogo ni navegación.
- Para recuperar el punto anterior, usar la rama de respaldo o el SHA indicado. No hacer force-push ni reset destructivo de `main`; preferir una reversión controlada tras revisar los commits posteriores.
- Registro detallado, hallazgos, decisiones pendientes y casos de prueba: [`EV_CALCULATOR_PRO_MASTER.md`](EV_CALCULATOR_PRO_MASTER.md).


## Asesor conversacional — fase 1 en desarrollo

- Aprobado el enfoque local y sin envío de consultas el 22/10/2026.
- Rama aislada de trabajo: [feature/asesor-ia-fase-1](https://github.com/Mariskal19/EV-Calculator-PRO/tree/feature/asesor-ia-fase-1).
- El motor local de criterios tiene una ejecución de smoke tests anterior confirmada como correcta. Se añadió también un matcher aislado para candidatos de catálogo con pruebas de filtros AND y protección frente a datos desconocidos; la nueva ejecución de CI está pendiente de confirmación. Todavía no hay integración visual ni adaptación real de `Vehicle`. Ver el registro y los límites en [EV_CALCULATOR_PRO_MASTER.md](EV_CALCULATOR_PRO_MASTER.md).
- El código no se ha integrado en `main`; la rama de respaldo previa se conserva sin cambios.
