package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.exception.CustomerNotFoundException;
import br.com.fiap.restaurante.core.gateway.CustomerGateway;
import br.com.fiap.restaurante.core.usecase.DeleteCustomerUseCase;

/**
 * Remove só o papel de cliente, e o usuário da pessoa perde o tipo {@code customer}. A pessoa continua cadastrada,
 * com os outros papéis que tiver; para excluí-la, use a exclusão de pessoa.
 */
public class DeleteCustomerUseCaseImpl implements DeleteCustomerUseCase {

    private final CustomerGateway customerGateway;
    private final PersonAppUserService personAppUserService;

    public DeleteCustomerUseCaseImpl(CustomerGateway customerGateway, PersonAppUserService personAppUserService) {
        this.customerGateway = customerGateway;
        this.personAppUserService = personAppUserService;
    }

    @Override
    public void execute(Long id) {
        if (!customerGateway.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }
        customerGateway.deleteById(id);
        personAppUserService.removeUserType(id, UserType.CUSTOMER_CODE);
    }
}
