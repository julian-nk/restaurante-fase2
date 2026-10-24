package br.com.fiap.restaurante.domain.usecases;
import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.interfaces.IAppUserTypeGateway;
import br.com.fiap.restaurante.interfaces.IUserTypeGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssociateUserTypeUseCaseTest {

    private IAppUserTypeGateway appUserTypeGateway;
    private IUserTypeGateway userTypeGateway;
    private AssociateUserTypeUseCase useCase;

    @BeforeEach
    void setUp() {
        appUserTypeGateway = mock(IAppUserTypeGateway.class);
        userTypeGateway = mock(IUserTypeGateway.class);

        useCase = new AssociateUserTypeUseCase(
                appUserTypeGateway,
                userTypeGateway
        );
    }

    @Test
    void deveAssociarTipoAoUsuarioQuandoDadosForemValidos() throws Exception{
        UserType userType = new UserType("Administrador", "ADMIN");

        Field idField = UserType.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(userType, 2L);

        when(appUserTypeGateway.appUserExists(1L))
                .thenReturn(true);

        when(userTypeGateway.findById(2L))
                .thenReturn(Optional.of(userType));

        assertDoesNotThrow(() -> useCase.execute(1L, 2L));

        verify(appUserTypeGateway).associate(1L, 2L);
    }

    @Test
    void deveRejeitarIdDeUsuarioNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(null, 2L)
        );

        verifyNoInteractions(appUserTypeGateway, userTypeGateway);
    }

    @Test
    void deveRejeitarIdDeUsuarioZeroOuNegativo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(0L, 2L)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(-1L, 2L)
        );

        verifyNoInteractions(appUserTypeGateway, userTypeGateway);
    }

    @Test
    void deveRejeitarIdDeTipoNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(1L, null)
        );

        verifyNoInteractions(appUserTypeGateway, userTypeGateway);
    }

    @Test
    void deveRejeitarIdDeTipoZeroOuNegativo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(1L, 0L)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(1L, -1L)
        );

        verifyNoInteractions(appUserTypeGateway, userTypeGateway);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {
        when(appUserTypeGateway.appUserExists(1L))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(1L, 2L)
        );

        assertEquals("Usuário não encontrado.", exception.getMessage());
        verify(userTypeGateway, never()).findById(anyLong());
        verify(appUserTypeGateway, never()).associate(anyLong(), anyLong());
    }

    @Test
    void deveLancarExcecaoQuandoTipoNaoExistir() {
        when(appUserTypeGateway.appUserExists(1L))
                .thenReturn(true);
        when(userTypeGateway.findById(2L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(1L, 2L)
        );

        assertEquals(
                "Tipo de usuário não encontrado.",
                exception.getMessage()
        );

        verify(appUserTypeGateway, never()).associate(anyLong(), anyLong());
    }
}