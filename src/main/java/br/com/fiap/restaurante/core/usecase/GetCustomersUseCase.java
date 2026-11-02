package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.CustomerOutputDto;

import java.util.List;

public interface GetCustomersUseCase {

    List<CustomerOutputDto> execute();
}
