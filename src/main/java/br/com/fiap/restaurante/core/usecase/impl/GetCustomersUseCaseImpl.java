package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.CustomerOutputDto;
import br.com.fiap.restaurante.core.gateway.CustomerGateway;
import br.com.fiap.restaurante.core.usecase.GetCustomersUseCase;

import java.util.List;

public class GetCustomersUseCaseImpl implements GetCustomersUseCase {

    private final CustomerGateway customerGateway;

    public GetCustomersUseCaseImpl(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    @Override
    public List<CustomerOutputDto> execute() {
        return customerGateway.findAll().stream().map(CustomerOutputDto::from).toList();
    }
}
