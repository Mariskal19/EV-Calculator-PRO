# EV Calculator PRO — MASTER

> Documento maestro del proyecto.  
> Última actualización: 01/10/2026 (estado consolidado tras auditorías de catálogo y cierre estable de Comparar coches 21:30).
>
> **Regla principal:** este archivo recoge el estado, decisiones y reglas de trabajo que deben conservarse al retomar el proyecto. No sustituye al código ni al catálogo; documenta cuál es la fuente de verdad de cada parte.

---

## 0. CURRENT STATE

- **Repositorio:** `Mariskal19/EV-Calculator-PRO`
- **Rama de trabajo principal:** `main`
- **Ramas stable:** no modificar sin autorización expresa.
- **Versión de referencia actual:** 1.0.4 / línea de trabajo posterior a los checkpoints de septiembre de 2026.
- **Google Play:** la publicación/revisión de producción seguía pendiente al último checkpoint conocido.
- **Catálogo:** objetivo operativo actual de **682 registros**.
- **Estado real verificado en `main` (01/10/2026): 682 registros** en `app/src/main/assets/catalog_es_2024_2026.json`.
- **Duplicados verificados:** 0 duplicados lógicos (`marca + modelo + año + versión`) y 0 IDs duplicados.
- **Dimensiones:** 682/682 registros tienen longitud, anchura y altura; no quedan registros sin dimensiones.
- **Batería utilizable:** auditoría cerrada el 01/10/2026; **682/682 registros** tienen `usableBatteryKwh`; quedan **0 pendientes**.
- **Precio:** **0 registros** siguen sin PVP tras la auditoría completada el 01/10/2026. Los 682 registros del catálogo tienen price informado.
- **batteryChemistry:** **682/682 registros** auditados y completos; **0 pendientes**. Cierre de auditoría: 01/10/2026 21:38:37 +02:00.
- **charge10to80Min:** **682/682 registros revisados**; **0 registros con dato DC 10–80% pendiente**. El único caso no aplicable queda documentado con charge10to80Applicable: false.
- **dcKw:** auditoría global cerrada el **01/10/2026 21:48:32 +02:00**. Hay **681/681 registros aplicables** con `dcKw` informado; el único registro no aplicable es `MG MGS9 PHEV Comfort 2026`, que no dispone de carga rápida DC y mantiene `dcKw: null`. **0 pendientes aplicables**.
- **Explorer 2024–2026:** auditoría aplicada; se eliminaron entradas antiguas duplicadas y se corrigió el conjunto de propulsiones. El último ajuste eliminó la entrada 2025 `79 kWh 150 kW RWD` duplicada.
- **Objetivo histórico de 682:** sigue documentado como objetivo, pero el estado actual contiene 682 registros válidos y sin duplicados. No se deben eliminar 75 registros arbitrariamente: antes hay que identificar y aprobar qué subconjunto deja de formar parte del catálogo protegido/base.
- **Auditoría final:** controles principales del catálogo completados para el estado auditado de 682 registros; antes de una certificación/release final debe repetirse la comprobación real y verificarse la integración en APK/AAB.
- **Build final/candidato Play:** pendiente de definir y verificar después de la comprobación final del catálogo y automatizaciones.
- **APK/AAB:** debe comprobarse que el catálogo corregido queda realmente incluido en el artefacto generado.
- **Stable:** no tocar.

---

# 1. PROPÓSITO DEL PROYECTO

EV Calculator PRO es una aplicación Android orientada al cálculo de costes y energía de carga de vehículos eléctricos, con catálogo de vehículos y herramientas de búsqueda/comparación.

Objetivos principales:

- Calculadora de carga sencilla y rápida.
- Cálculo de energía y coste.
- Catálogo de vehículos eléctricos mantenido y auditado.
- Búsqueda de coches y coches similares.
- Datos de batería, carga y dimensiones.
- Actualización automática del catálogo sin sobrescribir datos protegidos.
- Interfaz moderna light/dark y multidioma.
- Publicación y mantenimiento mediante GitHub Actions.

---

# 2. NAVEGACIÓN PRINCIPAL

La aplicación abre directamente en la calculadora.

No debe existir una portada inicial con tres tarjetas como navegación principal.

## Barra inferior permanente

Destinos actuales:

1. ⚡ **Cargar** → Charge Calculator
2. 🔋 **Coste** → Cost / cálculo de coste
3. 🚗 **Coches** → catálogo, búsqueda y funciones relacionadas
4. ⋮ **Más** → configuración y acciones secundarias

Reglas:

