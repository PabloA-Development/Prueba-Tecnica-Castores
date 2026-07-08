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
import com.castores.inventario.exception.ProductoNoEncontradoException;
import com.castores.inventario.repository.MovimientoRepository;
import com.castores.inventario.service.InventarioService;
import com.castores.inventario.service.ProductoService;

@SpringBootTest
@Transactional
class InventarioServiceTest {

    private static final String ADMIN = "admin@castores.local";

    @Autowired
    private ProductoService productoService;

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Test
    void productoNuevoIniciaEnCeroYActivo() {
        ProductoForm form = new ProductoForm();
        form.setNombre("Producto Test");
        form.setDescripcion("desc");

        Producto creado = productoService.registrarProducto(form);

        assertThat(creado.getIdProducto()).isNotNull();
        assertThat(creado.getCantidad()).isZero();
        assertThat(creado.isEstatus()).isTrue();
        assertThat(creado.getFechaCreacion()).isNotNull();
    }

    @Test
    void entradaSumaStockYRegistraMovimientoConUsuarioYFecha() {
        Producto p = productoService.registrarProducto(nuevoForm("Para entrada"));
        long movimientosAntes = movimientoRepository.count();

        inventarioService.agregarExistencias(p.getIdProducto(), 5, ADMIN);

        Producto actualizado = productoService.obtenerPorId(p.getIdProducto());
        assertThat(actualizado.getCantidad()).isEqualTo(5);
        assertThat(movimientoRepository.count()).isEqualTo(movimientosAntes + 1);

        Movimiento mov = ultimoMovimientoDe(p.getIdProducto());
        assertThat(mov.getTipoMovimiento()).isEqualTo(TipoMovimiento.ENTRADA);
        assertThat(mov.getCantidad()).isEqualTo(5);
        assertThat(mov.getUsuario().getCorreo()).isEqualTo(ADMIN);
        assertThat(mov.getFechaMovimiento()).isNotNull();
    }

    @Test
    void entradaConCantidadInvalidaFalla() {
        Producto p = productoService.registrarProducto(nuevoForm("Cantidad mala"));
        assertThatThrownBy(() -> inventarioService.agregarExistencias(p.getIdProducto(), 0, ADMIN))
                .isInstanceOf(CantidadInvalidaException.class);
        assertThatThrownBy(() -> inventarioService.agregarExistencias(p.getIdProducto(), -3, ADMIN))
                .isInstanceOf(CantidadInvalidaException.class);
    }

    @Test
    void entradaSobreProductoInexistenteFalla() {
        assertThatThrownBy(() -> inventarioService.agregarExistencias(999999, 5, ADMIN))
                .isInstanceOf(ProductoNoEncontradoException.class);
    }

    @Test
    void bajaYReactivacionCambianEstatusSinEliminar() {
        Producto p = productoService.registrarProducto(nuevoForm("Baja/alta"));

        productoService.darDeBaja(p.getIdProducto());
        assertThat(productoService.obtenerPorId(p.getIdProducto()).isEstatus()).isFalse();

        productoService.reactivar(p.getIdProducto());
        assertThat(productoService.obtenerPorId(p.getIdProducto()).isEstatus()).isTrue();
    }

    private ProductoForm nuevoForm(String nombre) {
        ProductoForm form = new ProductoForm();
        form.setNombre(nombre);
        return form;
    }

    private Movimiento ultimoMovimientoDe(Integer idProducto) {
        List<Movimiento> movs = movimientoRepository.findByTipoMovimientoOrderByFechaMovimientoDesc(
                TipoMovimiento.ENTRADA);
        return movs.stream()
                .filter(m -> m.getProducto().getIdProducto().equals(idProducto))
                .findFirst()
                .orElseThrow();
    }
}
