# Revisión catálogo ES 2024–2026 — segunda pasada

Fecha: 2026-09-09

Objetivo: segunda revisión de las 17 marcas restantes tras XPeng y MINI, respetando **MERCADO → MARCA → MODELO → AÑO/PERÍODO → VERSIÓN → CONFIGURACIÓN TÉCNICA**.

## Incorporaciones verificadas en esta pasada

Se han añadido a `vehicle_market_additions.json` únicamente configuraciones con respaldo técnico suficiente:

- **BYD**: ATTO 2 Active Urban Edition; DOLPHIN SURF Active, Boost y Comfort (2025).
- **Hyundai**: IONIQ 9 Long Range RWD, Long Range AWD y Long Range Performance AWD (2025).
- **Kia**: EV4 Hatchback y EV4 Fastback, con baterías 58,3 y 81,4 kWh (2026).
- **Volvo**: EX90 Single Motor, Twin Motor y Twin Motor Performance (2025).

Los datos técnicos se han tomado de documentación de fabricante/mercado español y se han mantenido separados cuando cambia batería, potencia, tracción o carrocería.

## Marcas que siguen requiriendo revisión específica

- Audi: comprobar históricamente Q4/Q6/A6 y cambios de configuración por periodo.
- BMW: revisar iX1, iX2, i4, i5, i7 e iX y sus cambios por año.
- CUPRA: revisar Born y Tavascan además de Raval.
- Ford: revisar Explorer, Capri y evolución del Mustang Mach-E.
- Jeep: revisar Avenger y nuevas incorporaciones eléctricas 2025–2026.
- Leapmotor: revisar T03 y C10 además de B10.
- Mercedes-Benz: revisar EQA/EQB/EQE/EQS y nuevas generaciones CLA/GLB/GLC.
- Opel: revisar Corsa Electric, Mokka Electric, Astra Electric y Grandland Electric.
- Renault: revisar Megane, Scenic, Renault 5 y Renault 4 respetando la fecha de llegada a España.
- Škoda: revisar Enyaq, Enyaq Coupé y Elroq por periodo.
- Toyota: revisar bZ4X y las nuevas propuestas eléctricas 2025–2026.
- Volkswagen: revisar ID.3, ID.4, ID.5, ID.7 e ID. Buzz por periodo y batería.
- Tesla: revisar Model 3 y Model Y por cambios técnicos de 2024–2026.

## Criterio

No se han añadido configuraciones solo para aumentar el número de vehículos. Si una fecha española, batería, potencia o autonomía histórica no queda suficientemente acreditada, permanece pendiente de revisión en lugar de inventarla.

La fuente única de runtime continúa siendo `catalog_es_2024_2026.json`; las incorporaciones entran mediante el generador y no mediante cambios directos en Java.
