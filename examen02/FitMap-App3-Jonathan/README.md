# FitMap App 3 — Competiciones y rutas

Aplicación Android nativa en Kotlin para Jonathan Pulig. Recibe las tiendas recomendadas desde la App 2 de Dennis, muestra competiciones locales, permite registrarse y dibuja rutas con polilíneas de colores y checkpoints numerados sobre OpenStreetMap.

## Contrato recibido desde App 2

Acciones aceptadas:

```text
com.fitmap.app3.RECEIVE_RECOMMENDATIONS
com.fitmap.app3.ACTION_TIENDAS_RECOMENDADAS
```

Extras compatibles:

- `fitmap.recommended_stores_json`: contrato completo recomendado.
- `tiendas_recomendadas`: contrato completo alternativo.
- `fitmap.stores`: arreglo JSON de tiendas para compatibilidad.

El contrato completo contiene `discipline`, `event` y `recommendedStores`. Si la aplicación se abre directamente, permanece esperando a App 2 sin inventar tiendas ni competiciones.

## Funcionalidades

- OpenStreetMap interactivo con zoom y ubicación del usuario.
- Recepción de tiendas y coordenadas del evento por Intent.
- Tres competiciones locales con distancia, horario y dificultad.
- Polilíneas verdes, naranjas o rojas según dificultad.
- Checkpoints numerados con información de control de tiempo.
- Selección y registro local en una competición.

## Preparación

Abre esta carpeta directamente en Android Studio. El proyecto usa SDK 35, Java 17 y osmdroid; no requiere una API key de mapas.
