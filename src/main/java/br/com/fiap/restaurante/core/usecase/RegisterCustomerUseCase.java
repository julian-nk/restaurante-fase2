package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.CreateCustomerInputDto;
import br.com.fiap.restaurante.core.dto.CustomerOutputDto;
import br.com.fiap.restaurante.core.dto.LoginInputDto;

public interface RegisterCustomerUseCase {

    /**
     * Cadastra o cliente. O usuário de acesso da pessoa recebe o tipo {@code customer}; se ela ainda não tem
     * usuário, ele é criado com o login e a senha informados.
     */
    CustomerOutputDto execute(CreateCustomerInputDto input, LoginInputDto login);
}
