package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.gateway.UserTypeGateway;
import br.com.fiap.restaurante.infra.database.jpa.repository.UserTypeRepository;
import br.com.fiap.restaurante.infra.database.mapper.UserTypeMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class UserTypeJpaGateway extends JpaGatewaySupport implements UserTypeGateway {

    private final UserTypeRepository userTypeRepository;
    private final UserTypeMapper mapper;

    public UserTypeJpaGateway(UserTypeRepository userTypeRepository, UserTypeMapper mapper) {
        this.userTypeRepository = userTypeRepository;
        this.mapper = mapper;
    }

    @Override
    public UserType save(UserType userType) {
        return call(() -> mapper.map(userTypeRepository.save(mapper.map(userType))));
    }

    @Override
    public Optional<UserType> findById(Long id) {
        return call(() -> userTypeRepository.findById(id).map(mapper::map));
    }

    @Override
    public List<UserType> findAllById(Collection<Long> ids) {
        return call(() -> userTypeRepository.findAllById(ids).stream()
                .map(mapper::map)
                .sorted(Comparator.comparing(UserType::getId))
                .toList());
    }

    @Override
    public List<UserType> findAll() {
        return call(() -> userTypeRepository.findAll().stream().map(mapper::map).toList());
    }

    @Override
    public boolean existsByCode(String code) {
        return call(() -> userTypeRepository.existsByCode(code));
    }

    @Override
    public void deleteById(Long id) {
        run(() -> userTypeRepository.deleteById(id));
    }
}
