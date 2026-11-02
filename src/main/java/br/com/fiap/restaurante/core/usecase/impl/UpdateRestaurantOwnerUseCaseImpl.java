package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.RestaurantOwner;
import br.com.fiap.restaurante.core.dto.RestaurantOwnerOutputDto;
import br.com.fiap.restaurante.core.dto.UpdateRestaurantOwnerInputDto;
import br.com.fiap.restaurante.core.exception.EmailAlreadyRegisteredException;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerNotFoundException;
import br.com.fiap.restaurante.core.factory.AddressFactory;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantOwnerGateway;
import br.com.fiap.restaurante.core.usecase.UpdateRestaurantOwnerUseCase;

public class UpdateRestaurantOwnerUseCaseImpl implements UpdateRestaurantOwnerUseCase {

    private final RestaurantOwnerGateway restaurantOwnerGateway;
    private final PersonGateway personGateway;
    private final AddressFactory addressFactory;

    public UpdateRestaurantOwnerUseCaseImpl(
            RestaurantOwnerGateway restaurantOwnerGateway,
            PersonGateway personGateway,
            AddressFactory addressFactory
    ) {
        this.restaurantOwnerGateway = restaurantOwnerGateway;
        this.personGateway = personGateway;
        this.addressFactory = addressFactory;
    }

    @Override
    public RestaurantOwnerOutputDto execute(UpdateRestaurantOwnerInputDto input) {
        RestaurantOwner owner = restaurantOwnerGateway.findById(input.id())
                .orElseThrow(() -> new RestaurantOwnerNotFoundException(input.id()));

        if (personGateway.isEmailUsedByAnotherPerson(input.email(), owner.getId())) {
            throw new EmailAlreadyRegisteredException(input.email());
        }

        owner.updatePersonalData(input.name(), input.email(),
                input.address() == null ? null : addressFactory.create(input.address()));
        owner.updateBusinessEmail(input.businessEmail());
        return RestaurantOwnerOutputDto.from(restaurantOwnerGateway.save(owner));
    }
}
