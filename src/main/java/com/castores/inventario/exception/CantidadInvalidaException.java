package com.castores.inventario.exception;

public class CantidadInvalidaException extends RuntimeException {

    public CantidadInvalidaException() {
        super("La cantidad debe ser mayor que cero.");
    }
}
