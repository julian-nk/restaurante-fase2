package br.com.fiap.restaurante.interfaces;

import br.com.fiap.restaurante.domain.entities.UserType;

import java.util.List;
import java.util.Optional;

public interface IUserTypeGateway {

    UserType save(UserType userType);

    Optional<UserType> findById(Long id);

    List<UserType> findAll();

    boolean existsByCode(String code);

    void deleteById(Long id);

}
