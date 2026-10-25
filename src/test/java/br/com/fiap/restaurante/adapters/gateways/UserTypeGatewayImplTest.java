
package br.com.fiap.restaurante.adapters.gateways;

import br.com.fiap.restaurante.domain.entities.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserTypeGatewayImplTest {

    private UserTypeRepository repository;
    private UserTypeGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        repository = mock(UserTypeRepository.class);
        gateway = new UserTypeGatewayImpl(repository);
    }

    @Test
    void shouldSaveUserType() {
        UserType userType = new UserType("Cliente", "customer");

        when(repository.save(userType)).thenReturn(userType);

        UserType result = gateway.save(userType);

        assertSame(userType, result);
        verify(repository).save(userType);
    }

    @Test
    void shouldFindUserTypeById() {
        UserType userType = new UserType("Cliente", "customer");

        when(repository.findById(1L))
                .thenReturn(Optional.of(userType));

        Optional<UserType> result = gateway.findById(1L);

        assertTrue(result.isPresent());
        assertSame(userType, result.get());
        verify(repository).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenUserTypeDoesNotExist() {
        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        Optional<UserType> result = gateway.findById(999L);

        assertTrue(result.isEmpty());
        verify(repository).findById(999L);
    }

    @Test
    void shouldReturnAllUserTypes() {
        List<UserType> userTypes = List.of(
                new UserType("Cliente", "customer"),
                new UserType("Administrador", "admin")
        );

        when(repository.findAll()).thenReturn(userTypes);

        List<UserType> result = gateway.findAll();

        assertEquals(2, result.size());
        assertSame(userTypes, result);
        verify(repository).findAll();
    }

    @Test
    void shouldCheckWhetherCodeExists() {
        when(repository.existsByCode("admin")).thenReturn(true);

        boolean result = gateway.existsByCode("admin");

        assertTrue(result);
        verify(repository).existsByCode("admin");
    }

    @Test
    void shouldReturnFalseWhenCodeDoesNotExist() {
        when(repository.existsByCode("unknown")).thenReturn(false);

        boolean result = gateway.existsByCode("unknown");

        assertFalse(result);
        verify(repository).existsByCode("unknown");
    }

    @Test
    void shouldDeleteUserTypeById() {
        assertDoesNotThrow(() -> gateway.deleteById(1L));

        verify(repository).deleteById(1L);
    }

    @Test
    void shouldCheckWhetherUserTypeIsAssociatedWithUsers() {
        when(repository.isAssociatedWithUsers(1L)).thenReturn(true);

        boolean result = gateway.isAssociatedWithUsers(1L);

        assertTrue(result);
        verify(repository).isAssociatedWithUsers(1L);
    }

    @Test
    void shouldReturnFalseWhenUserTypeIsNotAssociatedWithUsers() {
        when(repository.isAssociatedWithUsers(2L)).thenReturn(false);

        boolean result = gateway.isAssociatedWithUsers(2L);

        assertFalse(result);
        verify(repository).isAssociatedWithUsers(2L);
    }
}
