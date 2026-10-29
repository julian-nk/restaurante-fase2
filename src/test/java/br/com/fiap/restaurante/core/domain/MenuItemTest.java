package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.factory.impl.MenuItemFactoryImpl;
import br.com.fiap.restaurante.core.dto.CreateMenuItemInputDto;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    private final MenuItemFactoryImpl factory = new MenuItemFactoryImpl();

    private static CreateMenuItemInputDto input(Long restaurantId, String name, String description,
                                                BigDecimal price, String photoUrl) {
        return new CreateMenuItemInputDto(restaurantId, name, description, price, true, photoUrl);
    }

    @Test
    void shouldCreateMenuItemWithTrimmedValuesAndNoId() {
        MenuItem menuItem = factory.create(
                input(1L, " Pizza ", " Pizza de queijo ", new BigDecimal("39.90"), "/fotos/pizza.png"));

        assertNull(menuItem.getId());
        assertEquals(1L, menuItem.getRestaurantId());
        assertEquals("Pizza", menuItem.getName());
        assertEquals("Pizza de queijo", menuItem.getDescription());
        assertEquals(new BigDecimal("39.90"), menuItem.getPrice());
        assertTrue(menuItem.isDineInOnly());
        assertEquals("/fotos/pizza.png", menuItem.getPhotoUrl());
    }

    @Test
    void shouldRestoreMenuItemWithId() {
        MenuItem menuItem = factory.restore(
                9L, 2L, "Suco", "Suco de laranja", BigDecimal.ZERO, false, null);

        assertEquals(9L, menuItem.getId());
        assertEquals(2L, menuItem.getRestaurantId());
        assertEquals(BigDecimal.ZERO, menuItem.getPrice());
        assertFalse(menuItem.isDineInOnly());
        assertNull(menuItem.getPhotoUrl());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void shouldRejectNonPositiveRestaurantId(long restaurantId) {
        var dto = input(restaurantId, "Pizza", "Pizza de queijo", BigDecimal.TEN, null);

        var exception = assertThrows(InvalidFieldException.class, () -> factory.create(dto));
        assertEquals("O ID do restaurante deve ser maior que zero.", exception.getMessage());
    }

    @Test
    void shouldRejectNullRestaurantId() {
        var dto = input(null, "Pizza", "Pizza de queijo", BigDecimal.TEN, null);

        assertThrows(InvalidFieldException.class, () -> factory.create(dto));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void shouldRejectBlankName(String name) {
        var dto = input(1L, name, "Pizza de queijo", BigDecimal.TEN, null);

        var exception = assertThrows(InvalidFieldException.class, () -> factory.create(dto));
        assertEquals("O nome do prato é obrigatório.", exception.getMessage());
    }

    @Test
    void shouldRejectNameLongerThanLimit() {
        var dto = input(1L, "a".repeat(MenuItem.NAME_MAX_LENGTH + 1), "Pizza de queijo", BigDecimal.TEN, null);

        assertThrows(InvalidFieldException.class, () -> factory.create(dto));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void shouldRejectBlankDescription(String description) {
        var dto = input(1L, "Pizza", description, BigDecimal.TEN, null);

        var exception = assertThrows(InvalidFieldException.class, () -> factory.create(dto));
        assertEquals("A descrição do prato é obrigatória.", exception.getMessage());
    }

    @Test
    void shouldRejectNullPrice() {
        var dto = input(1L, "Pizza", "Pizza de queijo", null, null);

        assertThrows(InvalidFieldException.class, () -> factory.create(dto));
    }

    @Test
    void shouldRejectNegativePrice() {
        var dto = input(1L, "Pizza", "Pizza de queijo", new BigDecimal("-0.01"), null);

        var exception = assertThrows(InvalidFieldException.class, () -> factory.create(dto));
        assertEquals("O preço deve ser maior ou igual a zero.", exception.getMessage());
    }

    @Test
    void shouldRejectPhotoUrlLongerThanLimit() {
        var dto = input(1L, "Pizza", "Pizza de queijo", BigDecimal.TEN, "x".repeat(MenuItem.PHOTO_URL_MAX_LENGTH + 1));

        assertThrows(InvalidFieldException.class, () -> factory.create(dto));
    }

    @Test
    void shouldUpdateMenuItem() {
        MenuItem menuItem = factory.restore(1L, 1L, "Pizza", "Pizza de queijo", BigDecimal.TEN, false, null);

        menuItem.update(" Pizza grande ", " Pizza de queijo grande ", new BigDecimal("59.90"), true, "/foto.png");

        assertEquals(1L, menuItem.getId());
        assertEquals(1L, menuItem.getRestaurantId());
        assertEquals("Pizza grande", menuItem.getName());
        assertEquals("Pizza de queijo grande", menuItem.getDescription());
        assertEquals(new BigDecimal("59.90"), menuItem.getPrice());
        assertTrue(menuItem.isDineInOnly());
        assertEquals("/foto.png", menuItem.getPhotoUrl());
    }

    @Test
    void shouldNotChangeStateWhenUpdateIsInvalid() {
        MenuItem menuItem = factory.restore(1L, 1L, "Pizza", "Pizza de queijo", BigDecimal.TEN, false, null);

        assertThrows(InvalidFieldException.class,
                () -> menuItem.update("Pizza", "Pizza de queijo", new BigDecimal("-1"), false, null));

        assertEquals(BigDecimal.TEN, menuItem.getPrice());
    }
}
