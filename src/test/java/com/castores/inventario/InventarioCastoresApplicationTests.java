package com.castores.inventario;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.castores.inventario.entity.Producto;
import com.castores.inventario.entity.Rol;
import com.castores.inventario.entity.TipoMovimiento;
import com.castores.inventario.repository.MovimientoRepository;
import com.castores.inventario.repository.ProductoRepository;
import com.castores.inventario.repository.RolRepository;
import com.castores.inventario.repository.UsuarioRepository;

@SpringBootTest
class InventarioCastoresApplicationTests {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Test
    void contextoYEsquemaValidados() {

        assertThat(rolRepository).isNotNull();
    }

    @Test
    void rolesSembradosExisten() {
        assertThat(rolRepository.findByNombre("ADMINISTRADOR")).isPresent();
        assertThat(rolRepository.findByNombre("ALMACENISTA")).isPresent();
    }

    @Test
    void productosDemoActivosConCantidadInicialCero() {
        assertThat(productoRepository.findByEstatusTrue()).isNotEmpty();
        for (Producto p : productoRepository.findByEstatusTrue()) {
            assertThat(p.getCantidad()).isGreaterThanOrEqualTo(0);
            assertThat(p.isEstatus()).isTrue();
        }
    }

    @Test
    void repositoriosDeConsultaNoFallan() {

        assertThat(usuarioRepository.findByCorreo("inexistente@castores.local")).isEmpty();
        assertThat(movimientoRepository.findAllByOrderByFechaMovimientoDesc()).isNotNull();
        assertThat(movimientoRepository
                .findByTipoMovimientoOrderByFechaMovimientoDesc(TipoMovimiento.ENTRADA)).isNotNull();
    }
}
