package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.factory.impl.AppUserFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.UserTypeFactoryImpl;
import br.com.fiap.restaurante.core.exception.RepositoryAccessException;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserTypeEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserTypeId;
import br.com.fiap.restaurante.infra.database.jpa.entity.UserTypeEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.AppUserRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.AppUserTypeRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.UserTypeRepository;
import br.com.fiap.restaurante.infra.database.mapper.AppUserMapper;
import br.com.fiap.restaurante.infra.database.mapper.UserTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppUserTypeJpaGatewayTest {

    private AppUserTypeRepository appUserTypeRepository;
    private UserTypeRepository userTypeRepository;
    private AppUserTypeJpaGateway gateway;

    @BeforeEach
    void setUp() {
        appUserTypeRepository = mock(AppUserTypeRepository.class);
        userTypeRepository = mock(UserTypeRepository.class);
        gateway = new AppUserTypeJpaGateway(
                appUserTypeRepository,
                userTypeRepository,
                new UserTypeMapper(new UserTypeFactoryImpl())
        );
    }

    @Test
    void shouldAssociateBySavingTheAssociation() {
        gateway.associate(1L, 2L);

        ArgumentCaptor<AppUserTypeEntity> captor = ArgumentCaptor.forClass(AppUserTypeEntity.class);
        verify(appUserTypeRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().getAppUserId());
        assertEquals(2L, captor.getValue().getUserTypeId());
    }

    @Test
    void shouldRemoveByCompositeId() {
        gateway.remove(1L, 2L);

        verify(appUserTypeRepository).deleteById(new AppUserTypeId(1L, 2L));
    }

    @Test
    void shouldMapUserTypesOfAppUser() {
        when(userTypeRepository.findByAppUserId(1L))
                .thenReturn(List.of(new UserTypeEntity(2L, "Cliente", "customer")));

        List<UserType> result = gateway.findUserTypesByAppUserId(1L);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals("customer", result.get(0).getCode());
    }

    @Test
    void shouldWrapRepositoryFailures() {
        when(appUserTypeRepository.save(any(AppUserTypeEntity.class)))
                .thenThrow(new IllegalStateException("violação de FK"));

        assertThrows(RepositoryAccessException.class, () -> gateway.associate(1L, 99L));
    }

    @Test
    void appUserGatewayShouldCheckExistence() {
        AppUserRepository appUserRepository = mock(AppUserRepository.class);
        when(appUserRepository.existsById(1L)).thenReturn(true);
        when(appUserRepository.existsById(2L)).thenReturn(false);
        AppUserJpaGateway appUserGateway = new AppUserJpaGateway(appUserRepository, new AppUserMapper(new AppUserFactoryImpl()));

        assertTrue(appUserGateway.existsById(1L));
        assertFalse(appUserGateway.existsById(2L));
    }
}
