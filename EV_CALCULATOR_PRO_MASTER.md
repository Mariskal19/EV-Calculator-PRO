# EV Calculator PRO — MASTER

> Documento maestro del proyecto.  
> Última actualización: 02/10/2026 (comprobación de versionado 1.0.4.1 y build candidato).
>
> **Regla principal:** este archivo recoge el estado, decisiones y reglas de trabajo que deben conservarse al retomar el proyecto. No sustituye al código ni al catálogo; documenta cuál es la fuente de verdad de cada parte.

---

## 0. CURRENT STATE

- **Repositorio:** `Mariskal19/EV-Calculator-PRO`
- **Rama de trabajo principal:** `main`
- **Ramas stable:** no modificar sin autorización expresa.
- **Versión publicada en Google Play:** 1.0.4 / versionCode 56.
- **Versión candidata actual en `main`:** 1.0.4.1 / versionCode 58.
- **Google Play:** 1.0.4 / versionCode 56 está publicada. La **1.0.4.1 / versionCode 58** ya fue cargada en Producción y se inició el lanzamiento completo; el envío 17 está **En revisión** junto con los cambios de ficha.
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
- **Build publicado en Google Play:** **1.0.4 (versión 56)**. La versión 1.0.4.1/build 2062 fue verificada en APK como checkpoint técnico de catálogo, pero **no es la versión publicada en Producción**.
- **APK/AAB:** build #2065 (run `37053413924`) terminó correctamente desde `main`; el endpoint de artefactos no devuelve artefactos, por lo que la descarga/verificación física del AAB aún queda pendiente.
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

## Flujo definitivo de coches similares — 1.0.5

- **Comparar coches** mantiene su función de comparar hasta 3 vehículos.
- Desde Comparar coches se accede a **Buscar coches similares**.
- La nueva pantalla permite localizar un coche de referencia mediante buscador por marca/modelo/versión y filtros de mercado, año, tracción y rango de batería.
- Al seleccionar el coche de referencia, la app calcula y muestra **5 opciones similares**.
- La similitud combina batería, autonomía WLTP, potencia, consumo, precio, dimensiones, maletero, aceleración, carga DC y tracción.
- Al pulsar una de las 5 opciones se abre **CarDetailActivity**, la pantalla de detalle completa.
- Desde el detalle, **Añadir a comparativa** incorpora el vehículo a la comparativa existente.
- La fuente de datos es siempre **catálogo protegido + catálogo externo validado**, sin sobrescribir datos protegidos.
- La pantalla de detalle creada en 1.0.5 se conserva y se reutiliza en este flujo; no sustituye a la pantalla de búsqueda de similares.

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

### Prioridad 3 — Release 1.0.4.1

- [x] Confirmar `versionName = 1.0.4.1` en `app/build.gradle`.
- [x] Confirmar `versionCode = 58` en `app/build.gradle`.
- [x] Confirmar que es superior al versionCode publicado en Google Play (56).
- [x] Confirmar build #2065 / run `37053413924` con conclusión **success**.
- [ ] Obtener/descargar el AAB final del build.
- [ ] Instalar y probar en el Redmi Note 13 Pro el build final exacto.
- [x] Subir 1.0.4.1 / versionCode 58 a Google Play Producción.
- [x] Iniciar el lanzamiento completo de 1.0.4.1 en Producción.
- [ ] Esperar la revisión/publicación de Google Play y verificar que 1.0.4.1 / 58 aparece disponible públicamente.
- [x] Revisar Google Play: **Producción publicada con 1.0.4 / versionCode 56**.

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
| 02/10/2026 | **1.0.4.1 / versionCode 58 enviada a Producción y lanzamiento completo iniciado.** Play Console muestra el envío 17 **En revisión**; incluye también los cambios de descripción de la ficha. Pendiente verificar publicación pública. |
| 02/10/2026 | EV Calculator PRO ya está publicada en Google Play Producción. Envío 15, versión 1.0.4 / 56; publicación confirmada a las 15:00. El usuario salió de la beta y la prueba cerrada quedó en pausa. |
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


