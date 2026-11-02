package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.Person;
import br.com.fiap.restaurante.core.domain.RestaurantOwner;
import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.dto.CreateRestaurantOwnerInputDto;
import br.com.fiap.restaurante.core.dto.LoginInputDto;
import br.com.fiap.restaurante.core.dto.RestaurantOwnerOutputDto;
import br.com.fiap.restaurante.core.exception.EmailAlreadyRegisteredException;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerAlreadyRegisteredException;
import br.com.fiap.restaurante.core.factory.RestaurantOwnerFactory;
import br.com.fiap.restaurante.core.gateway.CustomerGateway;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantOwnerGateway;
import br.com.fiap.restaurante.core.usecase.RegisterRestaurantOwnerUseCase;
import br.com.fiap.restaurante.core.usecase.impl.PersonAppUserService.PendingAppUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Cadastra uma pessoa como dona de restaurante. Se já existir uma pessoa com o CPF informado (cadastrada sem
 * papel ou como cliente), a pessoa é reaproveitada: ela ganha o papel de dona e seus dados (nome, e-mail e endereço) são
 * substituídos pelos enviados.
 */
public class RegisterRestaurantOwnerUseCaseImpl implements RegisterRestaurantOwnerUseCase {

    private final RestaurantOwnerGateway restaurantOwnerGateway;
    private final PersonGateway personGateway;
    private final RestaurantOwnerFactory restaurantOwnerFactory;
    private final CustomerGateway customerGateway;
    private final PersonAppUserService personAppUserService;

    public RegisterRestaurantOwnerUseCaseImpl(
            RestaurantOwnerGateway restaurantOwnerGateway,
            PersonGateway personGateway,
            RestaurantOwnerFactory restaurantOwnerFactory,
            CustomerGateway customerGateway,
            PersonAppUserService personAppUserService
    ) {
        this.restaurantOwnerGateway = restaurantOwnerGateway;
        this.personGateway = personGateway;
        this.restaurantOwnerFactory = restaurantOwnerFactory;
        this.customerGateway = customerGateway;
        this.personAppUserService = personAppUserService;
    }

    @Override
    public RestaurantOwnerOutputDto execute(CreateRestaurantOwnerInputDto input, LoginInputDto login) {
        String cpf = Person.normalizeCpf(input.cpf());
        Long personId = personGateway.findIdByCpf(cpf).orElse(null);

        if (personId != null && restaurantOwnerGateway.existsById(personId)) {
            throw new RestaurantOwnerAlreadyRegisteredException(personId);
        }
        if (personGateway.isEmailUsedByAnotherPerson(input.email(), personId)) {
            throw new EmailAlreadyRegisteredException(input.email());
        }

        RestaurantOwner owner = restaurantOwnerFactory.create(personId, input);
        // o usuário fica com os tipos de todos os papéis da pessoa, inclusive o de cliente, se ela já for cliente
        List<String> userTypeCodes = new ArrayList<>(List.of(UserType.RESTAURANT_OWNER_CODE));
        if (personId != null && customerGateway.existsById(personId)) {
            userTypeCodes.add(UserType.CUSTOMER_CODE);
        }
        PendingAppUser appUser = personAppUserService.prepare(
                personId, login, personAppUserService.requireUserTypes(userTypeCodes));

        RestaurantOwner saved = restaurantOwnerGateway.save(owner);
        appUser.save(saved.getId());
        return RestaurantOwnerOutputDto.from(saved);
    }
}
