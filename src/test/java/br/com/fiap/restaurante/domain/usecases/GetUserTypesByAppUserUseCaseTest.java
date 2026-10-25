
package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.domain.exceptions.ResourceNotFoundException;
import br.com.fiap.restaurante.interfaces.IAppUserTypeGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetUserTypesByAppUserUseCaseTest {

    private IAppUserTypeGateway appUserTypeGateway;
    private GetUserTypesByAppUserUseCase useCase;

    @BeforeEach
    void setUp() {
        appUserTypeGateway = mock(IAppUserTypeGateway.class);
        useCase = new GetUserTypesByAppUserUseCase(appUserTypeGateway);
    }

    @Test
    void shouldReturnUserTypesByAppUser() {
        UserType admin = mock(UserType.class);
        UserType customer = mock(UserType.class);

        when(appUserTypeGateway.appUserExists(1L)).thenReturn(true);
        when(appUserTypeGateway.findUserTypesByAppUserId(1L))
                .thenReturn(List.of(admin, customer));

        List<UserType> result = useCase.execute(1L);

        assertEquals(2, result.size());
        assertSame(admin, result.get(0));
        assertSame(customer, result.get(1));

        verify(appUserTypeGateway).appUserExists(1L);
        verify(appUserTypeGateway).findUserTypesByAppUserId(1L);
    }

    @Test
    void shouldReturnEmptyListWhenAppUserHasNoUserTypes() {
        when(appUserTypeGateway.appUserExists(1L)).thenReturn(true);
        when(appUserTypeGateway.findUserTypesByAppUserId(1L))
                .thenReturn(List.of());

        List<UserType> result = useCase.execute(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(appUserTypeGateway).findUserTypesByAppUserId(1L);
    }

    @Test
    void shouldThrowWhenAppUserIdIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(null)
        );

        verifyNoInteractions(appUserTypeGateway);
    }

    @Test
    void shouldThrowWhenAppUserIdIsZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(0L)
        );

        verifyNoInteractions(appUserTypeGateway);
    }

    @Test
    void shouldThrowWhenAppUserIdIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(-1L)
        );

        verifyNoInteractions(appUserTypeGateway);
    }

    @Test
    void shouldThrowWhenAppUserDoesNotExist() {
        when(appUserTypeGateway.appUserExists(1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> useCase.execute(1L)
        );

        assertEquals("Usuário não encontrado.", exception.getMessage());

        verify(appUserTypeGateway).appUserExists(1L);
        verify(appUserTypeGateway, never())
                .findUserTypesByAppUserId(anyLong());
    }
}
