package br.com.fiap.restaurante.infra.web;

import br.com.fiap.restaurante.core.controller.AppUserTypeController;
import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;
import br.com.fiap.restaurante.core.exception.AppUserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Sem os filtros (inclusive o Spring Security): aqui só interessa o mapeamento HTTP da API.
@WebMvcTest(AppUserTypeApi.class)
@AutoConfigureMockMvc(addFilters = false)
class AppUserTypeApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppUserTypeController appUserTypeController;

    @Test
    void shouldAssociateUserType() throws Exception {
        mockMvc.perform(post("/api/app-users/1/types/2"))
                .andExpect(status().isNoContent());

        verify(appUserTypeController).associate(1L, 2L);
    }

    @Test
    void shouldListUserTypesOfAppUser() throws Exception {
        when(appUserTypeController.findUserTypes(1L))
                .thenReturn(List.of(new UserTypeOutputDto(2L, "Cliente", "customer")));

        mockMvc.perform(get("/api/app-users/1/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].code").value("customer"));
    }

    @Test
    void shouldReturnNotFoundWhenAppUserDoesNotExist() throws Exception {
        when(appUserTypeController.findUserTypes(99L)).thenThrow(new AppUserNotFoundException(99L));

        mockMvc.perform(get("/api/app-users/99/types"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("appUser.notFound"));
    }

    @Test
    void shouldRemoveUserType() throws Exception {
        mockMvc.perform(delete("/api/app-users/1/types/2"))
                .andExpect(status().isNoContent());

        verify(appUserTypeController).remove(1L, 2L);
    }
}
