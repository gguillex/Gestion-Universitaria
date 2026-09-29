---
name: Sistema de Gestión Universitaria
description: Almacén bien rotulado, traducido a sobrio: neutros fríos, tinta negra, rótulos condensados entre comillas y placas de etiqueta; el acceso es una etiqueta de bridas.
colors:
  fondo: "#f2f4f5"
  superficie: "#ffffff"
  placa: "#dde3e6"
  tinta: "#0c1114"
  nailon: "#161b1f"
  texto-suave: "#4b565d"
  inverso-suave: "#c3ccd1"
  linea-suave: "#ccd3d7"
  fila-hover: "#f6f8f9"
  seleccion: "#cfd8dc"
  accion: "#ff5a00"
  accion-hover: "#e85200"
  foco: "#d94a00"
typography:
  marca:
    fontFamily: "Big Shoulders Display, Arial Narrow, sans-serif"
    fontSize: "clamp(3.5rem, 1.5rem + 7vw, 6rem)"
    fontWeight: 900
    lineHeight: 0.86
    letterSpacing: "0"
    # en una columna (<= 860 px): clamp(2.75rem, 1rem + 9vw, 4.5rem)
  titulo:
    fontFamily: "Big Shoulders Display, Arial Narrow, sans-serif"
    fontSize: "clamp(2.5rem, 1.5rem + 3.6vw, 4.25rem)"
    fontWeight: 800
    lineHeight: 0.92
    letterSpacing: "0.005em"
  titulo-panel:
    fontFamily: "Big Shoulders Display, Arial Narrow, sans-serif"
    fontSize: "1.75rem"
    fontWeight: 800
    lineHeight: 1.1
    letterSpacing: "0.03em"
  titulo-vacio:
    fontFamily: "Big Shoulders Display, Arial Narrow, sans-serif"
    fontSize: "2rem"
    fontWeight: 800
    lineHeight: 1
    letterSpacing: "0.02em"
  marca-barra:
    fontFamily: "Big Shoulders Display, Arial Narrow, sans-serif"
    fontSize: "1.5rem"
    fontWeight: 800
    lineHeight: 1
    letterSpacing: "0.02em"
  rotulo:
    fontFamily: "Big Shoulders Display, Arial Narrow, sans-serif"
    fontSize: "1.0625rem"
    fontWeight: 600
    letterSpacing: "0.08em"
  enlace-barra:
    fontFamily: "Big Shoulders Display, Arial Narrow, sans-serif"
    fontSize: "1rem"
    fontWeight: 600
    letterSpacing: "0.05em"
  placa:
    fontFamily: "Big Shoulders Display, Arial Narrow, sans-serif"
    fontSize: "1.125rem"
    fontWeight: 700
    lineHeight: 1.35
  cuerpo:
    fontFamily: "Archivo, system-ui, Segoe UI, sans-serif"
    fontSize: "0.9375rem"
    fontWeight: 400
    lineHeight: 1.5
  cuerpo-destacado:
    fontFamily: "Archivo, system-ui, Segoe UI, sans-serif"
    fontSize: "1.0625rem"
    fontWeight: 700
    lineHeight: 1.5
  etiqueta-campo:
    fontFamily: "Archivo, system-ui, Segoe UI, sans-serif"
    fontSize: "0.8125rem"
    fontWeight: 600
    lineHeight: 1.5
  boton:
    fontFamily: "Archivo, system-ui, Segoe UI, sans-serif"
    fontSize: "0.8125rem"
    fontWeight: 800
    letterSpacing: "0.06em"
  boton-fila:
    fontFamily: "Archivo, system-ui, Segoe UI, sans-serif"
    fontSize: "0.6875rem"
    fontWeight: 700
    letterSpacing: "0.06em"
rounded:
  control: "2px"
  placa: "3px"
spacing:
  e-1: "4px"
  e-2: "8px"
  e-3: "12px"
  e-4: "16px"
  e-5: "24px"
  e-6: "32px"
  e-7: "48px"
  e-8: "64px"