## 2026-10-02 — Estado Google Play Producción

- **EV Calculator PRO** mantiene en Google Play Console el **envío 15** de Producción.
- Versión publicada en el envío: **56 (1.0.4)**.
- Cambios enviados: **1 cambio**.
- Fecha de envío: **12/09/2026 a las 15:47**.
- Estado actualizado el **02/10/2026**: **Publicada**.
- Publicación confirmada el **02/10/2026 a las 15:00**.
- Tras la publicación, se salió de la beta y la pista de prueba cerrada quedó en pausa.
- **No confundir:** el APK 1.0.4.1/build 2062 fue un checkpoint técnico verificado del catálogo, pero la versión que Google Play tiene publicada es **1.0.4 / versión 56**.


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


## 2026-10-02 — Comprobación de versionado y build candidato

- Comprobado directamente en `main`, `app/build.gradle`: **versionName 1.0.4.1** y **versionCode 58**.
- La versión actualmente publicada en Google Play es **1.0.4 / versionCode 56**.
- Por tanto, el candidato `1.0.4.1 / 58` tiene un versionCode válido y superior al publicado. No se debe reducir a 57: el repositorio ya está preparado con 58 y Google Play exige que cada nueva versión tenga un versionCode superior al publicado.
- Build de `main`: **#2065**, run `37053413924`, terminado correctamente (**success**), generado el 02/10/2026.
- El endpoint de artefactos de ese run actualmente devuelve **0 artefactos**, por lo que todavía no se considera completada la comprobación física del AAB.
- Catálogo de referencia: **682 registros**, con las auditorías principales cerradas y el checkpoint APK 1.0.4.1/build 2062 ya verificado con 682/682 dentro del APK.
- **Buscador de coches y Detalle del coche quedan explícitamente fuera de 1.0.4.1 y pasan a la versión 1.0.5.**

## 2026-10-02 — Estado consolidado y hoja de ruta de cierre

- **Catálogo:** 682/682 registros en `main`.
- **Precio:** 682/682 con `price`; 0 sin precio.
- **Batería utilizable:** 682/682; 0 pendientes.
- **Dimensiones:** 682/682.
- **auditDateTime:** 682/682.
- **Duplicados:** 0 IDs duplicados y 0 duplicados lógicos.
- **Comparar coches:** estable y probado a las **21:30**; checkpoint visual `7938efff5788eda55feb214c346b9c016a595296`.
- **Build:** existe un build correcto anterior, pero el ajuste visual de Comparar coches de 21:30 todavía debe pasar por un build de verificación antes de considerarlo candidato final.
- **Datos de carga:** `dcKw` queda cerrado: **681/681 registros aplicables completos**, con `MG MGS9 PHEV Comfort 2026` como único caso no aplicable por ausencia de carga DC. `batteryChemistry` y `charge10to80Min` también están cerrados.
- **Detalle del coche:** aplazado a versión **1.0.5**.
- **Buscador de coches:** aplazado a versión **1.0.5**.
- **Coches similares:** aplazado temporalmente junto con las nuevas pantallas de búsqueda.
- **Automatizaciones:** pendiente de comprobación final de workflows y schedules.
- **MASTER:** este documento queda como estado consolidado; las secciones históricas pueden contener estados intermedios, pero los apartados de cierre posteriores y la comprobación real de `main` son la referencia actual.
- **Google Play:** el envío 15 de Producción está **Publicado** desde el 02/10/2026 a las 15:00; versión **1.0.4 / 56**.
- **Beta personal:** el usuario salió del programa beta tras la publicación.
- **Prueba cerrada:** la pista está **en pausa**; no reanudarla salvo decisión expresa.
- **ASO:** iniciado tras la publicación; se están preparando nuevas descripciones de ficha sin subir una nueva versión de la app.

### Orden recomendado para el cierre

