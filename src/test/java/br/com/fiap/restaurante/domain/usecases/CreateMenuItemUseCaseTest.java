package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.MenuItem;
import br.com.fiap.restaurante.interfaces.IMenuItemGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateMenuItemUseCaseTest {

    private IMenuItemGateway menuItemGateway;
    private CreateMenuItemUseCase useCase;

    @BeforeEach
    void setUp() {
        menuItemGateway = mock(IMenuItemGateway.class);
        useCase = new CreateMenuItemUseCase(menuItemGateway);
    }

    @Test
    void shouldCreateMenuItemSuccessfully() {
        MenuItem savedItem = new MenuItem(
                1L,
                "Pizza",
                "Pizza de queijo",
                new BigDecimal("39.90"),
                false,
                null
        );

        when(menuItemGateway.save(any(MenuItem.class)))
                .thenReturn(savedItem);

        MenuItem result = useCase.execute(
                1L,
                " Pizza ",
                " Pizza de queijo ",
                new BigDecimal("39.90"),
                false,
                null
        );

        assertNotNull(result);
        assertEquals("Pizza", result.getName());
        assertEquals("Pizza de queijo", result.getDescription());
        assertEquals(new BigDecimal("39.90"), result.getPrice());

        verify(menuItemGateway).save(any(MenuItem.class));
    }

    @Test
    void shouldThrowWhenRestaurantIdIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(
                        null, "Pizza", "Pizza de queijo",
                        new BigDecimal("39.90"), false, null
                )
        );

        verifyNoInteractions(menuItemGateway);
    }

    @Test
    void shouldThrowWhenRestaurantIdIsZero() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(
                        0L, "Pizza", "Pizza de queijo",
                        new BigDecimal("39.90"), false, null
                )
        );

        verifyNoInteractions(menuItemGateway);
    }

    @Test
    void shouldThrowWhenRestaurantIdIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(
                        -1L, "Pizza", "Pizza de queijo",
                        new BigDecimal("39.90"), false, null
                )
        );

        verifyNoInteractions(menuItemGateway);
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(
                        1L, null, "Pizza de queijo",
                        new BigDecimal("39.90"), false, null
                )
        );

        verifyNoInteractions(menuItemGateway);
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(
                        1L, "   ", "Pizza de queijo",
                        new BigDecimal("39.90"), false, null
                )
        );

        verifyNoInteractions(menuItemGateway);
    }

    @Test
    void shouldThrowWhenNameExceeds150Characters() {
        String name = "A".repeat(151);

        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(
                        1L, name, "Pizza de queijo",
                        new BigDecimal("39.90"), false, null
                )
        );

        verifyNoInteractions(menuItemGateway);
    }

    @Test
    void shouldAcceptNameWithExactly150Characters() {
        String name = "A".repeat(150);

        when(menuItemGateway.save(any(MenuItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MenuItem result = useCase.execute(
                1L, name, "Pizza de queijo",
                new BigDecimal("39.90"), false, null
        );

        assertEquals(name, result.getName());
        verify(menuItemGateway).save(any(MenuItem.class));
    }

    @Test
    void shouldThrowWhenDescriptionIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(
                        1L, "Pizza", null,
                        new BigDecimal("39.90"), false, null
                )
        );

        verifyNoInteractions(menuItemGateway);
    }

    @Test
    void shouldThrowWhenDescriptionIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(
                        1L, "Pizza", "   ",
                        new BigDecimal("39.90"), false, null
                )
        );

        verifyNoInteractions(menuItemGateway);
    }

    @Test
    void shouldThrowWhenPriceIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(
                        1L, "Pizza", "Pizza de queijo",
                        null, false, null
                )
        );

        verifyNoInteractions(menuItemGateway);
    }

    @Test
    void shouldThrowWhenPriceIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(
                        1L, "Pizza", "Pizza de queijo",
                        new BigDecimal("-0.01"), false, null
                )
        );

        verifyNoInteractions(menuItemGateway);
    }

    @Test
    void shouldAcceptPriceOfZero() {
        when(menuItemGateway.save(any(MenuItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MenuItem result = useCase.execute(
                1L, "Pizza", "Pizza de queijo",
                BigDecimal.ZERO, false, null
        );

        assertEquals(BigDecimal.ZERO, result.getPrice());
        verify(menuItemGateway).save(any(MenuItem.class));
    }
}