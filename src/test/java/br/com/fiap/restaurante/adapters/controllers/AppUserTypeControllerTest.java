
package br.com.fiap.restaurante.adapters.controllers;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.domain.usecases.AssociateUserTypeUseCase;
import br.com.fiap.restaurante.domain.usecases.GetUserTypesByAppUserUseCase;
import br.com.fiap.restaurante.domain.usecases.RemoveUserTypeFromAppUserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppUserTypeController.class)
class AppUserTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssociateUserTypeUseCase associateUserTypeUseCase;

    @MockitoBean
    private GetUserTypesByAppUserUseCase getUserTypesByAppUserUseCase;

    @MockitoBean
    private RemoveUserTypeFromAppUserUseCase removeUserTypeFromAppUserUseCase;

    @Test
    void shouldAssociateUserTypeWithAppUser() throws Exception {
        mockMvc.perform(post("/api/app-users/1/types/2"))
                .andExpect(status().isNoContent());

        verify(associateUserTypeUseCase).execute(1L, 2L);
    }

    @Test
    void shouldReturnUserTypesByAppUser() throws Exception {
        UserType admin = mock(UserType.class);
        when(admin.getId()).thenReturn(2L);
        when(admin.getName()).thenReturn("Administrador");
        when(admin.getCode()).thenReturn("ADMIN");

        UserType customer = mock(UserType.class);
        when(customer.getId()).thenReturn(3L);
        when(customer.getName()).thenReturn("Cliente");
        when(customer.getCode()).thenReturn("CLIENT");

        when(getUserTypesByAppUserUseCase.execute(1L))
                .thenReturn(List.of(admin, customer));

        mockMvc.perform(get("/api/app-users/1/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Administrador"))
                .andExpect(jsonPath("$[0].code").value("ADMIN"))
                .andExpect(jsonPath("$[1].name").value("Cliente"));

        verify(getUserTypesByAppUserUseCase).execute(1L);
    }

    @Test
    void shouldReturnEmptyListWhenAppUserHasNoUserTypes() throws Exception {
        when(getUserTypesByAppUserUseCase.execute(1L))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/app-users/1/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(getUserTypesByAppUserUseCase).execute(1L);
    }

    @Test
    void shouldRemoveUserTypeFromAppUser() throws Exception {
        mockMvc.perform(delete("/api/app-users/1/types/2"))
                .andExpect(status().isNoContent());

        verify(removeUserTypeFromAppUserUseCase).execute(1L, 2L);
    }
}
