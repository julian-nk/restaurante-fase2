package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.MenuItem;
import br.com.fiap.restaurante.core.factory.impl.MenuItemFactoryImpl;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.exception.RepositoryAccessException;
import br.com.fiap.restaurante.infra.database.jpa.entity.MenuItemEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.MenuItemRepository;
import br.com.fiap.restaurante.infra.database.mapper.MenuItemMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MenuItemJpaGatewayTest {

    private static final BigDecimal PRICE = new BigDecimal("39.90");

    private final MenuItemFactoryImpl factory = new MenuItemFactoryImpl();
    private MenuItemRepository repository;
    private MenuItemJpaGateway gateway;

    @BeforeEach
    void setUp() {
        repository = mock(MenuItemRepository.class);
        gateway = new MenuItemJpaGateway(repository, new MenuItemMapper(factory));
    }

    private static MenuItemEntity entity(Long id) {
        return new MenuItemEntity(id, 1L, "Pizza", "Pizza de queijo", PRICE, true, "/foto.png");
    }

    @Test
    void shouldMapDomainToEntityOnSaveAndBack() {
        when(repository.save(any())).thenReturn(entity(1L));

        MenuItem saved = gateway.save(factory.restore(null, 1L, "Pizza", "Pizza de queijo", PRICE, true, "/foto.png"));

        ArgumentCaptor<MenuItemEntity> captor = ArgumentCaptor.forClass(MenuItemEntity.class);
        verify(repository).save(captor.capture());
        MenuItemEntity sent = captor.getValue();
        assertNull(sent.getId());
        assertEquals(1L, sent.getRestaurantId());
        assertEquals("Pizza", sent.getName());
        assertEquals("Pizza de queijo", sent.getDescription());
        assertEquals(PRICE, sent.getPrice());
        assertTrue(sent.isDineInOnly());
        assertEquals("/foto.png", sent.getPhotoUrl());
        assertEquals(1L, saved.getId());
    }

    @Test
    void shouldFindByIdAndAll() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity(1L)));
        when(repository.findAll()).thenReturn(List.of(entity(1L), entity(2L)));

        assertEquals("Pizza", gateway.findById(1L).orElseThrow().getName());
        assertEquals(2, gateway.findAll().size());
    }

    @Test
    void shouldFindByRestaurantId() {
        when(repository.findByRestaurantId(1L)).thenReturn(List.of(entity(1L)));
        when(repository.findByRestaurantId(9L)).thenReturn(List.of());

        assertEquals(1, gateway.findByRestaurantId(1L).size());
        assertTrue(gateway.findByRestaurantId(9L).isEmpty());
    }

    @Test
    void shouldDeleteById() {
        gateway.deleteById(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void shouldWrapRepositoryFailures() {
        when(repository.findById(1L)).thenThrow(new IllegalStateException("timeout"));

        assertThrows(RepositoryAccessException.class, () -> gateway.findById(1L));
    }

    @Test
    void shouldNotWrapCoreExceptions() {
        // registro inválido no banco: a validação do domínio deve aparecer como está
        when(repository.findById(1L)).thenReturn(Optional.of(
                new MenuItemEntity(1L, 1L, "", "Pizza de queijo", PRICE, false, null)));

        assertThrows(InvalidFieldException.class, () -> gateway.findById(1L));
    }
}