components:
  boton-principal:
    backgroundColor: "{colors.accion}"
    textColor: "{colors.tinta}"
    typography: "{typography.boton}"
    rounded: "{rounded.control}"
    height: "44px"
    padding: "0 24px"
  boton-principal-hover:
    backgroundColor: "{colors.accion-hover}"
  boton-secundario:
    backgroundColor: "{colors.superficie}"
    textColor: "{colors.tinta}"
    rounded: "{rounded.control}"
    height: "44px"
    padding: "0 16px"
  boton-fila:
    backgroundColor: "{colors.superficie}"
    textColor: "{colors.tinta}"
    typography: "{typography.boton-fila}"
    rounded: "{rounded.control}"
    height: "32px"
    padding: "0 12px"
  boton-fila-hover:
    backgroundColor: "{colors.tinta}"
    textColor: "{colors.superficie}"
  cabecera-tabla:
    backgroundColor: "{colors.nailon}"
    textColor: "{colors.superficie}"
    typography: "{typography.rotulo}"
    padding: "12px 16px"
  placa-id:
    backgroundColor: "{colors.placa}"
    textColor: "{colors.tinta}"
    rounded: "{rounded.control}"
    typography: "{typography.placa}"
  badge:
    backgroundColor: "{colors.placa}"
    textColor: "{colors.tinta}"
    rounded: "{rounded.control}"
  badge-admin:
    backgroundColor: "{colors.tinta}"
    textColor: "{colors.superficie}"
  campo:
    backgroundColor: "{colors.superficie}"
    textColor: "{colors.tinta}"
    rounded: "{rounded.control}"
    height: "44px"
    padding: "8px 12px"
  topbar:
    backgroundColor: "{colors.nailon}"
    textColor: "{colors.superficie}"
    rounded: "{rounded.placa}"
    height: "56px"
    padding: "12px 24px"
  saltar-contenido:
    backgroundColor: "{colors.accion}"
    textColor: "{colors.tinta}"
    rounded: "{rounded.control}"
    height: "44px"
    padding: "0 16px"
  topbar-enlace-activo:
    backgroundColor: "{colors.accion}"
    textColor: "{colors.tinta}"
    typography: "{typography.enlace-barra}"
    rounded: "{rounded.control}"
    height: "36px"
  panel:
    backgroundColor: "{colors.superficie}"
    textColor: "{colors.tinta}"
    rounded: "{rounded.placa}"
    padding: "24px"
  panel-acceso-cabecera:
    backgroundColor: "{colors.nailon}"
    textColor: "{colors.superficie}"
    typography: "{typography.titulo-panel}"
    padding: "12px 24px 12px 58px"
---

# Design System: Sistema de Gestión Universitaria

## Overview

**Creative North Star: "El almacén bien rotulado"**

Cada zona de la aplicación se nombra con su rótulo literal, entre comillas, y cada registro lleva su placa de etiqueta. Es la gramática industrial (rótulos condensados en mayúsculas, placas, franja de peligro) traducida a sobrio por decisión del usuario: fondo claro, tinta negra, sin fotos ni franjas decorativas, esquinas casi rectas y bordes de tinta. La interfaz es de trabajo (Operate): administración y profesorado gestionando registros en escritorio y móvil.

La densidad es media y el orden es rígido: un solo ritmo de espaciado, un solo borde, una sola familia de controles. El color es casi ausente y los neutros son fríos (un matiz azulado, no grises puros); la jerarquía la dan el peso, la condensación de la display y el contraste tinta/blanco. Se rechaza el panel SaaS de tarjetas blancas con acento índigo.

El detalle que la firma es la etiqueta de bridas: el panel de acceso es una etiqueta de almacén colgada de una brida negra, con el agujero perforado en su cabecera; la marca de la barra repite ese agujero en pequeño.

**Key Characteristics:**
- Neutros fríos (concreto, blanco algodón, gris etiqueta, tinta y nailon) con un único color de señal, el naranja de seguridad.
- Rótulos en Big Shoulders Display condensada en mayúsculas; los de sección, entre comillas tipográficas.
- Bordes de 1,5 px en tinta; radios de 2 y 3 px.
- Franja diagonal negra/blanca reservada al peligro y al límite.
- Etiqueta de bridas en el acceso, con un único balanceo al cargar.
- Fuentes propias servidas desde /css/fonts/; sin recursos externos.
- Todo valor vive en variables de :root en css/style.css; los JSP no llevan estilo propio.

## Colors

Paleta de almacén de neutros fríos con un solo naranja de señal; el negro y el gris hacen todo el trabajo estructural.

