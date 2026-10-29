package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.CreateCustomerInputDto;
import br.com.fiap.restaurante.core.dto.CustomerOutputDto;

public interface RegisterCustomerUseCase {

    CustomerOutputDto execute(CreateCustomerInputDto input);
}
