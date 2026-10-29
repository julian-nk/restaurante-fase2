package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.exception.CustomerNotFoundException;
import br.com.fiap.restaurante.core.gateway.CustomerGateway;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantOwnerGateway;
import br.com.fiap.restaurante.core.usecase.DeleteCustomerUseCase;

/**
 * Remove o papel de cliente. Como não pode haver pessoa sem papel, se ela não for também dona de restaurante
 * a pessoa é excluída junto, com o endereço e, em cascata, o usuário de acesso.
 */
public class DeleteCustomerUseCaseImpl implements DeleteCustomerUseCase {

    private final CustomerGateway customerGateway;
    private final RestaurantOwnerGateway restaurantOwnerGateway;
    private final PersonGateway personGateway;

    public DeleteCustomerUseCaseImpl(
            CustomerGateway customerGateway,
            RestaurantOwnerGateway restaurantOwnerGateway,
            PersonGateway personGateway
    ) {
        this.customerGateway = customerGateway;
        this.restaurantOwnerGateway = restaurantOwnerGateway;
        this.personGateway = personGateway;
    }

    @Override
    public void execute(Long id) {
        if (!customerGateway.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }

        if (restaurantOwnerGateway.existsById(id)) {
            customerGateway.deleteById(id);
        } else {
            personGateway.deleteById(id);
        }
    }
}
