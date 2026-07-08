package com.castores.inventario.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.castores.inventario.dto.MovimientoForm;
import com.castores.inventario.dto.ProductoForm;
import com.castores.inventario.exception.CantidadInvalidaException;
import com.castores.inventario.exception.ProductoNoEncontradoException;
import com.castores.inventario.service.InventarioService;
import com.castores.inventario.service.ProductoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/productos")
public class ProductoAdminController {

    private final ProductoService productoService;
    private final InventarioService inventarioService;

    public ProductoAdminController(ProductoService productoService,
                                   InventarioService inventarioService) {
        this.productoService = productoService;
        this.inventarioService = inventarioService;
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("productoForm", new ProductoForm());
        return "inventario/nuevo";
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute("productoForm") ProductoForm form,
                            BindingResult binding, RedirectAttributes flash) {
        if (binding.hasErrors()) {
            return "inventario/nuevo";
        }
        productoService.registrarProducto(form);
        flash.addFlashAttribute("exito", "Producto registrado correctamente.");
        return "redirect:/inventario";
    }

    @PostMapping("/{id}/baja")
    public String darDeBaja(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            productoService.darDeBaja(id);
            flash.addFlashAttribute("exito", "Producto dado de baja.");
        } catch (ProductoNoEncontradoException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inventario";
    }

    @PostMapping("/{id}/reactivar")
    public String reactivar(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            productoService.reactivar(id);
            flash.addFlashAttribute("exito", "Producto reactivado.");
        } catch (ProductoNoEncontradoException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inventario";
    }

    @GetMapping("/entrada")
    public String formularioEntrada(Model model) {
        model.addAttribute("movimientoForm", new MovimientoForm());
        model.addAttribute("productos", productoService.obtenerProductosActivos());
        return "inventario/entrada";
    }

    @PostMapping("/entrada")
    public String registrarEntrada(@Valid @ModelAttribute("movimientoForm") MovimientoForm form,
                                   BindingResult binding, Principal principal,
                                   Model model, RedirectAttributes flash) {
        if (binding.hasErrors()) {
            model.addAttribute("productos", productoService.obtenerProductosActivos());
            return "inventario/entrada";
        }
        try {
            inventarioService.agregarExistencias(form.getIdProducto(), form.getCantidad(),
                    principal.getName());
            flash.addFlashAttribute("exito", "Entrada registrada correctamente.");
            return "redirect:/inventario";
        } catch (CantidadInvalidaException | ProductoNoEncontradoException e) {
            model.addAttribute("productos", productoService.obtenerProductosActivos());
            model.addAttribute("error", e.getMessage());
            return "inventario/entrada";
        }
    }
}
