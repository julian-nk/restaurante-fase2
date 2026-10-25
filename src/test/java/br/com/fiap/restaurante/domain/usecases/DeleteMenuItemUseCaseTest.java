
package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.MenuItem;
import br.com.fiap.restaurante.domain.exceptions.ResourceNotFoundException;
import br.com.fiap.restaurante.interfaces.IMenuItemGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteMenuItemUseCaseTest {

    private IMenuItemGateway menuItemGateway;
    private DeleteMenuItemUseCase useCase;

    @BeforeEach
    void setUp() {
        menuItemGateway = mock(IMenuItemGateway.class);
        useCase = new DeleteMenuItemUseCase(menuItemGateway);
    }

    @Test
    void shouldDeleteMenuItemSuccessfully() {
        Long id = 1L;

        MenuItem menuItem = new MenuItem(
                10L,
                "Pizza",
                "Pizza de queijo",
                new BigDecimal("39.90"),
                false,
                null
        );

        when(menuItemGateway.findById(id))
                .thenReturn(Optional.of(menuItem));

        assertDoesNotThrow(() -> useCase.execute(id));

        verify(menuItemGateway).findById(id);
        verify(menuItemGateway).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenMenuItemDoesNotExist() {
        Long id = 99L;

        when(menuItemGateway.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> useCase.execute(id)
        );

        verify(menuItemGateway).findById(id);
        verify(menuItemGateway, never()).deleteById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenIdIsZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(0L)
        );

        verifyNoInteractions(menuItemGateway);
    }

    @Test
    void shouldThrowExceptionWhenIdIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(-1L)
        );

        verifyNoInteractions(menuItemGateway);
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(null)
        );

        verifyNoInteractions(menuItemGateway);
    }
}
