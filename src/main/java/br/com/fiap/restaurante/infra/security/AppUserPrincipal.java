package br.com.fiap.restaurante.infra.security;

import br.com.fiap.restaurante.core.dto.RequesterDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serial;
import java.util.Collection;


public class AppUserPrincipal extends User {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String ADMIN_ROLE = "ROLE_" + ApiAccessRules.roleOf(UserTypeEnum.ADMIN.getCode());

    private final Long personId;

    public AppUserPrincipal(String login, String passwordHash, Long personId,
                            Collection<? extends GrantedAuthority> authorities) {
        super(login, passwordHash, authorities);
        this.personId = personId;
    }

    public static RequesterDto requesterOf(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUserPrincipal user)) {
            throw new IllegalStateException("A requisição não tem um usuário da aplicação autenticado.");
        }
        return user.toRequester();
    }

    public Long getPersonId() {
        return personId;
    }

    public RequesterDto toRequester() {
        boolean admin = getAuthorities().stream()
                                        .anyMatch(authority -> ADMIN_ROLE.equals(authority.getAuthority()));
        return new RequesterDto(personId, admin);
    }
}
