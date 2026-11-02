package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.domain.MenuItem;
import br.com.fiap.restaurante.core.domain.Restaurant;
import br.com.fiap.restaurante.core.domain.StatesEnum;
import br.com.fiap.restaurante.core.factory.impl.AddressFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.MenuItemFactoryImpl;
import br.com.fiap.restaurante.core.dto.AddressInputDto;
import br.com.fiap.restaurante.core.dto.CreateMenuItemInputDto;
import br.com.fiap.restaurante.core.dto.CreateRestaurantInputDto;
import br.com.fiap.restaurante.core.dto.MenuItemOutputDto;
import br.com.fiap.restaurante.core.dto.OpeningHoursInputDto;
import br.com.fiap.restaurante.core.dto.RequesterDto;
import br.com.fiap.restaurante.core.dto.UpdateMenuItemInputDto;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.exception.MenuItemNotFoundException;
import br.com.fiap.restaurante.core.exception.NotRestaurantOwnerException;
import br.com.fiap.restaurante.core.exception.RestaurantNotFoundException;
import br.com.fiap.restaurante.core.factory.impl.OpeningHoursFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.RestaurantFactoryImpl;
import br.com.fiap.restaurante.core.gateway.MenuItemGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.core.usecase.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MenuItemUseCasesTest {

    private static final BigDecimal PRICE = new BigDecimal("39.90");

    /** Os pratos destes testes são do restaurante 1, cujo dono é a pessoa 7. */
    private static final RequesterDto OWNER = new RequesterDto(7L, false);
    private static final RequesterDto OTHER_OWNER = new RequesterDto(8L, false);
    private static final RequesterDto ADMIN = new RequesterDto(1L, true);

    private final MenuItemFactoryImpl factory = new MenuItemFactoryImpl();
    private MenuItemGateway gateway;
    private RestaurantGateway restaurantGateway;

    @BeforeEach
    void setUp() {
        gateway = mock(MenuItemGateway.class);
        restaurantGateway = mock(RestaurantGateway.class);
        when(restaurantGateway.findById(1L)).thenReturn(Optional.of(restaurantOfOwner7()));
    }

    private static Restaurant restaurantOfOwner7() {
        return new RestaurantFactoryImpl(new AddressFactoryImpl(), new OpeningHoursFactoryImpl()).create(
                new CreateRestaurantInputDto("Cantina", "12345678000199", "Italiana", 7L,
                        new AddressInputDto("Rua A", "10", null, "Centro", "Campinas", StatesEnum.SP, "13010000"),
                        List.of(new OpeningHoursInputDto(DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(15, 0)))));
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
            when(gateway.save(any())).thenReturn(pizza(1L));
            var useCase = new CreateMenuItemUseCaseImpl(gateway, restaurantGateway, factory);

            MenuItemOutputDto output = useCase.execute(
                    new CreateMenuItemInputDto(1L, "Pizza", "Pizza de queijo", PRICE, false, null), OWNER);

            assertEquals(pizzaOutput(1L), output);
            verify(gateway).save(any(MenuItem.class));
        }

        @Test
        void shouldNotCreateMenuItemForUnknownRestaurant() {
            when(restaurantGateway.findById(1L)).thenReturn(Optional.empty());
            var useCase = new CreateMenuItemUseCaseImpl(gateway, restaurantGateway, factory);

            assertThrows(RestaurantNotFoundException.class, () -> useCase.execute(
                    new CreateMenuItemInputDto(1L, "Pizza", "Pizza de queijo", PRICE, false, null), OWNER));

            verify(gateway, never()).save(any());
        }

        @Test
        void ownerShouldNotCreateMenuItemInRestaurantOfAnotherOwner() {
            var useCase = new CreateMenuItemUseCaseImpl(gateway, restaurantGateway, factory);

            assertThrows(NotRestaurantOwnerException.class, () -> useCase.execute(
                    new CreateMenuItemInputDto(1L, "Pizza", "Pizza de queijo", PRICE, false, null), OTHER_OWNER));

            verify(gateway, never()).save(any());
        }

        @Test
        void adminShouldCreateMenuItemInAnyRestaurant() {
            when(gateway.save(any())).thenReturn(pizza(1L));
            var useCase = new CreateMenuItemUseCaseImpl(gateway, restaurantGateway, factory);

            assertEquals(pizzaOutput(1L), useCase.execute(
                    new CreateMenuItemInputDto(1L, "Pizza", "Pizza de queijo", PRICE, false, null), ADMIN));
        }

        @Test
        void shouldNotSaveInvalidMenuItem() {
            var useCase = new CreateMenuItemUseCaseImpl(gateway, restaurantGateway, factory);

            assertThrows(InvalidFieldException.class, () -> useCase.execute(
                    new CreateMenuItemInputDto(1L, "Pizza", "Pizza de queijo", new BigDecimal("-1"), false, null),
                    OWNER));

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
            useCase = new UpdateMenuItemUseCaseImpl(gateway, restaurantGateway);
        }

        @Test
        void shouldUpdateMenuItem() {
            when(gateway.findById(1L)).thenReturn(Optional.of(pizza(1L)));
            when(gateway.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            MenuItemOutputDto output = useCase.execute(new UpdateMenuItemInputDto(
                    1L, "Pizza grande", "Pizza de queijo grande", new BigDecimal("59.90"), true, "/foto.png"), OWNER);

            assertEquals(new MenuItemOutputDto(1L, 1L, "Pizza grande", "Pizza de queijo grande",
                    new BigDecimal("59.90"), true, "/foto.png"), output);
        }

        @Test
        void shouldThrowWhenNotFound() {
            when(gateway.findById(99L)).thenReturn(Optional.empty());

            assertThrows(MenuItemNotFoundException.class, () -> useCase.execute(
                    new UpdateMenuItemInputDto(99L, "Pizza", "Pizza de queijo", PRICE, false, null), OWNER));
        }

        @Test
        void shouldRejectInvalidData() {
            when(gateway.findById(1L)).thenReturn(Optional.of(pizza(1L)));

            assertThrows(InvalidFieldException.class, () -> useCase.execute(
                    new UpdateMenuItemInputDto(1L, "Pizza", "", PRICE, false, null), OWNER));

            verify(gateway, never()).save(any());
        }

        @Test
        void ownerShouldNotUpdateMenuItemOfAnotherOwner() {
            when(gateway.findById(1L)).thenReturn(Optional.of(pizza(1L)));

            assertThrows(NotRestaurantOwnerException.class, () -> useCase.execute(
                    new UpdateMenuItemInputDto(1L, "Pizza grande", "Pizza de queijo", PRICE, false, null), OTHER_OWNER));

            verify(gateway, never()).save(any());
        }
    }

    @Nested
    class Delete {

        private DeleteMenuItemUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new DeleteMenuItemUseCaseImpl(gateway, restaurantGateway);
        }

        @Test
        void shouldDeleteMenuItem() {
            when(gateway.findById(1L)).thenReturn(Optional.of(pizza(1L)));

            useCase.execute(1L, OWNER);

            verify(gateway).deleteById(1L);
        }

        @Test
        void ownerShouldNotDeleteMenuItemOfAnotherOwner() {
            when(gateway.findById(1L)).thenReturn(Optional.of(pizza(1L)));

            assertThrows(NotRestaurantOwnerException.class, () -> useCase.execute(1L, OTHER_OWNER));

            verify(gateway, never()).deleteById(any());
        }

        @Test
        void adminShouldDeleteAnyMenuItem() {
            when(gateway.findById(1L)).thenReturn(Optional.of(pizza(1L)));

            useCase.execute(1L, ADMIN);

            verify(gateway).deleteById(1L);
        }

        @Test
        void shouldThrowWhenNotFound() {
            when(gateway.findById(99L)).thenReturn(Optional.empty());

            assertThrows(MenuItemNotFoundException.class, () -> useCase.execute(99L, OWNER));

            verify(gateway, never()).deleteById(any());
        }
    }
}
