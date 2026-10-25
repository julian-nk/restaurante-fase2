
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

class UpdateMenuItemUseCaseTest {

    private IMenuItemGateway menuItemGateway;
    private UpdateMenuItemUseCase useCase;

    @BeforeEach
    void setUp() {
        menuItemGateway = mock(IMenuItemGateway.class);
        useCase = new UpdateMenuItemUseCase(menuItemGateway);
    }

    @Test
    void shouldUpdateMenuItemSuccessfully() {
        Long id = 1L;

        MenuItem menuItem = new MenuItem(
                10L,
                "Pizza",
                "Pizza tradicional",
                new BigDecimal("30.00"),
                false,
                null
        );

        when(menuItemGateway.findById(id))
                .thenReturn(Optional.of(menuItem));

        when(menuItemGateway.save(menuItem))
                .thenReturn(menuItem);

        MenuItem result = useCase.execute(
                id,
                "Pizza Especial",
                "Pizza com queijo especial",
                new BigDecimal("45.90"),
                true,
                "https://exemplo.com/pizza.jpg"
        );

        assertNotNull(result);
        assertEquals("Pizza Especial", result.getName());
        assertEquals(
                "Pizza com queijo especial",
                result.getDescription()
        );
        assertEquals(
                0,
                new BigDecimal("45.90").compareTo(result.getPrice())
        );
        assertTrue(result.isDineInOnly());
        assertEquals(
                "https://exemplo.com/pizza.jpg",
                result.getPhotoUrl()
        );

        // O restaurante original deve ser preservado.
        assertEquals(10L, result.getRestaurantId());

        verify(menuItemGateway).findById(id);
        verify(menuItemGateway).save(menuItem);
    }

    @Test
    void shouldThrowExceptionWhenMenuItemDoesNotExist() {
        when(menuItemGateway.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> useCase.execute(
                        99L,
                        "Pizza",
                        "Pizza de queijo",
                        new BigDecimal("39.90"),
                        false,
                        null
                )
        );

        verify(menuItemGateway).findById(99L);
        verify(menuItemGateway, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenIdIsInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(
                        0L,
                        "Pizza",
                        "Pizza de queijo",
                        new BigDecimal("39.90"),
                        false,
                        null
                )
        );

        verifyNoInteractions(menuItemGateway);
    }

    @Test
    void shouldThrowExceptionWhenNameIsInvalid() {
        MenuItem menuItem = createMenuItem();

        when(menuItemGateway.findById(1L))
                .thenReturn(Optional.of(menuItem));

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(
                        1L,
                        " ",
                        "Pizza de queijo",
                        new BigDecimal("39.90"),
                        false,
                        null
                )
        );

        verify(menuItemGateway, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsInvalid() {
        MenuItem menuItem = createMenuItem();

        when(menuItemGateway.findById(1L))
                .thenReturn(Optional.of(menuItem));

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(
                        1L,
                        "Pizza",
                        " ",
                        new BigDecimal("39.90"),
                        false,
                        null
                )
        );

        verify(menuItemGateway, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsInvalid() {
        MenuItem menuItem = createMenuItem();

        when(menuItemGateway.findById(1L))
                .thenReturn(Optional.of(menuItem));

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(
                        1L,
                        "Pizza",
                        "Pizza de queijo",
                        new BigDecimal("-1.00"),
                        false,
                        null
                )
        );

        verify(menuItemGateway, never()).save(any());
    }

    private MenuItem createMenuItem() {
        return new MenuItem(
                10L,
                "Pizza",
                "Pizza tradicional",
                new BigDecimal("30.00"),
                false,
                null
        );
    }
}
