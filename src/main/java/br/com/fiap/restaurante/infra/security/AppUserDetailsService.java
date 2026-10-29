package br.com.fiap.restaurante.infra.security;

import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.UserTypeEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.AppUserRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.UserTypeRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final UserTypeRepository userTypeRepository;

    public AppUserDetailsService(AppUserRepository appUserRepository, UserTypeRepository userTypeRepository) {
        this.appUserRepository = appUserRepository;
        this.userTypeRepository = userTypeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        AppUserEntity user = appUserRepository.findByLoginIgnoreCase(login)
                                              .orElseThrow(() -> new UsernameNotFoundException(
                                                      "Usuário não encontrado: " + login));

        List<SimpleGrantedAuthority> roles = userTypeRepository.findByAppUserId(user.getId()).stream()
                                                               .map(UserTypeEntity::getCode)
                                                               .map(code -> new SimpleGrantedAuthority(
                                                                       "ROLE_" + code.toUpperCase(Locale.ROOT)))
                                                               .toList();

        return User.withUsername(user.getLogin())
                   .password(user.getPasswordHash())
                   .authorities(roles)
                   .build();
    }
}
