package br.com.fiap.restaurante.infra.web;

import br.com.fiap.restaurante.core.controller.UserTypeController;
import br.com.fiap.restaurante.core.dto.CreateUserTypeInputDto;
import br.com.fiap.restaurante.core.dto.UpdateUserTypeInputDto;
import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;
import br.com.fiap.restaurante.core.exception.UserTypeAlreadyExistsException;
import br.com.fiap.restaurante.core.exception.UserTypeNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Sem os filtros (inclusive o Spring Security): aqui só interessa o mapeamento HTTP da API.
@WebMvcTest(UserTypeApi.class)
@AutoConfigureMockMvc(addFilters = false)
class UserTypeApiTest {

    private static final UserTypeOutputDto CUSTOMER = new UserTypeOutputDto(1L, "Cliente", "customer");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserTypeController userTypeController;

    @Test
    void shouldCreateUserType() throws Exception {
        when(userTypeController.create(new CreateUserTypeInputDto("Cliente", "customer"))).thenReturn(CUSTOMER);

        mockMvc.perform(post("/api/user-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Cliente", "code": "customer"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Cliente"))
                .andExpect(jsonPath("$.code").value("customer"));
    }

    @Test
    void shouldRejectInvalidJsonBeforeReachingCore() throws Exception {
        mockMvc.perform(post("/api/user-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "", "code": "customer"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));

        verifyNoInteractions(userTypeController);
    }

    @Test
    void shouldReturnConflictWhenCodeAlreadyExists() throws Exception {
        when(userTypeController.create(any())).thenThrow(new UserTypeAlreadyExistsException("customer"));

        mockMvc.perform(post("/api/user-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Cliente", "code": "customer"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("userType.alreadyExists"));
    }

    @Test
    void shouldListUserTypes() throws Exception {
        when(userTypeController.findAll()).thenReturn(List.of(CUSTOMER));

        mockMvc.perform(get("/api/user-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("customer"));
    }

    @Test
    void shouldFindById() throws Exception {
        when(userTypeController.findById(1L)).thenReturn(CUSTOMER);

        mockMvc.perform(get("/api/user-types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cliente"));
    }

    @Test
    void shouldReturnNotFound() throws Exception {
        when(userTypeController.findById(99L)).thenThrow(new UserTypeNotFoundException(99L));

        mockMvc.perform(get("/api/user-types/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.code").value("userType.notFound"));
    }

    @Test
    void shouldUpdateUserType() throws Exception {
        var updated = new UserTypeOutputDto(1L, "Cliente VIP", "vip");
        when(userTypeController.update(new UpdateUserTypeInputDto(1L, "Cliente VIP", "vip"))).thenReturn(updated);

        mockMvc.perform(put("/api/user-types/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Cliente VIP", "code": "vip"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("vip"));
    }

    @Test
    void shouldDeleteUserType() throws Exception {
        mockMvc.perform(delete("/api/user-types/1"))
                .andExpect(status().isNoContent());

        verify(userTypeController).delete(1L);
    }
}
