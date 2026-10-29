package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.exception.CustomerNotFoundException;
import br.com.fiap.restaurante.core.gateway.CustomerGateway;
import br.com.fiap.restaurante.core.usecase.DeleteCustomerUseCase;

/**
 * Remove só o papel de cliente. A pessoa continua cadastrada, com os outros papéis que tiver; para excluí-la,
 * use a exclusão de pessoa.
 */
public class DeleteCustomerUseCaseImpl implements DeleteCustomerUseCase {

    private final CustomerGateway customerGateway;

    public DeleteCustomerUseCaseImpl(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    @Override
    public void execute(Long id) {
        if (!customerGateway.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }
        customerGateway.deleteById(id);
    }
}
