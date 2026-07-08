package com.castores.inventario.exception;

public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException() {
        super("El producto solicitado no existe.");
    }
}
