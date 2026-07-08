package com.castores.inventario.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.castores.inventario.dto.MovimientoForm;
import com.castores.inventario.exception.CantidadInvalidaException;
import com.castores.inventario.exception.InventarioInsuficienteException;
import com.castores.inventario.exception.ProductoInactivoException;
import com.castores.inventario.exception.ProductoNoEncontradoException;
import com.castores.inventario.service.InventarioService;
import com.castores.inventario.service.ProductoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/salidas")
public class SalidaController {

    private final ProductoService productoService;
    private final InventarioService inventarioService;

    public SalidaController(ProductoService productoService,
                            InventarioService inventarioService) {
        this.productoService = productoService;
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public String vistaSalida(Model model) {
        model.addAttribute("movimientoForm", new MovimientoForm());
        model.addAttribute("productos", productoService.obtenerProductosActivos());
        return "salidas/lista";
    }

    @PostMapping
    public String registrarSalida(@Valid @ModelAttribute("movimientoForm") MovimientoForm form,
                                  BindingResult binding, Principal principal,
                                  Model model, RedirectAttributes flash) {
        if (binding.hasErrors()) {
            model.addAttribute("productos", productoService.obtenerProductosActivos());
            return "salidas/lista";
        }
        try {
            inventarioService.realizarSalida(form.getIdProducto(), form.getCantidad(),
                    principal.getName());
            flash.addFlashAttribute("exito", "Salida registrada correctamente.");
            return "redirect:/salidas";
        } catch (CantidadInvalidaException | InventarioInsuficienteException
                 | ProductoInactivoException | ProductoNoEncontradoException e) {
            model.addAttribute("productos", productoService.obtenerProductosActivos());
            model.addAttribute("error", e.getMessage());
            return "salidas/lista";
        }
    }
}
