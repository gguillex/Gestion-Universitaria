---
name: Sistema de Gestión Universitaria
description: Almacén bien rotulado, traducido a sobrio: fondo claro, tinta negra, rótulos entre comillas y placas de etiqueta.
colors:
  fondo: "#f7f7f7"
  superficie: "#ffffff"
  placa: "#e2e2e2"
  tinta: "#0a0a0a"
  nailon: "#1a1a1a"
  texto-suave: "#4a4a4a"
  inverso-suave: "#c9c9c9"
  linea-suave: "#cfcfcf"
  fila-hover: "#f5f5f5"
  seleccion: "#d9d9d9"
  accion: "#ff5a00"
  accion-hover: "#e85200"
typography:
  marca:
    fontFamily: "Archivo, system-ui, Segoe UI, sans-serif"
    fontSize: "clamp(3rem, 1.6rem + 6vw, 6rem)"
    fontWeight: 900
    lineHeight: 0.88
    letterSpacing: "-0.015em"
    fontVariation: "font-stretch 68%"
  titulo:
    fontFamily: "Archivo, system-ui, Segoe UI, sans-serif"
    fontSize: "clamp(2.25rem, 1.4rem + 3.2vw, 3.75rem)"
    fontWeight: 800
    lineHeight: 0.95
    letterSpacing: "-0.01em"
    fontVariation: "font-stretch 68%"
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
  boton:
    fontFamily: "Archivo, system-ui, Segoe UI, sans-serif"
    fontSize: "0.8125rem"
    fontWeight: 800
    letterSpacing: "0.06em"
  rotulo-mono:
    fontFamily: "Chivo Mono, ui-monospace, Cascadia Mono, Consolas, monospace"
    fontSize: "0.6875rem"
    fontWeight: 500
    letterSpacing: "0.08em"
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
    rounded: "{rounded.control}"
    height: "32px"
    padding: "0 12px"
  boton-fila-hover:
    backgroundColor: "{colors.tinta}"
    textColor: "{colors.superficie}"
  cabecera-tabla:
    backgroundColor: "{colors.nailon}"
    textColor: "{colors.superficie}"
    typography: "{typography.rotulo-mono}"
    padding: "12px 16px"
  placa-id:
    backgroundColor: "{colors.placa}"
    textColor: "{colors.tinta}"
    rounded: "{rounded.control}"
    typography: "{typography.rotulo-mono}"
  badge:
    backgroundColor: "{colors.placa}"
    textColor: "{colors.tinta}"
    rounded: "{rounded.control}"
    typography: "{typography.rotulo-mono}"
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
  topbar-enlace-activo:
    backgroundColor: "{colors.accion}"
    textColor: "{colors.tinta}"
    rounded: "{rounded.control}"
    height: "36px"
  panel:
    backgroundColor: "{colors.superficie}"
    textColor: "{colors.tinta}"
    rounded: "{rounded.placa}"
    padding: "24px"
---

# Design System: Sistema de Gestión Universitaria

## Overview

**Creative North Star: "El almacén bien rotulado"**

Cada zona de la aplicación se nombra con su rótulo literal, entre comillas, y cada registro lleva su placa de etiqueta. Es la gramática industrial (rótulos condensados en mayúsculas, placas, franja de peligro) traducida a sobrio por decisión del usuario: fondo claro, tinta negra, sin fotos ni franjas decorativas, esquinas casi rectas y bordes de tinta. La interfaz es de trabajo (Operate): administración y profesorado gestionando registros en escritorio y móvil.

La densidad es media y el orden es rígido: un solo ritmo de espaciado, un solo borde, una sola familia de controles. El color es casi ausente; la jerarquía la dan el peso, el ancho condensado y el contraste tinta/blanco. Se rechaza el panel SaaS de tarjetas blancas con acento índigo.

**Key Characteristics:**
- Paleta neutra (blanco algodón, tinta, gris etiqueta) con un único color de señal, el naranja de seguridad.
- Rótulos de sección en mayúsculas condensadas entre comillas tipográficas.
- Bordes de 1,5 px en tinta; radios de 2 y 3 px.
- Franja diagonal negra/blanca reservada al peligro y al límite.
- Fuentes propias servidas desde /css/fonts/; sin recursos externos.
- Todo valor vive en variables de :root en css/style.css; los JSP no llevan estilo propio.

## Colors

Paleta acromática de almacén con un solo naranja de señal; el negro y el gris hacen todo el trabajo estructural.

