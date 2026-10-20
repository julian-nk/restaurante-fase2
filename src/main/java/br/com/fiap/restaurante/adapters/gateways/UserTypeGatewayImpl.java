
package br.com.fiap.restaurante.adapters.gateways;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.interfaces.IUserTypeGateway;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserTypeGatewayImpl implements IUserTypeGateway {

    private final UserTypeRepository repository;

    public UserTypeGatewayImpl(UserTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserType save(UserType userType) {
        return repository.save(userType);
    }

    @Override
    public Optional<UserType> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<UserType> findAll() {
        return repository.findAll();
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
