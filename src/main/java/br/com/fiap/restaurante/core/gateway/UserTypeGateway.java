package br.com.fiap.restaurante.core.gateway;

import br.com.fiap.restaurante.core.domain.UserType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserTypeGateway {

    UserType save(UserType userType);

    Optional<UserType> findById(Long id);

    /** Busca os tipos com esses ids, em ordem de id. Ids que não existem são ignorados. */
    List<UserType> findAllById(Collection<Long> ids);

    List<UserType> findAll();

    boolean existsByCode(String code);

    void deleteById(Long id);
}
