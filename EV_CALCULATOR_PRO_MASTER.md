# EV Calculator PRO — MASTER

> Documento maestro del proyecto.  
> Última actualización: 01/10/2026.
>
> **Regla principal:** este archivo recoge el estado, decisiones y reglas de trabajo que deben conservarse al retomar el proyecto. No sustituye al código ni al catálogo; documenta cuál es la fuente de verdad de cada parte.

---

## 0. CURRENT STATE

- **Repositorio:** `Mariskal19/EV-Calculator-PRO`
- **Rama de trabajo principal:** `main`
- **Ramas stable:** no modificar sin autorización expresa.
- **Versión de referencia actual:** 1.0.4 / línea de trabajo posterior a los checkpoints de septiembre de 2026.
- **Google Play:** la publicación/revisión de producción seguía pendiente al último checkpoint conocido.
- **Catálogo:** objetivo operativo actual de **607 registros**.
- **Último estado de auditoría conocido:** se detectaron **676 registros** en el estado revisado, por lo que el objetivo de 607 todavía no debe considerarse certificado.
- **Duplicados:** la comprobación conocida no detectó duplicados comerciales según la clave `marca + modelo + año + versión`; debe repetirse la comprobación final antes de certificar los 607.
- **Auditoría final:** puntos 3–6 pendientes de certificación definitiva si no constan cerrados posteriormente.
- **Build final:** pendiente de ejecutar/verificar con el catálogo corregido.
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

**Objetivo: 607 registros certificados y sin duplicados.**

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

### Importante

El último estado conocido mostró **676 registros**, por lo que no debe afirmarse que el catálogo tiene 607 hasta realizar una comprobación real posterior.

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
2. confirmar 607;
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

- [ ] Ejecutar puntos 3–6 si aún no están certificados.
- [ ] Confirmar número final: **607**.
- [ ] Confirmar ausencia de IDs duplicados.
- [ ] Confirmar ausencia de duplicados lógicos.
- [ ] Revisar registros corregidos.
- [ ] Revisar dimensiones.
- [ ] Revisar batería/carga.
- [ ] Confirmar `auditDateTime`.

### Prioridad 2 — Build

- [ ] Lanzar Build EV Calculator PRO desde una rama/estado de trabajo, nunca desde stable sin autorización.
- [ ] Confirmar compilación.
- [ ] Confirmar APK/AAB.
- [ ] Confirmar catálogo incluido en el artefacto.
- [ ] Confirmar que no se empaquetó una versión anterior.

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

---

## PRINCIPIO DEL PROYECTO

**EV Calculator PRO debe avanzar mediante cambios verificables, auditados y recuperables.**

**El catálogo protegido no se sobrescribe.**

**Los builds deben demostrar que el código y el catálogo que hemos validado son los que realmente llegan al APK/AAB.**

**Stable no se toca sin autorización expresa.**
