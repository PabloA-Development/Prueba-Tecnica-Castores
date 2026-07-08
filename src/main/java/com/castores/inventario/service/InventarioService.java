package com.castores.inventario.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.castores.inventario.entity.Movimiento;
import com.castores.inventario.entity.Producto;
import com.castores.inventario.entity.TipoMovimiento;
import com.castores.inventario.entity.Usuario;
import com.castores.inventario.exception.CantidadInvalidaException;
import com.castores.inventario.exception.InventarioInsuficienteException;
import com.castores.inventario.exception.ProductoInactivoException;
import com.castores.inventario.exception.ProductoNoEncontradoException;
import com.castores.inventario.repository.MovimientoRepository;
import com.castores.inventario.repository.ProductoRepository;
import com.castores.inventario.repository.UsuarioRepository;

@Service
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;

    public InventarioService(ProductoRepository productoRepository,
                             MovimientoRepository movimientoRepository,
                             UsuarioRepository usuarioRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void agregarExistencias(Integer idProducto, Integer cantidad, String correoUsuario) {
        validarCantidad(cantidad);

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(ProductoNoEncontradoException::new);
        Usuario responsable = obtenerUsuario(correoUsuario);

        producto.setCantidad(producto.getCantidad() + cantidad);
        productoRepository.save(producto);

        registrarMovimiento(TipoMovimiento.ENTRADA, producto, responsable, cantidad);
    }

    @Transactional
    public void realizarSalida(Integer idProducto, Integer cantidad, String correoUsuario) {
        validarCantidad(cantidad);

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(ProductoNoEncontradoException::new);

        if (!producto.isEstatus()) {
            throw new ProductoInactivoException();
        }
        if (cantidad > producto.getCantidad()) {
            throw new InventarioInsuficienteException();
        }

        Usuario responsable = obtenerUsuario(correoUsuario);

        producto.setCantidad(producto.getCantidad() - cantidad);
        productoRepository.save(producto);

        registrarMovimiento(TipoMovimiento.SALIDA, producto, responsable, cantidad);
    }

    private void validarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new CantidadInvalidaException();
        }
    }

    private Usuario obtenerUsuario(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalStateException(
                        "Usuario autenticado no encontrado: " + correo));
    }

    private void registrarMovimiento(TipoMovimiento tipo, Producto producto,
                                     Usuario usuario, Integer cantidad) {
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento(tipo);
        movimiento.setProducto(producto);
        movimiento.setUsuario(usuario);
        movimiento.setCantidad(cantidad);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimientoRepository.save(movimiento);
    }
}
