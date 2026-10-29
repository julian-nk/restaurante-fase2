package br.com.fiap.restaurante.core.gateway;

import br.com.fiap.restaurante.core.domain.UserType;

import java.util.List;
import java.util.Optional;

public interface UserTypeGateway {

    UserType save(UserType userType);

    Optional<UserType> findById(Long id);

    List<UserType> findAll();

    boolean existsByCode(String code);

    void deleteById(Long id);
}
