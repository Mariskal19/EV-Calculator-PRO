# EV Calculator PRO — MASTER

> Documento maestro del proyecto.  
> Última actualización: 22/10/2026 (checkpoint previo al asesor conversacional; auditoría de arquitectura solo lectura; respaldo de main).
>
> **Regla principal:** este archivo recoge el estado, decisiones y reglas de trabajo que deben conservarse al retomar el proyecto. No sustituye al código ni al catálogo; documenta cuál es la fuente de verdad de cada parte.

---

## 0. CURRENT STATE

- **Repositorio:** `Mariskal19/EV-Calculator-PRO`
- **Rama de trabajo principal:** `main`
- **Ramas stable:** no modificar sin autorización expresa.
- **Versión actual del código en `main`:** **1.0.5.1 / versionCode 61**, comprobada en `app/build.gradle` el 12/10/2026. Build y AAB de esta versión deben validarse antes de subirlos a Play.
- **Parámetros Android actuales:** `applicationId` `com.evcalculatorpro`, `minSdk 23`, `targetSdk 36`, `compileSdk 36`; Java 17 y Gradle 9.6.0 en el workflow de build.
- **Google Play:** último estado de publicación registrado en este documento: **1.0.4 / versionCode 56 publicada** el 02/10/2026. El envío posterior de 1.0.4.1 / 58 se documenta como histórico; comprobar Play Console antes de actualizar el estado público. La versión 1.0.5 de `main` no debe describirse como publicada sin esa comprobación.
- **Catálogo:** estado real actual en `main`: **688 registros** (`catalog-es-2024-2026-1.12`), tras auditoría y actualización de Zeekr el 20/10/2026.
- **Estado real verificado en `main` (20/10/2026): 688 registros** en `app/src/main/assets/catalog_es_2024_2026.json`.
- **Duplicados verificados (20/10/2026):** 0 duplicados lógicos (`marca + modelo + año + versión`) y 0 IDs duplicados.
- **Dimensiones:** comprobación de `main` el 20/10/2026: 688/688 registros tienen longitud, anchura y altura; no quedan registros sin dimensiones.
- **Batería utilizable:** comprobación de `main` el 20/10/2026: 688/688 registros tienen `usableBatteryKwh`; quedan 0 campos vacíos.
- **Precio:** comprobación de `main` el 20/10/2026: 688/688 registros tienen `price` informado. Los precios de tarifa no deben confundirse con ofertas temporales de contado/financiación.
- **batteryChemistry:** comprobación de `main` el 20/10/2026: 688/688 registros tienen `batteryChemistry`; los cambios de química Zeekr se documentan en la auditoría específica.
- **charge10to80Min:** el estado previo de 682/682 se registró el 01/10/2026; las dos nuevas versiones Zeekr incorporadas el 20/10 tienen su tiempo de carga informado. Repetir validación global antes de release.
- **dcKw:** auditoría global cerrada el **01/10/2026 21:48:32 +02:00**. Hay **681/681 registros aplicables** con `dcKw` informado; el único registro no aplicable es `MG MGS9 PHEV Comfort 2026`, que no dispone de carga rápida DC y mantiene `dcKw: null`. **0 pendientes aplicables**.
- **Explorer 2024–2026:** auditoría aplicada; se eliminaron entradas antiguas duplicadas y se corrigió el conjunto de propulsiones. El último ajuste eliminó la entrada 2025 `79 kWh 150 kW RWD` duplicada.
- **Estado del catálogo:** recuento histórico certificado de 682 (01/10/2026); 686 antes de la auditoría Zeekr; 688 en el estado actual (`catalog-es-2024-2026-1.12`, 20/10/2026). La diferencia neta de cuatro registros queda explicada por cuatro incorporaciones netas identificadas, más dos altas y dos bajas del Renault 5 por cambio de año de modelo. No borrar registros para forzar el recuento histórico.
- **Auditoría final:** se han validado los campos obligatorios, dimensiones y duplicados del JSON actual de 688 registros. La reconciliación histórica 682→686 queda documentada en la sección del 20/10/2026; la comprobación final del APK/AAB exacto sigue pendiente.
- **Build más reciente comprobado antes de esta limpieza:** workflow Build EV Calculator PRO, run `37907456650`, resultado **success**, sobre el commit `fae8168e5c8b133d52f5ee58110c228cdaf5cfca` (09/10/2026). El código de ese estado declara `1.0.5 / versionCode 59`; los commits de esta limpieza disparan nuevas ejecuciones y deben revisarse por separado.
- **Nota de artefactos:** el build correcto confirma compilación; no afirmar que se haya descargado o instalado manualmente el APK/AAB exacto si esa comprobación no se ha realizado.
- **Stable:** no tocar.

---

## 2026-10-20 — Auditoría y correcciones Zeekr