- La barra inferior debe estar disponible en las pantallas actuales y futuras cuando corresponda.
- Los iconos se mantienen iguales entre idiomas.
- Solo se traducen las etiquetas.
- No añadir antiguos botones superiores de volver/menú cuando la navegación inferior ya cubra esa función.
- Mantener el comportamiento y proporciones visuales acordados con las referencias de Google Play.

---

# 3. CHARGE CALCULATOR

Características confirmadas:

- Batería máxima configurable: **150 kWh**.
- Porcentaje inicial.
- Porcentaje objetivo.
- El objetivo no puede ser inferior al porcentaje inicial.
- Sin botón independiente de «Calcular»: el resultado se actualiza directamente.
- Mostrar:
  - porcentaje que se cargará;
  - energía correspondiente en kWh.
- Potencia de carga.
- Precio de la electricidad.
- Centinela / XGuard.
- Hora de salida.
- Compatibilidad con modo claro/oscuro.
- Sistema de idioma y moneda.

Formato numérico:

- Evitar mostrar ceros decimales innecesarios.
- Usar el separador decimal correspondiente al idioma.
- Revisar siempre los campos relacionados con batería/carga cuando se cambie el formato.

---

# 4. CATÁLOGO DE VEHÍCULOS

## 4.1 Fuentes

El catálogo está compuesto conceptualmente por:

- **Catálogo protegido/base:** datos auditados y validados que no deben ser sobrescritos por una fuente externa.
- **Catálogo externo/remoto:** nuevas incorporaciones que pueden proponerse mediante el sistema automático.
- **RemoteCatalogManager:** gestiona las adiciones remotas y su caché.

## 4.2 Regla de protección

Los datos auditados del catálogo protegido tienen prioridad.

Una actualización externa:

- no debe sobrescribir silenciosamente un registro validado;
- debe comprobarse antes de incorporarse;
- debe evitar duplicados;
- debe conservar los datos confirmados;
- puede aportar registros nuevos si superan las comprobaciones.

## 4.3 Clave lógica de duplicado

La comprobación de duplicados comerciales se basa en la combinación lógica:

`marca + modelo + año + versión`

Además debe comprobarse que no existan IDs duplicados.

## 4.4 Auditoría

Las auditorías deben registrar `auditDateTime` con fecha/hora real de ejecución.

No introducir fechas ficticias ni reutilizar una fecha antigua como si fuera una nueva auditoría.

## 4.5 Objetivo actual

**Objetivo documentado y certificado: 682 registros, sin duplicados.**

La diferencia de 75 registros no se resolverá mediante borrado automático. Debe definirse primero si esos registros son parte del catálogo protegido o incorporaciones externas que deben quedar fuera del conjunto certificado.

Antes de dar el catálogo por terminado:

- contar registros;
- detectar IDs duplicados;
- detectar duplicados lógicos;
- revisar los puntos 3–6 de la auditoría;
- comprobar registros corregidos;
- comprobar registros añadidos;
- verificar dimensiones;
- verificar batería/carga;
- confirmar fechas de auditoría;
- ejecutar build;
- verificar que el catálogo corregido termina dentro del APK/AAB.

### Estado consolidado

La comprobación real posterior confirmó **682 registros** en `main`. La cifra de 676 pertenece a un estado histórico y no debe utilizarse como estado actual.

---

# 5. CORRECCIONES Y CASOS CONOCIDOS

Correcciones registradas durante las auditorías:

- **XPeng G6 2026:** capacidad corregida de 80,2 kWh a **80,8 kWh** en la revisión correspondiente.
- **Deepal S05:** se detectaron y trataron duplicados.
- **BMW i4:** se revisaron 10 registros; corrección documentada de **M60 → M50** donde correspondía.
- **ATTO 3 EVO:** Design y Excellence revisados con autonomías de 510/470 km según el registro correspondiente.
- **SEALION 7 Excellence AWD:** capacidad revisada a **91,3 kWh**.

Estos casos no sustituyen a una auditoría completa posterior.

---

# 6. BÚSQUEDA Y COCHES SIMILARES

La aplicación incorpora/está desarrollando una pantalla de búsqueda de coches.

Datos disponibles/revisados:

- marca;
- modelo;
- versión;
- año;
- batería;
- carga;
- dimensiones;
- otros datos existentes en el catálogo.

## Dimensiones

Se decidió incorporar y revisar sistemáticamente:

- longitud;
- anchura;
- altura;
- otros campos dimensionales disponibles.

Presentación:

- unidades en metros cuando corresponda;
- separador decimal según idioma;
- eliminar ceros decimales innecesarios.

## Tipo de vehículo

La clasificación SUV/berlina/etc. queda como ampliación futura y no debe mezclarse con la auditoría actual de dimensiones salvo que se solicite expresamente.

