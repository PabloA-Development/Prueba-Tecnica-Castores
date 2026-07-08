package com.castores.inventario;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class InventarioWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void adminVeInventario() throws Exception {
        mockMvc.perform(get("/inventario")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ALMACENISTA")
    void almacenistaVeInventario() throws Exception {
        mockMvc.perform(get("/inventario")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void adminAccedeAltaProducto() throws Exception {
        mockMvc.perform(get("/admin/productos/nuevo")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ALMACENISTA")
    void almacenistaNoAccedeAAccionesAdmin() throws Exception {
        mockMvc.perform(get("/admin/productos/nuevo")).andExpect(status().isForbidden());
        mockMvc.perform(get("/admin/productos/entrada")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ALMACENISTA")
    void almacenistaAccedeAModuloSalidas() throws Exception {
        mockMvc.perform(get("/salidas")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void adminVeHistoricoYFiltro() throws Exception {
        mockMvc.perform(get("/movimientos")).andExpect(status().isOk());
        mockMvc.perform(get("/movimientos").param("tipo", "ENTRADA")).andExpect(status().isOk());
        mockMvc.perform(get("/movimientos").param("tipo", "SALIDA")).andExpect(status().isOk());
    }
}
