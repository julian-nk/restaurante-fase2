
package br.com.fiap.restaurante.adapters.controllers;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.domain.exceptions.ResourceNotFoundException;
import br.com.fiap.restaurante.domain.usecases.*;
import br.com.fiap.restaurante.dtos.CreateUserTypeRequest;
import br.com.fiap.restaurante.dtos.UpdateUserTypeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserTypeController.class)
class UserTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CreateUserTypeUseCase createUserTypeUseCase;

    @MockitoBean
    private GetUserTypesUseCase getUserTypesUseCase;

    @MockitoBean
    private GetUserTypeByIdUseCase getUserTypeByIdUseCase;

    @MockitoBean
    private UpdateUserTypeUseCase updateUserTypeUseCase;

    @MockitoBean
    private DeleteUserTypeUseCase deleteUserTypeUseCase;

    @Test
    void shouldCreateUserType() throws Exception {
        UserType userType = mock(UserType.class);
        when(userType.getId()).thenReturn(1L);
        when(userType.getName()).thenReturn("Administrador");
        when(userType.getCode()).thenReturn("ADMIN");

        when(createUserTypeUseCase.execute("Administrador", "ADMIN"))
                .thenReturn(userType);

        CreateUserTypeRequest request =
                new CreateUserTypeRequest("Administrador", "ADMIN");

        mockMvc.perform(post("/api/user-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Administrador"))
                .andExpect(jsonPath("$.code").value("ADMIN"));

        verify(createUserTypeUseCase).execute("Administrador", "ADMIN");
    }

    @Test
    void shouldReturnAllUserTypes() throws Exception {
        UserType admin = mock(UserType.class);
        when(admin.getId()).thenReturn(1L);
        when(admin.getName()).thenReturn("Administrador");
        when(admin.getCode()).thenReturn("ADMIN");

        UserType customer = mock(UserType.class);
        when(customer.getId()).thenReturn(2L);
        when(customer.getName()).thenReturn("Cliente");
        when(customer.getCode()).thenReturn("CLIENT");

        when(getUserTypesUseCase.execute()).thenReturn(List.of(admin, customer));

        mockMvc.perform(get("/api/user-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Administrador"))
                .andExpect(jsonPath("$[1].name").value("Cliente"));

        verify(getUserTypesUseCase).execute();
    }

    @Test
    void shouldReturnUserTypeById() throws Exception {
        UserType userType = mock(UserType.class);
        when(userType.getId()).thenReturn(1L);
        when(userType.getName()).thenReturn("Administrador");
        when(userType.getCode()).thenReturn("ADMIN");

        when(getUserTypeByIdUseCase.execute(1L))
                .thenReturn(Optional.of(userType));

        mockMvc.perform(get("/api/user-types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Administrador"))
                .andExpect(jsonPath("$.code").value("ADMIN"));

        verify(getUserTypeByIdUseCase).execute(1L);
    }

    @Test
    void shouldReturnNotFoundWhenUserTypeDoesNotExist() throws Exception {
        when(getUserTypeByIdUseCase.execute(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user-types/999"))
                .andExpect(status().isNotFound());

        verify(getUserTypeByIdUseCase).execute(999L);
    }

    @Test
    void shouldUpdateUserType() throws Exception {
        UserType userType = mock(UserType.class);
        when(userType.getId()).thenReturn(1L);
        when(userType.getName()).thenReturn("Gerente");
        when(userType.getCode()).thenReturn("MANAGER");

        when(updateUserTypeUseCase.execute(1L, "Gerente", "MANAGER"))
                .thenReturn(userType);

        UpdateUserTypeRequest request =
                new UpdateUserTypeRequest("Gerente", "MANAGER");

        mockMvc.perform(put("/api/user-types/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gerente"))
                .andExpect(jsonPath("$.code").value("MANAGER"));

        verify(updateUserTypeUseCase).execute(1L, "Gerente", "MANAGER");
    }

    @Test
    void shouldDeleteUserType() throws Exception {
        doNothing().when(deleteUserTypeUseCase).execute(1L);

        mockMvc.perform(delete("/api/user-types/1"))
                .andExpect(status().isNoContent());

        verify(deleteUserTypeUseCase).execute(1L);
    }

    @Test
    void shouldReturnBadRequestWhenCreatingUserTypeWithInvalidData()
            throws Exception {
        CreateUserTypeRequest request = new CreateUserTypeRequest("", "");

        mockMvc.perform(post("/api/user-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createUserTypeUseCase);
    }
}
