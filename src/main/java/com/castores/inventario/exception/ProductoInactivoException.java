package com.castores.inventario.exception;

public class ProductoInactivoException extends RuntimeException {

    public ProductoInactivoException() {
        super("No se pueden realizar movimientos de salida sobre un producto inactivo.");
    }
}
