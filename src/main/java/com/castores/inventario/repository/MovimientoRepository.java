package com.castores.inventario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.castores.inventario.entity.Movimiento;
import com.castores.inventario.entity.TipoMovimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    List<Movimiento> findAllByOrderByFechaMovimientoDesc();

    List<Movimiento> findByTipoMovimientoOrderByFechaMovimientoDesc(TipoMovimiento tipoMovimiento);
}
