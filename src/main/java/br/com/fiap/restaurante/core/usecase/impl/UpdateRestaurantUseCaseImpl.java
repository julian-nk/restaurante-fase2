package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.OpeningHours;
import br.com.fiap.restaurante.core.domain.Restaurant;
import br.com.fiap.restaurante.core.dto.AddressInputDto;
import br.com.fiap.restaurante.core.dto.RestaurantOutputDto;
import br.com.fiap.restaurante.core.dto.UpdateRestaurantInputDto;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.exception.RestaurantNotFoundException;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerNotFoundException;
import br.com.fiap.restaurante.core.factory.OpeningHoursFactory;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantOwnerGateway;
import br.com.fiap.restaurante.core.usecase.UpdateRestaurantUseCase;

import java.util.List;

public class UpdateRestaurantUseCaseImpl implements UpdateRestaurantUseCase {

    private final RestaurantGateway restaurantGateway;
    private final RestaurantOwnerGateway restaurantOwnerGateway;
    private final OpeningHoursFactory openingHoursFactory;

    public UpdateRestaurantUseCaseImpl(
            RestaurantGateway restaurantGateway,
            RestaurantOwnerGateway restaurantOwnerGateway,
            OpeningHoursFactory openingHoursFactory
    ) {
        this.restaurantGateway = restaurantGateway;
        this.restaurantOwnerGateway = restaurantOwnerGateway;
        this.openingHoursFactory = openingHoursFactory;
    }

    @Override
    public RestaurantOutputDto execute(UpdateRestaurantInputDto input) {
        Restaurant restaurant = restaurantGateway.findById(input.id())
                .orElseThrow(() -> new RestaurantNotFoundException(input.id()));

        restaurant.update(input.name(), input.cuisineType(), input.ownerId());

        AddressInputDto address = input.address();
        if (address == null) {
            throw new InvalidFieldException("O campo endereço é obrigatório.");
        }
        restaurant.updateAddress(address.street(), address.number(), address.complement(),
                address.neighborhood(), address.city(), address.state(), address.zipCode());

        List<OpeningHours> openingHours = input.openingHours() == null
                ? List.of()
                : input.openingHours().stream().map(openingHoursFactory::create).toList();
        restaurant.replaceOpeningHours(openingHours);

        if (!restaurantOwnerGateway.existsById(restaurant.getOwnerId())) {
            throw new RestaurantOwnerNotFoundException(restaurant.getOwnerId());
        }

        return RestaurantOutputDto.from(restaurantGateway.save(restaurant));
    }
}