---

# 7. DISEÑO VISUAL

Elementos confirmados:

- diseño moderno;
- modo claro/oscuro;
- soporte del tema del sistema;
- barra inferior permanente;
- iconografía consistente;
- cabecera de la aplicación;
- pantalla de comparación/búsqueda de coches.

Referencias visuales utilizadas durante el proyecto:

- `Cabecera EV Calculator.jpeg`
- `Barra Inferior Google Play.jpg`

La barra inferior debe conservar la forma visual acordada, pero con una altura más contenida que algunas referencias originales.

---

# 8. «MÁS»

Contenido previsto:

- ⚙️ Configuración
- Support / Donation
- Compartir app
- Calificar app
- Política de privacidad
- versión de la aplicación

Política de privacidad:

`https://mariskal19.github.io/EV-Calculator-PRO-Privacy/`

No reintroducir opciones antiguas de navegación que hayan sido sustituidas por la barra inferior.

---

# 9. CONFIGURACIÓN

Áreas reservadas:

- Apariencia
- Moneda
- Idioma
- opciones de conteo/cálculo
- otras preferencias de aplicación

Las nuevas opciones deben integrarse en la navegación existente y no crear menús superiores redundantes.

---

# 10. AUTOMATIZACIONES GITHUB ACTIONS

Automatizaciones relevantes del proyecto:

- actualización automática del catálogo protegido;
- validación/auditoría;
- motor Gaia/OpenEV;
- test/build;
- comprobaciones de catálogo;
- actualización programada.

## Motor Gaia / OpenEV

Se ha trabajado con la idea de que el motor externo pueda detectar datos nuevos, pero:

1. los datos protegidos no se sobrescriben;
2. los registros nuevos se validan;
3. los duplicados se detectan;
4. los nuevos registros se incorporan solo si superan las reglas.

## Horarios

Los schedules de GitHub Actions deben comprobarse mediante ejecuciones reales.

No asumir que un `schedule` se ejecutará exactamente a la hora indicada: GitHub puede retrasar o no ejecutar un workflow según disponibilidad/condiciones.

Cuando se cambie un schedule:

- comprobar el YAML;
- comprobar la siguiente ejecución;
- revisar el run real;
- distinguir workflow de test, catálogo y build.

---

# 11. BUILDS

El Build de EV Calculator PRO debe servir también como comprobación de integración del catálogo.

## Comprobación final obligatoria

Después de corregir y certificar el catálogo:

1. contar registros del catálogo fuente;
2. confirmar 682;
3. confirmar ausencia de duplicados;
4. ejecutar Build EV Calculator PRO;
5. comprobar que el build termina correctamente;
6. localizar APK/AAB generado;
7. comprobar que el catálogo corregido está incluido en el artefacto;
8. verificar que no se ha usado accidentalmente un catálogo antiguo;
9. conservar el resultado como checkpoint.

**No tocar ninguna rama stable durante este proceso.**

---

# 12. GOOGLE PLAY

Estado conocido al último checkpoint:

- La aplicación estaba pendiente de revisión/publicación en Google Play.
- Se habían trabajado cambios de:
  - multidioma;
  - monedas;
  - interfaz;
  - navegación;
  - catálogo.

## ASO

Pendiente de continuar cuando corresponda:

- título;
- descripción corta;
- descripción;
- palabras clave;
- capturas;
- elementos gráficos.

No modificar la ficha de Play como consecuencia de una suposición sobre el resultado de la revisión; comprobar siempre el estado real de Play Console.

---

# 13. VERSIONADO Y CHECKPOINTS

Checkpoints relevantes conocidos:

- Alpha: `versionCode 54`, `versionName 1.0.3.2`.
- Build 1.0.4: `versionCode 831` en el checkpoint documentado.
- Stable 23/09 alrededor de 20:00.
- Stable de Comparar Coches alrededor de 23:40.
- Stable de navegación inferior alrededor de 15:54.
- Stable de app + catálogo + automatización alrededor de 21:50.
- 30/09/2026 12:50: estado estable relacionado con la corrección de formatos de dimensiones/batería y eliminación de ceros decimales innecesarios.

**Nota:** los nombres exactos de ramas/commits deben verificarse en GitHub antes de usar un checkpoint como base de trabajo.

---

# 14. REGLAS DE TRABAJO

## Regla 1 — No tocar stable

Las ramas stable son puntos de recuperación.

No hacer commits, reescrituras ni force-push sobre ellas salvo autorización expresa.

## Regla 2 — Auditar antes de afirmar

No decir «catálogo certificado» sin comprobar:

