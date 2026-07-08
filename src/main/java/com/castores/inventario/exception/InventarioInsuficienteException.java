package com.castores.inventario.exception;

public class InventarioInsuficienteException extends RuntimeException {

    public InventarioInsuficienteException() {
        super("No hay inventario suficiente para realizar la salida.");
    }
}
