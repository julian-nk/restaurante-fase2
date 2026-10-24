package br.com.fiap.restaurante.adapters.gateways;
import br.com.fiap.restaurante.domain.entities.UserType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AppUserTypeRepositoryTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private AppUserTypeRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(
                repository, "entityManager", entityManager
        );
    }

    @Test
    void deveRetornarTrueQuandoUsuarioExiste() {
        when(entityManager.createNativeQuery(anyString()))
                .thenReturn(query);
        when(query.setParameter("appUserId", 1L)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1L);

        assertTrue(repository.appUserExists(1L));
    }

    @Test
    void deveRetornarFalseQuandoUsuarioNaoExiste() {
        when(entityManager.createNativeQuery(anyString()))
                .thenReturn(query);
        when(query.setParameter("appUserId", 99L)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(0L);

        assertFalse(repository.appUserExists(99L));
    }

    @Test
    void deveBuscarTiposDoUsuario() {
        UserType userType = new UserType("Administrador", "ADMIN");
        List<UserType> tiposEsperados = List.of(userType);

        when(entityManager.createNativeQuery(anyString(), eq(UserType.class)))
                .thenReturn(query);
        when(query.setParameter("appUserId", 1L)).thenReturn(query);
        when(query.getResultList()).thenReturn(tiposEsperados);

        List<UserType> resultado =
                repository.findUserTypesByAppUserId(1L);

        assertEquals(tiposEsperados, resultado);
    }

    @Test
    void deveAssociarUsuarioAoTipo() {
        when(entityManager.createNativeQuery(anyString()))
                .thenReturn(query);
        when(query.setParameter(anyString(), anyLong())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> repository.associate(1L, 2L));

        verify(query).executeUpdate();
    }

    @Test
    void deveRemoverAssociacaoDoUsuario() {
        when(entityManager.createNativeQuery(anyString()))
                .thenReturn(query);
        when(query.setParameter(anyString(), anyLong())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> repository.remove(1L, 2L));

        verify(query).executeUpdate();
    }
}