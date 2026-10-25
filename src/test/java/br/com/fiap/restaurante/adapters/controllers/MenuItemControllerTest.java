
package br.com.fiap.restaurante.adapters.controllers;

import br.com.fiap.restaurante.domain.entities.MenuItem;
import br.com.fiap.restaurante.domain.exceptions.ResourceNotFoundException;
import br.com.fiap.restaurante.domain.usecases.*;
import br.com.fiap.restaurante.dtos.CreateMenuItemRequest;
import br.com.fiap.restaurante.dtos.UpdateMenuItemRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuItemController.class)
class MenuItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CreateMenuItemUseCase createMenuItemUseCase;

    @MockitoBean
    private GetMenuItemsUseCase getMenuItemsUseCase;

    @MockitoBean
    private GetMenuItemByIdUseCase getMenuItemByIdUseCase;

    @MockitoBean
    private GetMenuItemsByRestaurantIdUseCase getMenuItemsByRestaurantIdUseCase;

    @MockitoBean
    private UpdateMenuItemUseCase updateMenuItemUseCase;

    @MockitoBean
    private DeleteMenuItemUseCase deleteMenuItemUseCase;


    @Test
    void shouldCreateMenuItem() throws Exception {
        MenuItem menuItem = spy(new MenuItem(
                1L,
                "Pizza",
                "Pizza de queijo",
                new BigDecimal("39.90"),
                false,
                null
        ));

        doReturn(1L).when(menuItem).getId();

        when(createMenuItemUseCase.execute(
                1L,
                "Pizza",
                "Pizza de queijo",
                new BigDecimal("39.90"),
                false,
                null
        )).thenReturn(menuItem);

        CreateMenuItemRequest request = new CreateMenuItemRequest(
                1L,
                "Pizza",
                "Pizza de queijo",
                new BigDecimal("39.90"),
                false,
                null
        );

        mockMvc.perform(post("/api/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pizza"))
                .andExpect(jsonPath("$.restaurantId").value(1))
                .andExpect(jsonPath("$.price").value(39.90));
    }

    @Test
    void shouldReturnMenuItemById() throws Exception {
        MenuItem menuItem = spy(new MenuItem(
                1L,
                "Pizza",
                "Pizza de queijo",
                new BigDecimal("39.90"),
                false,
                null
        ));

        doReturn(1L).when(menuItem).getId();

        when(getMenuItemByIdUseCase.execute(1L))
                .thenReturn(menuItem);

        mockMvc.perform(get("/api/menu-items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pizza"))
                .andExpect(jsonPath("$.restaurantId").value(1))
                .andExpect(jsonPath("$.price").value(39.90));
    }

    @Test
    void shouldDeleteMenuItem() throws Exception {
        doNothing().when(deleteMenuItemUseCase).execute(1L);

        mockMvc.perform(delete("/api/menu-items/1"))
                .andExpect(status().isNoContent());

        verify(deleteMenuItemUseCase).execute(1L);
    }

    @Test
    void shouldReturnAllMenuItems() throws Exception {
        MenuItem pizza = new MenuItem(
                1L,
                "Pizza",
                "Pizza de queijo",
                new BigDecimal("39.90"),
                false,
                null
        );

        MenuItem hamburger = new MenuItem(
                2L,
                "Hambúrguer",
                "Hambúrguer artesanal",
                new BigDecimal("29.90"),
                true,
                null
        );

        when(getMenuItemsUseCase.execute())
                .thenReturn(java.util.List.of(pizza, hamburger));

        mockMvc.perform(get("/api/menu-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Pizza"))
                .andExpect(jsonPath("$[1].name").value("Hambúrguer"))
                .andExpect(jsonPath("$[0].restaurantId").value(1))
                .andExpect(jsonPath("$[1].restaurantId").value(2));
    }

    @Test
    void shouldReturnMenuItemsByRestaurantId() throws Exception {
        MenuItem pizza = new MenuItem(
                1L,
                "Pizza",
                "Pizza de queijo",
                new BigDecimal("39.90"),
                false,
                null
        );

        MenuItem hamburger = new MenuItem(
                1L,
                "Hambúrguer",
                "Hambúrguer artesanal",
                new BigDecimal("29.90"),
                true,
                null
        );

        when(getMenuItemsByRestaurantIdUseCase.execute(1L))
                .thenReturn(java.util.List.of(pizza, hamburger));

        mockMvc.perform(get("/api/menu-items/restaurant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Pizza"))
                .andExpect(jsonPath("$[1].name").value("Hambúrguer"))
                .andExpect(jsonPath("$[0].restaurantId").value(1))
                .andExpect(jsonPath("$[1].restaurantId").value(1));

        verify(getMenuItemsByRestaurantIdUseCase).execute(1L);
    }


    @Test
    void shouldUpdateMenuItem() throws Exception {
        MenuItem updatedMenuItem = spy(new MenuItem(
                10L,
                "Pizza Especial",
                "Pizza com queijo especial",
                new BigDecimal("45.90"),
                true,
                "https://exemplo.com/pizza.jpg"
        ));

        doReturn(1L).when(updatedMenuItem).getId();

        UpdateMenuItemRequest request = new UpdateMenuItemRequest(
                "Pizza Especial",
                "Pizza com queijo especial",
                new BigDecimal("45.90"),
                true,
                "https://exemplo.com/pizza.jpg"
        );

        when(updateMenuItemUseCase.execute(
                1L,
                "Pizza Especial",
                "Pizza com queijo especial",
                new BigDecimal("45.90"),
                true,
                "https://exemplo.com/pizza.jpg"
        )).thenReturn(updatedMenuItem);

        mockMvc.perform(put("/api/menu-items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.restaurantId").value(10))
                .andExpect(jsonPath("$.name").value("Pizza Especial"))
                .andExpect(jsonPath("$.price").value(45.90))
                .andExpect(jsonPath("$.dineInOnly").value(true))
                .andExpect(jsonPath("$.photoUrl").value("https://exemplo.com/pizza.jpg"));

        verify(updateMenuItemUseCase).execute(
                1L,
                "Pizza Especial",
                "Pizza com queijo especial",
                new BigDecimal("45.90"),
                true,
                "https://exemplo.com/pizza.jpg"
        );
    }

    @Test
    void shouldReturnNotFoundWhenMenuItemDoesNotExist() throws Exception {
        when(getMenuItemByIdUseCase.execute(999L))
                .thenThrow(new ResourceNotFoundException("Item do cardápio não encontrado"));

        mockMvc.perform(get("/api/menu-items/999"))
                .andExpect(status().isNotFound());

        verify(getMenuItemByIdUseCase).execute(999L);
    }


    @Test
    void shouldReturnBadRequestWhenCreatingMenuItemWithInvalidData() throws Exception {
        CreateMenuItemRequest request = new CreateMenuItemRequest(
                1L,
                "",
                "Pizza de queijo",
                new BigDecimal("39.90"),
                false,
                null
        );

        mockMvc.perform(post("/api/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createMenuItemUseCase);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMenuItemThatDoesNotExist() throws Exception {
        UpdateMenuItemRequest request = new UpdateMenuItemRequest(
                "Pizza Especial",
                "Pizza com queijo especial",
                new BigDecimal("45.90"),
                true,
                "https://exemplo.com/pizza.jpg"
        );

        when(updateMenuItemUseCase.execute(
                999L,
                "Pizza Especial",
                "Pizza com queijo especial",
                new BigDecimal("45.90"),
                true,
                "https://exemplo.com/pizza.jpg"
        )).thenThrow(new ResourceNotFoundException("Item do cardápio não encontrado"));

        mockMvc.perform(put("/api/menu-items/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(updateMenuItemUseCase).execute(
                999L,
                "Pizza Especial",
                "Pizza com queijo especial",
                new BigDecimal("45.90"),
                true,
                "https://exemplo.com/pizza.jpg"
        );
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMenuItemThatDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Item do cardápio não encontrado"))
                .when(deleteMenuItemUseCase).execute(999L);

        mockMvc.perform(delete("/api/menu-items/999"))
                .andExpect(status().isNotFound());

        verify(deleteMenuItemUseCase).execute(999L);
    }
}
