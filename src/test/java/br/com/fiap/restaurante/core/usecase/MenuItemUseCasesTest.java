package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.domain.MenuItem;
import br.com.fiap.restaurante.core.factory.impl.MenuItemFactoryImpl;
import br.com.fiap.restaurante.core.dto.CreateMenuItemInputDto;
import br.com.fiap.restaurante.core.dto.MenuItemOutputDto;
import br.com.fiap.restaurante.core.dto.UpdateMenuItemInputDto;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.exception.MenuItemNotFoundException;
import br.com.fiap.restaurante.core.exception.RestaurantNotFoundException;
import br.com.fiap.restaurante.core.gateway.MenuItemGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.core.usecase.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MenuItemUseCasesTest {

    private static final BigDecimal PRICE = new BigDecimal("39.90");

    private final MenuItemFactoryImpl factory = new MenuItemFactoryImpl();
    private MenuItemGateway gateway;
    private RestaurantGateway restaurantGateway;

    @BeforeEach
    void setUp() {
        gateway = mock(MenuItemGateway.class);
        restaurantGateway = mock(RestaurantGateway.class);
    }

    private MenuItem pizza(Long id) {
        return factory.restore(id, 1L, "Pizza", "Pizza de queijo", PRICE, false, null);
    }

    private static MenuItemOutputDto pizzaOutput(Long id) {
        return new MenuItemOutputDto(id, 1L, "Pizza", "Pizza de queijo", PRICE, false, null);
    }

    @Nested
    class Create {

        @Test
        void shouldCreateMenuItem() {
            when(restaurantGateway.existsById(1L)).thenReturn(true);
            when(gateway.save(any())).thenReturn(pizza(1L));
            var useCase = new CreateMenuItemUseCaseImpl(gateway, restaurantGateway, factory);

            MenuItemOutputDto output = useCase.execute(
                    new CreateMenuItemInputDto(1L, "Pizza", "Pizza de queijo", PRICE, false, null));

            assertEquals(pizzaOutput(1L), output);
            verify(gateway).save(any(MenuItem.class));
        }

        @Test
        void shouldNotCreateMenuItemForUnknownRestaurant() {
            when(restaurantGateway.existsById(1L)).thenReturn(false);
            var useCase = new CreateMenuItemUseCaseImpl(gateway, restaurantGateway, factory);

            assertThrows(RestaurantNotFoundException.class, () -> useCase.execute(
                    new CreateMenuItemInputDto(1L, "Pizza", "Pizza de queijo", PRICE, false, null)));

            verify(gateway, never()).save(any());
        }

        @Test
        void shouldNotSaveInvalidMenuItem() {
            var useCase = new CreateMenuItemUseCaseImpl(gateway, restaurantGateway, factory);

            assertThrows(InvalidFieldException.class, () -> useCase.execute(
                    new CreateMenuItemInputDto(1L, "Pizza", "Pizza de queijo", new BigDecimal("-1"), false, null)));

            verifyNoInteractions(gateway);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldReturnAllMenuItems() {
            when(gateway.findAll()).thenReturn(List.of(pizza(1L), pizza(2L)));

            assertEquals(List.of(pizzaOutput(1L), pizzaOutput(2L)), new GetMenuItemsUseCaseImpl(gateway).execute());
        }

        @Test
        void shouldReturnMenuItemById() {
            when(gateway.findById(1L)).thenReturn(Optional.of(pizza(1L)));

            assertEquals(pizzaOutput(1L), new GetMenuItemByIdUseCaseImpl(gateway).execute(1L));
        }

        @Test
        void shouldThrowWhenMenuItemNotFound() {
            when(gateway.findById(99L)).thenReturn(Optional.empty());

            assertThrows(MenuItemNotFoundException.class, () -> new GetMenuItemByIdUseCaseImpl(gateway).execute(99L));
        }

        @Test
        void shouldReturnMenuItemsByRestaurant() {
            when(gateway.findByRestaurantId(1L)).thenReturn(List.of(pizza(1L)));

            assertEquals(List.of(pizzaOutput(1L)), new GetMenuItemsByRestaurantIdUseCaseImpl(gateway).execute(1L));
        }

        @Test
        void shouldReturnEmptyListWhenRestaurantHasNoMenuItems() {
            when(gateway.findByRestaurantId(5L)).thenReturn(List.of());

            assertTrue(new GetMenuItemsByRestaurantIdUseCaseImpl(gateway).execute(5L).isEmpty());
        }
    }

    @Nested
    class Update {

        private UpdateMenuItemUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new UpdateMenuItemUseCaseImpl(gateway);
        }

        @Test
        void shouldUpdateMenuItem() {
            when(gateway.findById(1L)).thenReturn(Optional.of(pizza(1L)));
            when(gateway.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            MenuItemOutputDto output = useCase.execute(new UpdateMenuItemInputDto(
                    1L, "Pizza grande", "Pizza de queijo grande", new BigDecimal("59.90"), true, "/foto.png"));

            assertEquals(new MenuItemOutputDto(1L, 1L, "Pizza grande", "Pizza de queijo grande",
                    new BigDecimal("59.90"), true, "/foto.png"), output);
        }

        @Test
        void shouldThrowWhenNotFound() {
            when(gateway.findById(99L)).thenReturn(Optional.empty());

            assertThrows(MenuItemNotFoundException.class, () -> useCase.execute(
                    new UpdateMenuItemInputDto(99L, "Pizza", "Pizza de queijo", PRICE, false, null)));
        }

        @Test
        void shouldRejectInvalidData() {
            when(gateway.findById(1L)).thenReturn(Optional.of(pizza(1L)));

            assertThrows(InvalidFieldException.class, () -> useCase.execute(
                    new UpdateMenuItemInputDto(1L, "Pizza", "", PRICE, false, null)));

            verify(gateway, never()).save(any());
        }
    }

    @Nested
    class Delete {

        private DeleteMenuItemUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new DeleteMenuItemUseCaseImpl(gateway);
        }

        @Test
        void shouldDeleteMenuItem() {
            when(gateway.findById(1L)).thenReturn(Optional.of(pizza(1L)));

            useCase.execute(1L);

            verify(gateway).deleteById(1L);
        }

        @Test
        void shouldThrowWhenNotFound() {
            when(gateway.findById(99L)).thenReturn(Optional.empty());

            assertThrows(MenuItemNotFoundException.class, () -> useCase.execute(99L));

            verify(gateway, never()).deleteById(any());
        }
    }
}
