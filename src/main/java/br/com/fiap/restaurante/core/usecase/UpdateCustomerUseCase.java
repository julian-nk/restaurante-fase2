package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.CustomerOutputDto;
import br.com.fiap.restaurante.core.dto.UpdateCustomerInputDto;

public interface UpdateCustomerUseCase {

    CustomerOutputDto execute(UpdateCustomerInputDto input);
}
