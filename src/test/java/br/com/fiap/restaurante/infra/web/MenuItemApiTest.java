package br.com.fiap.restaurante.infra.web;

import br.com.fiap.restaurante.core.controller.MenuItemController;
import br.com.fiap.restaurante.core.dto.CreateMenuItemInputDto;
import br.com.fiap.restaurante.core.dto.MenuItemOutputDto;
import br.com.fiap.restaurante.core.dto.UpdateMenuItemInputDto;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.exception.MenuItemNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Sem os filtros (inclusive o Spring Security): aqui só interessa o mapeamento HTTP da API.
@WebMvcTest(MenuItemApi.class)
@AutoConfigureMockMvc(addFilters = false)
class MenuItemApiTest {

    private static final BigDecimal PRICE = new BigDecimal("39.90");
    private static final MenuItemOutputDto PIZZA =
            new MenuItemOutputDto(1L, 1L, "Pizza", "Pizza de queijo", PRICE, false, null);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MenuItemController menuItemController;

    @Test
    void shouldCreateMenuItem() throws Exception {
        when(menuItemController.create(
                new CreateMenuItemInputDto(1L, "Pizza", "Pizza de queijo", PRICE, false, null))).thenReturn(PIZZA);

        mockMvc.perform(post("/api/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"restaurantId": 1, "name": "Pizza", "description": "Pizza de queijo",
                                 "price": 39.90, "dineInOnly": false}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.restaurantId").value(1))
                .andExpect(jsonPath("$.name").value("Pizza"))
                .andExpect(jsonPath("$.price").value(39.90));
    }

    @Test
    void shouldRejectInvalidJson() throws Exception {
        mockMvc.perform(post("/api/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"restaurantId": 0, "name": "", "description": "Pizza", "price": -1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));

        verifyNoInteractions(menuItemController);
    }

    @Test
    void shouldTranslateDomainValidationToBadRequest() throws Exception {
        when(menuItemController.findById(0L)).thenThrow(new InvalidFieldException("O ID do prato deve ser maior que zero."));

        mockMvc.perform(get("/api/menu-items/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("validation.invalidField"))
                .andExpect(jsonPath("$.message").value("O ID do prato deve ser maior que zero."));
    }

    @Test
    void shouldListMenuItems() throws Exception {
        when(menuItemController.findAll()).thenReturn(List.of(PIZZA));

        mockMvc.perform(get("/api/menu-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Pizza"));
    }

    @Test
    void shouldFindById() throws Exception {
        when(menuItemController.findById(1L)).thenReturn(PIZZA);

        mockMvc.perform(get("/api/menu-items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Pizza de queijo"));
    }

    @Test
    void shouldReturnNotFound() throws Exception {
        when(menuItemController.findById(99L)).thenThrow(new MenuItemNotFoundException(99L));

        mockMvc.perform(get("/api/menu-items/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("menuItem.notFound"));
    }

    @Test
    void shouldListByRestaurant() throws Exception {
        when(menuItemController.findByRestaurantId(1L)).thenReturn(List.of(PIZZA));

        mockMvc.perform(get("/api/menu-items/restaurant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].restaurantId").value(1));
    }

    @Test
    void shouldUpdateMenuItem() throws Exception {
        var updated = new MenuItemOutputDto(1L, 1L, "Pizza grande", "Pizza de queijo", PRICE, true, null);
        when(menuItemController.update(
                new UpdateMenuItemInputDto(1L, "Pizza grande", "Pizza de queijo", PRICE, true, null))).thenReturn(updated);

        mockMvc.perform(put("/api/menu-items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Pizza grande", "description": "Pizza de queijo",
                                 "price": 39.90, "dineInOnly": true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pizza grande"))
                .andExpect(jsonPath("$.dineInOnly").value(true));
    }

    @Test
    void shouldDeleteMenuItem() throws Exception {
        mockMvc.perform(delete("/api/menu-items/1"))
                .andExpect(status().isNoContent());

        verify(menuItemController).delete(1L);
    }
}
