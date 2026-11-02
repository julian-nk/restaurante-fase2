package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.CreatePersonInputDto;
import br.com.fiap.restaurante.core.dto.LoginInputDto;
import br.com.fiap.restaurante.core.dto.PersonOutputDto;

public interface CreatePersonUseCase {

    /** Cadastra a pessoa junto com o usuário de acesso dela; login e senha são obrigatórios. */
    PersonOutputDto execute(CreatePersonInputDto input, LoginInputDto login);
}