1. Comprobación de las **3 automatizaciones** y sus schedules mediante runs reales.
2. Auditoría global final del catálogo.
3. Build candidato desde `main` — **ya realizado: #2065 success**.
4. Obtener/verificar el AAB final y confirmar el catálogo incluido.
5. Prueba final en dispositivo.
6. Subir **1.0.4.1 / versionCode 58** a Google Play Producción.
7. Continuar ASO/ficha de Google Play sin necesidad de subir una nueva versión para cambios de texto.
8. Mantener **Buscador** y **Detalle** para 1.0.5.


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
- **Estado: ajuste anterior verificado visualmente; nueva presentación en dos líneas implementada y pendiente de build/prueba visual.**

## 2026-10-01 — Build de verificación solicitado

- Se solicita build desde `main` para verificar el cambio de visualización de batería del commit `05d27923a08a2f60b673aadb9d35bfa19e4ea49d`.
- El resultado del build y la prueba en dispositivo quedan pendientes hasta finalizar la ejecución.


## 2026-10-01 — Batería en dos líneas

- Cuando `batteryKwh` y `usableBatteryKwh` difieren, la fila Batería muestra la capacidad bruta en la primera línea y la utilizable en la segunda.
- Cuando coinciden, se mantiene una sola capacidad.
- Commit: `e26fcaebd7dad945719b3f4195790255ef1b31fa`.
- Estado: pendiente de build y prueba visual en dispositivo.


## 2026-10-01 — Resaltado de mejores resultados

- La fila `0–100 km/h` vuelve a participar en el cálculo de mejor resultado y se resalta en azul el menor tiempo.
- La fila `10–80 %` participa en el cálculo de mejor resultado y se resalta en azul el menor tiempo.
- Commit: `f242c2c85adcb9b3f038fe645b8f8cd9ca725d6f`.
- Estado: pendiente de build y prueba visual junto con el ajuste de batería en dos líneas.


## 2026-10-01 — Valores `null` en Comparar coches

- Los valores de texto nulos o con el literal `null` ahora se muestran como `—` en lugar de `null`.
- Aplicable, entre otros, a la fila de Tracción cuando el catálogo no tiene dato.
- Commit: `46a7da27e52b7b835d9566426447efd509aa5102`.


## 2026-10-01 — Estado estable de Comparar coches

- Confirmado por el usuario como correcto el estado actual de la pantalla Comparar coches.
- Batería: cuando bruta y utilizable difieren, se muestran en dos líneas; si coinciden, una sola.
- Mejores resultados: `0–100 km/h` y `10–80 %` resaltan en azul el menor tiempo.
- Valores textuales `null` o vacíos se muestran como `—`.
- Estado: **estable / listo para build y prueba final**.
- Último commit funcional de código: `46a7da27e52b7b835d9566426447efd509aa5102`.


## 2026-10-01 — Prueba completa de Comparar coches

- El usuario realizó una **prueba funcional y visual completa de la pantalla Comparar coches en el dispositivo** después de los últimos ajustes.
- Resultado: **correcto y validado**.
- Quedan confirmados en uso real: batería bruta/utilizable, resaltado de mejores resultados en `0–100 km/h` y `10–80 %`, sustitución de `null`/vacíos por `—`, dimensiones y formato numérico, traducción de la sección **Dimensiones** y sus etiquetas, y comportamiento general de la tabla y desplazamiento con las configuraciones probadas.
- La pantalla **Comparar coches queda cerrada como bloque funcional/visual**, salvo regresión futura.
- Siguiente bloque previsto: **revisión final del Buscador de coches**.


## 2026-10-01 — Comprobación previa de automatizaciones y release candidate