- número total;
- IDs;
- duplicados lógicos;
- datos corregidos;
- fechas de auditoría;
- dimensiones;
- batería/carga;
- integración en build.

## Regla 3 — Proteger datos validados

Un proveedor externo no puede reemplazar automáticamente un dato que ya ha sido auditado y protegido.

## Regla 4 — Los nuevos datos se validan

Un registro externo nuevo no entra directamente al catálogo definitivo.

## Regla 5 — Build después del catálogo

Una modificación del catálogo importante debe terminar con una comprobación de build para asegurar que el APK/AAB utiliza realmente la versión corregida.

## Regla 6 — No confundir test, catálogo y release

Cada workflow tiene una finalidad distinta.

Antes de actuar hay que identificar:

- workflow;
- rama;
- commit;
- catálogo utilizado;
- artefacto generado.

## Regla 7 — Fechas reales

Toda auditoría nueva debe usar fecha y hora reales de ejecución.

---

# 15. ESTADO PENDIENTE / PRÓXIMOS PASOS

### Prioridad 1 — Auditoría final del catálogo

- [x] Comprobar estado real del catálogo en `main`.
- [x] Confirmar **682** registros actuales.
- [x] Confirmar ausencia de IDs duplicados.
- [x] Confirmar ausencia de duplicados lógicos.
- [x] Revisar el conjunto Ford Explorer 2024–2026 incorporado/corregido.
- [x] Confirmar dimensiones presentes en 682/682 registros.
- [x] Confirmar `auditDateTime` presente en 682/682 registros.
- [x] Mantener el catálogo en 682 registros.
- [x] Completar 197 registros de `usableBatteryKwh` con fuente contrastada.
- [x] Resolver el último registro sin `usableBatteryKwh` (`M-HERO I 2024` → 135,0 kWh contrastados).
- [x] Auditar/completar los registros sin `price` usando PVP español; quedan **0 registros sin precio**.
- [x] Auditar globalmente `dcKw`: **681/681 aplicables** completos; 1 registro PHEV no aplicable (`MG MGS9 PHEV Comfort 2026`), 0 pendientes aplicables.

### Prioridad 2 — Build

- [x] Lanzar Build EV Calculator PRO desde `main`, sin tocar ninguna stable.
- [x] Confirmar compilación.
- [x] Confirmar APK/AAB.
- [x] Confirmar que el build validó el catálogo de `main` antes de compilar.
- [ ] Verificación byte-level del catálogo dentro del APK/AAB (no expuesta por el endpoint de artefactos; el build sí pasó la validación del catálogo fuente).

### Prioridad 3 — Release

- [ ] Determinar checkpoint que debe considerarse candidato a release.
- [ ] No convertirlo en stable hasta autorización.
- [ ] Revisar Google Play.

---

# 16. FUENTES DE VERDAD

| Área | Fuente de verdad |
|---|---|
| Código | Repositorio GitHub |
| Estado del proyecto | Este MASTER |
| Datos protegidos | Catálogo protegido validado |
| Datos externos | Fuente externa + proceso de validación |
| Automatizaciones | Workflows de GitHub Actions |
| Release estable | Rama/checkpoint stable correspondiente |
| Privacidad | Página oficial de privacidad |
| Estado de Play | Google Play Console |

---

# 17. CÓMO RETOMAR EL PROYECTO

Cuando se retome EV Calculator PRO:

1. Leer este archivo.
2. Comprobar la rama actual y el commit.
3. No modificar stable.
4. Comprobar el estado real del catálogo.
5. Revisar las tareas pendientes de la sección 15.
6. Ejecutar primero las auditorías antes de hacer cambios destructivos.
7. Actualizar este MASTER cuando una decisión quede confirmada.
8. Crear un checkpoint después de cada bloque importante.

---

# 18. HISTORIAL DEL MASTER

| Fecha | Cambio |
|---|---|
| 01/10/2026 | Creación de `EV_CALCULATOR_PRO_MASTER.md`. Recoge arquitectura, navegación, catálogo, automatizaciones, builds, reglas de trabajo y estado pendiente. |
| 01/10/2026 | Auditoría real de `main`: 682 registros, 0 duplicados lógicos, 0 IDs duplicados, dimensiones presentes en 682/682 y `auditDateTime` presente en 682/682. Se documenta que el objetivo certificado es 682 y coincide con el catálogo real auditado. |
| 01/10/2026 | Build `36836046343` completado correctamente desde `main`; validación de catálogo OK; APK y AAB publicados como `build-v1.0.4.1-1975`. |

---

## PRINCIPIO DEL PROYECTO