### Primary
- **Naranja de seguridad** (`accion`, #ff5a00; hover `accion-hover`, #e85200): relleno de la acción principal activa, siempre con texto negro encima. Aparece como relleno del primer botón de la barra de acciones, del botón de envío, del enlace activo de la barra, de la acción de un estado vacío y del enlace «Saltar al contenido».
- **Naranja de foco** (`foco`, #d94a00): solo el anillo de foco de 3 px en campos y enlaces. Es un naranja más oscuro que el de acción porque #ff5a00 daba 2,83:1 sobre el suelo y WCAG 1.4.11 exige 3:1; `foco` da 3,86:1 sobre el suelo, 4,26:1 sobre blanco y 4,07:1 sobre el nailon.

### Neutral
- **Concreto** (`fondo`, #f2f4f5): suelo de la página; también se ve a través del agujero de la etiqueta.
- **Algodón blanco** (`superficie`, #ffffff): tablas, formularios, paneles, avisos.
- **Gris etiqueta** (`placa`, #dde3e6): solo fondo de placas (ID, rol, placa de datos, botón deshabilitado).
- **Tinta negra** (`tinta`, #0c1114): texto, bordes, placa de rol admin, brida de la etiqueta, relleno de hover en acciones de fila.
- **Negro nailon** (`nailon`, #161b1f): superficies oscuras: barra superior, cabecera de tabla, cabecera del panel de acceso.
- **Texto suave** (`texto-suave`, #4b565d): texto secundario y rótulos de campo (6,8:1 sobre el suelo).
- **Inverso suave** (`inverso-suave`, #c3ccd1): texto secundario sobre nailon.
- **Línea suave** (`linea-suave`, #ccd3d7): separadores entre filas.
- **Fila hover** (`fila-hover`, #f6f8f9) y **selección de texto** (`seleccion`, #cfd8dc): estados neutros, sin naranja.

### Named Rules
**The Solo Relleno Rule.** El naranja de acción (#ff5a00) es solo relleno de la acción activa. Nunca es texto, borde, icono ni decoración; tampoco es el color de la brida.
**The Foco Oscuro Rule.** El anillo de foco usa `foco` (#d94a00), no el naranja de acción: es el único naranja que puede dibujar un anillo, porque cumple 3:1 sobre suelo, blanco y nailon.
**The Franja De Peligro Rule.** La franja diagonal negra/blanca a 45° (10 px de periodo) significa peligro o límite: botón de borrar, aviso de error, campo inválido, marca del mensaje de campo, asignatura completa. No se usa como adorno.

## Typography

**Display Font:** Big Shoulders Display variable (peso 100-900), con fallback Arial Narrow, sans-serif.
**Body Font:** Archivo variable (peso 400-900), con fallback system-ui, Segoe UI, sans-serif.

**Character:** la display, estrecha y de hombros altos, da el rótulo industrial: marca, títulos, enlaces de barra, cabeceras de tabla, placas y rótulos de las placas móviles. Archivo en ancho normal queda para lo que se lee y se pulsa: cuerpo, campos, botones y rótulos de campo, ahora en frase normal.

### Hierarchy
- **Marca** (display 900, clamp(3.5rem, 1.5rem + 7vw, 6rem), 0.86, mayúsculas): solo el título de la hoja de acceso. En una columna (hasta 860 px) el tamaño es fluido, clamp(2.75rem, 1rem + 9vw, 4.5rem), para no desbordar a 320 px.
- **Título de sección** (display 800, clamp(2.5rem, 1.5rem + 3.6vw, 4.25rem), 0.92, mayúsculas): el h2 de cada página, siempre entre comillas.
- **Título de panel** (display 800, 1.75rem, 1.1, 0.03em): h2 del panel de acceso, sobre nailon; hereda las comillas del h2.
- **Título de estado vacío** (display 800, 2rem, 1, 0.02em, entre comillas).
- **Marca de barra** (display 800, 1.5rem, 0.02em, mayúsculas) y **enlaces de barra** (display 600, 1rem, 0.05em; el activo 700).
- **Rótulo** (display 600, 1.0625rem, 0.08em, mayúsculas): cabeceras de tabla y etiquetas de las placas móviles; la placa de datos usa 500 con 0.06em, y el rótulo de los avisos 800 a 1.1875rem.
- **Placa** (display 700, 1.125rem, cifras tabulares): identificador de registro; la placa de rol (badge) usa 700 a 0.9375rem con 0.08em.
- **Cuerpo** (Archivo 400, 0.9375rem, 1.5): texto, celdas.
- **Cuerpo destacado** (Archivo 700, 1.0625rem): celda principal en placas móviles.
- **Rótulo de campo** (Archivo 600, 0.8125rem, texto suave, frase normal).
- **Botón** (Archivo 800, 0.8125rem, 0.06em, mayúsculas): botón de envío; los de barra y pie de formulario usan 700 con 0.04em; los de fila, 700 a 0.6875rem.

### Named Rules
**The Rótulo Entre Comillas Rule.** Un nombre de zona (h2, título de estado vacío, cabecera de aviso) se escribe en mayúsculas de la display y entre comillas tipográficas “ ”, generadas con ::before/::after, no escritas en el marcado.
**The Display Es Rótulo Rule.** Big Shoulders Display solo para rótulos, marca, identificadores, cabeceras y placas: texto corto en mayúsculas; nunca para texto corrido, campos ni botones, que son Archivo.
**The Dieciséis Rule.** Los campos de formulario usan 1rem (16 px) para evitar el zoom automático en iOS.

## Layout

Página centrada de 1280 px máximo con relleno de 24/32 px (16 px bajo 720 px). Un solo ritmo de espaciado: 4, 8, 12, 16, 24, 32, 48, 64 px. Objetivo táctil mínimo de 44 px (`alto-control`); acciones de fila de 32 px en escritorio y 44 px en móvil. En la barra superior, los enlaces de sección y «Cerrar sesión» miden 36 px de alto en escritorio y 44 px a 720 px o menos.

Estructura de página interna: enlace «Saltar al contenido», barra superior, h2 con 48 px de margen superior, barra de acciones, avisos, tabla o formulario (máximo 480 px), todo dentro de `<main id="contenido" tabindex="-1">` (presente en las 14 vistas internas). El acceso usa cuadrícula de dos columnas (1.15fr y panel de 320-400 px, hueco de 48 px, máximo 1040 px), apilada bajo 860 px (columna de 440 px máximo, marca fluida); el panel deja 60 px sobre sí para la brida.

Puntos de ruptura observados: 1240 px (por encima la barra cabe en una fila; por debajo pasa a dos filas y los enlaces forman una tira desplazable con difuminado a la derecha), 860 px (acceso apilado), 720 px (relleno y márgenes reducidos, botones de barra a ancho completo, enlaces de la barra y «Cerrar sesión» a 44 px de alto), 640 px (las tablas con .tabla-placas se convierten en placas), 480 px (botones de formulario a 100%).

En móvil cada fila de una tabla-placas es una placa: borde de tinta, ID como placa en la esquina superior derecha con prefijo «ID», celda principal en 17 px/700, resto con rótulo de la display (data-label) y acciones a ancho completo tras una línea suave.

## Elevation & Depth

Sistema plano con una sola sombra estructural. Los paneles y formularios se separan por borde de tinta de 1,5 px; la tabla, la barra y los avisos no llevan sombra. La profundidad se transmite por el contraste nailon/blanco.

### Shadow Vocabulary
- **Placa** (`box-shadow: 0 1px 0 rgba(12,17,20,.06), 0 10px 24px -14px rgba(12,17,20,.35)`): solo formularios y el panel de acceso; desplazamiento con desenfoque suave.
- **Anillo de foco** (`box-shadow: 0 0 0 3px #d94a00`, variable `--anillo-foco` sobre `foco`): foco visible en enlaces y campos; `main` no dibuja anillo al recibir el foco. En botones de envío el foco es un doble anillo blanco 3 px + tinta 6 px.

### Named Rules
**The Plano Salvo Placa Rule.** Sin sombra en reposo salvo la de las placas de formulario y acceso; no se añaden sombras duras desplazadas.

## Shapes

Esquinas casi rectas: 2 px en controles, placas de ID, badges y botones; 3 px en paneles, tablas, avisos y barra. Bordes de 1,5 px en tinta para todo contenedor y control; 1 px en línea suave para separadores de fila. La franja diagonal es la geometría decorativa con significado. Otras siluetas funcionales: el indicador de campo inválido (cuadrado de 12 px con franja) y el agujero circular de la etiqueta de bridas.

## Components

### Buttons
- **Shape:** casi recto (2 px), borde de tinta 1,5 px, 44 px de alto, mayúsculas, Archivo.
- **Principal:** relleno naranja, texto negro, peso 800, padding 0 24 px. Hover a #e85200; active baja 1 px; deshabilitado en gris etiqueta. Lleva la flecha SVG por máscara (`.flecha`, 18x12) que avanza 4 px en hover/foco.
- **Secundario (barra de acciones y pie de formulario):** blanco con borde de tinta; hover a fila-hover. El primer botón de una barra de acciones (.toolbar) es naranja.
- **Fila:** botones de 32 px; editar en blanco que se invierte a tinta en hover; borrar con franja de 10 px a la izquierda que también se invierte en hover.

### Cards / Containers
- **Placa/Panel:** superficie blanca, borde de tinta, radio 3 px, sombra de placa, relleno 24 px.
- **Placa de datos:** rejilla de pares rótulo/valor en la display sobre gris etiqueta; el rótulo en texto suave con dos puntos, el valor en 700.

### Inputs / Fields
- **Style:** blanco, borde de tinta 1,5 px, radio 2 px, 44 px de alto; rótulo de campo en Archivo 600, 13 px, frase normal, sobre el campo.
- **Focus:** anillo de 3 px en naranja de foco (#d94a00), sin contorno.
- **Error:** el mensaje (.campo-error) está en el marcado y se muestra con :user-invalid tras la interacción; el campo gana una franja de 5 px en el borde inferior y el mensaje lleva un cuadrado con franja.

### Navigation
Barra nailon con marca de la display en mayúsculas precedida de un anillo blanco pequeño (el agujero de la etiqueta), enlaces de la display en mayúsculas, usuario con badge de rol y enlace de cierre. Enlace activo por aria-current="page": relleno naranja con texto negro. Hover: fondo blanco al 8%. Alto de enlaces y «Cerrar sesión»: 36 px, 44 px en móvil (720 px o menos). El primer elemento del menú es el enlace «Saltar al contenido». En una fila desde 1240 px; por debajo, dos filas con tira desplazable.

### Saltar al contenido
Enlace (`.saltar`) que lleva a `<main id="contenido" tabindex="-1">`. Fuera de pantalla en reposo (top -80 px); al recibir el foco baja a 12 px del borde superior con relleno naranja de acción, texto negro, borde de tinta 1,5 px, radio 2 px, 44 px de alto, Archivo 800 a 13 px. Es una acción activa, así que el relleno de acción es correcto; el foco visible lo da el anillo global.

### Etiqueta de bridas (componente firma)
El panel de acceso (.auth-panel) es una etiqueta de almacén: cabecera nailon con el agujero perforado a la izquierda (círculo que deja ver el concreto, con un anillo tenue de material), y una brida negra (tinta, nunca naranja) que sube desde el agujero, hecha con ::before (cinta de 6x78 px) y ::after (cabeza de 14x20 px). Se balancea una sola vez al cargar (1300 ms, 350 ms de retardo, punto de giro en el agujero) y queda quieta; con prefers-reduced-motion no se anima. El cuerpo del panel lleva el formulario sin borde propio y el botón naranja a ancho completo con la flecha a la derecha.

### Tablas
Cabecera nailon con rótulos de la display en blanco, filas blancas con línea suave, hover fila-hover, cifras tabulares, celda vacía con raya (—). Estado vacío: título entre comillas, texto suave y acción naranja.

### Avisos
.error y .ok: caja blanca con borde de tinta y rótulo “ERROR” / “HECHO” en la display; el error añade franja de 6 px en el borde superior. Llevan role="alert" y role="status" en las vistas.

### Ocupación
Barra `progress.ocupacion` (16 px, borde de tinta, relleno tinta) con texto de Archivo 600 al lado; cuando está llena el relleno es la franja.

## Do's and Don'ts

### Do:
- **Do** usar naranja de acción (#ff5a00) solo como relleno de la acción principal activa con texto negro, y `foco` (#d94a00) para todo anillo de foco.
- **Do** poner `<main id="contenido" tabindex="-1">` en cada vista interna y mantener el enlace «Saltar al contenido» como primer elemento del menú.
- **Do** reservar la franja diagonal para peligro y límite: borrar, error, campo inválido, asignatura completa.
- **Do** nombrar cada zona con un rótulo entre comillas generado por CSS, y dar a cada registro su placa de ID en la display.
- **Do** tomar toda medida de las variables de :root y reutilizar las clases existentes (.toolbar, .placa, .badge, .error, .ok, .tabla-placas, .acciones-form) antes de crear estilos por página.
- **Do** dar a cada tabla nueva data-label y las clases celda-id, celda-principal, celda-acciones para que sea placa en móvil.
- **Do** acompañar el estado con texto o forma, no solo con color; avisos con role="alert" o "status".
- **Do** desactivar el balanceo de la etiqueta con prefers-reduced-motion y no repetirlo tras la carga.

### Don't:
- **Don't** dibujar el foco con #ff5a00 (2,83:1 sobre el suelo) ni bajar `foco` de 3:1.
- **Don't** usar naranja como texto, borde o decoración, ni para varias acciones a la vez en una misma zona, ni para la brida.
- **Don't** usar la franja como adorno ni en superficies sin peligro.
- **Don't** usar la display en campos, botones ni texto corrido, ni volver a mayúsculas monoespaciadas para los rótulos de campo.
- **Don't** añadir fotos, ilustraciones, degradados decorativos ni fuentes cargadas de terceros.
- **Don't** volver al panel SaaS de tarjetas blancas con acento índigo.
- **Don't** añadir sombras duras desplazadas ni radios superiores a 3 px.
- **Don't** poner `<style>` ni scriptlets en los JSP.
