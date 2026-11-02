package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.CustomerOutputDto;

public interface GetCustomerByIdUseCase {

    CustomerOutputDto execute(Long personId);
}