**EV Calculator PRO debe avanzar mediante cambios verificables, auditados y recuperables.**

**El catálogo protegido no se sobrescribe.**

**Los builds deben demostrar que el código y el catálogo que hemos validado son los que realmente llegan al APK/AAB.**

**Stable no se toca sin autorización expresa.**


## 2026-10-01 — Auditoría de datos de detalle del coche
- Se revisaron los 198 registros inicialmente sin `usableBatteryKwh`.
- Se completaron **119** con correspondencia de versión/batería contrastada con EV Database; quedan **79** pendientes.
- No se han rellenado los precios ausentes con estimaciones ni precios de Alemania/Países Bajos/Reino Unido: quedan **362** para auditoría específica de PVP español.
- Los registros modificados llevan `auditDateTime` de esta auditoría y la fuente ampliada con EV Database.

## 2026-10-01 — Auditoría ampliada de batería utilizable
- Se completaron **78** registros adicionales de `usableBatteryKwh`, pasando de 119 a **197** completados en esta fase.
- El catálogo permaneció en **682 registros**.
- Posteriormente se resolvió el único registro pendiente: `M-HERO I 2024 — BEV 800 kW AWD 142.87 kWh` → **135,0 kWh utilizables**, con contraste EVKX.
- Los 78 registros modificados en esta fase llevan `auditDate=2026-10-01`, `auditDateTime=2026-10-01T15:00:00+02:00` y fuente ampliada con EV Database.
- La verificación global final confirmó **682/682** con `usableBatteryKwh`.

## 2026-10-01 — Estado UI y trabajo actual
- Auditoría UI transversal realizada.
- **Barra inferior:** corregida y cerrada. El comportamiento correcto es contenido a pantalla completa + barra inferior superpuesta de 64dp; no se reserva un margen inferior adicional.
- **Icono 🚗 Coches:** corregido y cerrado. Mantener el diseño actual salvo nueva incidencia visual.
- **Edge-to-Edge:** revisado; `EdgeToEdgeHelper` queda sin cambios.
- No repetir esta auditoría salvo que aparezca una regresión.
- Corrección de `BaseNavigationActivity.java`: commit `6a0d5c569ab39ecfbb2ccf9fa3e91261ba81924c`.
- Build automático asociado: run `36844354921`, build 1980.
- **Coches similares:** aparcado temporalmente.
- **Buscador de coches:** avanzado.
- **Dimensiones del catálogo:** auditadas.
- **Detalle del coche:** pendiente de revisión final cuando se retome el bloque de coches.
- **Automatizaciones:** pendiente de comprobación final de workflows y schedules.
- **Google Play:** pendiente de revisar el estado real y preparar posteriormente el candidato de release.
- **ASO/ficha de Play:** pendiente para después de cerrar la versión candidata.
- No repetir auditorías ya cerradas salvo regresión o nueva modificación.

## 2026-10-01 — Auditoría de precios España, lote 1
- Se añadieron **57 precios PVP** con correspondencia clara de modelo/versión y fuente española.
- Quedan **305 registros** sin precio.
- Se usó como referencia Electrolitros, cuya metodología indica que sus cifras son PVP de tarifa en España, sin descuentos de marca, financiación ni Plan MOVES. citeturn1search0
- Los registros modificados llevan `market=ES`, `currency=EUR`, `auditDate=2026-10-01` y `auditDateTime=2026-10-01T15:00:00+02:00`.
- No se han estimado precios ni se han usado precios de otros mercados.

## 2026-10-01 — Auditoría de precios España, lote 2
- Se añadieron 4 precios PVP con correspondencia exacta de versión/batería: Ford Mustang Mach-E 72,6 RWD, Hyundai IONIQ 5 84 kWh RWD, Kia EV6 84 kWh RWD y Škoda Enyaq 85.
- Quedan 301 registros sin precio.
- Referencia de contraste: Electrolitros, tabla de PVP de España por versión.
- No se han añadido precios cuando la versión publicada no permitía una correspondencia suficientemente precisa.

## 2026-10-01 — Auditoría de precios España, lotes 3–4
- Se incorporaron 18 precios PVP España en los lotes 3–4; uno fue corregido inmediatamente tras detectar una cifra transcrita incorrectamente (Ford Explorer: 45.711 €).
- Estado: **280 registros sin precio** de 682.
- Fuente de contraste: Electrolitros, tabla de precios PVP España por versión, consultada el 1/10/2026. Los precios de la tabla son de tarifa, sin descuentos ni MOVES. citeturn1view0turn2view0

