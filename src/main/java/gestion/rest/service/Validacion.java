package gestion.rest.service;

import gestion.rest.exception.BadRequestException;

/** Comprobaciones de texto compartidas por los servicios (longitudes de las columnas de la BD). */
final class Validacion {

    static final int MAX_TEXTO = 200;

    /** Columnas TEXT (65.535 bytes): con caracteres de hasta 4 bytes, esto nunca las desborda. */
    static final int MAX_DESCRIPCION = 10_000;

    private Validacion() {}

    /** Texto obligatorio: no vacío y de como máximo {@value #MAX_TEXTO} caracteres. */
    static void obligatorio(String valor, String etiqueta) throws BadRequestException {
        if (valor == null || valor.trim().isEmpty())
            throw new BadRequestException(etiqueta + " es obligatorio");
        maximo(valor, etiqueta);
    }

    /** Texto opcional: si viene, como máximo {@value #MAX_TEXTO} caracteres. */
    static void maximo(String valor, String etiqueta) throws BadRequestException {
        maximo(valor, etiqueta, MAX_TEXTO);
    }

    static void maximo(String valor, String etiqueta, int limite) throws BadRequestException {
        if (valor != null && valor.length() > limite)
            throw new BadRequestException(etiqueta + " no puede superar los " + limite + " caracteres");
    }
}
