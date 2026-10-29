package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.exception.RepositoryAccessException;
import br.com.fiap.restaurante.core.factory.impl.AppUserFactoryImpl;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserTypeEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.AppUserRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.AppUserTypeRepository;
import br.com.fiap.restaurante.infra.database.mapper.AppUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Os captores de saveAll/deleteAll recebem Iterable genérico, daí os avisos suprimidos.
@SuppressWarnings({"unchecked", "rawtypes"})
class AppUserJpaGatewayTest {

    private static final AppUserEntity MARIA = new AppUserEntity(1L, 10L, "maria", "hash");

    private final AppUserFactoryImpl factory = new AppUserFactoryImpl();
    private AppUserRepository appUserRepository;
    private AppUserTypeRepository appUserTypeRepository;
    private AppUserJpaGateway gateway;

    @BeforeEach
    void setUp() {
        appUserRepository = mock(AppUserRepository.class);
        appUserTypeRepository = mock(AppUserTypeRepository.class);
        gateway = new AppUserJpaGateway(appUserRepository, appUserTypeRepository, new AppUserMapper(factory));
        when(appUserRepository.save(any())).thenReturn(MARIA);
    }

    private List<AppUserTypeEntity> captured(ArgumentCaptor<?> captor) {
        return (List<AppUserTypeEntity>) captor.getValue();
    }

    @Test
    void shouldSaveNewUserWithItsTypes() {
        AppUser saved = gateway.save(factory.restore(null, 10L, "maria", "hash", Set.of(2L, 3L)));

        ArgumentCaptor<Iterable> added = ArgumentCaptor.forClass(Iterable.class);
        verify(appUserTypeRepository).saveAll(added.capture());
        assertEquals(Set.of(2L, 3L),
                Set.copyOf(captured(added).stream().map(AppUserTypeEntity::getUserTypeId).toList()));
        assertTrue(captured(added).stream().allMatch(association -> association.getAppUserId().equals(1L)));
        verify(appUserTypeRepository, never()).deleteAll(any());
        verify(appUserRepository).flush();
        assertEquals(1L, saved.getId());
        assertEquals(Set.of(2L, 3L), saved.getUserTypeIds());
    }

    @Test
    void shouldKeepOnlyTheTypesTheUserHasWhenSaving() {
        AppUserTypeEntity type2 = new AppUserTypeEntity(1L, 2L);
        AppUserTypeEntity type3 = new AppUserTypeEntity(1L, 3L);
        when(appUserTypeRepository.findByAppUserId(1L)).thenReturn(List.of(type2, type3));

        gateway.save(factory.restore(1L, 10L, "maria", "hash", Set.of(3L, 4L)));

        ArgumentCaptor<Iterable> removed = ArgumentCaptor.forClass(Iterable.class);
        ArgumentCaptor<Iterable> added = ArgumentCaptor.forClass(Iterable.class);
        verify(appUserTypeRepository).deleteAll(removed.capture());
        verify(appUserTypeRepository).saveAll(added.capture());
        assertEquals(List.of(type2), captured(removed));
        assertEquals(List.of(4L), captured(added).stream().map(AppUserTypeEntity::getUserTypeId).toList());
    }

    @Test
    void shouldNotTouchTheAssociationsWhenTheTypesDidNotChange() {
        when(appUserTypeRepository.findByAppUserId(1L)).thenReturn(List.of(new AppUserTypeEntity(1L, 2L)));

        gateway.save(factory.restore(1L, 10L, "maria", "hash", Set.of(2L)));

        verify(appUserTypeRepository, never()).deleteAll(any());
        verify(appUserTypeRepository, never()).saveAll(any());
    }

    @Test
    void shouldFindByIdWithTheTypes() {
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(MARIA));
        when(appUserTypeRepository.findByAppUserId(1L))
                .thenReturn(List.of(new AppUserTypeEntity(1L, 3L), new AppUserTypeEntity(1L, 2L)));

        AppUser user = gateway.findById(1L).orElseThrow();

        assertEquals("hash", user.getPasswordHash());
        assertEquals(Set.of(2L, 3L), user.getUserTypeIds());
        assertTrue(gateway.findById(2L).isEmpty());
    }

    @Test
    void shouldFindAllLoadingTheTypesInOneQuery() {
        AppUserEntity joao = new AppUserEntity(2L, 20L, "joao", "hash");
        when(appUserRepository.findAll()).thenReturn(List.of(MARIA, joao));
        when(appUserTypeRepository.findByAppUserIdIn(List.of(1L, 2L)))
                .thenReturn(List.of(new AppUserTypeEntity(1L, 2L), new AppUserTypeEntity(1L, 3L)));

        List<AppUser> users = gateway.findAll();

        assertEquals(Set.of(2L, 3L), users.get(0).getUserTypeIds());
        assertTrue(users.get(1).getUserTypeIds().isEmpty());
        verify(appUserTypeRepository, never()).findByAppUserId(any());
    }

    @Test
    void shouldNotQueryTheTypesWhenThereAreNoUsers() {
        assertTrue(gateway.findAll().isEmpty());
        verify(appUserTypeRepository, never()).findByAppUserIdIn(any());
    }

    @Test
    void shouldDelegateExistenceChecksAndDeletion() {
        when(appUserRepository.existsById(1L)).thenReturn(true);
        when(appUserRepository.existsByPersonId(10L)).thenReturn(true);
        when(appUserRepository.existsByLoginIgnoreCase("maria")).thenReturn(true);

        assertTrue(gateway.existsById(1L));
        assertFalse(gateway.existsById(2L));
        assertTrue(gateway.existsByPersonId(10L));
        assertTrue(gateway.existsByLogin("maria"));
        gateway.deleteById(1L);
        verify(appUserRepository).deleteById(1L);
    }

    @Test
    void shouldWrapRepositoryFailures() {
        when(appUserTypeRepository.saveAll(any())).thenThrow(new IllegalStateException("violação de FK"));

        assertThrows(RepositoryAccessException.class,
                () -> gateway.save(factory.restore(1L, 10L, "maria", "hash", Set.of(99L))));
    }
}
