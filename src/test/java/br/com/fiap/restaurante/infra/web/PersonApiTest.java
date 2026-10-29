package br.com.fiap.restaurante.infra.web;

import br.com.fiap.restaurante.core.controller.PersonController;
import br.com.fiap.restaurante.core.domain.StatesEnum;
import br.com.fiap.restaurante.core.dto.AddressInputDto;
import br.com.fiap.restaurante.core.dto.AddressOutputDto;
import br.com.fiap.restaurante.core.dto.CreatePersonInputDto;
import br.com.fiap.restaurante.core.dto.PersonOutputDto;
import br.com.fiap.restaurante.core.dto.UpdatePersonInputDto;
import br.com.fiap.restaurante.core.exception.CpfAlreadyRegisteredException;
import br.com.fiap.restaurante.core.exception.PersonNotFoundException;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerHasRestaurantsException;
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
@WebMvcTest(PersonApi.class)
@AutoConfigureMockMvc(addFilters = false)
class PersonApiTest {

    private static final PersonOutputDto ANA = new PersonOutputDto(1L, "Ana", "ana@email.com", "39053344705", null);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonController personController;

    @Test
    void shouldCreatePerson() throws Exception {
        when(personController.create(new CreatePersonInputDto("Ana", "ana@email.com", "390.533.447-05", null)))
                .thenReturn(ANA);

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Ana", "email": "ana@email.com", "cpf": "390.533.447-05"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cpf").value("39053344705"));
    }

    @Test
    void shouldCreatePersonWithAddress() throws Exception {
        var address = new AddressInputDto("Av. Paulista", "1000", null, "Bela Vista", "São Paulo", StatesEnum.SP, "01310-100");
        var output = new PersonOutputDto(1L, "Ana", "ana@email.com", "39053344705",
                new AddressOutputDto(5L, "Av. Paulista", "1000", null, "Bela Vista", "São Paulo", "SP", "01310100"));
        when(personController.create(new CreatePersonInputDto("Ana", "ana@email.com", "39053344705", address)))
                .thenReturn(output);

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Ana", "email": "ana@email.com", "cpf": "39053344705",
                                 "address": {"street": "Av. Paulista", "number": "1000", "neighborhood": "Bela Vista",
                                             "city": "São Paulo", "state": "sp", "zipCode": "01310-100"}}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address.id").value(5));
    }

    @Test
    void shouldRejectInvalidJsonBeforeReachingCore() throws Exception {
        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Ana", "email": "sem-arroba", "cpf": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));

        verifyNoInteractions(personController);
    }

    @Test
    void shouldReturnConflictWhenCpfAlreadyExists() throws Exception {
        when(personController.create(any())).thenThrow(new CpfAlreadyRegisteredException("39053344705"));

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Ana", "email": "ana@email.com", "cpf": "39053344705"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("person.cpfAlreadyRegistered"));
    }

    @Test
    void shouldListPersons() throws Exception {
        when(personController.findAll()).thenReturn(List.of(ANA));

        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ana"));
    }

    @Test
    void shouldFindById() throws Exception {
        when(personController.findById(1L)).thenReturn(ANA);

        mockMvc.perform(get("/api/persons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@email.com"));
    }

    @Test
    void shouldReturnNotFound() throws Exception {
        when(personController.findById(99L)).thenThrow(new PersonNotFoundException(99L));

        mockMvc.perform(get("/api/persons/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("person.notFound"));
    }

    @Test
    void shouldUpdatePerson() throws Exception {
        var updated = new PersonOutputDto(1L, "Ana Pereira", "ana.pereira@email.com", "39053344705", null);
        when(personController.update(new UpdatePersonInputDto(1L, "Ana Pereira", "ana.pereira@email.com", null)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/persons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Ana Pereira", "email": "ana.pereira@email.com"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ana Pereira"));
    }

    @Test
    void shouldDeletePerson() throws Exception {
        mockMvc.perform(delete("/api/persons/1"))
                .andExpect(status().isNoContent());

        verify(personController).delete(1L);
    }

    @Test
    void shouldNotDeletePersonThatStillOwnsRestaurants() throws Exception {
        doThrow(new RestaurantOwnerHasRestaurantsException(1L)).when(personController).delete(1L);

        mockMvc.perform(delete("/api/persons/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("restaurantOwner.hasRestaurants"));
    }
}
