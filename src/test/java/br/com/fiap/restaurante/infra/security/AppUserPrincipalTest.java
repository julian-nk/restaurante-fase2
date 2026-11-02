package br.com.fiap.restaurante.infra.security;

import br.com.fiap.restaurante.core.dto.RequesterDto;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppUserPrincipalTest {

    private static AppUserPrincipal user(Long personId, String... roles) {
        return new AppUserPrincipal("login", "hash", personId, AuthorityUtils.createAuthorityList(roles));
    }

    @Test
    void shouldTellTheCoreWhoIsCalling() {
        assertEquals(new RequesterDto(7L, false), user(7L, "ROLE_RESTAURANT_OWNER").toRequester());
        assertEquals(new RequesterDto(1L, true), user(1L, "ROLE_ADMIN", "ROLE_RESTAURANT_OWNER").toRequester());
        assertEquals(new RequesterDto(3L, false), user(3L).toRequester());
    }

    @Test
    void shouldReadTheRequesterFromTheAuthentication() {
        AppUserPrincipal owner = user(7L, "ROLE_RESTAURANT_OWNER");

        assertEquals(new RequesterDto(7L, false), AppUserPrincipal.requesterOf(
                UsernamePasswordAuthenticationToken.authenticated(owner, null, owner.getAuthorities())));
    }

    @Test
    void shouldRejectRequestWithoutAppUser() {
        assertThrows(IllegalStateException.class, () -> AppUserPrincipal.requesterOf(null));
        assertThrows(IllegalStateException.class, () -> AppUserPrincipal.requesterOf(
                UsernamePasswordAuthenticationToken.authenticated("anonimo", null, AuthorityUtils.NO_AUTHORITIES)));
    }
}
