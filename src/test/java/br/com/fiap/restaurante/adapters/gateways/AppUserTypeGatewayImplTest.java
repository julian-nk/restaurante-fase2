package br.com.fiap.restaurante.adapters.gateways;

import br.com.fiap.restaurante.domain.entities.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppUserTypeGatewayImplTest {

    private AppUserTypeRepository repository;
    private AppUserTypeGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        repository = mock(AppUserTypeRepository.class);
        gateway = new AppUserTypeGatewayImpl(repository);
    }

    @Test
    void deveAssociarTipoAoUsuario() {
        gateway.associate(1L, 2L);

        verify(repository).associate(1L, 2L);
    }

    @Test
    void deveBuscarTiposDoUsuario() {
        List<UserType> userTypes = List.of(
                new UserType("Administrador", "ADMIN")
        );

        when(repository.findUserTypesByAppUserId(1L))
                .thenReturn(userTypes);

        List<UserType> resultado = gateway.findUserTypesByAppUserId(1L);

        assertEquals(userTypes, resultado);
        verify(repository).findUserTypesByAppUserId(1L);
    }

    @Test
    void deveRemoverTipoDoUsuario() {
        gateway.remove(1L, 2L);

        verify(repository).remove(1L, 2L);
    }

    @Test
    void deveVerificarSeUsuarioExiste() {
        when(repository.appUserExists(1L)).thenReturn(true);

        assertTrue(gateway.appUserExists(1L));

        verify(repository).appUserExists(1L);
    }

    @Test
    void deveRetornarFalseQuandoUsuarioNaoExiste() {
        when(repository.appUserExists(99L)).thenReturn(false);

        assertFalse(gateway.appUserExists(99L));

        verify(repository).appUserExists(99L);
    }
}