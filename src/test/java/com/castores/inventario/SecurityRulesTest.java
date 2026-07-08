package com.castores.inventario;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityRulesTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginEsPublico() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk());
    }

    @Test
    void sinAutenticarRedirigeALogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "ALMACENISTA")
    void almacenistaNoAccedeAMovimientos() throws Exception {
        mockMvc.perform(get("/movimientos")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void administradorNoAccedeASalidas() throws Exception {
        mockMvc.perform(get("/salidas")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void administradorAutorizadoEnMovimientos() throws Exception {

        mockMvc.perform(get("/movimientos")).andExpect(status().isOk());
    }

    @Test
    void loginCorrectoAutentica() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin@castores.local").password("Admin123$"))
                .andExpect(authenticated().withRoles("ADMINISTRADOR"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void loginConCredencialesInvalidasFalla() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin@castores.local").password("incorrecta"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
    }
}
