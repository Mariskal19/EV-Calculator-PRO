# Auditoría y corrección del subsistema de divisas

**Fecha:** 8 de noviembre de 2026  
**Repositorio:** `Mariskal19/EV-Calculator-PRO`  
**Rama:** `develop/post-1.0.6`  
**Versión preservada:** `versionName 1.0.6`, `versionCode 62`  
**Alcance:** corrección de la descarga, validación, caché y conversión de cotizaciones ECB.

## 1. Diagnóstico inicial

La inspección previa a los cambios encontró los siguientes defectos en `CurrencyRateManager.java`:

1. La lectura del CSV no localizaba la columna `OBS_VALUE` por nombre. Omitía la cabecera mediante una comprobación textual y tomaba siempre el último campo de cada fila.
2. La separación con `split(",")` no interpretaba correctamente campos entrecomillados, comas dentro de campos ni comillas escapadas.
3. La actualización guardaba cualquier conjunto no vacío de cotizaciones. Una respuesta parcial podía sustituir una caché completa.
4. `refreshIfNeeded()` escribía la fecha del intento antes de descargar. Un fallo podía bloquear nuevos intentos hasta el día siguiente.
5. `rate()` devolvía `1.0` si faltaba una cotización o la caché no se podía leer. Eso convertía un dato ausente en una cotización aparentemente válida.
6. La validación solo comprobaba que la tasa fuese mayor que cero; no garantizaba que fuera finita.
7. `lastUpdate()` devolvía la fecha escrita por la implementación anterior, que podía corresponder a una actualización parcial y no a una sustitución completa y válida.

### Consumidores localizados

Se inspeccionaron las clases Java de producción de la rama:

- `AppMenuApplication.java`: llamada a `CurrencyRateManager.refreshIfNeeded()`.
- `ConfigurationActivity.java`: llamada a `CurrencyRateManager.refreshIfNeeded()`.
- `PersistentMainActivity.java`: llamadas a `CurrencyRateManager.refreshIfNeeded()`.

No se encontraron llamadas de producción a `rate()`, `convertFromEur()` ni `lastUpdate()`. `MainActivity` utiliza la preferencia de moneda para el símbolo y el formato de los importes, pero no invoca esos métodos de cotización. Por tanto, la API de conversión existe en el subsistema, pero no está conectada actualmente a una conversión automática de precios de catálogo o de la interfaz.

## 2. Evidencias y archivos afectados

| Archivo | Cambio |
|---|---|
| `app/src/main/java/com/evchargecalculator/CurrencyRateManager.java` | Gestión Android de preferencias, descarga HTTP, reintentos y API pública de cotización/conversión |
| `app/src/main/java/com/evchargecalculator/CurrencyRateRepository.java` | Nuevo núcleo Java independiente de Android para CSV, validación, caché y conversiones |
| `.github/tests/CurrencyRateRepositorySmokeTest.java` | Nuevo conjunto de regresión automatizado sin dependencias externas |
| `.github/workflows/android-lint.yml` | Ejecución de pruebas de divisas antes de Android Lint y `assembleDebug` |
| `AUDITORIA_Y_CORRECCION_DIVISAS.md` | Este informe |

No se añadieron dependencias de producción ni se modificó `app/build.gradle`.

## 3. Causas de los problemas

- El parser asumía una estructura CSV fija y que el valor observado siempre ocupaba la última columna.
- El proceso de actualización mezclaba el concepto de intento de descarga con el de actualización satisfactoria.
- La persistencia se hacía cuando había al menos una tasa, no cuando se disponía del conjunto completo esperado.
- El valor alternativo `1.0` ocultaba la ausencia de cotizaciones y podía producir resultados numéricos incorrectos sin avisar.
- La fecha de caché no estaba vinculada a la validez e integridad del conjunto almacenado.

## 4. Solución implementada

### CSV y observaciones

- Se identifica `OBS_VALUE` mediante la cabecera, independientemente del orden de las columnas.
- Se implementa lectura CSV con soporte para campos entrecomillados, comas internas, comillas dobles escapadas, CRLF, saltos de línea dentro de campos y BOM UTF-8 inicial.
- Se utiliza la observación final de la respuesta. Si su campo está vacío, se rechaza la respuesta en vez de reutilizar una observación anterior.
- Se rechazan valores vacíos, no numéricos, cero, negativos, `NaN`, infinitos y desbordamientos como `1e309`.

### Descarga y caché

- Se requieren las cinco cotizaciones: USD, GBP, CHF, CAD y AUD.
- Se valida el conjunto completo antes de escribirlo.
- La caché anterior no se modifica si falla una descarga, una respuesta HTTP, el parseo o la validación de cualquiera de las cinco monedas.
- La sustitución de las tasas y de la fecha se realiza en una única edición de SharedPreferences.
- Se elimina el bloqueo basado en la fecha del último intento. La fecha solo avanza después de una actualización completa y válida.
- Un `AtomicBoolean` evita descargas simultáneas en el mismo proceso y se libera al terminar, incluso si hay una excepción.
- `refreshAsync()` conserva su semántica de actualización forzada; `refreshIfNeeded()` decide si corresponde iniciar la actualización diaria.

### Semántica y conversiones

