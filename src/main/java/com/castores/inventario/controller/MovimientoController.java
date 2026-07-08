package com.castores.inventario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.castores.inventario.entity.TipoMovimiento;
import com.castores.inventario.service.MovimientoService;

@Controller
@RequestMapping("/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public String historial(@RequestParam(name = "tipo", required = false) String tipo, Model model) {
        TipoMovimiento filtro = parseTipo(tipo);

        model.addAttribute("movimientos",
                filtro == null ? movimientoService.obtenerHistorial()
                               : movimientoService.filtrarPorTipo(filtro));

        model.addAttribute("filtro", filtro == null ? "TODOS" : filtro.name());
        return "movimientos/historial";
    }

    private TipoMovimiento parseTipo(String tipo) {
        if (tipo == null || tipo.isBlank() || "TODOS".equalsIgnoreCase(tipo)) {
            return null;
        }
        try {
            return TipoMovimiento.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
