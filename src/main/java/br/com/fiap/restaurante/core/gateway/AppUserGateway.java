package br.com.fiap.restaurante.core.gateway;

import br.com.fiap.restaurante.core.domain.AppUser;

import java.util.List;
import java.util.Optional;

public interface AppUserGateway {

    AppUser save(AppUser appUser);

    Optional<AppUser> findById(Long id);

    List<AppUser> findAll();

    boolean existsById(Long id);

    boolean existsByPersonId(Long personId);

    boolean existsByLogin(String login);

    void deleteById(Long id);
}
