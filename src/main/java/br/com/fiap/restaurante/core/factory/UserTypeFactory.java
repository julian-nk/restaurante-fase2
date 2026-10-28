package br.com.fiap.restaurante.core.factory;

import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.dto.CreateUserTypeInputDto;

public interface UserTypeFactory {

    /** Cria um novo tipo de usuário (ainda sem ID) a partir dos dados de entrada. */
    UserType create(CreateUserTypeInputDto input);

    /** Reconstrói um tipo de usuário já existente, por exemplo a partir do banco. */
    UserType restore(Long id, String name, String code);
}
