package com.castores.inventario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.castores.inventario.dto.ProductoForm;
import com.castores.inventario.entity.Movimiento;
import com.castores.inventario.entity.Producto;
import com.castores.inventario.entity.TipoMovimiento;
import com.castores.inventario.exception.CantidadInvalidaException;
import com.castores.inventario.exception.InventarioInsuficienteException;
import com.castores.inventario.exception.ProductoInactivoException;
import com.castores.inventario.repository.MovimientoRepository;
import com.castores.inventario.service.InventarioService;
import com.castores.inventario.service.ProductoService;

@SpringBootTest
@Transactional
class SalidaServiceTest {

    private static final String ADMIN = "admin@castores.local";
    private static final String ALMACENISTA = "almacen@castores.local";

    @Autowired
    private ProductoService productoService;

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private MovimientoRepository movimientoRepository;

    private Producto productoConStock(String nombre, int stock) {
        ProductoForm form = new ProductoForm();
        form.setNombre(nombre);
        Producto p = productoService.registrarProducto(form);
        inventarioService.agregarExistencias(p.getIdProducto(), stock, ADMIN);
        return productoService.obtenerPorId(p.getIdProducto());
    }

    @Test
    void salidaRestaStockYRegistraMovimientoConUsuario() {
        Producto p = productoConStock("Salida OK", 10);

        inventarioService.realizarSalida(p.getIdProducto(), 4, ALMACENISTA);

        assertThat(productoService.obtenerPorId(p.getIdProducto()).getCantidad()).isEqualTo(6);

        Movimiento mov = ultimaSalidaDe(p.getIdProducto());
        assertThat(mov.getTipoMovimiento()).isEqualTo(TipoMovimiento.SALIDA);
        assertThat(mov.getCantidad()).isEqualTo(4);
        assertThat(mov.getUsuario().getCorreo()).isEqualTo(ALMACENISTA);
        assertThat(mov.getFechaMovimiento()).isNotNull();
    }

    @Test
    void salidaMayorAlStockFallaYNoModificaInventario() {
        Producto p = productoConStock("Sin suficiente", 3);

        assertThatThrownBy(() -> inventarioService.realizarSalida(p.getIdProducto(), 5, ALMACENISTA))
                .isInstanceOf(InventarioInsuficienteException.class);

        assertThat(productoService.obtenerPorId(p.getIdProducto()).getCantidad()).isEqualTo(3);
    }

    @Test
    void salidaSobreProductoInactivoFalla() {
        Producto p = productoConStock("Inactivo", 5);
        productoService.darDeBaja(p.getIdProducto());

        assertThatThrownBy(() -> inventarioService.realizarSalida(p.getIdProducto(), 1, ALMACENISTA))
                .isInstanceOf(ProductoInactivoException.class);
    }

    @Test
    void salidaConCantidadInvalidaFalla() {
        Producto p = productoConStock("Cantidad mala", 5);
        assertThatThrownBy(() -> inventarioService.realizarSalida(p.getIdProducto(), 0, ALMACENISTA))
                .isInstanceOf(CantidadInvalidaException.class);
    }

    private Movimiento ultimaSalidaDe(Integer idProducto) {
        List<Movimiento> movs = movimientoRepository.findByTipoMovimientoOrderByFechaMovimientoDesc(
                TipoMovimiento.SALIDA);
        return movs.stream()
                .filter(m -> m.getProducto().getIdProducto().equals(idProducto))
                .findFirst()
                .orElseThrow();
    }
}
