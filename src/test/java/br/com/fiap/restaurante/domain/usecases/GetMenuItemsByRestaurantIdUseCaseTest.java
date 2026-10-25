
package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.MenuItem;
import br.com.fiap.restaurante.interfaces.IMenuItemGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetMenuItemsByRestaurantIdUseCaseTest {

    private IMenuItemGateway menuItemGateway;
    private GetMenuItemsByRestaurantIdUseCase useCase;

    @BeforeEach
    void setUp() {
        menuItemGateway = mock(IMenuItemGateway.class);
        useCase = new GetMenuItemsByRestaurantIdUseCase(
                menuItemGateway
        );
    }

    @Test
    void shouldReturnMenuItemsWhenRestaurantHasItems() {
        Long restaurantId = 1L;

        List<MenuItem> menuItems = List.of(
                new MenuItem(
                        restaurantId,
                        "Pizza",
                        "Pizza de queijo",
                        new BigDecimal("39.90"),
                        false,
                        null
                ),
                new MenuItem(
                        restaurantId,
                        "Hambúrguer",
                        "Hambúrguer artesanal",
                        new BigDecimal("29.90"),
                        true,
                        null
                )
        );

        when(menuItemGateway.findByRestaurantId(restaurantId))
                .thenReturn(menuItems);

        List<MenuItem> result = useCase.execute(restaurantId);

        assertEquals(2, result.size());
        assertEquals("Pizza", result.get(0).getName());
        assertEquals("Hambúrguer", result.get(1).getName());

        verify(menuItemGateway).findByRestaurantId(restaurantId);
    }

    @Test
    void shouldReturnEmptyListWhenRestaurantHasNoItems() {
        Long restaurantId = 1L;

        when(menuItemGateway.findByRestaurantId(restaurantId))
                .thenReturn(List.of());

        List<MenuItem> result = useCase.execute(restaurantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(menuItemGateway).findByRestaurantId(restaurantId);
    }

    @Test
    void shouldThrowExceptionWhenRestaurantIdIsInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(0L)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(-1L)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(null)
        );

        verifyNoInteractions(menuItemGateway);
    }
}