- `lastUpdate()` devuelve la fecha de la última sustitución completa y válida. Devuelve cadena vacía si no existe un conjunto completo y válido.
- EUR conserva una tasa de referencia exacta de `1.0`.
- `rate()` deja de devolver silenciosamente `1.0` cuando falta una cotización: lanza `IllegalStateException`. Un código no admitido provoca `IllegalArgumentException`.
- `convertFromEur()` exige una cotización válida.
- Se añade `convert()` para convertir entre dos divisas mediante EUR como base común.
- Los importes no finitos y los resultados de conversión no finitos se rechazan explícitamente.

## 5. Pruebas automatizadas y resultados reales

El workflow ampliado ejecuta el conjunto Java independiente y después `lintDebug` y `assembleDebug`.

### Ejecución final

**GitHub Actions Android Lint #60**  
Run: https://github.com/Mariskal19/EV-Calculator-PRO/actions/runs/42490692509  
Commit probado: `66a83a3550f0f07e1d1434718c40c17efaa2e721`  
Resultado global: **SUCCESS**.

- Pruebas de divisas: **42 comprobaciones correctas**.
- `:app:lintDebug`: **SUCCESS**.
- `:app:assembleDebug`: **SUCCESS**.
- APK Debug generada: `ev-calculator-pro-debug-60`, 7.604.001 bytes.
- Artifact ID APK: `13621154436`.
- Artifact ID informe Lint: `13621815463`.

APK Debug: https://github.com/Mariskal19/EV-Calculator-PRO/actions/runs/42490692509/artifacts/13621154436

### Informe Android Lint

El XML del artefacto `android-lint-report-60` se descargó y examinó:

- **6 advertencias, 0 errores**.
- 5 `IconLauncherShape`, correspondientes a los PNG de launcher ya conocidos.
- 1 `AppBundleLocaleChanges`, aviso conocido en `LanguageManager.java:57`.
- No aparecen nuevas incidencias relacionadas con divisas.

### Cobertura de las pruebas

- USD, GBP, CHF, CAD y AUD.
- Cabeceras reordenadas y localización de `OBS_VALUE` por nombre.
- Campos entrecomillados, comas internas y comillas escapadas.
- Campos vacíos, incluido un último registro vacío después de una observación anterior válida.
- Valores cero, negativos, inválidos, `NaN`, infinitos y desbordamientos.
- Fallo de descarga simulado como HTTP 503 y recuperación posterior.
- Actualización parcial y conservación de la caché anterior.
- Semántica de `lastUpdate()` y reintento en el mismo día después de un fallo.
- Conversiones EUR-divisa y entre divisas.
- Ausencia de cotización y rechazo de conversiones incorrectas.

La ejecución #59 también completó con éxito 41 comprobaciones, Lint y APK Debug. La #60 añade la comprobación específica del último registro vacío y vuelve a ejecutar toda la validación.

## 6. Versionado, rama y restricciones

Se verificó en `app/build.gradle`:

- `versionName = '1.0.6'`
- `versionCode = 62`

No se modificaron estos valores.

- No se modificó `main`.
- No se creó ninguna Release.
- No se publicó ningún APK/AAB.
- No se integraron cambios en otra rama.
- El APK Debug generado es exclusivamente de pruebas.

## 7. Limitaciones y cuestiones pendientes

1. El fallo HTTP se prueba mediante una fuente inyectada que lanza `IOException` simulando HTTP 503. El código de producción comprueba el estado HTTP y rechaza cualquier respuesta distinta de 200, pero no se ha ejecutado una prueba de integración contra un servidor HTTP local ni se ha simulado una indisponibilidad real del ECB.
2. El núcleo de parseo, caché y conversión está cubierto por pruebas Java. El adaptador Android de SharedPreferences se ha compilado y analizado con Lint, pero no cuenta todavía con pruebas instrumentadas específicas.
3. No se ha probado manualmente la APK Debug 60 en Redmi Note 13 Pro (Android 16) ni Redmi Note 7 (Android 10). La compilación CI no sustituye esas pruebas físicas.
4. Los métodos `rate()`, `convertFromEur()` y `lastUpdate()` no tienen consumidores actuales en producción. La corrección deja el subsistema preparado y seguro, pero no incorpora una conversión automática nueva a la interfaz.
5. La disponibilidad y frescura de las cotizaciones del ECB en producción dependen del servicio remoto. Ante fallos o ausencia de una observación final, se conserva la caché previa y no se marca el día como actualizado.

## 8. Hashes de commits

- Implementación inicial, pruebas y workflow: `6a3eaef06843eb317f268386a8465d9a12325eba`.
- Ajuste de observación final y semántica de actualización forzada: `906a0ebecbfe48ca60b193a66ad233a9fbdc8ec5`.
- Prueba del último registro vacío: `66a83a3550f0f07e1d1434718c40c17efaa2e721`.

## 9. Conclusión

**La corrección técnica y la validación automatizada pueden darse por completadas:** las 42 comprobaciones pasan, Android Lint termina sin errores y se genera correctamente la APK Debug, manteniendo versión, código y rama.

**El punto 1 no debe cerrarse todavía como validación funcional completa.** Falta que el usuario instale y pruebe la APK Debug 60 en ambos Redmi, especialmente el comportamiento de refresco y conservación de cotizaciones cuando no haya conexión. No se debe integrar ni publicar nada hasta revisar esas pruebas y recibir autorización expresa.
