package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.Person;
import br.com.fiap.restaurante.core.dto.CreateRestaurantOwnerInputDto;
import br.com.fiap.restaurante.core.dto.RestaurantOwnerOutputDto;
import br.com.fiap.restaurante.core.exception.EmailAlreadyRegisteredException;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerAlreadyRegisteredException;
import br.com.fiap.restaurante.core.factory.RestaurantOwnerFactory;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantOwnerGateway;
import br.com.fiap.restaurante.core.usecase.RegisterRestaurantOwnerUseCase;

/**
 * Cadastra uma pessoa como dona de restaurante. Se já existir uma pessoa com o CPF informado (porque ela é
 * cliente), a pessoa é reaproveitada: ela ganha o papel de dona e seus dados (nome, e-mail e endereço) são
 * substituídos pelos enviados.
 */
public class RegisterRestaurantOwnerUseCaseImpl implements RegisterRestaurantOwnerUseCase {

    private final RestaurantOwnerGateway restaurantOwnerGateway;
    private final PersonGateway personGateway;
    private final RestaurantOwnerFactory restaurantOwnerFactory;

    public RegisterRestaurantOwnerUseCaseImpl(
            RestaurantOwnerGateway restaurantOwnerGateway,
            PersonGateway personGateway,
            RestaurantOwnerFactory restaurantOwnerFactory
    ) {
        this.restaurantOwnerGateway = restaurantOwnerGateway;
        this.personGateway = personGateway;
        this.restaurantOwnerFactory = restaurantOwnerFactory;
    }

    @Override
    public RestaurantOwnerOutputDto execute(CreateRestaurantOwnerInputDto input) {
        String cpf = Person.normalizeCpf(input.cpf());
        Long personId = personGateway.findIdByCpf(cpf).orElse(null);

        if (personId != null && restaurantOwnerGateway.existsById(personId)) {
            throw new RestaurantOwnerAlreadyRegisteredException(personId);
        }
        if (personGateway.isEmailUsedByAnotherPerson(input.email(), personId)) {
            throw new EmailAlreadyRegisteredException(input.email());
        }

        return RestaurantOwnerOutputDto.from(
                restaurantOwnerGateway.save(restaurantOwnerFactory.create(personId, input)));
    }
}
