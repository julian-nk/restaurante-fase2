
package br.com.fiap.restaurante.adapters.gateways;

import br.com.fiap.restaurante.domain.entities.MenuItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuItemGatewayImplTest {

    private MenuItemRepository repository;
    private MenuItemGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        repository = mock(MenuItemRepository.class);
        gateway = new MenuItemGatewayImpl(repository);
    }

    @Test
    void shouldSaveMenuItem() {
        MenuItem menuItem = new MenuItem(
                1L, "Pizza", "Pizza de queijo",
                new BigDecimal("39.90"), false, null
        );

        when(repository.save(menuItem)).thenReturn(menuItem);

        MenuItem result = gateway.save(menuItem);

        assertSame(menuItem, result);
        verify(repository).save(menuItem);
    }

    @Test
    void shouldFindMenuItemById() {
        MenuItem menuItem = new MenuItem(
                1L, "Pizza", "Pizza de queijo",
                new BigDecimal("39.90"), false, null
        );

        when(repository.findById(1L))
                .thenReturn(Optional.of(menuItem));

        Optional<MenuItem> result = gateway.findById(1L);

        assertTrue(result.isPresent());
        assertSame(menuItem, result.get());
        verify(repository).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenMenuItemDoesNotExist() {
        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        Optional<MenuItem> result = gateway.findById(999L);

        assertTrue(result.isEmpty());
        verify(repository).findById(999L);
    }

    @Test
    void shouldReturnAllMenuItems() {
        List<MenuItem> menuItems = List.of(
                new MenuItem(
                        1L, "Pizza", "Pizza de queijo",
                        new BigDecimal("39.90"), false, null
                ),
                new MenuItem(
                        2L, "Hambúrguer", "Hambúrguer artesanal",
                        new BigDecimal("29.90"), true, null
                )
        );

        when(repository.findAll()).thenReturn(menuItems);

        List<MenuItem> result = gateway.findAll();

        assertEquals(2, result.size());
        assertSame(menuItems, result);
        verify(repository).findAll();
    }

    @Test
    void shouldReturnMenuItemsByRestaurantId() {
        List<MenuItem> menuItems = List.of(
                new MenuItem(
                        1L, "Pizza", "Pizza de queijo",
                        new BigDecimal("39.90"), false, null
                )
        );

        when(repository.findByRestaurantId(1L)).thenReturn(menuItems);

        List<MenuItem> result = gateway.findByRestaurantId(1L);

        assertEquals(1, result.size());
        assertSame(menuItems, result);
        verify(repository).findByRestaurantId(1L);
    }

    @Test
    void shouldReturnEmptyListWhenRestaurantHasNoMenuItems() {
        when(repository.findByRestaurantId(999L))
                .thenReturn(List.of());

        List<MenuItem> result = gateway.findByRestaurantId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findByRestaurantId(999L);
    }

    @Test
    void shouldDeleteMenuItemById() {
        assertDoesNotThrow(() -> gateway.deleteById(1L));

        verify(repository).deleteById(1L);
    }
}
