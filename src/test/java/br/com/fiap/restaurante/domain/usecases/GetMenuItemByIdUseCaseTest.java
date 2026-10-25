
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

class GetMenuItemByIdUseCaseTest {

    private IMenuItemGateway menuItemGateway;
    private GetMenuItemByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        menuItemGateway = mock(IMenuItemGateway.class);
        useCase = new GetMenuItemByIdUseCase(menuItemGateway);
    }

    @Test
    void shouldReturnMenuItemWhenIdExists() {
        MenuItem menuItem = new MenuItem(
                1L,
                "Pizza",
                "Pizza de queijo",
                new BigDecimal("39.90"),
                false,
                null
        );

        when(menuItemGateway.findById(1L))
                .thenReturn(Optional.of(menuItem));

        MenuItem result = useCase.execute(1L);

        assertNotNull(result);
        assertEquals("Pizza", result.getName());
        verify(menuItemGateway).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenMenuItemDoesNotExist() {
        when(menuItemGateway.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> useCase.execute(99L)
        );

        verify(menuItemGateway).findById(99L);
    }

    @Test
    void shouldThrowExceptionWhenIdIsInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(0L)
        );

        verifyNoInteractions(menuItemGateway);
    }
}
