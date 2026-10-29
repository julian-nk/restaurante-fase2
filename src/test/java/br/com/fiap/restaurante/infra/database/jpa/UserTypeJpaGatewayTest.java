package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.factory.impl.UserTypeFactoryImpl;
import br.com.fiap.restaurante.core.exception.RepositoryAccessException;
import br.com.fiap.restaurante.infra.database.jpa.entity.UserTypeEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.UserTypeRepository;
import br.com.fiap.restaurante.infra.database.mapper.UserTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserTypeJpaGatewayTest {

    private final UserTypeFactoryImpl factory = new UserTypeFactoryImpl();
    private UserTypeRepository repository;
    private UserTypeJpaGateway gateway;

    @BeforeEach
    void setUp() {
        repository = mock(UserTypeRepository.class);
        gateway = new UserTypeJpaGateway(repository, new UserTypeMapper(factory));
    }

    @Test
    void shouldMapDomainToEntityOnSaveAndBack() {
        when(repository.save(any())).thenReturn(new UserTypeEntity(1L, "Cliente", "customer"));

        UserType saved = gateway.save(factory.restore(null, "Cliente", "customer"));

        ArgumentCaptor<UserTypeEntity> captor = ArgumentCaptor.forClass(UserTypeEntity.class);
        verify(repository).save(captor.capture());
        assertNull(captor.getValue().getId());
        assertEquals("Cliente", captor.getValue().getName());
        assertEquals("customer", captor.getValue().getCode());
        assertEquals(1L, saved.getId());
    }

    @Test
    void shouldFindAllByIdOrderedById() {
        when(repository.findAllById(List.of(3L, 1L))).thenReturn(List.of(
                new UserTypeEntity(3L, "Administrador", "admin"), new UserTypeEntity(1L, "Cliente", "customer")));

        List<UserType> result = gateway.findAllById(List.of(3L, 1L));

        assertEquals(List.of(1L, 3L), result.stream().map(UserType::getId).toList());
    }

    @Test
    void shouldFindById() {
        when(repository.findById(1L)).thenReturn(Optional.of(new UserTypeEntity(1L, "Cliente", "customer")));

        Optional<UserType> result = gateway.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("customer", result.get().getCode());
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertTrue(gateway.findById(9L).isEmpty());
    }

    @Test
    void shouldFindAll() {
        when(repository.findAll()).thenReturn(List.of(
                new UserTypeEntity(1L, "Cliente", "customer"),
                new UserTypeEntity(2L, "Dono de Restaurante", "restaurant_owner")));

        assertEquals(2, gateway.findAll().size());
    }

    @Test
    void shouldCheckCodeAndDelete() {
        when(repository.existsByCode("customer")).thenReturn(true);

        assertTrue(gateway.existsByCode("customer"));
        gateway.deleteById(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void shouldWrapRepositoryFailures() {
        RuntimeException failure = new IllegalStateException("banco fora do ar");
        when(repository.findAll()).thenThrow(failure);
        doThrow(failure).when(repository).deleteById(1L);

        var exception = assertThrows(RepositoryAccessException.class, () -> gateway.findAll());
        assertSame(failure, exception.getCause());
        assertThrows(RepositoryAccessException.class, () -> gateway.deleteById(1L));
    }
}
