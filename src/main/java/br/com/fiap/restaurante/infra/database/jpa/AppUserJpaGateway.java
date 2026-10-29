package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.gateway.AppUserGateway;
import br.com.fiap.restaurante.infra.database.jpa.repository.AppUserRepository;
import br.com.fiap.restaurante.infra.database.mapper.AppUserMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AppUserJpaGateway extends JpaGatewaySupport implements AppUserGateway {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper mapper;

    public AppUserJpaGateway(AppUserRepository appUserRepository, AppUserMapper mapper) {
        this.appUserRepository = appUserRepository;
        this.mapper = mapper;
    }

    @Override
    public AppUser save(AppUser appUser) {
        return call(() -> mapper.map(appUserRepository.save(mapper.map(appUser))));
    }

    @Override
    public Optional<AppUser> findById(Long id) {
        return call(() -> appUserRepository.findById(id).map(mapper::map));
    }

    @Override
    public List<AppUser> findAll() {
        return call(() -> appUserRepository.findAll().stream().map(mapper::map).toList());
    }

    @Override
    public boolean existsById(Long id) {
        return call(() -> appUserRepository.existsById(id));
    }

    @Override
    public boolean existsByPersonId(Long personId) {
        return call(() -> appUserRepository.existsByPersonId(personId));
    }

    @Override
    public boolean existsByLogin(String login) {
        return call(() -> appUserRepository.existsByLoginIgnoreCase(login));
    }

    @Override
    public void deleteById(Long id) {
        run(() -> appUserRepository.deleteById(id));
    }
}
