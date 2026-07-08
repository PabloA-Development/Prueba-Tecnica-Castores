package com.castores.inventario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.castores.inventario.service.ProductoService;

@Controller
public class InventarioController {

    private final ProductoService productoService;

    public InventarioController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/inventario")
    public String listar(Model model) {
        model.addAttribute("productos", productoService.obtenerInventario());
        return "inventario/lista";
    }
}
