
package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.MenuItem;
import br.com.fiap.restaurante.interfaces.IMenuItemGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetMenuItemsUseCaseTest {

    private IMenuItemGateway menuItemGateway;
    private GetMenuItemsUseCase useCase;

    @BeforeEach
    void setUp() {
        menuItemGateway = mock(IMenuItemGateway.class);
        useCase = new GetMenuItemsUseCase(menuItemGateway);
    }

    @Test
    void shouldReturnAllMenuItems() {
        List<MenuItem> menuItems = List.of(
                new MenuItem(
                        1L,
                        "Pizza",
                        "Pizza de queijo",
                        new BigDecimal("39.90"),
                        false,
                        null
                ),
                new MenuItem(
                        1L,
                        "Hambúrguer",
                        "Hambúrguer artesanal",
                        new BigDecimal("29.90"),
                        true,
                        null
                )
        );

        when(menuItemGateway.findAll()).thenReturn(menuItems);

        List<MenuItem> result = useCase.execute();

        assertEquals(2, result.size());
        assertEquals("Pizza", result.get(0).getName());
        assertEquals("Hambúrguer", result.get(1).getName());

        verify(menuItemGateway).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoMenuItemsExist() {
        when(menuItemGateway.findAll()).thenReturn(List.of());

        List<MenuItem> result = useCase.execute();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(menuItemGateway).findAll();
    }
}