## 2026-10-01 — Auditoría de precios España, lote 5
- Se añadieron **3 precios nuevos** y se corrigieron **2 precios anteriores** al contrastarlos con la tabla actual de PVP España de Electrolitros.
- Entre las correcciones: Kia EV4 Long Range pasó a 41.990 € y Tesla/otros valores se ajustaron cuando la tabla actual mostraba una cifra distinta.
- Estado: **280 registros sin precio**.
- La referencia consultada indica que sus cifras son PVP de tarifa en España, sin descuentos ni MOVES, y muestra fecha de comprobación para cada precio. citeturn0search0turn0search1

## 2026-10-01 — Auditoría de precios España, lotes 6–9
- Se añadieron **32 precios** adicionales en cuatro bloques, pasando de **280 a 248 registros sin precio**.
- Lote 6: 10 precios.
- Lote 7: 15 precios.
- Lote 8: 4 precios con correspondencia especialmente clara en la tabla actual de Electrolitros (incluidos Citroën ë-C5 Aircross 97 kWh, Dongfeng BOX Pro 42,3 kWh e Hyundai IONIQ 6 N / N Line).
- Lote 9: 3 precios adicionales (MINI Aceman E, Nissan Ariya 63 kWh y Renault Twingo E-Tech 2026).
- Los registros modificados llevan `market=ES`, `currency=EUR`, `auditDate=2026-10-01` y `auditDateTime=2026-10-01T15:00:00+02:00`.
- Se mantiene la regla de no rellenar por estimación ni usar promociones, financiación, MOVES o precios de otros mercados. Electrolitros define su tabla como PVP de tarifa en España, sin descuentos de marca ni MOVES. citeturn1search0turn1search1
- **Nota:** se mantiene como siguiente control de calidad una pasada estricta de trazabilidad versión↔PVP para los registros cuya fuente o correspondencia histórica pueda ser menos literal.
- Commits de catálogo: `239967aeef6b3a9e5a4c46d6bda60c4dfa3567db`, `9f7f6a0245c4f42dc3415b31073a683619f88819`, `eee9899db24c40d745f97675961d8adb3e18347a`, `4637be6f1b0079452d5cc5d472509439d4082c8b`.

## 2026-10-01 — Auditoría de PVP España

- Se continuó la auditoría de PVP España del catálogo protegido.
- Estado actual real del catálogo: **682 registros; 0 sin campo `price`**.
- Batch 6: 10 precios añadidos, commit `239967aeef6b3a9e5a4c46d6bda60c4dfa3567db`.
- Batch 7: 15 precios añadidos, commit `9f7f6a0245c4f42dc3415b31073a683619f88819`.
- Batch 8: 41 precios ya auditados que tenían `price_eur` se normalizaron al campo `price`, sin inventar nuevos valores ni alterar su fecha de auditoría. Commit `9bec3c40d3575d37f57bc7fd861c5da19ab61149`.
- La fuente de referencia externa para PVP de tarifa en España sigue siendo Electrolitros cuando existe coincidencia exacta; sus precios excluyen descuentos de marca, financiación y MOVES. urlTabla de precios de Electrolitroshttps://electrolitros.com/precios/
- La auditoría continuó hasta completar los campos `price`; el cierre definitivo se documenta a continuación.


## 2026-10-01 — Cierre definitivo de auditoría de precios

- Auditoría de precios España completada para el catálogo protegido.
- **682/682 registros tienen precio (`price`) informado.**
- **0 registros sin precio.**
- Se completaron los últimos **20 registros** en el commit `072f75dbbb674a73c48fcb720a288e3e0043e663`.
- El catálogo queda cerrado en **682 registros con precio** como estado de referencia para esta auditoría.
- Los registros modificados en esta fase llevan fecha de auditoría **2026-10-01**.
- Mantener como control posterior una revisión de trazabilidad de aquellos PVP cuya fuente histórica o correspondencia de acabado sea menos literal; esta revisión no implica dejar campos de precio vacíos.
- **Siguiente paso:** auditoría final global del catálogo y build, comprobando que los 682 registros y sus precios auditados son los que terminan realmente dentro del APK/AAB.


## 2026-10-01 — Cierre definitivo de auditoría de batería utilizable

- Se localizó y verificó el único registro que quedaba sin `usableBatteryKwh`: **M-HERO I 2024 — BEV 800 kW AWD 142.87 kWh**.
- Batería bruta: **142,87 kWh**; batería utilizable/neto contrastada: **135,0 kWh**.
- El catálogo ya contiene `usableBatteryKwh: 135` y la fuente registrada indica contraste con EVKX.
- Comprobación global posterior: **682/682 registros tienen `usableBatteryKwh`**; **0 pendientes**.
- Con esto queda **cerrada la auditoría de batería utilizable** del catálogo actual.
- No repetir esta auditoría salvo regresión o modificación de datos.


