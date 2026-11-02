package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.Customer;
import br.com.fiap.restaurante.core.domain.Person;
import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.dto.CreateCustomerInputDto;
import br.com.fiap.restaurante.core.dto.CustomerOutputDto;
import br.com.fiap.restaurante.core.dto.LoginInputDto;
import br.com.fiap.restaurante.core.exception.CustomerAlreadyRegisteredException;
import br.com.fiap.restaurante.core.exception.EmailAlreadyRegisteredException;
import br.com.fiap.restaurante.core.factory.CustomerFactory;
import br.com.fiap.restaurante.core.gateway.CustomerGateway;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantOwnerGateway;
import br.com.fiap.restaurante.core.usecase.RegisterCustomerUseCase;
import br.com.fiap.restaurante.core.usecase.impl.PersonAppUserService.PendingAppUser;

import java.util.ArrayList;
import java.util.List;

public class RegisterCustomerUseCaseImpl implements RegisterCustomerUseCase {

    private final CustomerGateway customerGateway;
    private final PersonGateway personGateway;
    private final CustomerFactory customerFactory;
    private final RestaurantOwnerGateway restaurantOwnerGateway;
    private final PersonAppUserService personAppUserService;

    public RegisterCustomerUseCaseImpl(
            CustomerGateway customerGateway,
            PersonGateway personGateway,
            CustomerFactory customerFactory,
            RestaurantOwnerGateway restaurantOwnerGateway,
            PersonAppUserService personAppUserService
    ) {
        this.customerGateway = customerGateway;
        this.personGateway = personGateway;
        this.customerFactory = customerFactory;
        this.restaurantOwnerGateway = restaurantOwnerGateway;
        this.personAppUserService = personAppUserService;
    }

    @Override
    public CustomerOutputDto execute(CreateCustomerInputDto input, LoginInputDto login) {
        String cpf = Person.normalizeCpf(input.cpf());
        Long personId = personGateway.findIdByCpf(cpf).orElse(null);

        if (personId != null && customerGateway.existsById(personId)) {
            throw new CustomerAlreadyRegisteredException(personId);
        }
        if (personGateway.isEmailUsedByAnotherPerson(input.email(), personId)) {
            throw new EmailAlreadyRegisteredException(input.email());
        }

        Customer customer = customerFactory.create(personId, input);
        // o usuário fica com os tipos de todos os papéis da pessoa, inclusive o de dono, se ela já for dona
        List<String> userTypeCodes = new ArrayList<>(List.of(UserType.CUSTOMER_CODE));
        if (personId != null && restaurantOwnerGateway.existsById(personId)) {
            userTypeCodes.add(UserType.RESTAURANT_OWNER_CODE);
        }
        PendingAppUser appUser = personAppUserService.prepare(
                personId, login, personAppUserService.requireUserTypes(userTypeCodes));

        Customer saved = customerGateway.save(customer);
        appUser.save(saved.getId());
        return CustomerOutputDto.from(saved);
    }
}