- Se revisaron los workflows actuales:
  - **Automatic protected catalog update** (`catalog-auto-update.yml`): programado diariamente a **08:19 y 15:00 Europe/Madrid**; usa Gaia/OpenEV y solo escribe `catalog_remote_additions.json`, por lo que el catálogo protegido no se sobrescribe directamente.
  - **Validate Spanish car catalog** (`catalog.yml`): se ejecuta manualmente y cuando cambia el catálogo protegido; valida años 2024–2026, año de llegada y consumo.
  - **GitHub schedule test** (`schedule-test.yml`): cron de prueba cada 10 minutos en los minutos 7,17,27,37,47,57. El run **36797923876 (#4)** se ejecutó por `schedule` y terminó correctamente el 01/10/2026 00:47 UTC, confirmando que el scheduler funciona.
  - **Build EV Calculator PRO** (`build.yml`): `push` a `main` (salvo cambios exclusivos en catalog_remote_additions) y manual; genera APK + AAB de release.
- El build **36844354921 (#1980)** terminó correctamente, pero corresponde al commit anterior `6a0d5c...`; **no se considera todavía el build candidato de la versión actual** porque después se realizaron los cambios finales de Comparar coches, traducciones y documentación.
- Este checkpoint restaura el contenido completo del MASTER y deja preparado un nuevo build de verificación con el estado actual. Cuando termine, habrá que comprobar APK/AAB y, especialmente, que el catálogo de 682 registros queda integrado en el artefacto.


## 2026-10-01 — Catálogo verificado dentro del APK 1.0.4.1 build 2062

- Se inspeccionó directamente el APK **EV-Calculator-PRO-v1.0.4.1.apk** del build **2062**.
- Se extrajo `assets/catalog_es_2024_2026.json` del binario.
- Resultado: **682/682 registros presentes dentro del APK**.
- Claves lógicas `marca + modelo + año + versión`: **682 únicas / 682**.
- El catálogo está físicamente empaquetado en el APK y disponible como asset local; no depende de una descarga externa para esos registros.
- SHA-256 del catálogo extraído del APK: `e4362d76ee86a306b9b5bd7552dfa136931fad20fc9d43ab5da0be8b4795c8ea`.
- Tamaño del JSON extraído: **922.755 bytes**.
- Esta comprobación **certifica la presencia e integridad interna del catálogo en el APK**. No se marca como verificación byte a byte frente al fichero fuente de GitHub porque el SHA anotado previamente (`410a5855...`) corresponde al **SHA del blob Git**, no a un SHA-256 del contenido.
- **Estado: catálogo 682/682 verificado dentro del APK 1.0.4.1 build 2062.**

## 2026-10-02 — Estable: traducción de «Quitar» en Comparar coches

- [x] **Comparar coches → acción «Quitar»**: corregido el texto que aparecía siempre en español.
- [x] Integrado con `LanguageManager`.
- [x] Traducciones **ES / EN / FR / DE / IT / PT** añadidas y verificadas.
- [x] Corrección confirmada como **estable**.

## 2026-10-02 — Estable: traducción del diálogo de información de pérdidas de carga

- [x] **ⓘ Información sobre pérdidas de carga**: corregido el texto explicativo que quedaba siempre en español.
- [x] Verificada la causa: faltaba la traducción del mensaje completo en `LanguageManager.java`.
- [x] Añadidas traducciones para **EN / ES / FR / DE / IT / PT**.
- [x] Corrección confirmada como **estable** en el código actual (commit `bd7be1b0ce65c5de942bd1bfeb217d3eb63248b2`).

## 2026-10-02 — 1.0.4.1 enviada a Producción

- **Google Play Producción:** se ha cargado correctamente **versionCode 58 / versionName 1.0.4.1**.
- Se ha pulsado **«Iniciar lanzamiento completo»**.
- **Envío 17:** incluye la versión 1.0.4.1 y los cambios de descripción de la ficha de Play Store.
- Estado actual mostrado por Play Console: **En revisión**.
- La versión anterior **1.0.4 / versionCode 56** permanece como versión publicada mientras Google completa la revisión del nuevo envío.
- **Siguiente comprobación:** esperar a que Google termine la revisión/publicación y verificar en Google Play que la versión pública pasa a **1.0.4.1 / 58**.

## 2026-10-02 — Avisos de GitHub Actions pendientes

Quedan anotados para revisar **después de publicar la 1.0.4.1**:

- [ ] **Gradle 9.1.0**: GitHub Actions lo marca como versión desactualizada. Revisar y actualizar cuando la versión 1.0.4.1 esté publicada y estable.
- [ ] **Runner `ubuntu-latest`**: GitHub ha avisado de la migración a **Ubuntu 26** a partir del **19/10/2026**. Revisar los workflows y comprobar compatibilidad antes de esa fecha.


## 2026-10-03 — Clasificación IARC

- **IARC Global Rating ID:** `9d7279dd-a574-8646-8790-3fa100060b99`.
- **Producto:** EV Calculator PRO.
- **Storefront:** Google Play.
- **Fecha de clasificación:** 03/10/2026.
- IARC confirma que las clasificaciones generadas a partir del cuestionario enviado están **Live**.
- Este ID queda registrado como referencia de la clasificación IARC actual de EV Calculator PRO.


## 2026-10-03 — Plan de colección visual para capturas de Google Play

- Se acuerda crear una colección coherente de **6 capturas promocionales** para la ficha de Google Play, inspirada en composiciones cuidadas de apps actuales (por ejemplo, la referencia GreenEV compartida por el usuario).
- **Orden acordado:** las primeras **3 capturas en tema claro** y las siguientes **3 en tema oscuro**, para mostrar variedad y comunicar que la app admite ambos temas.
- No duplicar sin más las mismas imágenes cambiando el tema: variar ligeramente composición, fondo y mensaje manteniendo una identidad visual común.
- Mantener una línea visual de **azul eléctrico/cian** para EV Calculator PRO; las tres primeras con una estética más luminosa y las tres últimas con fondos oscuros/eléctricos.
- Usar siempre la **interfaz real de EV Calculator PRO** como contenido del móvil, sin inventar botones, textos ni funciones que no estén en la aplicación.
- Temas de trabajo propuestos:
  1. **Claro — «Calcula tu carga»**: Charge Calculator.
  2. **Claro — «Controla el coste de cargar»**: pantalla Coste.
  3. **Claro — «Compara coches eléctricos»**: Comparar coches.
  4. **Oscuro — «Planifica tu carga»**: Charge Calculator.
  5. **Oscuro — «Conoce tu coche»**: comparación/detalle; ajustar la captura a las pantallas que estén realmente disponibles en la versión que se promocione.
  6. **Oscuro — «Todo tu EV en una app»**: composición final/resumen de funciones reales.
- La composición puede utilizar fondos con degradados, recursos gráficos eléctricos y mockups de móvil con encuadres dinámicos, pero los textos y datos de la interfaz deben seguir siendo legibles.
- **Próximo paso:** continuar la creación de la colección empezando por la captura 1 en tema claro. Antes de componerla, disponer de una captura real y actual de Charge Calculator para preservar la fidelidad de la interfaz. Guardar cada recurso final en la Biblioteca solo cuando se haya completado y verificado el guardado.


## 2026-10-04 — Colección visual Google Play creada

- Se ha creado la colección completa de 6 capturas promocionales siguiendo el plan acordado:
  1. Claro — «Calcula tu carga».
  2. Claro — «Controla el coste de cargar».
  3. Claro — «Compara coches eléctricos».
  4. Oscuro — «Planifica tu carga».
  5. Oscuro — «Compara y conoce tus opciones».
  6. Oscuro — «Todo tu EV en una app».
- La colección mantiene una identidad coherente: azul eléctrico/cian, composición promocional, fondos claros para 1–3 y fondos oscuros/eléctricos para 4–6, con variación de encuadre para evitar seis imágenes clonadas.
- Regla de fidelidad cumplida: las pantallas mostradas dentro de los móviles proceden de capturas reales de EV Calculator PRO. No se han creado interfaces ficticias.
- La captura 2 utiliza la pantalla real de Charge Calculator para comunicar el coste de carga, ya que la pantalla específica Coste no estaba disponible entre los recursos reales recuperados para esta colección.
- La captura 5 utiliza la pantalla real de Comparar coches, adaptando el mensaje para no inventar una pantalla de Detalle que todavía corresponde a la versión 1.0.5.
- La colección final se ha guardado en la Biblioteca: /EV Calculator PRO/Google Play/Colección 6 capturas/
- Archivos:
  - 01_Calcula_tu_carga_Light.png
  - 02_Controla_el_coste_Light.png
  - 03_Compara_coches_Light.png
  - 04_Planifica_tu_carga_Dark.png
  - 05_Conoce_tus_opciones_Dark.png
  - 06_Todo_tu_EV_Dark.png
- Estado: colección v1 creada y guardada. Antes de subirla a Google Play se debe hacer una revisión visual final de las seis piezas y, si se dispone de una captura real de la pantalla Coste/Detalle en una versión posterior, sustituir únicamente la pieza correspondiente manteniendo exactamente la identidad visual de la colección.


## 2026-10-04 — Automatización definitiva del catálogo

- Se ha comprobado que las ejecuciones automáticas del workflow Automatic protected catalog update (catalog-auto-update.yml) se lanzan y terminan correctamente.
- Se elimina el schedule de 08:19 de GitHub Actions.
- El workflow queda configurado con una única ejecución programada diaria a las 15:00, zona horaria Europe/Madrid.
- Se mantiene workflow_dispatch para lanzamientos manuales.
- Se mantiene el disparo por push cuando cambian los archivos definidos en el workflow.
- La automatización externa de cron-job.org de las 07:00 ha sido eliminada por el usuario.
- No quedan automatizaciones de ChatGPT activas para este proceso.
- Configuración definitiva: actualización automática programada del catálogo → todos los días a las 15:00 (Europe/Madrid).
- No debe volver a añadirse una segunda ejecución programada salvo decisión expresa.

## 2026-10-05 — Recursos reales definitivos para capturas Google Play

- Se fija como **fuente de verdad de las capturas reales de la campaña** la Biblioteca compartida por el usuario:
  https://chatgpt.com/library/share/6ac02f21b0b481919c69f981d517bedb?account_id=personal
- Para las composiciones promocionales **no se deben inventar interfaces ni sustituir las pantallas reales de EV Calculator PRO por mockups ficticios**.
- El **icono oficial de la app** debe tomarse del recurso real de la Biblioteca: `Icono de Calculadora EV Profesional.png`.
- La **captura real de Charge Calculator en tema claro** para la primera pieza es: `Captura 1 Claro.jpg`.
- La primera pieza de la colección queda definida como:
  - **Captura 01 — tema claro**.
  - Usar el **icono real** de EV Calculator PRO.
  - Usar dentro del mockup del teléfono la **interfaz real de `Captura 1 Claro.jpg`**.
  - Mantener la identidad visual común de las 6 piezas: azul eléctrico/cian, composición premium para Google Play, tipografía y jerarquía coherentes, fondo trabajado y recursos gráficos coordinados.
  - Las seis imágenes son una **única colección/campaña coordinada**, no seis capturas independientes.
- Los recursos promocionales generados anteriormente sirven únicamente como **referencia de estilo/composición** si contienen interfaces ficticias; no deben reutilizar esas interfaces ficticias en la versión final.
- La siguiente generación debe partir de estos recursos reales y conservar la fidelidad de la pantalla y del icono.
- **Estado:** pendiente de generar/revisar la nueva versión de la captura 01 cuando el generador de imágenes vuelva a estar disponible.

## 2026-10-05 — Checkpoint de seguridad antes de 1.0.5

- Se crea la rama de recuperación **`stable/pre-1.0.5-2026-10-05`**.
- Esta rama queda basada exactamente en el commit **`7ac0edce0e017844e41334eabc21ce1e7012793c`**, correspondiente al estado estable conocido tras fijar los recursos reales de las capturas de Google Play.
- **Regla:** esta rama es un punto de seguridad y **no debe modificarse** durante el desarrollo de 1.0.5.
- Antes de iniciar cambios funcionales de 1.0.5 se revisa la infraestructura de build:
  - AGP actual: **9.0.1**.
  - Gradle usado por GitHub Actions: **9.1.0**.
  - Java: **Temurin 17**.
  - Runner actual del workflow: **`ubuntu-latest`**.
- Decisión técnica provisional: **no actualizar AGP/Gradle todavía**; la combinación AGP 9.0.1 + Gradle 9.1.0 se considera estable y no se introduce una actualización innecesaria antes de 1.0.5.
- Queda pendiente probar el runner **`ubuntu-26.04`** de forma controlada antes de decidir si se sustituye `ubuntu-latest`. Si la prueba falla, se podrá volver a `ubuntu-24.04`.
- El desarrollo de **1.0.5** se realizará en una rama de trabajo separada, manteniendo este checkpoint como rollback.


## 2026-10-05 — Upgrade de toolchain validado para 1.0.5

- Se crea la rama aislada **`test/agp-9.4.0`** para probar la actualización sin tocar el checkpoint estable.
- Prueba completada correctamente en GitHub Actions, run **2084** (`37379945025`).
- Combinación validada: **AGP 9.4.0 + Gradle 9.6.0 + Java 17 + ubuntu-26.04**.
- El build Release completó correctamente `assembleRelease` y `bundleRelease`, incluyendo R8/minificación, firma y generación de APK/AAB.
- La prueba no produjo cambios automáticos adicionales en `main`: el paso de persistencia indicó que el código de búsqueda ya estaba optimizado.
- Tras la validación, **`develop/1.0.5` adopta AGP 9.4.0 y Gradle 9.6.0**.
- El checkpoint **`stable/pre-1.0.5-2026-10-05`** permanece intacto y sigue siendo el rollback de seguridad.
- Siguiente fase: desarrollo funcional de 1.0.5 (búsqueda de vehículos, detalle y flujo hacia comparación) sobre `develop/1.0.5`.


## 2026-10-06 — Preparada versión 1.0.5

- Se implementa el flujo inicial **Búsqueda → Detalle del coche → Añadir a comparativa**. La búsqueda abre ahora una pantalla de detalle con los datos del vehículo seleccionado y conserva la clave lógica del catálogo para integrarlo con la comparativa sin depender de un ID remoto mutable.
- El detalle muestra batería, autonomía, consumo, potencia, tracción, carga AC/DC, 10–80 %, maletero, peso, dimensiones y precio, respetando datos ausentes como `—` y diferenciando catálogo protegido de adiciones externas validadas.
- Se añade `CarDetailActivity` y su registro en `AndroidManifest.xml`, con navegación inferior común y traducciones ES/EN/FR/DE/IT/PT.
- Pendiente: compilación y prueba funcional de 1.0.5 antes de continuar con coches similares o nuevos cambios de UI.

- Inicio del bloque funcional **Búsqueda de coches**: el buscador de Comparar coches se mantiene sobre el catálogo protegido + adiciones externas cacheadas, sin sobrescritura de registros protegidos.
- La búsqueda 1.0.5 se mejora con normalización de acentos, coincidencia por múltiples términos y ordenación por relevancia (marca/modelo antes que coincidencias secundarias), manteniendo filtro por mercado y exclusión de vehículos ya seleccionados.
- Este cambio está en `develop/1.0.5` y queda pendiente de compilación/prueba funcional antes de continuar con Detalle del coche.

- Se establece **versionName 1.0.5** y **versionCode 59** en `app/build.gradle`.
- La versión 1.0.5 queda preparada para iniciar el desarrollo funcional sobre `develop/1.0.5`.
- No se ha generado ni publicado todavía un APK/AAB de 1.0.5; primero se implementarán y probarán las nuevas funciones.