## 2026-10-01 — Estado Google Play Producción

- **EV Calculator PRO** mantiene en Google Play Console el **envío 15** de Producción.
- Versión publicada en el envío: **56 (1.0.4)**.
- Cambios enviados: **1 cambio**.
- Fecha de envío: **12/09/2026 a las 15:47**.
- Estado comprobado el **01/10/2026**: **En revisión**.
- No realizar un nuevo envío ni modificar este release mientras permanezca en revisión, salvo que Google Play solicite alguna acción.


## 2026-10-01 — Punto de recuperación antes de cambio en Comparar coches

- **Commit de recuperación:** `4761bbf00fd6e3d97b2beabc920d287656022c9d`.
- Este commit corresponde al estado del proyecto inmediatamente anterior al nuevo cambio previsto en la pantalla **Comparar coches**.
- Si el cambio visual/funcional posterior provoca una regresión, usar este commit como referencia para recuperar el estado anterior.


## 2026-10-01 — Stable Comparar coches 21:10

- Se da por **estable** la pantalla **Comparar coches** tras la prueba visual en el dispositivo.
- Filas azules de sección: **52 dp**, sin cambios.
- Filas de características/valores: **42 dp**, reducidas desde 52 dp para compactar la tabla.
- Cambio aplicado únicamente en `CompararCochesActivity.java`.
- **Commit estable:** `533a4cc5f04bd32098b971dc613dc3dd0fd80b2d`.
- Este commit queda como checkpoint de recuperación para la versión de **21:10 del 01/10/2026**.


## 2026-10-01 — Stable Comparar coches 21:30

- Se da por **estable** el ajuste final de la tabla de **Comparar coches** tras la prueba visual en el dispositivo.
- Se mantiene la tipografía de los valores en **13sp**, igual que el resto de valores; no se usa autosize.
- Para 1–2 coches, la columna de valores queda limitada a **128 dp** para hacerla más estrecha sin aumentar el tamaño de letra.
- El consumo (`17,5 kWh/100 km`, etc.) puede ocupar hasta **2 líneas** para evitar cortes.
- Las filas de características mantienen **42 dp** y las filas azules de sección **52 dp**.
- **Commit del ajuste visual:** `7938efff5788eda55feb214c346b9c016a595296`.
- Este estado queda guardado como checkpoint estable de **21:30 del 01/10/2026**. No modificarlo salvo nueva incidencia visual.


## 2026-10-01 — Estado consolidado y hoja de ruta de cierre

- **Catálogo:** 682/682 registros en `main`.
- **Precio:** 682/682 con `price`; 0 sin precio.
- **Batería utilizable:** 682/682; 0 pendientes.
- **Dimensiones:** 682/682.
- **auditDateTime:** 682/682.
- **Duplicados:** 0 IDs duplicados y 0 duplicados lógicos.
- **Comparar coches:** estable y probado a las **21:30**; checkpoint visual `7938efff5788eda55feb214c346b9c016a595296`.
- **Build:** existe un build correcto anterior, pero el ajuste visual de Comparar coches de 21:30 todavía debe pasar por un build de verificación antes de considerarlo candidato final.
- **Datos de carga:** `dcKw` queda cerrado: **681/681 registros aplicables completos**, con `MG MGS9 PHEV Comfort 2026` como único caso no aplicable por ausencia de carga DC. `batteryChemistry` y `charge10to80Min` también están cerrados.
- **Detalle del coche:** pendiente de revisión final.
- **Buscador de coches:** pendiente de revisión final.
- **Coches similares:** aparcado temporalmente.
- **Automatizaciones:** pendiente de comprobación final de workflows y schedules.
- **MASTER:** este documento queda como estado consolidado; las secciones históricas pueden contener estados intermedios, pero los apartados de cierre posteriores y la comprobación real de `main` son la referencia actual.
- **Google Play:** el envío 15 de Producción seguía **En revisión** el 01/10/2026.
- **ASO:** pendiente después de cerrar la versión candidata.

### Orden recomendado para el cierre

1. Revisión final de **Detalle del coche**.
3. Revisión final del **Buscador de coches**.
4. Comprobación de las **3 automatizaciones** y sus schedules mediante runs reales.
5. Auditoría global final del catálogo.
6. Build candidato desde `main`.
7. Verificación del APK/AAB y del catálogo incluido.
8. Prueba final en dispositivo.
9. Preparación de ASO/ficha de Google Play.
10. Revisar el estado real de Google Play antes de cualquier acción de release.


