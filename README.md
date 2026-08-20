# EV Charge Calculator v1.0.15

Android EV charging calculator.

## Cambios de esta versión
- Títulos de las tarjetas sin iconos, con mayor separación inferior y espaciado uniforme.
- Primera barra de porcentaje de Batería: margen horizontal aumentado para que el indicador quede completamente visible en 0 % y 100 %.
- Segunda barra de Batería: mantiene el tramo coloreado únicamente entre el porcentaje actual y el objetivo.
- Hora Salida: el campo editable queda en la misma línea que el título, en negrita y más visible; se elimina el texto duplicado “Hora Salida”.
- Hora Salida: usa automáticamente la hora actual del sistema para calcular el tiempo disponible.
- Hora Salida: resultado visual destacado con fondo cobalto cuando se llega a tiempo y fondo rojizo de alerta cuando no se llega a tiempo.
- Cuando se llega a tiempo, muestra un bloque cobalto redondeado con reloj grande, “Hora Inicio Recomendada” y la hora en una sola fila.
- Cuando no se llega a tiempo, muestra un bloque rojizo redondeado con ⚠️ grande a la izquierda, “No llegas a tiempo” y “Faltan…” en dos filas; sin una exclamación adicional pequeña.
- Actualizado el workflow de GitHub Actions para utilizar versiones actuales de las acciones.
- Soporte de coma decimal en la interfaz; también se aceptan puntos al introducir datos.
- Pie de pantalla: se añade margen inferior para respetar la zona de navegación del teléfono.
- Pie de pantalla: `Powered by EV Charge Calculator · v1.0.15`.

- Corrección de la fila de “Hora Salida” para evitar que el texto quede cortado verticalmente.
- Estado de error > 24 h: utiliza el mismo formato rojo de dos líneas y el ⚠️ grande a la izquierda, sin exclamación pequeña adicional.

## Archivos de entrega
- ZIP: `EVChargeCalculator-v1.0.15.zip`
- APK: `EVChargeCalculator-v1.0.15.apk`
- Artefacto de GitHub Actions: `EVChargeCalculator-v1.0.15`
