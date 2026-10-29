package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.CustomerOutputDto;
import br.com.fiap.restaurante.core.exception.CustomerNotFoundException;
import br.com.fiap.restaurante.core.gateway.CustomerGateway;
import br.com.fiap.restaurante.core.usecase.GetCustomerByIdUseCase;

public class GetCustomerByIdUseCaseImpl implements GetCustomerByIdUseCase {

    private final CustomerGateway customerGateway;

    public GetCustomerByIdUseCaseImpl(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    @Override
    public CustomerOutputDto execute(Long personId) {
        return customerGateway.findById(personId)
                .map(CustomerOutputDto::from)
                .orElseThrow(() -> new CustomerNotFoundException(personId));
    }
}
