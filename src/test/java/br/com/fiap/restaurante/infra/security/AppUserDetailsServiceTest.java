package br.com.fiap.restaurante.infra.security;

import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.UserTypeEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.AppUserRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.UserTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppUserDetailsServiceTest {

    private AppUserRepository appUserRepository;
    private UserTypeRepository userTypeRepository;
    private AppUserDetailsService service;

    @BeforeEach
    void setUp() {
        appUserRepository = mock(AppUserRepository.class);
        userTypeRepository = mock(UserTypeRepository.class);
        service = new AppUserDetailsService(appUserRepository, userTypeRepository);
    }

    @Test
    void shouldLoadUserWithPasswordHashAndTypesAsRoles() {
        when(appUserRepository.findByLoginIgnoreCase("Dono"))
                .thenReturn(Optional.of(new AppUserEntity(2L, 20L, "dono", "$2a$10$hash")));
        when(userTypeRepository.findByAppUserId(2L)).thenReturn(List.of(
                new UserTypeEntity(1L, "Dono de Restaurante", "restaurant_owner"),
                new UserTypeEntity(4L, "Cliente Premium", "premium_customer")));

        UserDetails user = service.loadUserByUsername("Dono");

        assertEquals("dono", user.getUsername());
        assertEquals("$2a$10$hash", user.getPassword());
        assertEquals(Set.of("ROLE_RESTAURANT_OWNER", "ROLE_PREMIUM_CUSTOMER"),
                AuthorityUtils.authorityListToSet(user.getAuthorities()));
    }

    @Test
    void shouldLoadUserWithoutTypes() {
        when(appUserRepository.findByLoginIgnoreCase("maria"))
                .thenReturn(Optional.of(new AppUserEntity(3L, 30L, "maria", "$2a$10$hash")));

        assertTrue(service.loadUserByUsername("maria").getAuthorities().isEmpty());
    }

    @Test
    void shouldRejectUnknownLogin() {
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("ninguem"));
        verifyNoInteractions(userTypeRepository);
    }
}