- Revisada la gama eléctrica de Zeekr España y contrastados los datos del catálogo con las fichas oficiales. El Zeekr 9X no se incorpora porque es un híbrido enchufable, no un eléctrico puro.
- Catálogo actualizado a `catalog-es-2024-2026-1.12`: **688 registros totales**, **13 registros Zeekr**, sin duplicados lógicos ni IDs duplicados tras los cambios.
- Corregidas las dimensiones MY2026 del 001, X y 7X. Para anchura se conserva en `widthMm` la medida de carrocería sin retrovisores; la web oficial publica la medida con retrovisores, por lo que no se deben comparar ambas directamente.
- Corregida la química del 7X Core RWD a LFP, coherente con su batería de 75 kWh; corregida la batería útil del X Long Range RWD a 60 kWh y mantenida su química LFP según la ficha técnica europea MY2026.
- Corregida la aceleración del X Long Range RWD a 5,5 s y del X Privilege AWD a 3,7 s; la aceleración del 7GT Long Range RWD queda en 5,3 s y la del 7GT Privilege AWD en 3,3 s.
- Añadidas las configuraciones 2026 **001 Privilege AWD** y **X Core RWD**, ausentes de la base previa. Datos que no aparecen completos en la ficha oficial española se identifican con su fuente secundaria en el registro; el precio de tarifa no debe confundirse con campañas temporales.
- Validación posterior al cambio: dimensiones, batería utilizable, precio y química de batería informados en los 688 registros; 0 duplicados lógicos y 0 IDs duplicados.
- Fuentes: [Zeekr 001 España](https://zeekr.es/zeekr-001), [Zeekr X España](https://zeekr.es/zeekr-x), [Zeekr 7X España](https://zeekr.es/zeekr-7x), [Zeekr 7GT España](https://zeekr.es/zeekr-7gt), [ficha técnica europea Zeekr X febrero 2026](https://www.datocms-assets.com/128969/1773048205-zeekr-x-a2-a4-product-leaflet-en-master.pdf), [km77: Zeekr X Core RWD](https://www.km77.com/coches/zeekr/x/2024/estandar/estandar/x-core-rwd/datos), [EV Database: X Long Range RWD MY26](https://ev-database.org/car/3585/Zeekr-X-Long-Range-RWD), [tarifas Zeekr 001 2026](https://www.motor.es/zeekr/001/).

## 2026-10-12 — Protección automática de Play y compatibilidad Android

- Google Play Console rechazó el AAB con `minSdk 23` al estar activada la **Protección automática**, indicando que esa función requiere API 24 o superior.
- El interruptor de Protección automática se ha desactivado desde Play Console por decisión del usuario, para evitar excluir dispositivos con Android 6.0/API 23.
- Se restaura `minSdk = 23` en `app/build.gradle`, actualizando a `versionCode = 61` y manteniendo `versionName = '1.0.5.1'`. El objetivo es mantener la compatibilidad con Android 6 y generar un AAB nuevo con la configuración corregida.
- Commit provisional que elevó el mínimo a API 24: `bbbc86e948d105e2e3e17dc2466c1f15fc604b9e`. Commit que restaura API 23: `464c493ab3434e06b6b5b4ca862cb7e10d657bc5`.
- La protección automática se ha desactivado; otros servicios de protección de Play que figuran activos en la consola no se han modificado por este cambio.
- **Pendiente:** comprobar que GitHub Actions termina con éxito para la configuración restaurada, descargar/verificar el AAB de `1.0.5.1` y subir ese artefacto nuevo a Play Console. No utilizar el AAB anterior generado con `minSdk 24` ni dar por publicada la versión hasta comprobar el resultado en Play Console.

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
2. 💰 **Coste** → Cost / cálculo de coste
3. 🚗 **Buscar** → búsqueda de coches similares
4. ⚖️ **Comparar** → comparación directa de coches
5. ⋮ **Más** → configuración y acciones secundarias

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

**Estado de referencia histórico: 682 registros (01/10/2026). Estado actual en main: 688 registros (20/10/2026), sin duplicados lógicos ni IDs duplicados.**

La diferencia histórica se analiza por commits y por clave lógica; no se resolverá mediante borrado automático. Mantener como fuente de verdad el JSON de `main` y documentar cualquier variación con los registros concretos que la explican.

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

La comprobación real del 01/10/2026 confirmó 682 registros en `main`; esa cifra es histórica y ha sido superada. El JSON actual contiene 688 registros. La diferencia neta de cuatro registros hasta 686 está reconciliada en el apartado «Reconciliación del recuento histórico del catálogo» del 20/10/2026; las dos fichas Zeekr nuevas elevan el total de 686 a 688.

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

## Tipo de vehículo y filtro de compatibilidad

La pantalla **Buscar coches similares** no busca únicamente coches con especificaciones matemáticamente próximas. Su objetivo es encontrar **coches similares que realmente puedan competir entre sí como alternativas de compra**.

La arquitectura acordada para el algoritmo es por capas:

1. **Carrocería / tipo de vehículo — filtro obligatorio de entrada**
   - SUV con SUV.
   - Crossover con crossover.
   - **SUV y crossover se consideran compatibles entre sí**, porque forman un mismo universo competitivo.
   - Berlina, familiar, hatchback, coupé, monovolumen, etc. no deben entrar como rivales directos de un SUV salvo que en el futuro se defina explícitamente una compatibilidad equivalente.
   - La carrocería no es un peso porcentual: es una condición previa de elegibilidad.

2. **Segmento / clase — filtro de compatibilidad**
   - El segmento se utiliza como segundo filtro.
   - Si coincide, el vehículo es elegible.
   - En SUV/crossover se permite que el catálogo utilice letras de segmento distintas cuando las dimensiones demuestran que pertenecen a la misma clase física competitiva.
   - No se debe utilizar la marca como criterio de similitud o de competitividad.

3. **Zona competitiva**
   - Una vez superados carrocería y segmento/clase, se evaluará la proximidad de mercado: precio y características de uso/posicionamiento.
   - Dentro de la distancia de competencia, el precio tiene un peso del **35 %**; esta fase representa el **60 %** de la puntuación final. No confundir este peso comercial con los pesos de la fase técnica.
   - Esta capa debe derivarse automáticamente de los datos del catálogo, sin listas manuales de rivales ni reglas específicas como «G6 → Model Y».

4. **Similitud técnica**
   - Sobre los candidatos elegibles se aplica el algoritmo técnico ponderado.
   - Pesos de la **fase técnica direccional activa**:
     - Autonomía WLTP: **25 %**
     - Potencia: **20 %**
     - Aceleración 0–100 km/h: **20 %**
     - Tiempo de carga 10–80 %: **20 %**
     - Consumo: **15 %**
     - Total: **100 %**
   - Composición de la distancia de competencia: precio **35 %**, clase física/dimensiones **30 %**, carrocería **20 %** y segmento **15 %**. Los pesos se normalizan si algún dato no está disponible.

**Comprobación real del catálogo en main (07/10/2026):**
- 682 registros.
- bodyStyle: **682/682 completos**; 0 ausentes/vacíos.
- Distribución bodyStyle: SUV 347, hatchback 90, sedan 108, wagon 23, crossover 84, coupe 18, van 8, convertible 3, liftback 1.
- segment: **682/682 completos**; 0 ausentes/vacíos.
- Distribución segment: A 18, B 115, C 171, D 125, E 84, F 19, J 137, M 8, S 5.

La comprobación confirma que el catálogo actual **sí dispone de los dos campos estructurados necesarios para aplicar el primer y segundo filtro automáticamente**. No es necesario añadir manualmente la carrocería a los 682 registros.

## Estado actual de la implementación

Implementación activa en `SimilarCarsActivity`:
- El flujo de candidatos aplica filtros obligatorios de año/marca, carrocería compatible, clase física y zona de precio antes de puntuar.
- SUV y crossover se admiten como compatibles entre sí; una berlina no puede entrar como rival de un SUV por obtener buena puntuación técnica.
- La distancia de competencia combina precio, dimensiones/clase física, carrocería y segmento.
- La fase técnica es direccional: solo penaliza al candidato cuando queda por debajo del referente en potencia, aceleración, autonomía, consumo o tiempo 10–80 %.
- La combinación final activa es **60 % competencia + 40 % técnica**, ordenada por el valor interno completo antes del redondeo visual.
- Se conserva el mejor representante de cada marca y se presentan los primeros resultados elegibles.
- Los datos proceden del catálogo protegido más las adiciones remotas validadas mediante `RemoteCatalogManager`.

El flujo activo ya aplica filtros obligatorios de carrocería, clase física y zona de precio antes del ranking. La puntuación final vigente combina **60 % competencia + 40 % características técnicas direccionales**. No reabrir el diseño de la zona competitiva ni cambiar los pesos sin una incidencia reproducible y una revisión de los valores internos.

## Flujo actual de coches similares — 1.0.5

- La barra inferior ofrece accesos directos e independientes:
  1. **Buscar** → abre directamente la pantalla de búsqueda de coches similares, cuyo título interno sigue siendo **Buscar coches similares**.
  2. **Comparar** → abre directamente la pantalla de comparación de hasta 3 vehículos.
- **Comparar coches** no contiene la pantalla de búsqueda de similares: mantiene exclusivamente su función de comparación.
- **Buscar coches similares** permite localizar un coche de referencia mediante buscador por marca/modelo/versión y filtros de mercado, año, tracción y rango de batería.
- Al seleccionar el coche de referencia, la app calcula y muestra hasta **10 opciones similares** (constante `FINAL_TOP = 10` en el código activo).
- El ranking combina una fase comercial (precio, dimensiones/clase física, carrocería y segmento) con una fase técnica direccional (potencia, aceleración 0–100, autonomía WLTP, consumo y tiempo de carga 10–80 %). La batería, el maletero, la tracción y la carga DC no son variables directas de la fase técnica direccional activa.
- Al pulsar una de las 5 opciones se abre **CarDetailActivity**, la pantalla de detalle completa.
- Desde el detalle, **Añadir a comparativa** incorpora el vehículo a la comparativa existente.
- La fuente de datos es siempre **catálogo protegido + catálogo externo validado**, sin sobrescribir datos protegidos.
- La navegación inferior no utiliza **CochesActivity** como selector: **Buscar** y **Comparar** son accesos directos independientes. El catálogo general queda aplazado.

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
- Release Google Play 1.0.4: `versionCode 56` (valor confirmado en el registro de publicación). Se descarta la anotación histórica `versionCode 831` por inconsistente con el versionado real.
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

### Histórico — Release 1.0.4.1 (no es la versión actual de desarrollo)

- [x] Confirmar `versionName = 1.0.4.1` en `app/build.gradle`.
- [x] Confirmar `versionCode = 58` en `app/build.gradle`.
- [x] Confirmar que es superior al versionCode publicado en Google Play (56).
- [x] Confirmar build #2065 / run `37053413924` con conclusión **success**.
- [ ] Obtener/descargar el AAB final del build.
- [ ] Instalar y probar en el Redmi Note 13 Pro el build final exacto.
- [x] Subir 1.0.4.1 / versionCode 58 a Google Play Producción.
- [x] Iniciar el lanzamiento completo de 1.0.4.1 en Producción.
- [ ] Verificar en Play Console el resultado final de aquel envío 1.0.4.1 / 58 (tarea histórica; no confundir con la versión de desarrollo actual 1.0.5 / 59).
- [x] Revisar Google Play: **Producción publicada con 1.0.4 / versionCode 56**.

### Prioridad actual — coherencia de documentación y versión

- [x] Confirmar `versionName = 1.0.5` y `versionCode = 59` en `app/build.gradle`.
- [x] Actualizar README y MASTER para separar la versión de `main` del estado de Google Play.
- [x] Corregir el estado de las incidencias del filtro de carrocería y de la etiqueta del coche de referencia.
- [x] Confirmar build de `main` en success: run `37907456650`.
- [ ] Confirmar en Play Console el estado actual de cualquier envío posterior a 1.0.4 / 56 antes de actualizar la sección de publicación.

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
8. Mantener sincronizados `README.md`, `app/build.gradle`, el workflow de build y los comentarios de código con la versión actual.
9. Crear un checkpoint después de cada bloque importante.

---

# 18. HISTORIAL DEL MASTER

| Fecha | Cambio |
|---|---|
| 07/10/2026 | **Algoritmo de coches similares:** se fija como arquitectura que la carrocería es el primer filtro obligatorio, seguida de segmento/clase; SUV y crossover son compatibles. Comprobación real del catálogo en main: 682/682 registros tienen bodyStyle y 682/682 tienen segment. La siguiente fase será derivar automáticamente la zona competitiva y después aplicar la similitud técnica ponderada, sin reglas manuales por marca/modelo. |\n| 02/10/2026 | **1.0.4.1 / versionCode 58 enviada a Producción y lanzamiento completo iniciado.** Play Console muestra el envío 17 **En revisión**; incluye también los cambios de descripción de la ficha. Pendiente verificar publicación pública. |
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
- **Build:** los estados históricos de build descritos en esta sección corresponden a versiones anteriores; para el estado actual de `main`, consultar el run más reciente de GitHub Actions y el versionado de `app/build.gradle`.
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
6. Para una publicación futura, verificar el estado real de Play Console y usar un `versionCode` superior al último publicado; el código de desarrollo actual es **1.0.5 / 59**.
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

## 2026-10-07 — Stable 17:00 — Búsqueda optimizada y validada

- **Checkpoint funcional:** se fija el estado del proyecto como estable a las **17:00 del 07/10/2026**.
- La búsqueda de **Buscar coches similares** queda **cerrada como estable** tras prueba real del usuario.
- La búsqueda de **Comparar coches** queda igualmente **cerrada como estable**.
- Se optimizó el rendimiento sin cambiar la lógica funcional ni el orden de resultados: caché de campos normalizados por vehículo, normalización/tokenización del texto una sola vez por consulta, cálculo de relevancia una sola vez por candidato y ordenación usando las puntuaciones calculadas.
- Se añadió un **debounce de ~70 ms** y cancelación de refrescos pendientes para evitar trabajo acumulado durante la escritura rápida.
- El usuario verificó en dispositivo que ahora la búsqueda **funciona muy rápido**.
- Se mantiene el comportamiento funcional existente y el algoritmo de resultados.
- **Regla desde este checkpoint:** no tocar esta parte salvo que aparezca una incidencia real o una regresión comprobada.
- El flujo **Buscar similares → Detalle → Añadir a comparativa** mantiene como primer vehículo de la comparativa el **coche de referencia**, seguido del similar seleccionado.
- Este estado se considera el **baseline estable de referencia** para continuar el desarrollo de EV Calculator PRO.


## 2026-10-07 — Stable 19:55 — Scroll final validado en Android 10 y Android 16

- Se da por **estable** el ajuste final del espacio de scroll inferior en las pantallas con navegación inferior fija.
- La solución mantiene la barra inferior superpuesta/fija y añade **48 dp de espacio de scroll interno** al final de los ScrollView, evitando tanto el solapamiento de contenido como la aparición de una banda externa de 64 dp.
- El usuario ha probado el resultado en **Redmi Note 7 (Android 10)** y **Redmi Note 13 Pro (Android 16)**.
- Resultado confirmado por el usuario: **“Se ve perfecto en los dos”**.
- El espacio visual antes de la barra inferior queda consistente entre ambas versiones de Android.
- Commit estable: `e76b931f826b0d25c0e4f731ed9b534f0dbbe0bb` — `fix: reducir espacio final del scroll a 48dp`.
- Este checkpoint queda como **baseline estable de referencia a las 19:55 del 07/10/2026**.
- No modificar este comportamiento salvo regresión real comprobada.


## 2026-10-07 — Estado consolidado de trabajo y ramas

- **Rama activa de desarrollo:** `main`. Todo el trabajo activo de 1.0.5 debe continuar en `main`.
- La rama histórica `develop/1.0.5` **ya no es la rama de desarrollo activa**. Se conserva temporalmente como referencia/backup histórico y no debe retomarse para continuar el desarrollo.
- Ramas/checkpoints históricos `stable/pre-1.0.5-2026-10-05` y `stable/1.0.5-similar-cars`: no modificar salvo autorización expresa.
- El trabajo de búsqueda, detalle y coches similares pertenece a **1.0.5**, que ya es la versión declarada por `main` (`versionCode 59`).
- **Buscar coches similares → Detalle → Añadir a comparativa:** al pasar al comparador, el **coche de referencia siempre ocupa la primera posición** y el coche similar seleccionado ocupa la siguiente posición.
- Al iniciar el flujo desde la búsqueda hacia el comparador, se limpia la selección previa y se reconstruye el comparador con **referencia primero + similar elegido después**.
- **Búsqueda optimizada:** se mantienen cachés de campos normalizados por vehículo, normalización/tokenización única por consulta, cálculo único de puntuación por candidato, ordenación mediante puntuaciones cacheadas, debounce aproximado de 70 ms y cancelación de refrescos pendientes.
- **Scroll final estable:** la navegación inferior permanece fija/superpuesta y los ScrollView reciben 48 dp de espacio interno inferior. Este ajuste fue probado y validado en Redmi Note 7 (Android 10) y Redmi Note 13 Pro (Android 16), con resultado visual correcto.
- No modificar estos bloques estables salvo regresión real comprobada.

### Checkpoints confirmados el 07/10/2026

- **17:00 — Búsqueda optimizada:** commit `9b5781da1d600cf36d135a6028d4dd3263495fec`.
- **19:55 — Scroll final:** commit `e76b931f826b0d25c0e4f731ed9b534f0dbbe0bb`.
- **19:55 — Actualización de MASTER:** commit `dd092d2bf95c10a098e14cd03f9457d032d0ddd9`.


## 2026-10-08 — Cierre y validación final de coches similares

- Se da por **cerrado y estable** el algoritmo actual de **Buscar coches similares**.
- Se mantiene el reparto definitivo: **72 % competencia + 28 % características**.
- El ranking utiliza siempre el `double` completo para ordenar; el porcentaje visible se redondea únicamente al final.
- Pipeline definitivo: filtros de año/marca → carrocería/clase física → zona competitiva → cálculo de competencia y características → combinación 72/28 → ordenación por `Double.compare(score)` → TOP 20 → deduplicación por marca conservando el menor score → nueva ordenación → 8 primeras posiciones.
- Se retiró del código de producción el **panel de diagnóstico temporal** y sus métodos de trazado, una vez confirmada la corrección del ranking.
- Validación real con XPeng G6 como referencia:
  - **Tesla Model Y #4:** competencia 83,66 %, características 59,69 %, total 76,95 %, score `0.23050165`.
  - **Opel Grandland Electric #5:** competencia 81,39 %, características 65,10 %, total 76,83 %, score `0.23167884`.
  - Tesla está correctamente por delante: diferencia interna `0.00117619` y diferencia visible de **0,12 puntos porcentuales**.
- El puesto observado en pantalla coincide con el ranking calculado: **Tesla #4 y Opel #5**.
- No modificar pesos ni ordenamiento por esta diferencia mínima salvo nueva incidencia real y reproducible.


## 2026-10-08 — Stable 10:40 — Ranking coches similares y visualización CV

- Se fija como **versión estable de referencia** el estado probado por el usuario a las **10:40 del 08/10/2026**.
- Algoritmo de coches similares: **72 % competencia + 28 % características**, con ordenación por el valor interno completo antes del redondeo visual.
- Validación real con XPeng G6 como referencia:
  - **Tesla Model Y #4:** competencia 83,66 %, características 59,69 %, total 76,95 %, score 0.23050165.
  - **Opel Grandland Electric #5:** competencia 81,39 %, características 65,10 %, total 76,83 %, score 0.23167884.
  - Tesla queda correctamente por delante por 0.00117619 de score interno (0,12 puntos porcentuales visibles).
- Se retiró el diagnóstico temporal del código de producción una vez validado el ranking.
- En las fichas de coches similares, los **CV se muestran redondeados a 0 decimales** mediante HALF_UP; el cálculo interno no cambia.
- **Commit final de esta corrección:** b808bd19dcb03a63d92eeb675a5967566320a23c — fix: redondear CV en fichas de coches similares.
- Este checkpoint queda como **baseline estable de referencia de las 10:40 del 08/10/2026**.
- No modificar este estado salvo nueva incidencia o regresión comprobada.


### 2026-10-08 — Stable 13:20 — Tarjetas de coches similares
- Punto estable fijado a las **13:20 del 08/10/2026**.
- En las tarjetas de resultados, el nombre del coche queda centrado verticalmente respecto al número de posición y al porcentaje de coincidencia.
- Ajuste final: **4 dp hacia arriba** mediante `translationY(-dp(4))` sobre el nombre.
- No se modifica el ranking, los cálculos, las puntuaciones ni el porcentaje mostrado.
- Commit de código: `703c64e3ebc6b893a5b1d62fc702e51f7401489a`.
- Este checkpoint se considera la referencia estable de la interfaz de tarjetas similares a las 13:20.


## 2026-10-08 — Stable final — Ranking de coches similares con distancia técnica direccional

- Se fija como **nuevo estado estable** el commit `5b5ed3c06fc6b359b1d549c7d8a5c0980f95f800` — `Refina ranking de rivales con distancia técnica direccional`.
- El usuario ha probado la versión resultante en el dispositivo y confirma que **el resultado funciona muy bien**.
- Este checkpoint sustituyó el reparto experimental anterior de 72/28: el **código activo de la versión 1.0.5 usa 60 % competencia + 40 % características técnicas**.
- La fase de competencia sigue determinando la proximidad comercial del rival; la segunda fase calcula una **distancia técnica direccional**.
- La distancia técnica direccional **solo penaliza al candidato cuando es peor que el coche de referencia**. Si el candidato es mejor en una variable, esa variable no genera penalización.
- Variables y pesos de la fase técnica actual:
  - Potencia: **20 %**
  - 0–100 km/h: **20 %**
  - Autonomía WLTP: **25 %**
  - Consumo: **15 %**
  - Tiempo 10–80 %: **20 %**
  - Total: **100 %**
- La normalización de cada variable utiliza una tolerancia y `tanh`; para magnitudes donde más es mejor se penaliza `referencia - candidato`, y para las que menos es mejor se penaliza `candidato - referencia`.
- Se elimina del cálculo activo la antigua **bonificación arbitraria por carga DC**. La carga 10–80 % ya participa de forma natural con un **20 % dentro de la fase técnica**.
- El ranking final se ordena utilizando el **valor `double` interno completo**, sin ordenar por el porcentaje redondeado que se muestra en pantalla.
- Para cada marca se conserva el vehículo representante que minimiza el score combinado **60 % competencia + 40 % técnica**, aplicando además la penalización de año prevista.
- Después se ordenan los representantes por el score combinado y se muestran las primeras opciones disponibles.
- Se mantiene la exclusión de la propia marca del coche de referencia y el filtro temporal de modelos alrededor del año de referencia.
- Se mantiene el ajuste visual estable de las tarjetas: nombre del coche desplazado **4 dp hacia arriba** para quedar correctamente alineado con posición y porcentaje.
- Los **CV se muestran redondeados a 0 decimales** en las tarjetas, sin modificar los valores internos utilizados en los cálculos.
- La corrección anterior que eliminó el diagnóstico temporal se mantiene: el panel de diagnóstico no forma parte del código de producción.
- El workflow de build de `main` continúa configurado para ejecutarse automáticamente con cada `push` a `main`; no se ha modificado esta configuración.
- **Regla de estabilidad:** no volver a cambiar pesos, fórmula o dirección del algoritmo por diferencias mínimas de ranking sin una incidencia reproducible y una comprobación de los valores internos.

### Checkpoint asociado

- **Commit:** `5b5ed3c06fc6b359b1d549c7d8a5c0980f95f800`
- **Mensaje:** `Refina ranking de rivales con distancia técnica direccional`
- **Estado:** probado por el usuario y aceptado como estable.
- **Rama:** `main`.
- **Referencia anterior de interfaz:** `703c64e3ebc6b893a5b1d62fc702e51f7401489a` (Stable 13:20, nombre de tarjeta a -4 dp).


## 2026-10-09 — Corrección del filtro obligatorio de carrocería

- Incidencia reproducible reportada por el usuario: al seleccionar **XPeng G6 Standard RWD 2026** aparecía **Tesla Model 3** como primer similar, pese a ser una berlina.
- Causa localizada en `SimilarCarsActivity.showSimilar()`: el bucle activo calculaba y puntuaba candidatos sin llamar a `inCompetitiveZone(reference, v)`. Existían métodos de filtro para carrocería/clase física/zona económica, pero no se aplicaban al flujo principal antes del ranking.
- Corrección aplicada en `main`: el flujo activo descarta el candidato con `if(!inCompetitiveZone(reference,v)) continue;` antes de calcular sus puntuaciones. También se añade el filtro al método auxiliar `buildCompetitionCandidates()`.
- Regla obligatoria: **si la referencia es SUV, solo se admiten SUV y crossover**; nunca berlinas, familiares, compactos, coupés ni monovolúmenes. SUV/crossover pueden ser compatibles entre sí. La carrocería/clase se filtra antes de competir por puntuación, por lo que ningún score técnico puede compensar una carrocería incompatible.
- Se conserva el algoritmo de puntuación vigente; no se cambian pesos ni la fórmula técnica. El filtro existente también exige clase física compatible y aplica la zona de precio configurada.
- Commit de código: `9036d2f1b4974b1fac9f390795e19fb3fcf737f2` — `fix: filtrar rivales por carrocería y clase antes del ranking`.
- Estado: el usuario confirmó posteriormente que la incidencia quedó solucionada. El filtro obligatorio permanece aplicado antes del cálculo del ranking. Build de `main` comprobado en success el 09/10/2026; no se han modificado los pesos por esta corrección.


### 2026-10-09 — Unificar el formato del coche de referencia en Buscar coches similares
- El selector mostraba cada coche con `pickerLabel()` (marca/modelo, año, versión normalizada y batería), pero al seleccionarlo reconstruía otra etiqueta con marca/modelo y versión sin normalizar, omitiendo año y batería.
- La selección ahora reutiliza `pickerLabel()` y convierte el salto de línea en separadores, para que la referencia conserve los mismos datos y nombres que el resultado elegido en el buscador.
- Commit de código: `7a3f955f437dc144eed904f0831a6e92d6c95a9d`. El usuario dio por solucionada la incidencia. El build posterior `37907456650` terminó en success sobre el commit de documentación `fae8168e5c8b133d52f5ee58110c228cdaf5cfca`.


## 2026-10-09 — Limpieza de versionado y documentación

- README y MASTER alineados con la versión que declara `main`: **1.0.5 / versionCode 59**.
- Se distingue la versión de desarrollo del último estado de Google Play registrado (**1.0.4 / versionCode 56 publicada**); no se da por publicada la 1.0.5 sin comprobar Play Console.
- Se corrigen la descripción de la fase técnica, los pesos de competencia, el máximo de resultados (**10**) y las referencias históricas inconsistentes de versionado.
- Se eliminó de `SimilarCarsActivity` el helper de bonificación DC no utilizado y su comentario obsoleto sobre el reparto 72/28; los comentarios activos describen ahora el reparto **60/40** como competencia + distancia técnica direccional.
- Commits de documentación/código: `db169d8c327bcd7d2274b95e1d6787dbbe1d06e0`, `72e959f9a824a2996d62497a471cb4c28ca43174`, `4089e249f68cb6affeb241395bd5da74b7d0f6fb`, `e6d5f2f39c42d45328654fb644677de013766bdd`, `8206df73069106bbc3a684390c8a29fb964d7de6` y `8d872878b506eef0cf4eb28761b6d96605b5afef`.
- Los builds disparados por estos commits deben revisarse en GitHub Actions antes de dar por validado el estado completo.


## Checkpoint estable — 09/10/2026 a las 11:30

- **Nombre de referencia:** `backup/stable-2026-10-09-1130`.
- **Versión de código:** `1.0.5` / `versionCode 59`.
- **Contenido incluido:** limpieza del README y MASTER, documentación del ranking de coches similares 60/40, filtros obligatorios de carrocería/clase y corrección de la etiqueta del coche de referencia; helper DC no utilizado eliminado.
- **Último commit de documentación antes de registrar este checkpoint:** `3ed523ca5947399a621f443d56c1dba55808ddb2`.
- **Validación:** las ejecuciones de build asociadas a las últimas modificaciones seguían en curso al registrar este checkpoint. El checkpoint guarda el estado del código y los documentos, pero no certifica un build final correcto ni una prueba en dispositivo.
- **Protección:** esta rama es una referencia de recuperación; no modificarla ni sobrescribirla sin autorización expresa.

## 2026-10-09 — Rivales del Tesla Model 3: corrección de filtros y dimensiones

- Se corrigieron las dimensiones del BYD Seal en las cinco variantes de catálogo de 2024–2026: **4.800 × 1.875 × 1.460 mm**, contrastadas con la ficha oficial de BYD. Antes figuraba una altura de 1.670 mm, que lo hacía fallar el filtro de clase física.
- Se corrigieron las dimensiones oficiales del Polestar 2 Long Range Single Motor 2024 a **4.606 × 1.859 × 1.479 mm**. La clasificación de carrocería del catálogo sigue siendo `hatchback`, pero el algoritmo ahora permite que un hatchback/fastback/liftback compita con una berlina si ambos pertenecen al mismo segmento y pasan el filtro estricto de dimensiones. Se aplica una distancia de carrocería moderada, no una equivalencia total.
- Se amplió el margen predeterminado de precio del **15 % al 20 %** para no excluir alternativas comerciales próximas como el Hyundai IONIQ 6 y el Polestar 2 únicamente por una diferencia moderada de PVP.
- Afinado posterior: la compatibilidad berlina↔hatchback **no se aplica a cualquier hatchback**. Los registros `fastback`/`liftback` pueden pasar por la regla de compatibilidad; los registrados como `hatchback` solo se consideran fastback-like si miden al menos 4.550 mm y su relación altura/longitud es ≤ 0,325. En todos los casos siguen siendo obligatorios el mismo segmento y el filtro dimensional estricto (±8 % longitud, ±5 % anchura, ±8 % altura). Código: commit `7e982ca2f318aab76fd30183cec80ed3de6c9f18`.
- No se añadieron excepciones por marca/modelo y no se alteraron los pesos del ranking (60 % competencia / 40 % distancia técnica direccional).
- Cambio posterior (2026-10-09): se admite también la compatibilidad **berlina↔familiar (`wagon`)** únicamente si ambos vehículos comparten el mismo segmento y cumplen el filtro físico estricto (±8 % longitud, ±5 % anchura, ±8 % altura). Esto permite evaluar como alternativas de berlina al Volkswagen ID.7 Tourer, Mercedes-Benz CLA Shooting Brake eléctrico y Zeekr 7GT cuando sus datos superen también el filtro de precio.
- Regla de seguridad de carrocería explícita: **nunca proponer un SUV/crossover como rival de una berlina, ni una berlina/familiar como rival de un SUV**. La compatibilidad SUV↔crossover solo se mantiene cuando la referencia también es SUV/crossover. No se fuerza que el ranking alcance 10: muestra solo las marcas que realmente superen los filtros.
- Commit de código de compatibilidad berlina↔familiar: `56a389a95fe2b708b707a56081b2639bff7458ef`. Build y prueba en dispositivo pendientes.
- Referencias oficiales consultadas: BYD Seal, https://media.byd.com/byd-seal-arrives-in-europe-setting-the-standard-in-breakthrough-technology-and-stunning-design/?lang=eng ; Polestar 2, https://www.polestar.com/es/polestar-2/specifications .
- Commit de código: `0b4345a35a08a1155deaaf53a77e4930a56b75b7`.
- Commit de catálogo: `f4fe3175d5e25c185ea6f1ce168788ab04ddbdf4`.
- Validación automática disparada: build `37913433681` y validación de catálogo `37913433684`. Pendiente de que ambos finalicen; no se declara aún el APK como validado.


## 2026-10-09 — Orden de resultados del buscador de coches

- Se modificó `CompararCochesActivity.orderedSearchVehicles()` para priorizar cuántos términos de búsqueda aparecen en la combinación marca+modelo; a continuación ordena por año descendente y usa la puntuación de coincidencia como criterio posterior. Esto evita que una palabra que aparece accidentalmente en la versión o en otros datos haga alternar años entre versiones del mismo modelo.
- El filtro de coincidencias no cambia y se mantiene la ordenación secundaria existente por marca, modelo, tipo de versión, batería y nombre de versión.
- Commit de código: `b2f7f0d897ea7a8b699cc2e07ce720017db84117`.
- Build y comprobación en dispositivo pendientes; no afirmar validación hasta que termine la acción y se pruebe una búsqueda como «Model 3».


## 2026-10-09 — Corrección adicional de agrupación por modelo en el buscador

- Tras observar que «Tesla Model 3» todavía podía alternar años, se endureció el comparador: cuando dos resultados tienen la misma marca+modelo normalizados, el criterio prioritario entre ellos es año descendente, antes de cualquier puntuación secundaria.
- Commit de código: `cd6da5f4b1af2db49b098095b02ad264c6b5e5ca`.
- Build y prueba en dispositivo pendientes. Verificar que todas las variantes de 2026 precedan a las de 2025 y 2024 al buscar «Model 3».


## 2026-10-09 — Corrección del comparador de ordenación del buscador

- El usuario confirmó que seguían apareciendo primero las versiones Premium Gran autonomía de 2025 y después el Performance de 2026, por lo que la corrección condicional anterior no bastaba.
- Causa técnica probable: el comparador aplicaba reglas diferentes según si dos vehículos compartían modelo, lo que podía romper la transitividad exigida por la ordenación y permitir resultados inesperados.
- Se reemplazó por una ordenación lexicográfica consistente: coincidencia de términos marca+modelo, puntuación de búsqueda, agrupación por marca+modelo normalizados y año descendente dentro del grupo; después se aplican criterios secundarios.
- Commit de código: `c4b0261a2e14dd0a67136d896e337fd9dfa10883`.
- Pendiente: compilar esta revisión concreta e instalar el APK recién generado. No dar el orden por solucionado hasta que el usuario confirme que Performance 2026 aparece antes que Premium Gran autonomía 2025 al buscar «Model 3».


## 2026-10-09 — Corrección aplicada al buscador correcto: Buscar coches similares

- El usuario aclaró que el problema de orden afectaba al selector de la pantalla **Buscar coches similares**, no al selector de **Comparar coches**, cuyo comportamiento estaba correcto.
- Se restauró en `CompararCochesActivity.java` el comparador previo a la intervención equivocada: commit `bceeea71125ea73ced4e5702af114700e4605856`.
- En `SimilarCarsActivity.java`, se ajustó el orden del selector para priorizar coincidencia de términos en marca/modelo, después año descendente y luego puntuación de versión. Esto evita que los Premium de 2025 se antepongan al Performance de 2026 para el mismo Tesla Model 3.
- Commit de la corrección en la pantalla adecuada: `68f4927b11d415e575aff8a9fb0c58ffff58f544`.
- Pendiente: build y prueba del APK con búsqueda «Model 3». No marcar como resuelto hasta validación del usuario.



## 2026-10-09 — Búsqueda escalonada de rivales cuando el filtro estricto devuelve pocos resultados

- Al revisar el catálogo de 683 vehículos para la referencia XPeng P7+ AWD Performance 2026 (54.400 €, segmento E), se comprobó que el filtro estricto devolvía solo dos marcas: Audi A6 Sportback e-tron y Zeekr 001. La causa era principalmente la exclusión absoluta de coches de segmento D aunque tuvieran carrocería y dimensiones próximas, además del límite económico estricto.
- Se cambió `SimilarCarsActivity.showSimilar()` para recalcular los candidatos de forma escalonada:
  1. Filtros estrictos existentes.
  2. Si quedan menos de cinco marcas, permitir segmentos adyacentes (p. ej. D/E), manteniendo carrocería compatible, dimensiones y límite de precio del 20 %.
  3. Si aún quedan menos de cinco marcas, ampliar el límite de precio al 30 %.
- La búsqueda nunca mezcla berlinas con SUV/crossover; el segmento sigue penalizando en la puntuación competitiva, pero deja de ser una barrera absoluta cuando la lista necesita ampliarse.
- Commit de código: `151609d2a9e015231a67bc94dc90e54ce11ecb6c`.
- Build de GitHub Actions pendiente de verificación; falta comprobar el número y orden real de rivales en el APK generado.


## 2026-10-09 — Recordar la última búsqueda de coches similares

- `SimilarCarsActivity` guarda en las preferencias compartidas la clave lógica del coche de referencia seleccionado y el mercado usado en el selector.
- Al volver a abrir la pantalla, restaura el coche de referencia y reconstruye los resultados automáticamente; también recupera el último mercado al abrir el selector.
- La clave lógica permite recuperar la versión aunque cambie el ID interno del catálogo. Si el vehículo ya no existe, se limpia la referencia obsoleta.
- Si el catálogo remoto incorpora vehículos mientras la pantalla está abierta, los resultados se recalculan para la referencia actual.
- Commits de código: `0e251740d49d552344192a223cc9393ef856db15` y `fab43b106c152c85aabaea1ce973fa1587fa0c64`.
- Pendiente: verificar el build de GitHub Actions y probar que la búsqueda se restaura correctamente al salir y volver a entrar.


## Navegación inferior — cambio acordado 09/10/2026

- Se conserva la barra inferior con cinco accesos: **Cargar**, **Coste**, **Buscar**, **Comparar** y **Más**.
- **Buscar coches** abre directamente `SimilarCarsActivity`; dentro de esa pantalla se mantiene el título descriptivo «Buscar coches similares» y la búsqueda persistente.
- **Comparar** abre directamente `CompararCochesActivity`.
- El acceso anterior «Coches» (pantalla intermedia que agrupaba Comparar y Buscar coches similares) deja de aparecer en la barra. No se implementa ahora un catálogo independiente; queda aplazado.
- «Buscar» utiliza el icono de coche anterior (`ic_nav_car_modern`) y abre directamente la búsqueda de coches similares. «Comparar» abre la comparativa directa.
- Respaldo previo al cambio: rama `backup/pre-navigation-change-2026-10-09`, apuntando al commit `6b36b6626656179b9da8ef0d3e118f586a7e72ab`.
- Pendiente tras el commit: comprobar el workflow de compilación y validar visualmente en dispositivo la barra de cinco accesos, especialmente el ajuste de las etiquetas.


## Ajuste de etiqueta de navegación — 09/10/2026

- Se acorta la etiqueta de la tercera pestaña a **Buscar**, manteniendo el acceso directo a `SimilarCarsActivity` y el título interno de la pantalla.
- Se añade la traducción de la etiqueta breve en los seis idiomas admitidos.


- **Ajuste visual de navegación (09/10/2026):** la pestaña **Buscar** utiliza el icono de coche anterior (`ic_nav_car_modern`) con su proporción horizontal restaurada. Para **Comparar**, se descarta el dibujo de coches por resultar poco claro a tamaño pequeño y se usa `ic_nav_compare`: tres barras verticales ascendentes, más gruesas y más juntas, sin base, para mejorar su lectura a tamaño pequeño.
- **Icono de Coste (09/10/2026):** sustituido el símbolo de euro por una cartera (`ic_nav_cost`) para que la pestaña represente costes sin asociarse a una moneda concreta; el texto «Coste» y la navegación permanecen igual.
- **Uniformidad visual de Buscar (09/10/2026):** el fondo de contenido de `SimilarCarsActivity` usa los mismos tonos que las demás pantallas de contenido (claro `#F1F6FB`, oscuro `#07131C`). La barra inferior conserva su fondo compartido propio.

## 2026-10-10 — Revisión de recomendaciones de Google Play

- Recomendación de **BitmapFactory sin reducción de resolución**: corregida en `CompararCochesActivity.HeaderBitmapView`. Primero se leen las dimensiones con `inJustDecodeBounds`; después se calcula `inSampleSize` y se decodifica la cabecera con `BitmapFactory.Options`, evitando cargar innecesariamente el recurso a resolución completa.
- Commit de la corrección: `2aac7cdda538df754787f3dbf4786c8bfb52dc30`.
- Build de GitHub Actions: ejecución `38062578906`, completada con resultado **success**; se generaron APK y AAB firmados y se publicó el release automático de la versión de código `1.0.5` / `versionCode 59`.
- Recomendación de **edge-to-edge en Android 15+**: revisión del código actual. La app usa `compileSdk 36` y `targetSdk 36`; `EdgeToEdgeHelper` activa `WindowCompat.setDecorFitsSystemWindows(window, false)`, gestiona los insets de barras del sistema y recortes de pantalla, y ajusta los iconos de las barras según el tema. Se comprobó que las pantallas de actividad registradas en el proyecto invocan el helper.
- No se ha cambiado arbitrariamente el comportamiento visual de los insets porque el helper central ya está implementado y había sido probado en el dispositivo del usuario. La recomendación de Play no demuestra por sí sola un fallo concreto. La validación final requiere instalar el APK de la versión 1.0.5 en el dispositivo Android 16 y comprobar navegación por gestos y por tres botones, especialmente la barra inferior, encabezados y contenido desplazable.
- Estado: **reducción de bitmap corregida y compilación validada**; edge-to-edge implementado y revisado en código, pendiente de prueba visual de regresión y de comprobar si la recomendación de Play desaparece tras publicar una versión que incluya estos cambios. No marcar las recomendaciones como resueltas en Play Console hasta verificarlo.



## 2026-10-10 — Auditoría conservadora de código obsoleto

- Segunda revisión de referencias y navegación antes de eliminar código. Se confirma que la antigua `CochesActivity` era una pantalla intermedia sin uso en la navegación actual (la barra abre directamente `SimilarCarsActivity` y `CompararCochesActivity`). No tenía intent-filter propio ni otra ruta de entrada; `CarDetailActivity` sigue siendo necesaria y se conserva.
- Eliminada la declaración `CochesActivity` del manifiesto y después su archivo `app/src/main/java/com/evchargecalculator/CochesActivity.java`.
- Commits: manifiesto `70cbf8847bcdb60e179719495ac4237905eb1bbf`; eliminación de actividad `6754088b8d683250767ed948022b2b7addd31d75`.
- No se borran todavía recursos gráficos candidatos ni `scripts/patch_search_virtualization.py`: aunque el workflow usa `.github/scripts/patch_search_virtualization.py`, los scripts difieren en el contenido generado y no se ha demostrado que el de raíz carezca de uso manual. Tampoco se modifica `AppMenuApplication` ni el tratamiento de insets hasta validar su interacción con `EdgeToEdgeHelper`.
- Pendiente: comprobar que el workflow de compilación posterior a la limpieza termina correctamente antes de considerar cerrada esta retirada.


## 2026-10-11 — Cabecera propia de Buscar coches similares

- La pantalla utiliza la imagen original subida por el usuario: `app/src/main/res/raw/cabecera_coches_similares.jpg`.
- Se ajusta la composición para coincidir con **Comparar coches**: imagen a ancho completo y altura de 260 dp, título blanco sobre degradado, y tarjeta descriptiva superpuesta en el borde inferior. La pila de cabecera reserva 336 dp para evitar solapamientos con el contenido siguiente.
- Se conserva el recorte proporcional `centerCrop` y la decodificación con muestreo para limitar memoria. La imagen original no se modifica.
- Commit de recurso/código: `5ca2208f017512ad15afa176f91e07d8939219e4`; ajuste de composición: `924512b3d5c7f51dd574dab7cf959421711579a0`.
- Pendiente: comprobar el resultado de GitHub Actions y probar visualmente en el dispositivo, en tema claro y oscuro.


## 2026-10-11 — Unificación de tarjeta y traducciones en Buscar coches similares

- Se unifican el contenido descriptivo y el selector en una sola tarjeta debajo de la imagen de cabecera.
- Se mantiene el título «Buscar coches similares» en blanco y negrita superpuesto a la imagen, con degradado oscuro, además del título en negrita dentro de la tarjeta.
- Orden dentro de la tarjeta: título, explicación con **10 alternativas** y etiqueta «Busca tu coche de referencia», seguida del selector «Seleccionar coche del catálogo».
- La pantalla usa la imagen original subida por el usuario, ancho completo, altura de 260 dp y superposición ligera de la tarjeta sobre el borde inferior. Se conserva `centerCrop` y la decodificación con muestreo.
- Se completan las traducciones de los textos actuales de esta pantalla para los seis idiomas admitidos (español, inglés, francés, alemán, italiano y portugués). Se corrige también el texto de catálogo que todavía hablaba de 8 alternativas y se añaden traducciones exactas para la explicación, la etiqueta de búsqueda y la nota de ordenación.
- Código de traducciones: commits `34ffa49ddcfa75dc483f5b9bed3ce19cda672143`, `e565feb27eaf821c9ec5c8012353dc0f9e74954a`, `706a57eb36f2df9e435c7aabe609faf375fd3a7d` y `c285051595f7550fbd861d855da473b9dfe9d138`.
- Código de cabecera: commit `389f4c933e607186643203d7247f2bed9dda93d8`.
- Pendiente: validar el workflow que se active con esta actualización y probar visualmente en el dispositivo, en tema claro y oscuro.

## 2026-10-12 — Validación de EV Calculator PRO 1.0.5 build 2324

- GitHub Actions run `38232801394` terminó con **success** para el commit `3a4d9b12957432e38369ec29eaba07e70c74f30f`.
- Se generaron y publicaron los artefactos:
  - APK: `EV-Calculator-PRO-v1.0.5.apk`.
  - AAB: `EV-Calculator-PRO-v1.0.5.aab`.
- Release: [build-v1.0.5-2324](https://github.com/Mariskal19/EV-Calculator-PRO/releases/tag/build-v1.0.5-2324).
- El usuario instaló y probó la app en su móvil e informa que, en general, todo parece funcionar correctamente. Se da por superada la prueba general inicial del build 2324, sin afirmar que se haya realizado una batería exhaustiva de pruebas de regresión.
- La carpeta `capturas 1.0.5/` ya contiene ocho imágenes JPG y un `README.md`, como biblioteca visual de esta versión.
- Pendiente de seguimiento externo al build: revisar en Google Play Console el estado de publicación y las recomendaciones vigentes, ya que no se pueden confirmar desde GitHub.


## 2026-10-12 — Código de versión rechazado por estar usado

- Google Play indicó que `versionCode 60` ya se había utilizado y no acepta reutilizarlo.
- Se incrementa `versionCode` a **61** en `app/build.gradle`; se mantiene `versionName = '1.0.5.1'`, `minSdk = 23` y la compatibilidad con Android 6.
- El AAB anterior de build 2330 con código 60 no debe volver a subirse. Esperar a que termine el nuevo build y utilizar únicamente el artefacto nuevo con `versionCode 61`.


## 2026-10-13 — Deslizador de batería validado en dispositivo

- El usuario ha probado la corrección del deslizador de porcentaje de batería y confirma que funciona correctamente.
- Se conserva la animación del control: el pulgar aumenta de tamaño al tocar y vuelve a su tamaño normal al soltar. **No eliminar ni desactivar esta animación** como parte de la corrección.
- Al soltar el dedo, no se vuelve a recalcular la posición con la coordenada final de `ACTION_UP`; se conserva el último valor establecido durante `ACTION_MOVE`. Esto evita el pequeño salto del porcentaje al terminar el gesto.
- Commit de restauración de la animación junto con la corrección de liberación: `ed59f75cdd1ee5d28c4b0ea203767bdff1c91f84`.
- Estado: **validado por el usuario en su dispositivo**. Mantener este comportamiento como base estable para los siguientes cambios.


## Auditoría Renault Twingo E-Tech eléctrico (18/10/2026)

- Se revisó la ficha oficial española de Renault y se separaron las versiones **Evolution** y **techno** del año 2026 en `catalog_es_2024_2026.json`.
- Datos comunes contrastados: batería 27,5 kWh, potencia 60 kW (80 CV), tracción trasera, autonomía WLTP hasta 263 km, consumo 12,2 kWh/100 km, maletero 360 l y longitud oficial 3.790 mm.
- Aceleración corregida a **0–100 km/h en 12,1 s**; el valor anterior de 6 s era incorrecto.
- Precios PVP sin ayudas/promociones: Evolution **19.500 €** y techno **21.100 €**, según comunicación oficial de Renault Group España. Se armonizaron `price` y `price_eur` para evitar discrepancias.
- La carga de serie es AC de 6,6 kW. El cargador DC de 50 kW y la carga AC de 11 kW corresponden al paquete opcional Advanced Charge; por ello, en ambas fichas estándar se eliminan `dcKw` y `charge10to80Min` y se marca `charge10to80Applicable: false`. No se debe interpretar el dato opcional como equipamiento de serie.
- Fuentes oficiales: [ficha técnica](https://www.renault.es/electricos/twingo-e-tech-electrico/ficha-tecnica.html), [recarga](https://www.renault.es/electricos/twingo-e-tech-electrico/recarga.html), [versiones y precios](https://www.renault.es/electricos/twingo-e-tech-electrico/precios-versiones.html) y [comunicación oficial de precios](https://prensa.renault.es/un-gran-programa-para-renault-twingo-e-tech-electrico-durante-todo-el-ano-2026/?lang=spa).
- **Recuento:** el archivo consultado antes de esta intervención contenía 683 entradas, aunque el último estado global documentado aquí era 682. Con el acabado techno añadido, el archivo contiene 684 entradas. Esta modificación no certifica por sí sola el catálogo completo: antes de la siguiente release hay que reconciliar esa diferencia histórica y ejecutar las validaciones de duplicados, campos obligatorios y APK/AAB.

## Auditoría adicional Renault 5 E-Tech (18/10/2026)

- Se revisaron las fichas Renault de 2024–2026 en el catálogo y se contrastaron las variantes del Renault 5 con la ficha técnica oficial española y el catálogo técnico de Renault.
- Se corrigió la ficha de 40 kWh y 95 CV: se identifica como **EV40 95 CV**, se sitúa en 2025 (la ficha anterior tenía año 2024 aunque su ID ya indicaba 2025), consumo WLTP 14,5 kWh/100 km y aceleración 0–100 km/h de 12 s. Se retiraron los valores de carga rápida DC y tiempo 10–80 %, ya que Renault indica que la versión básica de 95 CV no incorpora carga DC.
- Se corrigió la ficha de 52 kWh y 150 CV a **EV52 150 CV** de 2025: consumo WLTP 14,9 kWh/100 km, aceleración 0–100 km/h de 8 s y carga 15–80 % en 30 min. La fuente técnica oficial de Renault respalda la batería de 52 kWh, hasta 410 km WLTP y potencia de 150 CV.
- Se actualizó la versión de catálogo a `catalog-es-2024-2026-1.6`; el total permanece en 684 registros. Los precios existentes de estas fichas se conservaron, pero **no se volvieron a validar** en esta auditoría.
- El resto de las fichas Renault (Renault 4, Scenic y Mégane) se revisó en busca de discrepancias. No se cambiaron automáticamente los campos dudosos que pueden depender del año, acabado, homologación o equipamiento opcional; requieren una comprobación individual adicional antes de considerarlos certificados.
- Fuentes oficiales: [ficha técnica Renault 5](https://www.renault.es/electricos/r5-e-tech-electrico/ficha-tecnica.html), [motores y baterías Renault 5](https://www.renault.es/electricos/r5-e-tech-electrico/motores.html), [recarga Renault 5](https://www.renault.es/electricos/r5-e-tech-electrico/recarga.html), [catálogo técnico Renault 5, noviembre de 2025](https://catalogo.renault.es/es/mobile/renault5/mobile/ct_renault5_es_november_2025.pdf).
- Pendiente: validar la compilación y probar el APK resultante; revisar por separado las fichas Renault 4, Scenic y Mégane con fuentes oficiales específicas de cada año y acabado.

### Ampliación de auditoría Renault: masas en orden de marcha

- Se corrigieron las masas de Renault 4 y Renault 5 que no coincidían con los catálogos técnicos oficiales:
  - **Renault 4:** EV40 1410 kg; EV52 Evolution 1462 kg; EV52 Techno/Iconic 1521 kg. En los registros genéricos EV52 se utiliza el mínimo oficial de 1462 kg.
  - **Renault 5:** EV40 95 CV 1353 kg; EV40 120 CV 1372 kg; EV52 150 CV 1449 kg.
- Son valores oficiales de masa en vacío en orden de marcha (mínimo/máximo homologado), no MMA. En acabados concretos puede haber variación por equipamiento; por eso los valores de acabado se han aproximado al mínimo o máximo publicado según el acabado, y los registros genéricos usan el mínimo.
- Fuentes: [catálogo técnico Renault 4, mayo de 2025](https://catalogo.renault.es/es/mobile/renault4/mobile/ct_renault_4_es_may_2025.pdf) y [catálogo técnico Renault 5, noviembre de 2025](https://catalogo.renault.es/es/mobile/renault5/mobile/ct_renault5_es_november_2025.pdf).
- La versión de catálogo sigue en `catalog-es-2024-2026-1.7`; no se añadieron ni eliminaron vehículos en esta ampliación.

### Auditoría Renault 4 E-Tech 2026: corrección de prestaciones (18/10/2026)

- Se corrigieron las dos entradas de Renault 4 E-Tech 2026 contra las fichas vigentes de Renault España y el comunicado oficial de actualización de gama de octubre de 2026:
  - **40 kWh / 90 kW:** WLTP 310 km, consumo 14,7 kWh/100 km, 0–100 km/h en 9,2 s y carga rápida DC máxima de 80 kW.
  - **52 kWh / 110 kW:** WLTP 415 km, consumo 14,4 kWh/100 km, 0–100 km/h en 8,5 s y carga rápida DC máxima de 100 kW.
- Se eliminó el tiempo de carga charge10to80Min de ambas entradas: Renault publica 30 minutos para **15–80 %**, que no se debe registrar como si fuese un tiempo oficial de **10–80 %**.
- La versión de catálogo pasa a `catalog-es-2024-2026-1.8`. No se cambió el año 2025 ni se extrapolaron las cifras actualizadas de 2026 a las filas anteriores.
- Fuentes oficiales: [ficha técnica Renault 4 España](https://www.renault.es/electricos/r4-e-tech-electrico/ficha-tecnica.html), [motores y batería Renault 4 España](https://www.renault.es/electricos/r4-e-tech-electrico/motores.html), [recarga Renault 4 España](https://www.renault.es/electricos/r4-e-tech-electrico/recarga.html) y [comunicado Renault del 8 de octubre de 2026 sobre mejoras de gama](https://media.renault.com/renault-4-e-tech-electric-continuous-improvements-for-customers/?lang=eng).

### Auditoría Renault Mégane y Scénic: cargador AC de serie (18/10/2026)

- Se corrigió `acKw` de 22 a **11 kW** en las entradas Mégane EV60 Iconic 2025, Mégane EV60 Techno 2025 y Scénic Long Range 87 kWh 2025. La cifra del catálogo representa la potencia de serie, no el máximo que se puede conseguir pagando una opción.
- Renault España documenta 11 kW de serie y 22 kW opcional en las fichas consultadas. El cargador opcional de 22 kW no debe mostrarse como estándar en una ficha de acabado si el registro no indica que lleva esa opción.
- La gama nueva de Mégane de 67 kWh y la gama Scénic actualizada de 89 kWh / futura 67 kWh LFP se mantienen como pendiente de cobertura: la gama comercial y fechas de disponibilidad deben confirmarse específicamente para España antes de añadir registros 2026.
- Fuentes oficiales: [ficha Mégane E-Tech actual](https://www.renault.es/electricos/megane/ficha-tecnica.html), [ficha Mégane E-Tech anterior](https://www.renault.es/electricos/megane-electrico/datos-tecnicos.html), [ficha Scénic E-Tech](https://www.renault.es/electricos/scenic/datos-tecnicos.html) y [actualización global Scénic de septiembre de 2026](https://media.renault.com/scenic-e-tech-electric-upgraded-for-improved-comfort-connectivity-and-electric-performance?lang=eng).
- La versión de catálogo pasa a `catalog-es-2024-2026-1.9` tras estas correcciones y la actualización del Renault 4 2026.

### Auditoría Renault Mégane E-Tech 67 kWh LFP y Scénic E-Tech 2026 (18/10/2026)

- **Añadida una nueva ficha del Mégane E-Tech 2026, Techno 67 kWh LFP, mercado ES.** Datos contrastados con fuentes oficiales Renault España:
  - Batería LFP de 67 kWh útiles; potencia 160 kW (220 CV); WLTP 501 km; consumo 15,7 kWh/100 km; 0–100 km/h en 7,6 s.
  - Recarga AC de 11 kW de serie y DC de hasta 165 kW. Renault publica 24 minutos para **15–80 %**, por lo que no se ha registrado ese tiempo como 10–80 %.
  - Masa en vacío en orden de marcha (MVODM): **1772 kg**. Dimensiones publicadas para la nueva versión: 4200 × 1782 × 1522 mm; maletero 440 L.
  - Precio de tarifa desde **28.670 €** según el comunicado de apertura de pedidos del 1 de octubre de 2026. Se conserva el precio de tarifa de lanzamiento y no el precio promocional variable del configurador.
- La nueva ficha eleva el catálogo a `catalog-es-2024-2026-1.10`, con **685 vehículos y 20 registros Renault**.
- **Scénic E-Tech 2026:** Renault España confirmó el 2 de octubre de 2026 que la batería NMC de 89 kWh ya forma parte de la gama española, con hasta 642 km WLTP, DC hasta 150 kW y carga del 15 al 80 % en 28 minutos. El acabado Techno Gran Autonomía figura con tarifa recomendada de 45.085 €; Esprit Alpine Gran Autonomía, 47.685 €. El precio promocional «desde» 30.660 € corresponde a la versión de autonomía confort que llegará en unas semanas, no a una tarifa equivalente de la versión de 89 kWh.
- No se añadió todavía una fila Scénic 89 kWh: el comunicado español consultado no especifica la masa en vacío ni el consumo homologado concreto de la nueva batería, y no se deben copiar automáticamente los datos de la anterior versión de 87 kWh. Se mantiene pendiente obtener ficha técnica española específica de la gama 2026 y validar masa, consumo, capacidad útil y precio de cada acabado.
- La batería Scénic LFP de 67 kWh ofrece hasta 467 km y 15–80 % en 24 minutos, pero Renault España la describe como **disponible más adelante**; no se añade como variante disponible en el catálogo español hasta que se confirme su comercialización efectiva.
- Fuentes oficiales:
  - [Apertura de pedidos del Nuevo Mégane E-Tech, 1/10/2026](https://prensa.renault.es/nuevo-renault-megane-e-tech-electrico-mas-autonomia-equipamiento-y-tecnologia-desde-28670-eur/?lang=spa)
  - [Ficha técnica del Nuevo Mégane E-Tech, Renault España](https://prensa.renault.es/nuevo-renault-megane-e-tech-electrico-mas-caracter-dinamismo-y-tecnologia/?lang=spa)
  - [Configurador y ficha técnica del Nuevo Mégane E-Tech](https://www.renault.es/electricos/megane/configurador.html)
  - [Comunicado Renault España: Scénic E-Tech eléctrico 2026, 2/10/2026](https://prensa.renault.es/renault-scenic-e-tech-electrico-mejorado-para-ofrecer-mas-comodidad-conectividad-y-rendimiento-electrico-desde-30660eur/?lang=spa)

### Cierre de auditoría Renault: Scénic 89 kWh y consumos Renault 4 (18/10/2026)

- **Añadida la ficha española Scénic E-Tech 2026 Techno Gran Autonomía 89 kWh NMC, 220 CV**, porque Renault España ya publica la versión y su tarifa recomendada. Datos oficiales Renault: hasta 642 km WLTP, 160 kW, 150 kW DC, AC 11 kW de serie (22 kW opcional), 545 L de maletero, dimensiones 4470 × 1864 × 1571 mm y PVP recomendado de 45.085 €.
- Para completar los campos técnicos que el comunicado español no publica, se han utilizado datos externos de EV Database para la versión MY27 de 89 kWh: capacidad nominal 93,5 kWh, capacidad útil 89 kWh, consumo WLTP 16,6 kWh/100 km, aceleración 0–100 km/h de 7,9 s y peso en vacío UE de 1.923 kg. Estos campos quedan explícitamente identificados como externos en la ficha; deben prevalecer los datos oficiales españoles si Renault publica una ficha técnica nacional específica.
- El tiempo de carga oficial del Scénic nuevo es **15–80 % en 28 minutos**; no se ha copiado a `charge10to80Min`, porque el intervalo no es equivalente.
- Se han corregido consumos del Renault 4 E-Tech de la ficha 2025: 40 kWh, de 13 a **14,7 kWh/100 km**; 52 kWh, de 12,7 a **14,4 kWh/100 km** en Evolution, Techno e Iconic. Los valores anteriores coincidían con una división simplificada de batería/autonomía y no con el consumo WLTP publicado por Renault.
- Catálogo resultante: `catalog-es-2024-2026-1.11`, **686 vehículos y 21 registros Renault**, sin duplicados de combinación marca/modelo/año/versión ni identificadores disponibles.
- **Scénic LFP 67 kWh:** el configurador español ya muestra datos técnicos para esa motorización, pero el comunicado de Renault del 2 de octubre todavía la describe como una versión que llegará más adelante. No se incluye aún en el catálogo de versiones disponibles hasta confirmar que ya se pueden formalizar pedidos/entregas en España y conseguir su peso específico. El configurador muestra 467 km WLTP, 17,0 kWh/100 km, 160 kW, 0–100 en 7,9 s, AC 11 kW y DC 165 kW; no basta para certificar disponibilidad comercial ni peso en vacío.
- Fuentes:
  - [Renault España: gama Scénic E-Tech 2026 y precios](https://prensa.renault.es/renault-scenic-e-tech-electrico-mejorado-para-ofrecer-mas-comodidad-conectividad-y-rendimiento-electrico-desde-30660eur/?lang=spa)
  - [Renault España: configurador Scénic E-Tech](https://www.renault.es/electricos/scenic/configurador.html)
  - [EV Database: Scénic E-Tech 89 kWh 220 hp MY27](https://ev-database.org/car/3787/Renault-Scenic-E-Tech-89-kWh-220hp)
  - [Renault España: ficha técnica Renault 4 E-Tech](https://www.renault.es/electricos/r4-e-tech-electrico/ficha-tecnica.html)

### Verificación de precios y acabados Renault 2026 (18/10/2026)

- Precios de tarifa española verificados en el comunicado oficial de Renault para Scénic MY2026: **Techno Gran Autonomía: 45.085 €; Esprit Alpine Gran Autonomía: 47.685 €**. Son precios recomendados con IVA y transporte, no los precios promocionales «desde».
- La ficha técnica pública consultada identifica 642 km WLTP como máximo de la batería NMC de 89 kWh, pero no desglosa en esa página el WLTP exacto de Esprit Alpine con llanta de 20 pulgadas. Por eso se ha incluido una sola ficha de comparación técnica, Techno, sin copiarle automáticamente la autonomía a Esprit Alpine.
- Los precios de las fichas de años/modelos anteriores se conservan como precios asociados a esas versiones históricas cuando proceden de su fuente de catálogo; no deben interpretarse como oferta comercial actual de octubre de 2026. En las fichas donde el precio no se ha revalidado en esta auditoría, el campo `source` lo indica.



## 2026-10-20 — Reconciliación del recuento histórico del catálogo (682 → 686 → 688)

### Resultado

Se comparó el JSON del catálogo del commit `006fd7585d4a557cd7af42e89306dd9b14a3aa18` (01/10/2026, 682 registros) con el commit `7ea3295854104af62c4225508e01ce0c7b8d7052` (18/10/2026, 686 registros), usando el historial del archivo y la clave lógica `marca + modelo + año + versión`.

Las cuatro incorporaciones netas que explican el paso de 682 a 686 son:

1. **Renault Twingo E-Tech eléctrico 2026 techno**, 60 kW, tracción trasera, batería de 27,5 kWh, PVP registrado 21.100 €.
2. **XPeng G6 2026 RWD Standard Range**, 185 kW, batería de 68,5 kWh, PVP registrado 43.083 €.
3. **Renault Mégane E-Tech Electric 2026 Techno**, batería LFP de 67 kWh, 220 CV, PVP registrado 28.670 €.
4. **Renault Scénic E-Tech Electric 2026 Techno Gran Autonomía**, batería NMC de 89 kWh, 220 CV, PVP registrado 45.085 €.

### Cambios Renault 5 que no alteran el total neto

El diff también muestra dos altas y dos bajas que se compensan:

- **Altas:** Renault 5 E-Tech 2025 EV40 95 CV (27.990 €) y Renault 5 E-Tech 2025 EV52 150 CV (32.990 €).
- **Bajas:** las fichas equivalentes etiquetadas como Renault 5 E-Tech 2024 de 40 kWh (27.990 €) y 52 kWh (32.990 €).

La corrección trasladó esas dos variantes al año de modelo 2025, coherente con los identificadores y la auditoría técnica; por tanto, el historial refleja seis altas y dos bajas, es decir, un aumento neto de cuatro registros (682 → 686). No se deben reponer las fichas de 2024 ni borrar las de 2025 para hacer coincidir el catálogo con la cifra histórica.

### Paso de 686 a 688

El 20/10/2026 se añadieron dos configuraciones Zeekr nuevas: **Zeekr 001 Privilege AWD 2026** y **Zeekr X Core RWD 2026**. El catálogo quedó en 688 registros y 13 registros Zeekr. La auditoría posterior comprobó 0 IDs duplicados y 0 duplicados lógicos.

### Referencias de auditoría

- [Historial de cambios del catálogo](https://github.com/Mariskal19/EV-Calculator-PRO/commits/main/app/src/main/assets/catalog_es_2024_2026.json)
- [Catálogo actual en main](https://github.com/Mariskal19/EV-Calculator-PRO/blob/main/app/src/main/assets/catalog_es_2024_2026.json)
- La comparación histórica se hizo sobre el archivo JSON en los commits indicados; los precios son los valores registrados en esas fichas y no deben interpretarse necesariamente como ofertas vigentes.

### Estado y criterio de mantenimiento

- **682** = recuento certificado el 01/10/2026.
- **686** = estado anterior a la auditoría Zeekr, tras la reconciliación Renault/XPeng descrita arriba.
- **688** = estado de `main` tras añadir las dos configuraciones Zeekr el 20/10/2026.
- No hay cuatro coches pendientes de añadir: son cuatro incorporaciones netas históricas ya presentes en el catálogo.
- Mantener `stable` intacta. No modificar la rama ni los registros protegidos para forzar un total antiguo.


## 2026-10-21 — Auditoría Lynk & Co (solo eléctricos puros)

### Alcance y criterio
- Se ha revisado la gama oficial española de Lynk & Co y los tres registros Lynk & Co del catálogo protegido.
- Solo se incluyen vehículos 100 % eléctricos. **Lynk & Co 01 y 08 quedan excluidos** porque son híbridos enchufables; no se añaden registros de esos modelos.
- El único modelo 100 % eléctrico de la gama española revisada es el Lynk & Co 02. La web oficial distingue los acabados Core y More y publica 435 km y 445 km WLTP, respectivamente.

### Correcciones realizadas en el catálogo protegido
- **Batería utilizable:** corregida de 65 a **62 kWh** en los dos registros de 2024. EV Database indica 66 kWh nominales y 62 kWh utilizables para el 02 MY25; se mantiene la capacidad nominal de 66 kWh.
- **Lynk & Co 02 Core 2025:** corregido el peso de 1895 a **1820 kg**, conforme a la masa en vacío publicada por la marca para el 02.
- **Tiempo DC 10–80 % del registro Core 2025:** corregido de 27 a **30 minutos**. La página española actual del 02 indica 30 minutos; el valor de 27 minutos no se ha podido respaldar con la fuente oficial consultada.
- **Tracción del registro Core 2025:** completada como **RWD**, coherente con la ficha oficial y con los otros registros del 02.
- Se conserva el catálogo en **688 registros**. No se han añadido híbridos ni nuevos años/modelos en esta operación.
- Los dos registros de 2024 mantienen los valores publicados para Core y More: 435/445 km WLTP, consumos de 17,6/17,1 kWh por 100 km y carga AC de 11/22 kW. La página oficial de características indica hasta 150 kW DC, mientras que la página general del modelo indica 85 kW DC y 30 minutos. Por esta discrepancia entre páginas oficiales, **no se ha cambiado la potencia DC de los registros existentes**; queda pendiente verificarla por año/modelo y ficha técnica PDF antes de introducir una variante MY27.
- Los precios 2024 se conservan como precios históricos registrados, no como oferta necesariamente vigente. No se ha inventado un precio para una variante MY27.

### Fuentes consultadas
- Lynk & Co España, características oficiales del 02: https://www.lynkco.com/es-es/cars/02/features
- Lynk & Co España, página general del 02: https://www.lynkco.com/es-es/cars/02
- Lynk & Co España, comunicado oficial de actualización MY27 del 11/06/2026: https://press.lynkco.com/es-ES/266847-lynk-co-presenta-la-actualizacion-de-su-modelo-02-para-el-ano-2027/
- EV Database, Lynk & Co 02 MY25 (batería nominal y utilizable): https://ev-database.org/car/3045/LynkCo-02

### Resultado
Auditoría de los tres registros existentes completada con las correcciones anteriores. **Pendiente separado:** comprobar la ficha técnica PDF MY27 y resolver la discrepancia oficial de potencia DC antes de crear o modificar registros para el año modelo 2027.


## 2026-10-22 — Checkpoint de seguridad previo al asesor conversacional

### Respaldo verificado antes de modificar documentación o código
- **Rama principal observada:** `main`.
- **HEAD de `main` al crear el respaldo:** `a2021ffa2bae1846f4de7f777a2c937f6713434f`.
- **Commit:** `Document Lynk & Co protected catalog audit`, fechado 2026-10-21 19:55:05 UTC.
- **Rama de respaldo creada:** `backup/pre-asesor-ia-2026-10-22`.
- **HEAD de la rama de respaldo comprobado:** coincide exactamente con `a2021ffa2bae1846f4de7f777a2c937f6713434f`.
- El respaldo es una rama Git de checkpoint: conserva el árbol completo versionado del repositorio en ese commit (código, recursos, catálogos y documentación versionados). No se han modificado ni borrado ramas `stable`.
- La rama `main` ya estaba al día con el commit más reciente visible en el repositorio en el momento de la comprobación. No había commits posteriores que incorporar antes del checkpoint.

### Estado de la aplicación en el checkpoint
Verificado en `app/build.gradle`:
- `versionName = 1.0.5.1`; `versionCode = 61`.
- `applicationId = com.evcalculatorpro`; `minSdk = 23`; `targetSdk/compileSdk = 36`.
- Java 17 según el estado documentado; dependencia directa en el archivo Gradle revisado: AndroidX Core 1.16.0.
- Catálogo protegido: `app/src/main/assets/catalog_es_2024_2026.json`; el MASTER registra 688 configuraciones al 20/10/2026.
- No se ha ejecutado compilación ni prueba de dispositivo como parte de esta auditoría; no se afirma que el checkpoint se haya compilado en esta sesión.

### Auditoría previa de la función «Buscar coches similares» (solo lectura)
Archivos revisados:
- `app/src/main/java/com/evchargecalculator/SimilarCarsActivity.java`
- `app/src/main/java/com/evchargecalculator/RemoteCatalogManager.java`
- `app/src/main/java/com/evchargecalculator/CarDetailActivity.java`
- `app/src/main/java/com/evchargecalculator/CompararCochesActivity.java`
- `app/build.gradle`
- `README.md`

Hallazgos:
1. `SimilarCarsActivity` carga el JSON protegido y añade registros de `RemoteCatalogManager.loadCached()`; llama a `refreshIfDue()` para actualizar adiciones remotas. La validación y deduplicación remota existente deben conservarse.
2. El modelo `Vehicle` de la pantalla Similar contiene precio, batería nominal/utilizable, WLTP, consumo, potencia, carga AC/DC, tiempo de carga, aceleración, maletero, peso, dimensiones, carrocería, segmento, tracción, mercado y año.
3. `CompararCochesActivity` tiene otra clase `Vehicle` independiente y su propio parser. Antes de implementar el asesor hay que comparar los campos y alias JSON de ambas clases para evitar divergencias; no introducir todavía un tercer parser duplicado sin una decisión explícita.
4. Los campos numéricos ausentes se convierten generalmente a `0`; el asesor debe interpretar cero como desconocido cuando corresponda y no como un valor real (por ejemplo, precio cero).
5. La navegación existente permite abrir `CarDetailActivity` y `CompararCochesActivity`; el asesor debe reutilizar los flujos existentes sin alterar la búsqueda clásica.
6. `RemoteCatalogManager` usa una URL de GitHub, caché local y comprobación cada 24 horas. Esto no autoriza ni requiere transmitir las consultas del usuario: el asesor inicial debe procesar el texto localmente.
7. El modelo conversacional no debe persistir consultas o historial en `SharedPreferences`, bases de datos, archivos ni logs. El contexto se limita a la sesión activa y se descarta al salir/reiniciar.
8. El algoritmo clásico de similares está orientado a rivales de un vehículo de referencia. La búsqueda libre por preferencias debe implementarse separadamente y convivir en la misma pantalla, sin sustituir ni degradar el modo clásico.

### Diseño acordado, aún no implementado
- Mantener la pantalla actual «Buscar coches similares».
- Mostrar desde el inicio dos opciones verticales: **Buscar con IA** (texto libre, acción de búsqueda y resultados/seguimientos temporales) y **Elegir coche de referencia** (flujo clásico actual).
- No crear una pantalla de elección adicional ni añadir una nueva entrada a la navegación principal.
- Primera fase sin API de IA en la nube ni coste por consulta: motor local de interpretación de criterios, filtrado y ranking sobre el catálogo existente. Modelo local descargable queda como posible fase futura, no aprobado para esta primera implementación.
- No inventar modelos ni especificaciones. Si falta precio, maletero u otro dato necesario para una condición obligatoria, explicitar que no se puede confirmar el cumplimiento.
- Casos de prueba previstos: carrocería y presupuesto; añadir/retirar criterios en un seguimiento; correcciones de preferencias; pregunta comparativa como «¿cuál tiene más maletero?»; campos desconocidos; cero resultados; salida/reentrada sin recuperar conversación; modo avión; conservación del flujo clásico.

### Secuencia antes de programar
1. Terminar auditoría de navegación, esquema de catálogo y diferencias entre parsers.
2. Proponer una arquitectura mínima adaptada a las convenciones reales del proyecto.
3. Revisar y aprobar el plan antes de cambiar código.
4. Implementar por fases, validar compilación y pruebas, y revisar el diff.
5. Mantener el catálogo protegido intacto y no tocar `stable` sin autorización expresa.

### Recuperación del checkpoint
- **Checkpoint inmutable de referencia:** commit `a2021ffa2bae1846f4de7f777a2c937f6713434f`.
- **Rama de recuperación:** [`backup/pre-asesor-ia-2026-10-22`](https://github.com/Mariskal19/EV-Calculator-PRO/tree/backup/pre-asesor-ia-2026-10-22).
- Para inspeccionar/restaurar el estado completo en un entorno Git, hacer checkout de esa rama o del SHA anterior. Para volver a ese estado en `main`, preferir una reversión controlada mediante commits/PR tras revisar los cambios posteriores; no hacer `reset --hard` ni force-push de `main` sin autorización explícita.
- Los cambios de documentación que registran este checkpoint se hacen después de crear y verificar la rama de respaldo; por tanto, el checkpoint sigue apuntando al estado previo a esos cambios.


### Continuación de auditoría de navegación e interfaz (22/10/2026, solo lectura)

- `AndroidManifest.xml` declara `SimilarCarsActivity`, `CarDetailActivity` y `CompararCochesActivity` como actividades internas no exportadas. La actividad de lanzamiento es `PersistentMainActivity`, que deriva de `MainActivity`.
- `BottomNavigationHelper` ya dirige el acceso **Buscar** al `SimilarCarsActivity`; `SimilarCarsActivity` hereda de `BaseNavigationActivity` y devuelve el índice 2. Por tanto, el asesor encaja en la pantalla actual y no requiere una nueva entrada en la navegación.
- La pantalla actual construye su interfaz programáticamente en `SimilarCarsActivity.build()` (no usa un layout XML para esta pantalla). Hay una cabecera visual y una tarjeta de selección de referencia; el bloque de resultados clásicos se muestra cuando se elige un coche. La integración visual deberá insertar la tarjeta conversacional dentro de este mismo árbol y preservar la tarjeta/selector existente.
- `BaseNavigationActivity.setContentView()` envuelve la vista con la navegación inferior persistente y añade espacio de desplazamiento a los `ScrollView`. Debe tenerse en cuenta al integrar el nuevo bloque para no duplicar la navegación ni dejar los últimos resultados ocultos.
- `CarDetailActivity` recibe las especificaciones por extras del `Intent` y muestra `—` cuando los valores numéricos no son positivos. El asesor puede abrir una ficha usando el flujo ya existente, pero habrá que asegurar que los extras se construyan desde el mismo objeto del catálogo y no desde datos reinterpretados o inventados.
- Los JSON local y remoto comparten varios nombres de campo, pero no son totalmente uniformes: aparecen alias como `drive`/`drivetrain`, `consumptionKwh100`/`consumption`, `rangeKm`/`wltpKm`, y diferentes nombres de batería utilizable. El parser de `SimilarCarsActivity` ya maneja parte de estos alias; el parser de `CompararCochesActivity` maneja otros. La normalización compartida es una decisión de arquitectura pendiente y debe validarse contra los dos JSON antes de refactorizar.
- El archivo remoto de adiciones incluye campos opcionales nulos, por ejemplo `drive`, `drivetrain` y `charge10to80Min`. El asesor debe tolerar campos ausentes/nulos y mantener la política de “dato desconocido”, sin convertirlos en ceros reales.
- `AndroidManifest.xml` ya solicita permiso `INTERNET`, usado por funciones existentes como la actualización de catálogo y otros servicios. No se ha añadido permiso alguno ni se ha habilitado un servicio de IA externo.
- Se comprobó el estado del respaldo tras crearlo: `backup/pre-asesor-ia-2026-10-22` sigue apuntando al commit de checkpoint `a2021ffa2bae1846f4de7f777a2c937f6713434f`. Las únicas modificaciones realizadas después del respaldo en `main` hasta este punto son documentación: el MASTER y el README. No se ha modificado código fuente, recursos ni archivos JSON de catálogo.

### Commits documentales posteriores al checkpoint

- `a6e74b6dff06a5bcd94753d854914c3e7a152fc2` — `docs: checkpoint before conversational EV advisor` (MASTER).
- `fbc83910b31d83fe22911d1d134cb09ed61a1497` — `docs: record pre-advisor checkpoint and current audit status` (README).
- El HEAD de `main` antes de añadir estas últimas notas era `fbc83910b31d83fe22911d1d134cb09ed61a1497`; este commit documental pasa a ser el nuevo HEAD. Verificar el HEAD de GitHub al retomar el trabajo.
- Estos commits son posteriores al respaldo deliberadamente; no mueven ni sustituyen el checkpoint. Si fuera necesario volver exactamente al estado anterior a la documentación, usar la rama de respaldo o el SHA de checkpoint indicado arriba.


### Continuación de auditoría: parsers, campos y flujos de selección (22/10/2026, solo lectura)

#### Comparación de modelos y parsers

Se inspeccionaron los constructores Vehicle(JSONObject) de SimilarCarsActivity y CompararCochesActivity, el cargador remoto, el flujo de detalle y el catálogo JSON protegido. No se modificó código.

- Los dos parsers comparten la mayoría de campos técnicos, pero no tienen exactamente la misma normalización. Ambos aceptan alias para marca, versión, año, batería nominal, WLTP, consumo, potencia, tracción, carga, aceleración, maletero, peso y dimensiones.
- **Diferencia confirmada en batería utilizable:** el parser de Comparar acepta usableBatteryKwh, netBatteryKwh y batteryNetKwh; el parser de Similar solo lee usableBatteryKwh. Si una entrada remota usa uno de los otros alias, Similar la tratará como si no tuviera capacidad útil.
- **Diferencia confirmada en química de batería:** Comparar acepta batteryChemistry, batteryType y chemistry; Similar acepta batteryChemistry y batteryType, pero no chemistry.
- **Diferencia confirmada en tracción:** Comparar también acepta drivetrainType; Similar solo acepta drivetrain y drive.
- **Diferencia confirmada en tiempo de carga:** Comparar acepta charge10to80Min, chargeMin y chargeTime10to80Min; Similar acepta charge10to80Min y chargeMin.
- Similar necesita además bodyStyle/alias body_style y segment para su algoritmo de rivales. Comparar no los mantiene en su modelo Vehicle, ya que no los usa en la tabla técnica.
- Ambos convierten muchos valores ausentes o nulos a cero mediante optDouble(..., 0). Para la futura búsqueda conversacional, cero debe mantenerse como representación interna compatible con el catálogo actual, pero la capa de criterios debe distinguir «dato desconocido» de un valor medido real. No se debe presentar un precio cero como coche gratuito ni usar un campo desconocido para afirmar que cumple un requisito.
- Los dos parsers tienen claves lógicas equivalentes basadas en marca, modelo, mercado, año, batería y versión normalizada. Se debe reutilizar esa identidad lógica para resolver resultados y evitar depender exclusivamente de IDs generados.
- Comparar calcula un ID estable a partir de la clave lógica cuando lo necesita y puede completar campos ausentes al fusionar registros lógicamente idénticos. Similar crea su ID de reserva a partir de una cadena propia. Por tanto, el asesor debe conservar la clave lógica del objeto elegido y resolverlo contra la lista viva de vehículos, en vez de construir IDs por su cuenta.

#### Catálogo protegido y remoto

- El catálogo protegido tiene raíz JSON con schemaVersion, datasetVersion, marketDefault, catalogOrder y matriz vehicles. Los registros usan principalmente nombres como make, model, year, market, version, batteryKwh, drive, wltpKm, consumptionKwh100, price, acKw, dcKw, charge10to80Min, batteryChemistry, trunkLiters, weightKg, dimensiones, usableBatteryKwh, bodyStyle y segment.
- El JSON remoto tiene raíz con version, updatedAt y vehicles; usa algunos alias adicionales y campos nulos. El cargador remoto descarga únicamente el archivo de catálogo, lo almacena en caché y limita la comprobación a una vez cada 24 horas. Esta descarga de catálogo es independiente de la consulta de texto del usuario.
- Los validadores de adiciones remotas de Similar y Comparar se parecen, pero no son idénticos: Comparar también rechaza una entrada cuando se informan ambas potencias AC/DC y DC es menor que AC; Similar no tiene ese control. No cambiar estos validadores durante la implementación del asesor sin revisar el alcance y probar el impacto en los flujos clásicos.
- La lista de Similar marca el origen remoto y filtra los años a 2024–2026, además de requerir marca, modelo, versión, batería, WLTP, potencia y consumo válidos. Los datos opcionales (precio, maletero, tiempo de carga, etc.) pueden faltar aunque la ficha siga siendo válida.

#### Detalle y comparativa: integración recomendada

- CarDetailActivity recibe sus campos por extras de Intent; la llamada existente desde Similar pasa las especificaciones del mismo objeto Vehicle, así como vehicle_id, logical_key y contexto del flujo de similares.
- El botón «Añadir a comparativa» persiste selección e identidades lógicas y abre CompararCochesActivity. El flujo clásico de Similar también puede añadir la referencia y el resultado como pareja.
- La futura búsqueda conversacional debe entregar al flujo de detalle/comparación el objeto real seleccionado del catálogo y reutilizar la clave lógica. No debe reconstruir una ficha desde el texto libre ni crear un vehículo artificial.
- La integración propuesta sigue limitada a la pantalla actual de «Buscar coches similares». El modo conversacional tendrá su propio filtrado/ranking y no sustituirá el algoritmo de rivales ni modificará el selector clásico.

#### Decisión técnica pendiente antes de programar

1. Diseñar una capa de acceso/normalización mínima que pueda compartir campos entre las dos funciones sin alterar sus reglas de ranking, validación, identidad o selección.
2. Decidir si el asesor usa directamente el modelo SimilarCarsActivity.Vehicle durante la primera fase o si conviene extraer un modelo común. Evitar crear un tercer parser duplicado.
3. Añadir pruebas de regresión para alias, nulos, claves lógicas, resultados remotos y el flujo de añadir a comparativa.
4. Mantener la primera versión completamente local para interpretar consultas: no enviar texto libre a GitHub ni a otro servicio; el tráfico ya existente de actualización del catálogo sigue siendo independiente.
5. No implementar aún: terminar de acordar la arquitectura y revisar el plan con el usuario antes de cambiar código.


### Diseño funcional propuesto del asesor conversacional (22/10/2026; aún sin código)

#### Objetivo de la primera versión

Implementar una búsqueda guiada en lenguaje natural dentro de SimilarCarsActivity, manteniendo visibles desde el inicio dos tarjetas verticales: «Buscar con IA» y «Elegir coche de referencia». La segunda conserva el flujo actual. El modo nuevo será un intérprete local de criterios con conversación de seguimiento; no debe presentarse como un modelo generativo remoto ni como comprensión ilimitada.

#### Criterios iniciales a interpretar

- Precio máximo y mínimo; no inferir precios desconocidos.
- Carrocería cuando bodyStyle esté informado y se pueda normalizar de forma inequívoca.
- Autonomía WLTP mínima/máxima.
- Maletero mínimo/máximo cuando trunk sea conocido.
- Capacidad de batería mínima/máxima.
- Potencia mínima/máxima.
- Consumo máximo y preferencia de menor consumo.
- Tracción concreta cuando el dato exista y sus alias se hayan normalizado.
- Marca/modelo explícitos para búsquedas por vehículo.
- Preguntas comparativas sencillas sobre resultados actuales: mayor autonomía, mayor maletero, menor precio/consumo, siempre que existan datos comparables.
- Seguimientos para añadir un criterio, modificar un límite, quitar un criterio, cambiar una preferencia o pedir una aclaración.

#### Reglas de interpretación y ranking

1. Mantener un estado conversacional en memoria de la instancia de la pantalla: filtros obligatorios, preferencias de ordenación, última lista de resultados y mensaje de aclaración pendiente. No escribirlo en SharedPreferences, base de datos, ficheros ni logs.
2. Tratar frases con «máximo», «como mucho», «menos de», «mínimo», «al menos» como límites explícitos; frases como «me gustaría», «preferiría» o «si puede ser» son preferencias, salvo que la frase indique claramente una condición obligatoria.
3. Una corrección explícita del usuario reemplaza el valor previo del mismo criterio (p. ej. cambiar 35.000 € por 40.000 €); añadir otro criterio conserva los anteriores. «Quita el límite de precio» debe borrar únicamente ese filtro.
4. Los filtros obligatorios solo pueden evaluar campos conocidos. Si falta el dato necesario, no marcar el coche como cumplimiento confirmado; informar que se excluye de los resultados verificados por dato desconocido o mostrar una sección aparte de «datos insuficientes» solo si la UI lo permite sin inducir a error.
5. Ordenar los resultados que cumplen los filtros según preferencias explícitas; sin preferencias, aplicar un orden estable y explicar los criterios. No reutilizar el algoritmo de similitud de rivales para esta búsqueda por requisitos.
6. Resolver cada resultado al vehículo real de la lista de SimilarCarsActivity, usando la identidad lógica existente. La ficha debe abrirse con el objeto del catálogo, no reconstruirse a partir del texto.
7. Si no se entiende el criterio, la unidad o el sentido del límite, preguntar antes de aplicar un filtro potencialmente incorrecto. Si no hay resultados, explicar qué restricciones impidieron encontrar coincidencias y ofrecer relajar una a elección del usuario.
8. El modo de seguimiento y comparación se limita a los resultados de la sesión; al salir y volver a entrar, la conversación debe empezar vacía aunque el flujo clásico pueda seguir restaurando su coche de referencia guardado.

#### Idiomas

La aplicación dispone de español, inglés, francés, alemán, italiano y portugués. Las etiquetas de interfaz nuevas deben añadirse a LanguageManager en los seis idiomas. El intérprete debe limitarse a los idiomas que pueda reconocer de forma explícita y probarse por idioma; no debe fingir que entiende una frase no soportada. Definir patrones y sinónimos por idioma para criterios y operadores, y mostrar una explicación de los tipos de consulta aceptados.

#### Arquitectura mínima recomendada

- Integrar dos tarjetas en el diseño programático actual de SimilarCarsActivity, sin nueva Activity, nueva pantalla intermedia ni nueva pestaña de navegación.
- Crear una clase auxiliar dedicada al intérprete/estado de criterios para mantener la lógica fuera de build() y no mezclarla con showSimilar()/collectCompetitors().
- Reutilizar la lista de vehículos ya cargada y validada por SimilarCarsActivity, incluyendo la caché remota existente; no crear un tercer catálogo ni un tercer parser.
- Antes de filtrar, normalizar las diferencias de alias necesarias para el asesor en una capa auxiliar de lectura. Evitar modificar constructores y validadores compartidos salvo que una prueba de regresión justifique el cambio.
- Presentar resultados en tarjetas compactas y mantener el campo de texto, botón de envío y mensajes de seguimiento dentro de la tarjeta de «Buscar con IA». Cuidar teclado, desplazamiento y barra inferior fija.
- Abrir ficha y comparativa mediante los flujos existentes. Reutilizar la clave lógica del vehículo y respetar el límite de tres coches de la comparativa.

#### Alcance excluido de la primera versión

- Ningún proveedor o API de IA de pago, ni envío del texto del usuario a GitHub u otro servidor.
- Ningún historial persistente, analítica de consultas o almacenamiento de mensajes.
- No alterar el catálogo protegido, el catálogo remoto, el refresco de 24 horas ni el algoritmo clásico de similares.
- No generar especificaciones, precios, carrocerías ni respuestas numéricas no respaldadas por el catálogo.
- No prometer comprensión abierta: se admiten los patrones definidos y se solicita aclaración cuando la consulta se salga de ellos.

#### Matriz mínima de validación antes de dar la función por lista

- Operadores y unidades: «menos de 40.000 €», «máximo 40.000», «al menos 450 litros», «más de 400 km».
- Combinación de criterios, seguimiento que añade criterio, sustitución de un límite y eliminación de criterio.
- Preferencias blandas frente a filtros obligatorios.
- Campos nulos/ausentes y valores cero usados como desconocido.
- Sin coincidencias, consulta ambigua y pregunta no soportada.
- Preguntas comparativas con datos completos e incompletos.
- Resolución de ficha y adición a comparativa con identidad lógica correcta.
- Catálogo remoto/caché disponible, modo avión, salida y nueva entrada sin recuperar el chat.
- Flujo clásico de selección y algoritmo de similares sin regresiones.
- Revisión de cadenas de interfaz y patrones de interpretación en los seis idiomas soportados.

Estado del diseño: aprobado por el usuario el 22/10/2026. La implementación se inicia por fases en la rama `feature/asesor-ia-fase-1`; no se integra en `main` hasta revisar y validar los cambios.


### Asesor conversacional — fase 1: intérprete local aislado (22/10/2026)

- Rama de trabajo: `feature/asesor-ia-fase-1`.
- Commit de código inicial: `2263e66491850103a7a5ae329ad2bb524bbdb62e`.
- Archivo añadido: `app/src/main/java/com/evchargecalculator/AdvisorCriteriaEngine.java`.
- Esta clase es Java puro, sin dependencias Android, red, catálogo ni persistencia. Mantiene los criterios en memoria, permite obtener una copia del estado y reiniciarlo explícitamente.
- Criterios iniciales incluidos: límites de precio, autonomía WLTP, maletero, batería, potencia y consumo; carrocería y tracción; preferencias de menor consumo, mayor autonomía y mayor maletero; y eliminación explícita de algunos filtros.
- El intérprete utiliza patrones locales acotados para español, inglés, francés, alemán, italiano y portugués. Devuelve una clave de aclaración cuando no reconoce una consulta o detecta un límite/unidad ambiguos. No intenta inferir nombres de modelos arbitrarios.
- **Estado de validación:** todavía no se ha ejecutado compilación ni batería de pruebas. Este commit es una primera implementación aislada, no una función visible en la aplicación ni una versión lista para publicar.
- **Revisión pendiente:** probar los operadores, decimales y separadores de miles, expresiones por idioma, sustitución de límites y eliminación de criterios. Revisar especialmente la detección de unidades y los casos en los que una consulta contiene más de un criterio antes de conectar el motor al catálogo.
- **Sin cambios:** no se ha editado `SimilarCarsActivity`, `LanguageManager`, los flujos de detalle/comparativa, los JSON del catálogo, el refresco remoto ni el algoritmo clásico. No se ha cambiado `main` ni la rama de respaldo.
- Próximo paso: revisión estática y pruebas dirigidas del motor aislado; corregir defectos antes de pasar a la integración con vehículos reales.


#### Revisión estática adicional — operadores numéricos

- Commit de corrección: `bd5f6c67cd3dbf3d25d25d3aad8487c6aa9438d1`.
- Se corrigió la selección del operador numérico para priorizar el operador reconocido más cercano al número. Esto evita que una consulta con varios criterios, como «menos de 35.000 € y más de 450 km», se marque como ambigua solo porque el operador del primer criterio queda dentro de la ventana de texto del segundo.
- Se añadieron expresiones acotadas para límites de presupuesto habituales como «puedo llegar a…» y «mi presupuesto es…» en español, y algunos equivalentes en inglés/portugués.
- **Importante:** esto es una corrección basada en revisión estática; no equivale a pruebas ejecutadas ni demuestra que todos los patrones funcionen. Compilación y pruebas reproducibles siguen pendientes antes de integrar el motor con el catálogo.


#### Pruebas aisladas preparadas

- Se añadió `.github/tests/AdvisorCriteriaEngineSmokeTest.java`, una prueba sin dependencias externas para compilar y ejercitar el motor con Java 17.
- Casos incluidos: carrocería SUV y precio máximo; maletero mínimo; dos criterios numéricos en una sola frase; añadir autonomía sin perder el presupuesto anterior; rechazo de un número sin unidad/contexto; eliminar solo el presupuesto; y un patrón básico en inglés.
- Se añadió el workflow `.github/workflows/advisor-engine-test.yml`, que compila únicamente el motor aislado y ejecuta esas comprobaciones al cambiar los archivos de la fase 1.
- Los archivos están en la rama de trabajo; el resultado de Actions debe revisarse en GitHub antes de afirmar que las pruebas han pasado. Este workflow no compila ni publica la aplicación Android completa.


- Ajuste adicional antes de ejecutar la batería: se amplió el reconocimiento de unidades de volumen para aceptar expresiones españolas/portuguesas como `litros`, además de `L`, `liters` y `litres`. Commit: `d7680c56ac24c6259ccbb647431e45b0bed2bcbe`. La prueba de «450 litros» se conserva como caso de regresión.


#### Comprobación solicitada — 22/10/2026

- Revisión adicional del código y del arnés de pruebas: se detectó un error de escape en la expresión regular de la unidad de maletero. El patrón dinámico tenía una barra invertida de más, por lo que podía buscar literalmente `\\b` en vez de un límite de palabra; esto habría hecho fallar el caso «450 litros».
- Corrección aplicada en `a164c053eeb7f78431e62db865db01cc4f6c40ef` en `feature/asesor-ia-fase-1`.
- Estado de verificación: la inspección remota de commit no devuelve comprobaciones de CI registradas. En este entorno no se ha podido ejecutar `javac` contra los archivos descargados porque el contenedor no tiene resolución de red. **No afirmar que las pruebas han pasado** hasta comprobar la ejecución del workflow en la pestaña Actions de GitHub.


- **Resultado confirmado de CI:** workflow [Advisor engine smoke tests, ejecución 39807809446](https://github.com/Mariskal19/EV-Calculator-PRO/actions/runs/39807809446), commit `31b9f52d9a4d17c6e607ccde980d951b173e8f9b`, finalizó con `success` el 22/10/2026. Tanto la compilación Java aislada como el paso de smoke tests terminaron correctamente. Esto valida los casos de humo incluidos, pero no sustituye las pruebas de integración con el catálogo ni la compilación completa de Android.


### Asesor conversacional — fase 2: filtro aislado contra candidatos de catálogo (22/10/2026)

- Rama de trabajo: `feature/asesor-ia-fase-1`; la rama `main` y el respaldo `backup/pre-asesor-ia-2026-10-22` no se han modificado por esta fase.
- Nuevo archivo: `app/src/main/java/com/evchargecalculator/AdvisorCatalogMatcher.java`.
- La clase recibe una lista de candidatos adaptados desde los objetos `Vehicle` ya cargados por `SimilarCarsActivity`. No lee archivos, no solicita datos a la red, no refresca el catálogo y no modifica la búsqueda clásica. La adaptación desde `Vehicle` y la integración en pantalla quedan deliberadamente pendientes.
- Los filtros obligatorios se combinan con AND. Si se solicita un límite numérico, un valor ausente o no positivo no se interpreta como cero real ni puede superar el filtro. Se cubren precio, autonomía, maletero, batería, potencia y consumo; carrocería, tracción y marca/modelo requieren coincidencia normalizada explícita.
- Las preferencias de menor consumo, mayor autonomía y mayor maletero solo ordenan los candidatos que cumplen los filtros; por sí solas no eliminan candidatos. Los empates mantienen el orden de catálogo.
- Se ampliaron las pruebas aisladas para comprobar filtros combinados, campos desconocidos, carrocería incompatible, preferencias blandas y consulta sin criterios.
- Se actualizó `.github/workflows/advisor-engine-test.yml` para compilar el motor y el matcher con Java 17 y ejecutar la batería ampliada.
- **Estado de validación:** el código y las pruebas están en la rama, pero la ejecución de CI de esta fase debe finalizar con éxito antes de darla por validada. No equivale a una compilación Android completa ni a integración real en la interfaz.
- **Siguiente paso:** confirmar CI; después diseñar la adaptación segura de los campos de `Vehicle` y probar una muestra del catálogo protegido y de la caché remota, sin modificar sus datos ni el algoritmo clásico.


- **CI de la fase 2 confirmado:** [ejecución 39808523386](https://github.com/Mariskal19/EV-Calculator-PRO/actions/runs/39808523386), job `119928151226`, commit `dfcfef3d193ecbf63e8ac23d91a3395f356312b0`, finalizó con `success` el 22/10/2026. El workflow compiló `AdvisorCriteriaEngine`, `AdvisorCatalogMatcher` y las pruebas con Java 17, y ejecutó correctamente la batería ampliada. Es validación de Java aislado con candidatos de prueba, no prueba todavía la carga/filtrado de los 688 registros del catálogo ni una compilación Android completa.


### Asesor conversacional — fase 3: prueba con catálogo protegido real (22/10/2026)

- Se añadieron `.github/tests/project_protected_catalog.py` y `.github/tests/AdvisorCatalogIntegrationSmokeTest.java`. El script proyecta los campos relevantes del JSON a un TSV temporal solo dentro del runner de CI; no escribe en el catálogo ni altera archivos versionados.
- El workflow compila el intérprete, el matcher y ambas suites de pruebas; luego ejecuta las pruebas unitarias y el matcher contra los registros reales de `app/src/main/assets/catalog_es_2024_2026.json`.
- **Resultado CI confirmado:** [ejecución 39808809409](https://github.com/Mariskal19/EV-Calculator-PRO/actions/runs/39808809409), job `119929150983`, commit `310e3d55b88b17e74587ae94c568aa135dae840b`, terminó con `success`.
- Salida observada: `Projected 688 protected catalog records`; smoke tests del motor/matcher: **16 comprobaciones**; prueba con catálogo: **688 registros**, **98 coincidencias** para precio máximo de 40.000 € + autonomía mínima de 400 km y **589 coincidencias** para autonomía mínima de 400 km.
- La prueba confirma que el matcher aplica los límites combinados, no admite datos de autonomía desconocidos como cumplimiento de un mínimo y una preferencia blanda no elimina registros. Los recuentos son resultado de esa versión del catálogo en la rama de trabajo; no constituyen auditoría de la exactitud comercial de los datos.
- **Límite importante:** el test carga los datos del JSON real a través de una proyección de prueba, pero todavía no está conectado a los objetos `Vehicle` en ejecución ni a la caché remota. Tampoco se ha compilado la aplicación Android completa en esta fase.
- Siguiente paso recomendado: revisar el mapeo de campos de `Vehicle` al candidato, integrar la búsqueda conversacional sin tocar `showSimilar()` ni `collectCompetitors()`, añadir cadenas en los seis idiomas y ejecutar el build Android antes de considerar cualquier integración en `main`.


#### Corrección de seguridad conversacional antes de integrar UI (22/10/2026)

- En la revisión del matcher se detectó un riesgo en el intérprete: un operador de un criterio anterior podía afectar al siguiente número de una frase, y una consulta parcialmente ambigua podía conservar cambios de criterio hechos en la misma frase.
- Se ajustó la detección para limitar el operador a la cláusula actual y se hizo transaccional cada turno: si cualquier parte necesita aclaración, no se conserva ninguna actualización parcial de esa frase.
- Se añadieron regresiones para `hasta 35.000 € y 450 km` (debe pedir aclaración sobre autonomía y no inferir un límite) y para verificar que el estado queda intacto.
- **CI confirmado:** [ejecución 39809050708](https://github.com/Mariskal19/EV-Calculator-PRO/actions/runs/39809050708), job `119929993028`, commit `55bf756f89400bbaf55fef143cf0c95f1e2783fb`, `success`. La batería del intérprete/matcher pasó **18 comprobaciones**; la prueba del catálogo real volvió a pasar con 688 registros, 98 coincidencias combinadas y 589 por autonomía.


#### Integración de interfaz y diagnóstico de compilación Android (22/10/2026)

- Se añadió en `SimilarCarsActivity` una tarjeta de búsqueda conversacional local y resultados limitados a diez coches, con acceso a detalles y a la comparativa existente (máximo tres). La selección clásica de coche de referencia permanece debajo y se conserva su ruta de búsqueda; no se añadió una pestaña de navegación.
- Se añadieron cadenas de interfaz en los seis idiomas mediante `LanguageManager`. El intérprete y el matcher siguen siendo locales: la consulta no se envía a la red.
- Primer workflow Android: [39809356092](https://github.com/Mariskal19/EV-Calculator-PRO/actions/runs/39809356092) falló antes de compilar Java porque `app/build.gradle` exige variables de firma de release durante la evaluación del proyecto, incluso al ejecutar `assembleDebug`. No fue un error de compilación de la nueva interfaz.
- Se añadió `.github/workflows/advisor-android-compile.yml`, que crea una clave efímera solo para CI, ejecuta el parche de virtualización usado por la compilación y solicita únicamente `assembleDebug`; no genera ni publica un artefacto de release.
- El workflow se ajustó para usar esa clave efímera sin sobrescribirla con una ruta ficticia. La ejecución posterior [39809681492](https://github.com/Mariskal19/EV-Calculator-PRO/actions/runs/39809681492) está en curso en el momento de esta nota; **no declarar compilación Android correcta hasta que finalice con `success`**.
- Pendiente de revisar tras el resultado: posibles errores reales de compilación, corrección de referencias de traducción, mapeo de `Vehicle`/clave lógica y pruebas manuales en Android. Todo sigue en `feature/asesor-ia-fase-1`; no fusionar con `main` todavía.


#### Revisión funcional posterior y control de estado de búsqueda (22/10/2026)

- La compilación Android de la integración inicial terminó con éxito en [CI 39809681492](https://github.com/Mariskal19/EV-Calculator-PRO/actions/runs/39809681492), commit `28a032b4d61aaa57eb262ac1bef8add25bb73de0`. Es un `assembleDebug`; no equivale a build Release ni a prueba en dispositivo.
- Revisión estática: `LanguageManager.getEffectiveLanguage` existe; el comparador usa las claves `compare_vehicle_ids`, `compare_vehicle_ids_ordered`, `compare_vehicle_logical_ordered` y `compare_selection_initialized` escritas por el asesor. El catálogo protegido no se ha modificado.
- Se añadió la acción **Nueva búsqueda**, traducida en los seis idiomas, para limpiar el texto, los resultados y los criterios que el motor mantenía en memoria. Esto evita que un usuario empiece una búsqueda distinta arrastrando filtros anteriores sin darse cuenta.
- Los commits de esta mejora son `16a0b8f` (reinicio de búsqueda) y `c32185e` (traducción). La nueva compilación Android de este último cambio está en curso: [CI 39813697512](https://github.com/Mariskal19/EV-Calculator-PRO/actions/runs/39813697512). No registrar éxito hasta que concluya con `success`.
- Sigue pendiente prueba funcional en dispositivo, en especial criterios multi-turno, consulta ambigua, resultados con datos ausentes, detalles y selección de comparativa. No fusionar con `main` antes de terminar estas comprobaciones.


#### Última compilación Android y limpieza de resultados obsoletos (22/10/2026)

- Confirmada la compilación Android Debug tras los últimos cambios en la búsqueda conversacional: [GitHub Actions 39813987883](https://github.com/Mariskal19/EV-Calculator-PRO/actions/runs/39813987883), commit `5bf7f2a820503181aea84f2a3799aa17aa3a54bf`, job `119947484503`, conclusión `success`.
- El job completó correctamente la configuración de Java 17 y Gradle 9.6.0, el parche de búsqueda local, la configuración de firma efímera para evaluar Gradle y el paso **Compile Android debug variant**.
- También terminó correctamente la compilación de la revisión con el botón **Nueva búsqueda** y sus traducciones, en [CI 39813697512](https://github.com/Mariskal19/EV-Calculator-PRO/actions/runs/39813697512).
- La función `runAdvisorSearch()` elimina los resultados anteriores al comienzo de cada intento; si la consulta nueva está vacía o necesita aclaración, no deja visibles resultados que pertenecían a la búsqueda previa.
- Este resultado valida `assembleDebug`, no una compilación Release ni el comportamiento en un dispositivo físico. Quedan pendientes las pruebas manuales de criterios combinados, aclaraciones, acumulación de criterios entre turnos, reinicio, detalle, comparativa y búsqueda clásica.
- Estado de ramas: los cambios continúan aislados en `feature/asesor-ia-fase-1`; no se ha fusionado nada en `main`. El catálogo protegido y su mecanismo de actualización permanecen sin cambios.


### Decisión de interpretación de objetivos de precio y maletero (22/10/2026)

- El asesor distingue **objetivos aproximados** de límites explícitos en ambos criterios.
- **Precio/presupuesto:** conservar el comportamiento de presupuesto cercano ya implementado; ordenar por proximidad al importe objetivo y admitir una pequeña desviación, sin convertir un objetivo de precio en un máximo rígido.
- **Maletero:** «quiero uno de 450 de maletero», «maletero de 450 litros» o «around 450 L of boot space» representan un **objetivo aproximado**. Se priorizan capacidades conocidas cercanas al valor, sin exigir que todos los coches superen 450 L.
- **Operadores explícitos siguen siendo límites:** «al menos 450 litros» / «mínimo 450 L» son un mínimo real; «máximo 450 litros» es un máximo real. Si falta el dato, el coche no se considera coincidencia verificada para ese criterio.
- Cuando precio y maletero son objetivos, la clasificación considera la proximidad relativa a ambos.
- Se añaden pruebas de regresión para distinguir el objetivo de maletero de un mínimo explícito y comprobar el ranking por cercanía.


### Tolerancia para límites máximos del asesor conversacional (22/10/2026)

- Decisión acordada: un criterio expresado como **«como máximo X»** admite una pequeña tolerancia para no descartar alternativas cercanas.
- **Precio máximo:** tolerancia del **5 %** (por ejemplo, 30.000 € admite hasta 31.500 €; un vehículo de 31.000 € se admite).
- **Autonomía, maletero, batería, potencia y consumo máximos:** tolerancia del **3 %** (por ejemplo, 450 km admite hasta 463,5 km).
- **Límites inferiores** («al menos», «mínimo», «más de» según su operador): continúan aplicándose sin tolerancia por debajo del mínimo solicitado.
- La tolerancia se aplica en el filtrado del matcher, no solo en la ordenación. Los objetivos aproximados, como «unos 450 litros», siguen usando su lógica separada de proximidad y no se convierten en límites máximos.
- La tolerancia solo amplía el límite superior; los valores desconocidos/no positivos siguen sin poder satisfacer un filtro numérico.
- Nota sobre precio: se conserva el comportamiento previo del matcher para presupuestos con máximo (80 %–105 % del presupuesto cuando no hay mínimo), por lo que el precio mantiene además su lógica histórica de búsqueda en torno al presupuesto. Esta lógica debe revisarse por separado si se quiere que todo precio inferior al máximo sea admisible sin suelo mínimo.
- La batería de humo incluye casos límite para precio, autonomía, maletero, batería, potencia y consumo. La validación CI debe confirmarse tras estos cambios; todavía no es una prueba de dispositivo ni una publicación.


## 2026-10-22 — Asesor conversacional: criterio de precio incompleto

- En la rama `feature/asesor-ia-fase-1`, el motor local reconoce una consulta que menciona un coche de referencia (`XPeng G6`) y termina con una petición de similitud de precio sin concretar la relación.
- Ejemplo: «Quiero un coche similar al XPeng G6 de precio…». El resultado de interpretación es `clarify_reference_price_relation`: debe solicitar al usuario si quiere una alternativa más barata, más cara o de precio parecido.
- Regla de seguridad semántica: no se inventa un presupuesto, no se añade un filtro numérico y no se modifica ningún criterio previo mientras la frase esté incompleta.
- Se añaden pruebas smoke para la frase sin terminar y la variante con puntos suspensivos.
- Esta modificación prepara el contrato del motor de interpretación. La frase de aclaración visible en la pantalla del asesor debe mapear `clarify_reference_price_relation` a una pregunta localizada; no debe mostrar la clave técnica.
- La política de filtros máximos acordada sigue vigente: precio máximo explícito +5 %; otros máximos numéricos +3 %; mínimos exactos; «unos X litros» se interpreta como objetivo aproximado.


### Interfaz del asesor: búsqueda automática al terminar de escribir (22/10/2026)

- En `SimilarCarsActivity`, se elimina el botón «Buscar coches» de la tarjeta del asesor conversacional.
- La búsqueda se ejecuta automáticamente cuando el usuario deja de escribir durante **1100 ms** (debounce), en vez de recalcular con cada pulsación. Si se pulsa Buscar/Hecho en el teclado, se ejecuta inmediatamente.
- Al borrar el texto se limpian el estado y los resultados visibles; «Nueva búsqueda» también cancela cualquier ejecución pendiente antes de reiniciar los criterios.
- Alcance limitado a la tarjeta «Buscar con IA». La selección clásica de coche de referencia, la búsqueda clásica y `CompararCochesActivity` permanecen intactas.
- Commit: `5737975a9711c45b33113f6517fd24b71f6858b4` en `feature/asesor-ia-fase-1`. Pendiente comprobar la compilación Android y probar el comportamiento en dispositivo. No se ha fusionado con `main`.


### Corrección del filtro de carrocería «berlina» (22/10/2026)

- Se amplía la equivalencia de berlina/sedán para incluir carrocerías fastback y liftback, habituales en coches de perfil berlina, evitando excluir el Peugeot E-408 por tener la etiqueta `liftback`.
- Se corrige en la adaptación del asesor la clasificación de `Kia EV4 Hatchback`, que el catálogo tenía marcada como `sedan`. El ajuste queda aislado al asesor y no modifica el catálogo protegido ni su proceso de actualización.
- Se conserva `Kia EV4 Fastback` como berlina y se evita que la búsqueda de berlina incluya el EV4 Hatchback.
- Se añaden pruebas de regresión para incluir sedán/fastback/liftback y excluir hatchback.
- La búsqueda sigue limitada a un máximo de 10 modelos únicos. La prueba nueva comprueba el filtrado de carrocería, no la cantidad comercial completa del mercado español.
- Cambios en `feature/asesor-ia-fase-1`; no fusionar con `main` hasta validar CI y, si procede, una nueva prueba en móvil.


### Interpretación de presupuesto aproximado (25/10/2026)

- Se detectó en prueba de dispositivo que la frase «Búscame un coche de unos 40000 euros» se trataba como ambigua porque el motor reconocía el importe, pero no la expresión aproximativa «unos».
- `unos`, `alrededor de`, `aproximadamente`, `sobre`, `cerca de`, `en torno a` y `más o menos` se interpretan como presupuesto objetivo aproximado, no como límite máximo estricto. Se añade soporte equivalente para expresiones frecuentes en inglés, francés, alemán, italiano y portugués.
- El criterio conserva el precio como objetivo para ordenar/filtrar coches de esa zona económica; expresiones explícitas como «hasta 40.000 €» siguen siendo un techo presupuestario.
- Se añadió una prueba de regresión para la frase exacta del usuario y para comprobar que no se marca como techo estricto.
- Cambios en `feature/asesor-ia-fase-1`; validar las pruebas automáticas y compilar APK antes de volver a instalar. No fusionar con `main`.


## 2026-10-25 — Precio aproximado en búsquedas de carrocería

- Se ha detectado durante la prueba manual que la consulta «Búscame una berlina de 40 mil euros» podía devolver vehículos muy por encima del precio solicitado.
- Causa identificada en el intérprete local: no normalizaba la forma coloquial española «40 mil euros» y no trataba la construcción «berlina de 40.000 euros» como un precio objetivo aproximado.
- Corrección en la rama `feature/asesor-ia-fase-1`: convertir expresiones numéricas «N mil euros» a su valor numérico y tratar un importe precedido por «de» como objetivo aproximado cuando no hay operador explícito de límite.
- Se añaden pruebas para comprobar que la consulta se interpreta como carrocería berlina + objetivo de 40.000 €, y que un vehículo de 66.000 € queda excluido del resultado.
- Pendiente de validar: ejecución de smoke tests, compilación Android y nueva prueba manual con la APK resultante. No fusionar con `main` ni publicar sin autorización expresa.


### Complemento — importe coloquial sin unidad

- Se amplía la normalización de expresiones como «40 mil» para interpretarlas como 40.000 euros en consultas de búsqueda de vehículos, aunque el usuario omita «euros».
- Se añade una prueba para «Búscame una berlina de 40 mil» que comprueba carrocería berlina y objetivo aproximado de 40.000 €.
- Pendiente: validar CI y compilación Android antes de entregar una nueva APK. Sigue todo en `feature/asesor-ia-fase-1`; no fusionar en `main` sin autorización.


## 2026-10-25 — Validación de resultados visibles del asesor

- Prueba en dispositivo confirmada por el usuario: «Búscame una berlina de 40 mil» y «Búscame una berlina de 40 mil euros» muestran ambas 7 resultados.
- La prueba de integración encuentra 17 registros de catálogo dentro del rango aproximado de 28.000–46.000 € y compatibles con la equivalencia de carrocería sedán/fastback/liftback. Esos registros incluyen varias versiones/años de los mismos modelos; no son 17 tarjetas distintas.
- La pantalla elimina duplicados por marca + modelo y no muestra el Kia EV4 Hatchback como berlina, porque la etiqueta `sedan` del catálogo está corregida a `hatchback` exclusivamente en el adaptador del asesor.
- Con estas reglas, el conjunto esperado de siete modelos únicos es: Tesla Model 3, Peugeot E-408, Kia EV4 Fastback, Mazda Mazda6e, XPeng P7+, Hyundai IONIQ 6 y BYD Seal.
- El matcher ordena por proximidad al presupuesto objetivo de 40.000 €. Se añade una prueba de integración para comprobar los siete modelos visibles, la eliminación de versiones repetidas y su orden esperado.
- Cambio de prueba en la rama `feature/asesor-ia-fase-1`; commit `545549e61e784cec0b9cd9b5bab875dc42b1fbec`. Pendiente confirmar que GitHub Actions ejecuta la prueba nueva correctamente.
- No se modifica el catálogo protegido ni la lógica de producción en este paso. No fusionar con `main` ni publicar sin autorización expresa.


### Prueba de filtros combinados con catálogo real (25/10/2026)

- Se amplía la integración para ejecutar contra los 688 registros la consulta de ejemplo: «SUV hasta 35.000 €, más de 450 km y maletero de al menos 450 litros».
- Resultado: **3 candidatos**. La prueba comprueba para cada uno carrocería SUV/crossover, precio no superior a 36.750 € (techo explícito con tolerancia documentada del 5 %), autonomía WLTP mínima de 450 km y maletero mínimo de 450 litros.
- Smoke tests: **47 comprobaciones del motor** y prueba de integración de catálogo completadas correctamente.
- Workflow correcto: [Advisor engine smoke tests, run 40204847979](https://github.com/Mariskal19/EV-Calculator-PRO/actions/runs/40204847979).
- El cambio solo afecta a pruebas y documentación en `feature/asesor-ia-fase-1`; no se ha generado una APK nueva porque no se ha modificado código de la aplicación.


- Se añadió además una comprobación de resultados visibles (no solo versiones de catálogo): las 3 versiones que cumplen se agrupan en **2 modelos únicos**, ordenados por proximidad al límite de 35.000 €: KGM Torres EVX y MG MGS5 EV. La aserción inicial de orden se corrigió para reflejar el comparador real; la ejecución final pasó.
- Último workflow correcto: [Advisor engine smoke tests, run 40204994536](https://github.com/Mariskal19/EV-Calculator-PRO/actions/runs/40204994536). Resultado: 47 pruebas del motor; 17 coincidencias brutas de berlina, 7 modelos únicos visibles; SUV combinado: 3 versiones, 2 modelos únicos.


## 2026-10-25 — Confirmación manual de pruebas del Asesor IA

- El usuario confirma que la prueba 1 funciona correctamente.
- En la prueba 2, la búsqueda combinada de SUV hasta 35.000 €, autonomía superior a 450 km y maletero mínimo de 450 litros muestra dos coches.
- Esto concuerda con la prueba de integración sobre el catálogo protegido: hay tres versiones coincidentes que se agrupan en dos modelos únicos (KGM Torres EVX y MG MGS5 EV).
- No se cambia lógica de producción ni se publica una APK en este paso.


## 2026-10-25 — Prioridad de versiones recientes en el Asesor IA

- Se corrige `SimilarCarsActivity.runAdvisorSearch()` para determinar primero el año más reciente presente en el catálogo para cada combinación normalizada de marca y modelo.
- El Asesor IA solo evalúa versiones del año más reciente de ese modelo antes de aplicar filtros y ordenar resultados. Conserva todas las variantes/acabados de ese año para que presupuesto, autonomía, carrocería y demás criterios puedan seleccionar entre ellas.
- Esto evita que una versión antigua gane solo por quedar más cerca del precio objetivo. Si ninguna variante del año más reciente cumple los criterios, el modelo no se ofrece mediante una versión antigua como sustituto silencioso.
- Alcance y limitación: el año más reciente del catálogo no prueba por sí solo que el acabado siga actualmente a la venta; para afirmarlo hace falta una auditoría comercial de fuentes oficiales. La lógica usa el año declarado por cada registro y no modifica el catálogo protegido ni `main`.
- Commit de implementación en `feature/asesor-ia-fase-1`: `93f46c16f619c11ab47e0f00341417ba324e04e7`. Compilación Android pendiente de resultado de GitHub Actions antes de entregar APK para prueba.


## Asesor IA: orden de resultados por precio (2026-10-25)
- Tras filtrar los años anteriores y priorizar el año más reciente disponible por marca/modelo, los resultados del Asesor IA se ordenan por precio de catálogo ascendente.
- Los vehículos sin precio válido se colocan al final; en empates se ordena por marca y modelo.
- Cambio de código: `3bd55bcaaa47c19156f0b49339782999e4461e5d` en `feature/asesor-ia-fase-1`.
- Pendiente: validar la compilación CI y probar en dispositivo con búsquedas como «Búscame una berlina de 40 mil».


## Asesor IA: ocultar teclado al enviar búsqueda (2026-10-25)
- Al confirmar la búsqueda desde la acción Buscar/Listo del teclado Android, la app oculta el teclado y quita el foco del campo antes de ejecutar la búsqueda.
- La búsqueda automática mientras se escribe se mantiene, evitando que el teclado se cierre mientras el usuario redacta.
- Cambio de código: `b5b94a9f5f00a8a720f462519a58d3c957d5131a` en `feature/asesor-ia-fase-1`.
- Pendiente de compilación CI y prueba en dispositivo.

### Ocultar teclado al comenzar la búsqueda automática del Asesor IA (2026-10-25)
- Prueba en dispositivo: la búsqueda automática se iniciaba tras la pausa de escritura, pero el teclado seguía visible.
- Corrección: `runAdvisorSearch()` ahora oculta el teclado y quita el foco del campo al comenzar la búsqueda. Esto cubre tanto la búsqueda automática por debounce (1100 ms sin escribir) como el envío desde el teclado.
- Rama: `feature/asesor-ia-fase-1`.

### Ajuste del retraso de búsqueda/ocultación del teclado a 1100 ms (2026-10-25)
- Se aumenta el debounce de la búsqueda automática de 1000 a **1100 ms** desde la última pulsación. Al iniciarse la búsqueda, `runAdvisorSearch()` oculta el teclado y quita el foco del campo.
- La acción Buscar/Hecho del teclado sigue ejecutando la búsqueda inmediatamente.
- Cambio en `SimilarCarsActivity.java`, rama `feature/asesor-ia-fase-1`. Pendiente de CI y prueba en dispositivo.

### Selección de resultados cerca del precio objetivo del Asesor IA (2026-10-25)
- Incidencia detectada: una consulta como `Búscame un SUV de 50 mil euros` podía mostrar solo los 10 modelos más baratos del intervalo aproximado (35.000–57.500 €), con un máximo visible cercano a 36.900 €, aunque el catálogo contiene SUV más próximos a 50.000 €.
- Corrección en `SimilarCarsActivity.java`: para un precio objetivo aproximado, seleccionar primero hasta 10 modelos únicos con menor diferencia absoluta respecto al objetivo y después mostrar esa selección ordenada de menor a mayor precio. Los límites explícitos como `hasta 50.000 €` conservan el comportamiento de listar los más baratos primero.
- Rama: `feature/asesor-ia-fase-1`. Pendiente de CI y prueba con consultas aproximadas de 40.000 € y 50.000 €, además de límites explícitos.


## 2026-10-25 — Asesor IA: conservar variantes de un modelo al filtrar por año

- Incidencia detectada con la consulta «Búscame un SUV de 47 mil euros»: el asesor descartaba todas las versiones XPeng G6 de 2024/2025 porque el catálogo contiene una versión Standard Range de 2026.
- **No se modifica el catálogo protegido.** El problema estaba en el filtro previo de `SimilarCarsActivity`, que elegía el año más reciente por marca/modelo completo antes de aplicar los criterios.
- Corrección en la rama `feature/asesor-ia-fase-1`: el asesor evalúa primero todas las versiones disponibles en el catálogo y después selecciona una versión representativa por modelo. Para un precio aproximado, selecciona la versión más cercana al objetivo antes de ordenar los resultados por precio; para «hasta X €», se mantiene la lógica de precio ascendente.
- Caso de regresión que debe verificarse en la compilación: consulta «SUV de 47 mil euros» debe poder seleccionar XPeng G6 RWD Long Range de 2025 (47.083 €) en lugar de descartarlo únicamente porque existe un G6 Standard Range de 2026 (43.083 €). El catálogo permanece intacto.
- Commit de código: `c8a18bb202e9bfac72b05579de7abd484db0aa10`. Pendiente: validar CI y probar el APK generado antes de dar la incidencia por cerrada.


## 2026-10-25 — Asesor IA: versión más reciente por acabado equivalente

- Criterio confirmado con el usuario: no se filtra por el año más reciente del modelo completo, ni se recuperan versiones antiguas de un acabado solo porque tengan mejor precio.
- El asesor agrupa versiones equivalentes por marca, modelo, mercado, tracción y nombre normalizado del acabado. Al normalizar, ignora cifras técnicas (kW, kWh, CV, autonomía, etc.) y etiquetas de tracción repetidas dentro del texto; la tracción sigue formando parte de la clave independiente.
- Dentro de cada acabado equivalente, solo se evalúa el año más reciente disponible en el catálogo. Si ese acabado no cumple el presupuesto u otros criterios, no se ofrece un año anterior como sustituto. Los acabados distintos del mismo modelo siguen compitiendo por separado.
- Caso XPeng G6: Long Range 2025 (47.083 €) se conserva; Long Range 2024 (49.990 €) se descarta por ser anterior. Standard Range 2026 se conserva como acabado distinto; Standard Range 2024 se descarta. Performance AWD también se compara solo con su año más reciente disponible.
- El catálogo protegido y `main` permanecen sin cambios. Implementación en `feature/asesor-ia-fase-1`, commit `bb7c2c6798d2f58d533ea2295db396b8f6e49b35`. Pendiente CI y prueba de regresión con «SUV de 47 mil euros».


## 2026-10-25 — Asesor IA: importes expresados en miles

- En la prueba real del APK, la consulta «SUV de 47 mil euros» no devolvía ningún XPeng G6.
- Causa localizada en `AdvisorCriteriaEngine`: la extracción numérica no admitía el multiplicador «mil» entre la cifra y la moneda, de modo que la expresión no se convertía en un objetivo de 47.000 €.
- Corrección aplicada para reconocer importes de precio con «mil» o «k» (por ejemplo, «47 mil euros» y «47k €») y multiplicarlos por 1.000. El ajuste está limitado al criterio de precio.
- Código: commit `e2b48382eb6f56240a706bc1b6dfb2dc9129f06a` en `feature/asesor-ia-fase-1`.
- Pendiente: compilación CI y nueva prueba del APK con «SUV de 47 mil euros». No se modifica el catálogo protegido ni `main`.


## 2026-10-25 — Asesor IA: consultas de coches parecidos a un modelo

- Incidencia reproducida por el usuario: «Quiero un coche parecido al XPeng G6» no devolvía resultados.
- Causa: el intérprete de criterios del Asesor IA estaba pensado para filtros numéricos y atributos; no derivaba una petición de similitud con un modelo concreto al motor dedicado de coches similares.
- Corrección en `SimilarCarsActivity.java`: detectar peticiones de similitud en lenguaje natural que mencionen XPeng G6, seleccionar una referencia G6 existente en el catálogo, guardar esa referencia y ejecutar el motor de competidores/similitud existente. Se muestra explícitamente qué año y versión se ha usado como referencia.
- Se hace visible el encabezado de resultados al ejecutar el motor de similitud.
- Commit: `529955aebb4d2d5094db16d3ab30ce31b3d2d1b1`.
- Pendiente: confirmar compilación CI y probar en el APK frases como «Quiero un coche parecido al XPeng G6» y «Busca alternativas similares al XPeng G6».
- Se mantiene el trabajo en `feature/asesor-ia-fase-1`; no se modifica `main` ni el catálogo protegido.


### Asesor IA — detección genérica de consultas de coches similares (25/10/2026)
- Se generalizó `tryRunNaturalSimilarityQuery()` en `SimilarCarsActivity.java`: las frases de similitud ya no están limitadas al XPeng G6; buscan la marca y el modelo mencionados entre los vehículos cargados del catálogo.
- La comparación compacta tolera diferencias de espacios, por ejemplo `Zeekr 7GT` aunque el usuario escriba `Zeekr 7 GT`.
- Si el modelo existe, se elige la versión/año más reciente siguiendo el criterio de selección ya usado y se abre el motor existente de 10 alternativas similares. Si no se localiza, ahora se informa al usuario en lugar de dejar la consulta sin respuesta.
- Verificación de catálogo en la rama de trabajo: hay tres variantes del `Zeekr 7GT` de 2026 (Core RWD, Long Range RWD y Privilege AWD). El catálogo protegido no se ha modificado.
- Cambio de código: commit `4a8391f5eae7aaeac6b7fb4d2b9da94f6b644f3c` en `feature/asesor-ia-fase-1`. Pendiente validar compilación CI y probar en el teléfono con `Quiero un coche parecido al zeekr 7gt`; no reutilizar APKs antiguos.


### Asesor IA — ampliación de frases de similitud y nombres cortos (25/10/2026)
- Se amplió la detección de lenguaje natural para incluir expresiones con «como», además de «parecido», «similar», «semejante», «alternativa», «rival» y «competidor», y equivalentes habituales en inglés.
- Primero se intenta identificar la marca y el modelo completos en el catálogo. Si el usuario omite la marca (por ejemplo, «Búscame un coche como el G6»), se busca el nombre del modelo como frase completa y se acepta solo cuando no hay ambigüedad entre marcas/modelos.
- Si el nombre corto puede referirse a varios modelos, el asesor solicita que se añada la marca en vez de escoger uno arbitrariamente.
- Al resolver la referencia, se reutiliza el motor existente de coches similares; no se altera el catálogo ni se crea un motor paralelo.
- Ejemplos previstos: «Búscame un coche como el G6», «Quiero algo parecido al XPeng G6», «Busca alternativas al Zeekr 7GT» y «Un coche similar al Model Y».
- Código: commit `2598eed450a8e5364bc9ae636f59fa13073e2205` en `feature/asesor-ia-fase-1`. Pendiente confirmar compilación CI y probar estas consultas en el APK nuevo; no reutilizar APKs anteriores. `main` y el catálogo protegido siguen intactos.


### Asesor IA — mayor visibilidad del estado y los resultados (25/10/2026)
- Se aumenta el texto del estado de búsqueda de 12 a 15 sp, en azul y negrita, para que mensajes como «Preparando búsqueda…», avisos y encabezados de resultados destaquen mejor.
- Se aumentan los nombres de los coches de los resultados de 15 a 16 sp y se muestran en azul y negrita para facilitar la lectura visual de los resultados.
- Cambio visual limitado al Asesor IA; no altera la lógica de búsqueda, el catálogo protegido ni `main`.
- Código: commit `4c73188fbfc6373d2afc1d13c5ebf562fa46bf52` en `feature/asesor-ia-fase-1`. Pendiente comprobar compilación CI y validar visualmente el APK nuevo en el móvil.


### Asesor IA — petición genérica con un modelo de referencia (25/10/2026)

- Incidencia reportada en dispositivo: «Quiero un coche el XPeng G6» se interpretaba como un límite ambiguo porque no incluía las palabras «parecido» o «similar».
- Se amplía el enrutamiento: una petición genérica de coche que mencione un modelo completo existente en el catálogo se entiende como solicitud de alternativas a ese modelo y utiliza el motor existente de coches similares. Las peticiones genéricas sin modelo de referencia siguen pidiendo criterios; las búsquedas por presupuesto como «Quiero un SUV de 47 mil euros» no se desvían al motor de similitud.
- Se extrae la decisión a `AdvisorSimilarityIntent`, una clase Java independiente de Android, y se añaden pruebas de regresión para la frase exacta del usuario, la frase explícita de similitud y dos casos negativos.
- Se amplía el workflow de pruebas para compilar el nuevo helper y se incluye `SimilarCarsActivity.java` en los cambios que disparan la compilación Android.
- Rama: `feature/asesor-ia-fase-1`. No se modifica `main` ni el catálogo protegido. La compilación y las pruebas de GitHub Actions deben confirmarse antes de instalar una APK nueva.


## Asesor IA — distinguir modelo concreto de coches similares (25/10/2026)

En la rama `feature/asesor-ia-fase-1`, el enrutamiento de consultas se generaliza para todo el catálogo, sin reglas específicas para XPeng:

- Peticiones directas como «Búscame el XPeng G6», «Muéstrame el Tesla Model Y» o equivalentes deben mostrar el modelo solicitado y sus versiones disponibles en el catálogo.
- Las expresiones de similitud («parecido», «similar», «alternativas», «rival», «como…») siguen activando la búsqueda de coches similares, incluso si también se pide mostrar/buscar.
- Las peticiones por criterios (por ejemplo, tipo de carrocería y presupuesto) continúan en el intérprete de filtros.
- Si un nombre corto identifica varios modelos, se solicita marca/modelo para evitar una selección arbitraria.
- Se añadieron pruebas automáticas para búsquedas directas de más de una marca, precedencia de similitud y exclusión de consultas genéricas por criterios.

No se modifica `main` ni el catálogo protegido.


## Asesor IA — corrección de SUV frente a BYD Dolphin Surf (25/10/2026)

- Incidencia observada en el móvil: la consulta «Quiero un SUV de unos 40000» devolvía BYD Dolphin Surf, pese a que el usuario pidió carrocería SUV.
- Causa confirmada: el catálogo protegido etiqueta incorrectamente el Dolphin Surf como `suv`. La web oficial de BYD lo describe como hatchback de cinco puertas y segmento A.
- Corrección acotada en el adaptador del Asesor IA: BYD Dolphin Surf se interpreta como `hatchback` para el filtrado de carrocería, sin editar el catálogo protegido ni alterar otras pantallas.
- Se detectó una segunda causa: «Quiero un SUV de unos 40000» no incluye moneda ni la palabra «presupuesto/precio», por lo que el intérprete podía ignorar los 40000 y aplicar solo la carrocería. Ahora una expresión de precio aproximado («unos», «alrededor de», etc.) también activa la lectura del número sin unidad.
- Las pruebas cubren la frase exacta y verifican que se excluyan Dolphin Surf por carrocería, Fiat Grande Panda por quedar muy por debajo del rango de precio aproximado y SUVs por encima del rango; se mantiene el SUV cercano a 40000.
- Se añadieron pruebas de regresión para «Quiero un SUV de unos 40000»: el filtro debe conservar SUVs que se ajustan al precio y excluir carrocerías y precios fuera del rango.
- Rama: `feature/asesor-ia-fase-1`. No se modifica `main` ni el catálogo protegido. Pendiente verificar GitHub Actions y probar el APK actualizado en el móvil.


## 2026-10-25 — Margen simétrico para presupuestos aproximados del Asesor IA

- Aprobado por el usuario: reducir el margen de precio aproximado de −30 % / +15 % a **±15 %**.
- El matcher local usa ahora una banda del 85 % al 115 % del precio objetivo. Ejemplo: «SUV de unos 40.000» permite precios de **34.000 a 46.000 €**, ambos límites incluidos.
- Los límites explícitos como «hasta 40.000 €» mantienen su regla independiente: precio inferior sin mínimo y hasta un 5 % de tolerancia por encima (42.000 €).
- Se actualizan las pruebas de regresión para comprobar los límites 34.000/46.000 €, excluir precios fuera de la banda y mantener la tolerancia de los límites explícitos.
- Cambios en `feature/asesor-ia-fase-1`. No se modifica `main` ni el catálogo protegido. Pendiente validar smoke tests, compilación Android y APK antes de pedir prueba en el móvil.


## Corrección de precio implícito en búsquedas por carrocería (2026-10-25)

- Se corrige el caso `Búscame una berlina de 62800`: si una búsqueda menciona una carrocería reconocida y una cifra plausible de precio (10.000–250.000), se admite esa cifra como objetivo de precio aunque no incluya `€`, `precio` o `presupuesto`.
- El número se interpreta como objetivo aproximado y mantiene la banda económica existente (±15 %), no como precio máximo.
- Se limita la detección a cifras de cinco dígitos o más para no confundir expresiones como `berlina de 5 puertas` con un precio.
- Cambios en `AdvisorCriteriaEngine.java`, únicamente en `feature/asesor-ia-fase-1`; no se modifica el catálogo protegido ni `main`.

## 2026-10-27 — Pruebas manuales del Asesor IA: berlinas y presupuesto aproximado

Pruebas realizadas por el usuario en el APK instalado en su Redmi Note 13 Pro. **No repetir estas dos pruebas salvo que cambie el motor de búsqueda, el catálogo relevante o una corrección afecte a la extracción de carrocería/precio.**

1. Consulta exacta: `Búscame una berlina de 40.000 €`
   - Resultado observado: **7 coches**.
   - El usuario confirma que esta búsqueda devuelve resultados; queda registrada como prueba manual satisfactoria.

2. Consulta exacta: `Búscame una berlina de 62800`
   - Resultado observado: **9 coches**.
   - El usuario confirma que los 9 resultados son correctos: carrocería y precios adecuados.
   - Esta prueba cubre la incidencia previa en la que una cifra de 62.800 podía devolver coches de alrededor de 38.000 €.

Criterio de precio aproximado vigente: **±15 %** sobre el objetivo. Por tanto:
- 40.000 € → banda esperada de **34.000–46.000 €**.
- 62.800 € → banda esperada de **53.380–72.220 €**.

Estado: **ambas pruebas manuales superadas según confirmación del usuario**. Es una validación en dispositivo, no una prueba automatizada nueva. No se modifica `main` ni el catálogo protegido; esta anotación se añade únicamente a `feature/asesor-ia-fase-1`.

## 2026-10-27 — Prueba manual: búsqueda directa de un modelo

- Consulta exacta en el APK: `Búscame el XPeng G6`.
- Resultado observado por el usuario: **6 versiones del XPeng G6**.
- Interpretación: la consulta directa devuelve el modelo solicitado y sus versiones disponibles, en lugar de desviar la petición a la búsqueda de coches similares.
- Estado: **prueba manual satisfactoria según confirmación del usuario**.
- No repetir esta prueba salvo que cambie el enrutamiento de consultas directas/similitud o el catálogo del XPeng G6.
- La anotación se realiza únicamente en `feature/asesor-ia-fase-1`; no se modifica `main` ni el catálogo protegido.

## 2026-10-27 — Prueba manual: alternativas similares al XPeng G6

- Consulta exacta en el APK: `Quiero un coche parecido al XPeng G6`.
- Resultado observado por el usuario: **10 coches en la sección de similares**.
- Interpretación: la petición de similitud activa el motor de coches similares y muestra alternativas, no las versiones del XPeng G6.
- Estado: **prueba manual satisfactoria según confirmación del usuario**.
- No repetir esta prueba salvo que cambie el enrutamiento de similitud, el ranking de coches similares o los datos relevantes del catálogo.
- Anotación realizada solo en `feature/asesor-ia-fase-1`; no se modifica `main` ni el catálogo protegido.

## 2026-10-27 — Prueba manual: similitud con nombre corto «G6»

- Consulta exacta en el APK: `Búscame un coche como el G6`.
- Resultado observado por el usuario: **10 coches similares**.
- Interpretación: el asesor reconoce el nombre corto «G6» como referencia no ambigua del XPeng G6 y ejecuta la búsqueda de alternativas.
- Estado: **prueba manual satisfactoria según confirmación del usuario**.
- No repetir salvo cambios en la resolución de nombres cortos, en el enrutamiento de similitud o en el catálogo que introduzcan ambigüedad.
- Anotación solo en `feature/asesor-ia-fase-1`; no se modifica `main` ni el catálogo protegido.



## 2026-10-27 — Pruebas manuales del Asesor IA: presupuesto aproximado y límite máximo

Pruebas realizadas por el usuario en el APK instalado. Registrar como validaciones manuales del comportamiento visible; no equivalen a una prueba automatizada.

### Precio aproximado: SUV de unos 50.000 €

- Consulta: `Búscame un SUV de unos 50.000 €`.
- Resultado observado: **10 SUV**.
- El usuario confirma que los precios de los resultados son correctos y cercanos al objetivo.
- Criterio esperado: precio aproximado con margen simétrico de ±15 % (42.500–57.500 €); los resultados se seleccionan por proximidad al objetivo y luego se muestran en orden ascendente de precio.
- Estado: **prueba manual satisfactoria según confirmación del usuario**.

### Límite máximo: SUV de hasta 30.000 €

- Consulta: `Búscame un SUV de hasta 30.000 €`.
- Resultado observado: **10 SUV**.
- Precios observados por el usuario: desde **22.800 € hasta 29.350 €**.
- Todos los resultados observados están por debajo del límite de 30.000 €, por lo que no fue necesario utilizar la tolerancia máxima del 5 % (hasta 31.500 €).
- Estado: **prueba manual satisfactoria según confirmación del usuario**.

No repetir estas pruebas salvo que cambie el motor de extracción/filtrado de precio, el ranking de precio aproximado o los datos relevantes del catálogo. Las anotaciones corresponden únicamente a `feature/asesor-ia-fase-1`; no se modifica `main` ni el catálogo protegido.


## 2026-10-27 — Corrección: las búsquedas nuevas no heredan filtros antiguos

- Incidencia detectada: `SimilarCarsActivity` reutilizaba la instancia de `AdvisorCriteriaEngine` y llamaba a `interpret()` sin limpiar los criterios al iniciar una consulta nueva. Los filtros de autonomía, maletero o precio de una consulta previa podían contaminar la siguiente búsqueda y provocar resultados incorrectos o cero resultados.
- Corrección aplicada únicamente en `feature/asesor-ia-fase-1`: antes de interpretar una búsqueda nueva, se reinicia el motor. Se conservan los criterios solo para expresiones de seguimiento reconocibles, por ejemplo `más de 400 km`, `al menos 450 litros`, `y además...` o `sin...`.
- El botón «Nueva búsqueda» mantiene su función de limpiar el estado y el campo de texto.
- No se modificó `main`, ninguna rama stable ni el catálogo protegido.
- Commits de la corrección: `5afb27c8ae01e52c97299336db7705d1d2167d26` (reset de búsquedas nuevas) y `937c2619f83cf566ed588c997dfa169cf4d152e2` (normalización de expresiones de seguimiento).
- Validación pendiente tras el push: revisar los workflows **Advisor engine smoke tests** y **Advisor Android compile check**; comprobar que pasan las pruebas automáticas y descargar el APK debug generado para pruebas manuales.
- Prueba manual de regresión recomendada en dispositivo: ejecutar primero `Búscame una berlina de hasta 35.000 €, con más de 400 km de autonomía y al menos 450 litros de maletero` y después `Búscame una berlina de hasta 35.000 €`. La segunda consulta debe ser independiente de los criterios de autonomía y maletero de la primera.


## 2026-10-27 — Prueba del asesor: familiar hasta 45.000 €

- Prueba manual en el APK de la rama `feature/asesor-ia-fase-1`: consulta «Búscame un coche familiar de hasta 45.000 €».
- Resultado observado en el teléfono: 2 modelos, con precios de aproximadamente 44.000 € y 45.900 €.
- Auditoría del catálogo protegido de esa rama: 24 registros con `bodyStyle: wagon`, correspondientes a 7 modelos distintos. Aplicando el margen actual del 5 % al límite explícito, el techo efectivo es 47.250 €.
- Solo hay 2 modelos wagon distintos dentro de ese techo: Subaru E-Outback (44.000 €) y Zeekr 7GT Core RWD (45.990 €). Por tanto, los dos resultados observados son coherentes con el catálogo y con los filtros actuales; no se cambia el margen ni se amplía artificialmente la carrocería para llenar 10 resultados.
- Otros familiares del catálogo quedan fuera del techo: Mercedes-Benz CLA Shooting Brake Eléctrico (49.686,30 €), Volkswagen ID.7 Tourer y Zeekr 001 (versiones aplicables por encima del límite).
- Conclusión: no se detecta un fallo del filtro en este caso. Una prueba útil de contraste es «Búscame un coche familiar de hasta 50.000 €», cuyo techo efectivo es 52.500 € y debería incorporar el Mercedes-Benz CLA Shooting Brake Eléctrico si el filtro de carrocería sigue funcionando.
- Este resultado corresponde al catálogo de la rama de trabajo; no implica publicación en Google Play.


### Resultado de contraste — familiar hasta 50.000 € (2026-10-27)

- El usuario confirma que la aplicación devuelve 3 resultados para «Búscame un coche familiar de hasta 50.000 €».
- Con tolerancia del 5 %, el límite efectivo es 52.500 €. En el catálogo protegido, los tres modelos wagon actuales que entran en ese límite son Subaru E-Outback (44.000 €), Zeekr 7GT Core RWD (45.990 €) y Mercedes-Benz CLA Shooting Brake Eléctrico CLA 200 (49.686,30 €).
- El recuento de 3 resultados coincide con los modelos elegibles del catálogo. No se modifica el filtro ni se fuerza un mínimo artificial de resultados.

- **Control de repetición:** dar por cerradas y no volver a ejecutar estas dos consultas (`hasta 45.000 €` y `hasta 50.000 €`) mientras no cambien el motor de filtros/ranking, la tolerancia del 5 % o los modelos/precios del catálogo. Si alguno cambia, repetir únicamente la prueba afectada.

## 2026-10-27 — Prueba manual: SUV hasta 45.000 €, autonomía y maletero

- Consulta exacta introducida en el APK de la rama `feature/asesor-ia-fase-1`: `Búscame un SUV de hasta 45.000 €, con más de 400 km de autonomía y al menos 500 litros de maletero`.
- Resultado observado por el usuario: **10 coches**.
- Rango de precios observado: **43.000–46.900 €**.
- El usuario confirma que los 10 resultados superan los **400 km de autonomía** y que todos tienen **más de 500 litros de maletero**.
- Regla de precio vigente: un límite explícito permite hasta un 5 % de tolerancia; para 45.000 €, el techo efectivo es **47.250 €**. Todos los precios comunicados están dentro del rango.
- Conclusión: **prueba manual satisfactoria según confirmación del usuario**; la búsqueda combina carrocería SUV, precio máximo, autonomía mínima y volumen mínimo de maletero.
- No repetir esta consulta salvo cambios en el motor de extracción/filtros, la tolerancia de precio o los datos relevantes del catálogo.
- Documentado solo en `feature/asesor-ia-fase-1`; no se modifica `main` ni el catálogo protegido.

## 2026-10-27 — Revisión de la prueba de regresión: presupuesto demasiado bajo para validar el reinicio

- Prueba manual en el APK instalado:
  1. Consulta inicial: `Búscame una berlina de hasta 35.000 €, con más de 400 km de autonomía y al menos 450 litros de maletero` → **0 coches**.
  2. Sin pulsar «Nueva búsqueda», consulta: `Búscame una berlina de hasta 35.000 €` → **0 coches**.
- Revisión del catálogo protegido de `feature/asesor-ia-fase-1`: la berlina eléctrica más barata registrada con precio positivo es el Tesla Model 3 (36.990 €). Con la tolerancia actual del 5 %, «hasta 35.000 €» admite como máximo 36.750 €, así que el Model 3 también queda fuera. Por tanto, el segundo resultado de cero coches es coherente con el catálogo y **no demuestra que los filtros anteriores se hayan heredado**.
- Conclusión: esta prueba es **inconclusa para validar el reinicio de criterios**, no evidencia suficiente de un fallo. No cambiar el código ni ampliar el límite de precio para forzar resultados.
- Siguiente prueba de regresión válida, sin pulsar «Nueva búsqueda» entre consultas:
  1. `Búscame una berlina de hasta 40.000 €, con más de 400 km de autonomía y al menos 450 litros de maletero`.
  2. `Búscame una berlina de hasta 40.000 €`.
  La primera puede devolver cero resultados si no hay modelos que cumplan los tres filtros; la segunda debería devolver las berlinas elegibles según el catálogo y el techo efectivo de 42.000 €, independientemente de autonomía y maletero. Confirmar los resultados reales en el APK antes de declarar validada la corrección.
- No modificar `main` ni el catálogo protegido. Registrar esta aclaración solo en `feature/asesor-ia-fase-1`.

## 2026-10-27 — Prueba manual de regresión: reinicio de criterios validado

- En el APK instalado, el usuario ejecutó ambas consultas seguidas, sin pulsar «Nueva búsqueda» entre ellas:
  1. `Búscame una berlina de hasta 40.000 €, con más de 400 km de autonomía y al menos 450 litros de maletero` → **2 coches**.
  2. `Búscame una berlina de hasta 40.000 €` → **3 coches**.
- Resultado: la segunda consulta obtiene más resultados al retirar las condiciones de autonomía y maletero. Esto es coherente con que los filtros de la primera búsqueda no se hayan heredado. La corrección del reinicio de criterios queda **validada en esta prueba manual básica**.
- Alcance: la prueba confirma este caso concreto, no todas las combinaciones ni todos los tipos de seguimiento. No se cambia el código ni el catálogo protegido a raíz de este resultado.


## 2026-10-27 — Prueba manual de regresión: cambio de carrocería y presupuesto

- En el APK instalado, el usuario ejecutó dos consultas seguidas, sin pulsar «Nueva búsqueda» ni cambiar manualmente los filtros entre ellas:
  1. `Búscame un SUV de hasta 45.000 €, con más de 400 km de autonomía y al menos 500 litros de maletero` → **10 coches**.
  2. `Búscame una berlina de hasta 40.000 €` → **3 coches**.
- Resultado: la segunda búsqueda cambia tanto la carrocería como el presupuesto y devuelve 3 resultados. Esto es coherente con que la búsqueda nueva se procese de forma independiente y no herede los criterios de autonomía y maletero de la consulta anterior.
- Conclusión: **prueba manual satisfactoria** para el cambio de tipo de carrocería y presupuesto. Junto con la prueba anterior de berlina, aporta una segunda comprobación del reinicio de criterios.
- Alcance: confirma únicamente estas consultas y resultados observados; no equivale a una cobertura exhaustiva de todas las combinaciones.
- No se modifica el código, `main` ni el catálogo protegido; solo se registra el resultado en `feature/asesor-ia-fase-1`.


## 2026-10-27 — Prueba manual de regresión: modelo concreto seguido de búsqueda nueva

- En el APK instalado, el usuario ejecutó dos consultas seguidas, sin pulsar «Nueva búsqueda» entre ellas:
  1. `Búscame el XPeng G6` → **6 versiones**.
  2. `Búscame un SUV de hasta 30.000 €` → **10 coches**.
- Resultado observado: la segunda consulta devuelve resultados para una carrocería y un presupuesto distintos de la búsqueda por modelo concreto. Esto es coherente con que se haya iniciado una búsqueda nueva y no se hayan conservado los criterios de la consulta anterior.
- La cantidad de 10 resultados por sí sola no permite comprobar los precios ni la elegibilidad de cada vehículo; queda pendiente contrastar el rango de precios mostrado por el usuario si se necesita validar también el límite presupuestario en esta prueba.
- Conclusión provisional: **prueba satisfactoria para comprobar que se puede pasar de una búsqueda por modelo concreto a una búsqueda nueva**, con validación limitada a los recuentos comunicados.
- No se modifica el código, `main` ni el catálogo protegido; solo se registra el resultado en `feature/asesor-ia-fase-1`.

- Verificación adicional del presupuesto en la segunda consulta: el usuario informa que los precios de los 10 SUV mostrados van de **29.264 € a 30.990 €**. Con el margen vigente del 5 % sobre 30.000 € (techo efectivo **31.500 €**), todos los precios indicados están dentro del límite permitido. La validación de precios queda satisfactoria según el rango comunicado.


## Prueba manual de seguimiento acumulativo (2026-10-27)

- Consulta inicial de SUV hasta 45.000 euros: 10 resultados.
- Se añadió el criterio de autonomía superior a 500 km: 10 resultados. El usuario comprobó que ninguno bajaba de 500 km; el mínimo observado fue exactamente 500 km, por lo que queda pendiente confirmar el comportamiento del límite estricto.
- Se añadió después el requisito de maletero de al menos 600 litros: 5 resultados. El usuario confirma que los cinco cumplen ese volumen.
- Evaluación: evidencia favorable de que se acumulan los criterios y se reducen resultados al añadir el requisito de maletero. Validación parcial: aún falta confirmar que los cinco mantienen el presupuesto, la carrocería SUV y la autonomía requerida.
- Registro únicamente en esta rama de trabajo. Sin cambios de código, `main` ni catálogo protegido.

- Comprobación adicional de presupuesto de los 5 resultados finales: el usuario indica un rango de precios de **37.500–45.990 €**. Con el margen vigente del 5 % sobre el límite de 45.000 € (techo efectivo **47.250 €**), todos los precios comunicados están dentro del máximo admitido. Esto confirma el presupuesto para esos resultados; la autonomía estricta >500 km queda pendiente de comprobación individual, ya que el mínimo observado previamente era exactamente 500 km.

- Verificación final de autonomía de los 5 resultados: el usuario confirma que **ninguno marca 500 km exactos**; por tanto, todos superan estrictamente los 500 km solicitados. Junto con la comprobación previa de que los cinco tienen al menos 600 litros de maletero y el rango de precios 37.500–45.990 € (por debajo del techo efectivo de 47.250 €), queda validada según los datos observados la acumulación de carrocería SUV, presupuesto, autonomía y maletero en esta prueba manual.


## 2026-10-27 — Incidencia en seguimiento negativo: exclusión por longitud

- Consulta inicial en el APK: `Búscame un SUV de hasta 45.000 €` → 10 resultados.
- Seguimiento, sin pulsar «Nueva búsqueda»: `Y además, sin coches de más de 4,7 metros` → 10 resultados.
- El usuario identifica entre los resultados el **Skywell BE11**, cuya ficha en la app muestra **4,72 m** de longitud. Este vehículo supera el máximo de 4,7 m, por lo que no debería aparecer tras aplicar la exclusión.
- Resultado: **fallo reproducido en la prueba manual**; la condición negativa de longitud no se está aplicando correctamente o no se está interpretando como criterio acumulativo.
- Acción recomendada: revisar extracción/normalización de expresiones negativas (`sin coches de más de X metros`) y la aplicación del filtro de longitud en el motor del Asesor IA. Añadir prueba de regresión que compruebe que 4,72 m queda excluido con un máximo de 4,7 m.
- Solo se registra la incidencia en `feature/asesor-ia-fase-1`. No se modifica el código, `main` ni el catálogo protegido en esta acción.


## 2026-10-27 — Corrección del filtro negativo de longitud

- Incidencia reproducida manualmente: consulta inicial `Búscame un SUV de hasta 45.000 €` → 10 resultados; seguimiento `Y además, sin coches de más de 4,7 metros` → 10 resultados, incluyendo Skywell BE11 con longitud mostrada de 4,72 m.
- Causa técnica identificada: el intérprete no tenía un criterio de longitud y el adaptador del catálogo no pasaba `lengthMm` al matcher; por tanto, la frase negativa no podía filtrar por ese dato.
- Corrección aplicada en `feature/asesor-ia-fase-1`:
  - `AdvisorCriteriaEngine`: reconoce expresiones negativas como `sin coches de más de 4,7 metros` y guarda un máximo de longitud de 4,7 m, manteniendo los criterios de seguimiento previos.
  - `AdvisorCatalogMatcher`: aplica un límite estricto; 4,72 m queda excluido de un máximo de 4,7 m. Si hay un límite activo y la longitud no está disponible, el vehículo se excluye para no dar por cumplido un dato desconocido.
  - `SimilarCarsActivity`: pasa la longitud del catálogo (`lengthMm`) al matcher en metros.
  - `.github/tests/AdvisorCriteriaEngineSmokeTest.java`: añade regresión para parseo del seguimiento, conservación de SUV/presupuesto y exclusión de Skywell BE11 a 4,72 m, conservando un coche de 4,70 m.
- Commits de código/pruebas: `6a5c83ea6f49d5c2413904a7dde9540b1a380169`, `5f6c0c2d9b7a60c8b6128cb806c282dff10fbf8b`, `7f46bf7f3cdebc9d930bd5c6af031300b6c2f87b`, `e1ff5b33804b315c24288f102d0c3f1e8e4a4d87`, `903141abf82a0d6f456b94124df33ce958301719`.
- Estado de validación al documentar: código y prueba de regresión añadidos; aún no se ha confirmado el resultado de los workflows de GitHub Actions ni una compilación Android exitosa. Descargar y probar el APK de debug cuando el workflow de compilación termine.
- No se modifica `main` ni el catálogo protegido.
