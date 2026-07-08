package com.castores.inventario.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.castores.inventario.entity.Movimiento;
import com.castores.inventario.entity.TipoMovimiento;
import com.castores.inventario.repository.MovimientoRepository;

@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;

    public MovimientoService(MovimientoRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }

    public List<Movimiento> obtenerHistorial() {
        return movimientoRepository.findAllByOrderByFechaMovimientoDesc();
    }

    public List<Movimiento> filtrarPorTipo(TipoMovimiento tipo) {
        return movimientoRepository.findByTipoMovimientoOrderByFechaMovimientoDesc(tipo);
    }
}