### Primary
- **Naranja de seguridad** (`accion`, #ff5a00; hover `accion-hover`, #e85200): relleno de la acción principal activa, siempre con texto negro encima. Aparece como relleno del primer botón de la barra de acciones, del botón de envío, del enlace activo de la barra y de la acción de un estado vacío; y como anillo de foco de 3 px en campos y enlaces.

### Neutral
- **Blanco algodón neutro** (`fondo`, #f7f7f7): suelo de la página.
- **Algodón blanco** (`superficie`, #ffffff): tablas, formularios, paneles, avisos.
- **Gris etiqueta** (`placa`, #e2e2e2): solo fondo de placas (ID, rol, placa de datos, botón deshabilitado).
- **Tinta negra** (`tinta`, #0a0a0a): texto, bordes, placa de rol admin, relleno de hover en acciones de fila.
- **Negro nailon** (`nailon`, #1a1a1a): superficies oscuras: barra superior, cabecera de tabla, cabecera del panel de acceso.
- **Texto suave** (`texto-suave`, #4a4a4a): texto secundario y rótulos de campo (7:1 sobre blanco).
- **Inverso suave** (`inverso-suave`, #c9c9c9): texto secundario sobre nailon.
- **Línea suave** (`linea-suave`, #cfcfcf): separadores entre filas.
- **Fila hover** (`fila-hover`, #f5f5f5) y **selección de texto** (`seleccion`, #d9d9d9): estados neutros, sin naranja.

### Named Rules
**The Solo Relleno Rule.** El naranja es relleno de la acción activa o anillo de foco. Nunca es texto, borde, icono ni decoración.
**The Franja De Peligro Rule.** La franja diagonal negra/blanca a 45° (10 px de periodo) significa peligro o límite: botón de borrar, aviso de error, campo inválido, marca del mensaje de campo, asignatura completa. No se usa como adorno.

## Typography

**Display/Body Font:** Archivo variable (ejes de peso 400-900 y de ancho 62%-125%), con fallback system-ui, Segoe UI, sans-serif.
**Label/Mono Font:** Chivo Mono (400-600), con fallback ui-monospace, Cascadia Mono, Consolas.

**Character:** Archivo condensado (ancho 68%) en mayúsculas pesadas da el rótulo industrial; Chivo Mono da las etiquetas de campo, cabeceras y placas. El cuerpo es Archivo normal, legible y sobrio.

### Hierarchy
- **Marca** (900, clamp(3rem, 1.6rem + 6vw, 6rem), 0.88): solo el título de la hoja de acceso.
- **Título de sección** (800, clamp(2.25rem, 1.4rem + 3.2vw, 3.75rem), 0.95, mayúsculas, ancho 68%): el h2 de cada página, siempre entre comillas.
- **Título de panel** (800, 1.25rem, ancho 68%): h2 del panel de acceso, sobre nailon; hereda las comillas del h2.
- **Cuerpo** (400, 0.9375rem, 1.5): texto, celdas.
- **Cuerpo destacado** (700, 1.0625rem): celda principal en placas móviles.
- **Botón** (800, 0.8125rem, 0.06em, mayúsculas): botón de envío; los botones de barra y de formulario usan 700 con 0.04em.
- **Rótulo mono** (500, 0.6875rem, 0.08em, mayúsculas): cabeceras de tabla, etiquetas de campo, enlaces de la barra, placas.

### Named Rules
**The Rótulo Entre Comillas Rule.** Un nombre de zona (h2, título de estado vacío, cabecera de aviso) se escribe en mayúsculas condensadas y entre comillas tipográficas “ ”, generadas con ::before/::after, no escritas en el marcado.
**The Mono Es Dato Rule.** Chivo Mono solo para identificadores, rótulos de campo, cabeceras y placas; nunca para texto corrido.
**The Dieciséis Rule.** Los campos de formulario usan 1rem (16 px) para evitar el zoom automático en iOS.

## Layout

Página centrada de 1280 px máximo con relleno de 24/32 px (16 px bajo 720 px). Un solo ritmo de espaciado: 4, 8, 12, 16, 24, 32, 48, 64 px. Objetivo táctil mínimo de 44 px (`alto-control`); acciones de fila de 32 px en escritorio y 44 px en móvil.

Estructura de página interna: barra superior, h2 con 48 px de margen superior, barra de acciones, avisos, tabla o formulario (máximo 480 px). La acceso usa cuadrícula de dos columnas (1.15fr y panel de 320-400 px, hueco de 48 px, máximo 1040 px), apilada bajo 860 px.

Puntos de ruptura observados: 1240 px (la barra pasa a dos filas y los enlaces forman una tira desplazable con difuminado a la derecha; el JSP de menú centra el enlace activo), 860 px (acceso apilado), 720 px (relleno y márgenes reducidos, botones de barra a ancho completo), 640 px (las tablas con .tabla-placas se convierten en placas), 480 px (botones de formulario a 100%).

En móvil cada fila de una tabla-placas es una placa: borde de tinta, ID como placa en la esquina superior derecha con prefijo «ID», celda principal en 17 px/700, resto con etiqueta mono (data-label) y acciones a ancho completo tras una línea suave.

## Elevation & Depth

Sistema plano con una sola sombra estructural. Los paneles y formularios se separan por borde de tinta de 1,5 px; la tabla, la barra y los avisos no llevan sombra. La profundidad se transmite por el contraste nailon/blanco.

### Shadow Vocabulary
- **Placa** (`box-shadow: 0 1px 0 rgba(10,10,10,.06), 0 10px 24px -14px rgba(10,10,10,.35)`): solo formularios y el panel de acceso; desplazamiento con desenfoque suave.
- **Anillo de foco** (`box-shadow: 0 0 0 3px #ff5a00`): foco visible en enlaces y campos. En botones de envío el foco es un doble anillo blanco 3 px + tinta 6 px.

### Named Rules
**The Plano Salvo Placa Rule.** Sin sombra en reposo salvo la de las placas de formulario y acceso; no se añaden sombras duras desplazadas.

## Shapes

Esquinas casi rectas: 2 px en controles, placas de ID, badges y botones; 3 px en paneles, tablas, avisos y barra. Bordes de 1,5 px en tinta para todo contenedor y control; 1 px en línea suave para separadores de fila. La franja diagonal es la única geometría decorativa con significado. Excepción funcional: el indicador de campo inválido es un cuadrado de 12 px con franja.

## Components

### Buttons
- **Shape:** casi recto (2 px), borde de tinta 1,5 px, 44 px de alto, mayúsculas.
- **Principal:** relleno naranja, texto negro, peso 800, padding 0 24 px. Hover a #e85200; active baja 1 px; deshabilitado en gris etiqueta. Lleva la flecha SVG por máscara (`.flecha`, 18x12) que avanza 4 px en hover/foco.
- **Secundario (barra de acciones y pie de formulario):** blanco con borde de tinta; hover a fila-hover. El primer botón de una barra de acciones (.toolbar) es naranja.
- **Fila:** botones de 32 px; editar en blanco que se invierte a tinta en hover; borrar con franja de 10 px a la izquierda que también se invierte en hover.

### Cards / Containers
- **Placa/Panel:** superficie blanca, borde de tinta, radio 3 px, sombra de placa, relleno 24 px. El panel de acceso lleva cabecera nailon con el título en blanco.
- **Placa de datos:** rejilla de pares rótulo/valor en mono sobre gris etiqueta.

### Inputs / Fields
- **Style:** blanco, borde de tinta 1,5 px, radio 2 px, 44 px de alto; rótulo mono en mayúsculas sobre el campo.
- **Focus:** anillo naranja de 3 px, sin contorno.
- **Error:** el mensaje (.campo-error) está en el marcado y se muestra con :user-invalid tras la interacción; el campo gana una franja de 5 px en el borde inferior y el mensaje lleva un cuadrado con franja.

### Navigation
Barra nailon con marca en mayúsculas condensadas, enlaces en mono, usuario con badge de rol y enlace de cierre. Enlace activo por aria-current="page": relleno naranja con texto negro. Hover: fondo blanco al 8%. Bajo 1240 px, dos filas con tira desplazable.

### Tablas
Cabecera nailon con rótulos mono en blanco, filas blancas con línea suave, hover fila-hover, cifras tabulares, celda vacía con raya (—). Estado vacío: título entre comillas, texto suave y acción naranja.

### Avisos
.error y .ok: caja blanca con borde de tinta y rótulo “ERROR” / “HECHO” en mayúsculas condensadas; el error añade franja de 6 px en el borde superior. Llevan role="alert" y role="status" en las vistas.

### Ocupación
Barra `progress.ocupacion` (16 px, borde de tinta, relleno tinta) con texto mono al lado; cuando está llena el relleno es la franja.

## Do's and Don'ts

### Do:
- **Do** usar naranja (#ff5a00) solo como relleno de la acción principal activa con texto negro, y como anillo de foco.
- **Do** reservar la franja diagonal para peligro y límite: borrar, error, campo inválido, asignatura completa.
- **Do** nombrar cada zona con un rótulo entre comillas generado por CSS, y dar a cada registro su placa de ID en mono.
- **Do** tomar toda medida de las variables de :root y reutilizar las clases existentes (.toolbar, .placa, .badge, .error, .ok, .tabla-placas, .acciones-form) antes de crear estilos por página.
- **Do** dar a cada tabla nueva data-label y las clases celda-id, celda-principal, celda-acciones para que sea placa en móvil.
- **Do** acompañar el estado con texto o forma, no solo con color; avisos con role="alert" o "status".

### Don't:
- **Don't** usar naranja como texto, borde o decoración, ni para varias acciones a la vez en una misma zona.
- **Don't** usar la franja como adorno ni en superficies sin peligro.
- **Don't** añadir fotos, ilustraciones, degradados decorativos ni fuentes de terceros.
- **Don't** volver al panel SaaS de tarjetas blancas con acento índigo.
- **Don't** añadir sombras duras desplazadas ni radios superiores a 3 px.
- **Don't** poner `<style>` ni scriptlets en los JSP.
