package com.castores.inventario;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.castores.inventario.dto.ProductoForm;
import com.castores.inventario.entity.Movimiento;
import com.castores.inventario.entity.Producto;
import com.castores.inventario.entity.TipoMovimiento;
import com.castores.inventario.service.InventarioService;
import com.castores.inventario.service.MovimientoService;
import com.castores.inventario.service.ProductoService;

@SpringBootTest
@Transactional
class MovimientoServiceTest {

    private static final String ADMIN = "admin@castores.local";
    private static final String ALMACENISTA = "almacen@castores.local";

    @Autowired
    private ProductoService productoService;

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private MovimientoService movimientoService;

    private Producto generarMovimientos() {
        ProductoForm form = new ProductoForm();
        form.setNombre("Historial Test");
        Producto p = productoService.registrarProducto(form);
        inventarioService.agregarExistencias(p.getIdProducto(), 10, ADMIN);
        inventarioService.realizarSalida(p.getIdProducto(), 3, ALMACENISTA);
        return p;
    }

    @Test
    void historialIncluyeEntradasYSalidasOrdenadoDescendente() {
        generarMovimientos();

        List<Movimiento> historial = movimientoService.obtenerHistorial();
        assertThat(historial).hasSizeGreaterThanOrEqualTo(2);

        for (int i = 0; i < historial.size() - 1; i++) {
            assertThat(historial.get(i).getFechaMovimiento().truncatedTo(ChronoUnit.SECONDS))
                    .isAfterOrEqualTo(historial.get(i + 1).getFechaMovimiento().truncatedTo(ChronoUnit.SECONDS));
        }
    }

    @Test
    void filtroPorEntradaSoloDevuelveEntradas() {
        generarMovimientos();
        List<Movimiento> entradas = movimientoService.filtrarPorTipo(TipoMovimiento.ENTRADA);
        assertThat(entradas).isNotEmpty();
        assertThat(entradas).allMatch(m -> m.getTipoMovimiento() == TipoMovimiento.ENTRADA);
    }

    @Test
    void filtroPorSalidaSoloDevuelveSalidas() {
        generarMovimientos();
        List<Movimiento> salidas = movimientoService.filtrarPorTipo(TipoMovimiento.SALIDA);
        assertThat(salidas).isNotEmpty();
        assertThat(salidas).allMatch(m -> m.getTipoMovimiento() == TipoMovimiento.SALIDA);
    }
}
