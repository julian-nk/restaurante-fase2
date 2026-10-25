
package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.domain.exceptions.ResourceNotFoundException;
import br.com.fiap.restaurante.interfaces.IAppUserTypeGateway;
import br.com.fiap.restaurante.interfaces.IUserTypeGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RemoveUserTypeFromAppUserUseCaseTest {

    private IAppUserTypeGateway appUserTypeGateway;
    private IUserTypeGateway userTypeGateway;
    private RemoveUserTypeFromAppUserUseCase useCase;

    @BeforeEach
    void setUp() {
        appUserTypeGateway = mock(IAppUserTypeGateway.class);
        userTypeGateway = mock(IUserTypeGateway.class);

        useCase = new RemoveUserTypeFromAppUserUseCase(
                appUserTypeGateway,
                userTypeGateway
        );
    }

    @Test
    void shouldRemoveUserTypeFromAppUserSuccessfully() {
        UserType userType = mock(UserType.class);
        when(userType.getId()).thenReturn(2L);

        when(appUserTypeGateway.appUserExists(1L)).thenReturn(true);
        when(userTypeGateway.findById(2L))
                .thenReturn(Optional.of(userType));

        assertDoesNotThrow(() -> useCase.execute(1L, 2L));

        verify(appUserTypeGateway).appUserExists(1L);
        verify(userTypeGateway).findById(2L);
        verify(appUserTypeGateway).remove(1L, 2L);
    }

    @Test
    void shouldThrowWhenAppUserIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(null, 2L));

        verifyNoInteractions(appUserTypeGateway, userTypeGateway);
    }

    @Test
    void shouldThrowWhenAppUserIdIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(0L, 2L));

        verifyNoInteractions(appUserTypeGateway, userTypeGateway);
    }

    @Test
    void shouldThrowWhenAppUserIdIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(-1L, 2L));

        verifyNoInteractions(appUserTypeGateway, userTypeGateway);
    }

    @Test
    void shouldThrowWhenUserTypeIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(1L, null));

        verifyNoInteractions(appUserTypeGateway, userTypeGateway);
    }

    @Test
    void shouldThrowWhenUserTypeIdIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(1L, 0L));

        verifyNoInteractions(appUserTypeGateway, userTypeGateway);
    }

    @Test
    void shouldThrowWhenUserTypeIdIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(1L, -2L));

        verifyNoInteractions(appUserTypeGateway, userTypeGateway);
    }

    @Test
    void shouldThrowWhenAppUserDoesNotExist() {
        when(appUserTypeGateway.appUserExists(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.execute(1L, 2L));

        verify(appUserTypeGateway).appUserExists(1L);
        verifyNoInteractions(userTypeGateway);
        verify(appUserTypeGateway, never()).remove(anyLong(), anyLong());
    }

    @Test
    void shouldThrowWhenUserTypeDoesNotExist() {
        when(appUserTypeGateway.appUserExists(1L)).thenReturn(true);
        when(userTypeGateway.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.execute(1L, 2L));

        verify(userTypeGateway).findById(2L);
        verify(appUserTypeGateway, never()).remove(anyLong(), anyLong());
    }
}
