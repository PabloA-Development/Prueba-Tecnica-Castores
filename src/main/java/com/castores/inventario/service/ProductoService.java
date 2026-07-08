package com.castores.inventario.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.castores.inventario.dto.ProductoForm;
import com.castores.inventario.entity.Producto;
import com.castores.inventario.exception.ProductoNoEncontradoException;
import com.castores.inventario.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> obtenerInventario() {
        return productoRepository.findAll();
    }

    public List<Producto> obtenerProductosActivos() {
        return productoRepository.findByEstatusTrue();
    }

    public Producto obtenerPorId(Integer idProducto) {
        return productoRepository.findById(idProducto)
                .orElseThrow(ProductoNoEncontradoException::new);
    }

    public Producto registrarProducto(ProductoForm form) {
        Producto producto = new Producto();
        producto.setNombre(form.getNombre());
        producto.setDescripcion(form.getDescripcion());
        producto.setCantidad(0);
        producto.setEstatus(true);
        producto.setFechaCreacion(LocalDateTime.now());
        return productoRepository.save(producto);
    }

    public void darDeBaja(Integer idProducto) {
        cambiarEstatus(idProducto, false);
    }

    public void reactivar(Integer idProducto) {
        cambiarEstatus(idProducto, true);
    }

    private void cambiarEstatus(Integer idProducto, boolean activo) {
        Producto producto = obtenerPorId(idProducto);
        producto.setEstatus(activo);
        productoRepository.save(producto);
    }
}