## 2026-10-01 — Cierre de auditoría de química de batería

- Auditoría completada sobre los **118 registros** que tenían `batteryChemistry` vacío.
- Se completaron **118/118** registros.
- Estado final: **682/682 registros** tienen `batteryChemistry` informado; **0 pendientes**.
- Se asignaron las químicas contrastadas por versión/batería, principalmente **NMC** y **LFP**.
- El contraste se realizó con EV Database y, en casos concretos, con documentación/fuentes del fabricante. EV Database identifica NMC en las familias Audi PPE, BMW i5/iX, Kia EV3/EV4, Polestar 2/4, Subaru Solterra/Uncharted/E-Outback, Toyota bZ4X y Volkswagen ID.7, y LFP en versiones como Kia EV2 Standard Range, Mazda CX-6e, Opel Frontera 44 kWh y Zeekr 7X Core. citeturn1search0turn2search11turn2search7turn3search0turn4search6turn5search1turn5search4turn4search11
- Casos específicos: **MG Cyberster → NMC** y **Mercedes G 580 → NMC**. citeturn10search27turn8search0
- **Dacia Spring 2024 26,8 kWh → NMC**; la química LFP corresponde a la generación posterior con batería de 24,3 kWh. citeturn12search1turn12search10
- Commit de catálogo: `345bd8b21569075ca15bba3358734a6aa1220991`.
- Auditoría ejecutada el **01/10/2026 a las 21:38:37 +02:00**.
- No repetir esta auditoría salvo regresión o modificación de datos.


## 2026-10-01 — Cierre de auditoría de charge10to80Min

- Revisado el único registro que figuraba pendiente: **MG MGS9 PHEV Comfort 2026**.
- El registro queda **auditado**, pero `charge10to80Min` es **no aplicable**: el MGS9 PHEV no dispone de carga rápida DC (`dcKw` no disponible); MG especifica carga AC de hasta 11 kW. citeturn1search0turn1search3
- No se ha introducido un número ficticio para representar un tiempo DC inexistente.
- Se añadió `charge10to80Applicable: false` y la auditoría queda fechada el **01/10/2026 21:45 +02:00**.
- Estado de auditoría: **682/682 registros revisados; 0 registros con dato DC 10-80% pendiente de determinar**.
- Commit: `006fd7585d4a557cd7af42e89306dd9b14a3aa18`.


## 2026-10-01 — Confirmación de campos de batería y carga

- Se confirma como estado de referencia que batteryChemistry está cerrado: **682/682, 0 pendientes**.
- Se confirma como estado de referencia que charge10to80Min está cerrado: **682/682 revisados, 0 registros con tiempo DC 10–80% pendiente de determinar**.
- El único registro no aplicable a tiempo DC 10–80% es **MG MGS9 PHEV Comfort 2026**, documentado mediante charge10to80Applicable: false.
- Por tanto, **no quedan pendientes de auditoría en batteryChemistry ni charge10to80Min**. No repetir estas auditorías salvo regresión o modificación de datos.


## 2026-10-01 — Cierre de auditoría de dcKw

- Auditoría global realizada sobre los **682 registros** del catálogo en `main`.
- **681/681 registros aplicables** tienen `dcKw` informado.
- El único registro sin `dcKw` es **MG MGS9 PHEV Comfort 2026**, y se confirma como **no aplicable** porque es PHEV y su documentación oficial de MG España especifica carga AC de hasta 11 kW, sin carga rápida DC. citeturn2search19turn2search0
- No se ha inventado ningún valor DC para ese registro: mantiene `dcKw: null`.
- **Resultado: 0 registros aplicables pendientes de `dcKw`.**
- Auditoría ejecutada el **01/10/2026 a las 21:48:32 +02:00**.
- No repetir esta auditoría salvo regresión o modificación de datos.


## 2026-10-01 — Ajuste de visualización de batería en Comparar coches

- Revisada la implementación real de la fila **Batería** en `CompararCochesActivity.java`.
- Antes mostraba únicamente `batteryKwh` (capacidad bruta), aunque el catálogo también dispone de `usableBatteryKwh`.
- Se implementa la nueva lógica:
  - si solo existe un valor, mostrar ese valor;
  - si existen ambos y son iguales (con tolerancia de redondeo), mostrar una sola capacidad;
  - si difieren, mostrar **capacidad bruta + capacidad utilizable**;
  - si no existe ninguno, mostrar `—`.
- Cambio aplicado en `main`.
- Commit: `05d27923a08a2f60b673aadb9d35bfa19e4ea49d`.
- **Estado: cambio implementado, pendiente de verificación mediante build y prueba visual en dispositivo.**
