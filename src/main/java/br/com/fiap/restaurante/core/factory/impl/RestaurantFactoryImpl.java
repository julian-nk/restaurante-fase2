package br.com.fiap.restaurante.core.factory.impl;

import br.com.fiap.restaurante.core.domain.Address;
import br.com.fiap.restaurante.core.domain.OpeningHours;
import br.com.fiap.restaurante.core.domain.Restaurant;
import br.com.fiap.restaurante.core.dto.CreateRestaurantInputDto;
import br.com.fiap.restaurante.core.factory.AddressFactory;
import br.com.fiap.restaurante.core.factory.OpeningHoursFactory;
import br.com.fiap.restaurante.core.factory.RestaurantFactory;

import java.util.List;

public class RestaurantFactoryImpl implements RestaurantFactory {

    private final AddressFactory addressFactory;
    private final OpeningHoursFactory openingHoursFactory;

    public RestaurantFactoryImpl(AddressFactory addressFactory, OpeningHoursFactory openingHoursFactory) {
        this.addressFactory = addressFactory;
        this.openingHoursFactory = openingHoursFactory;
    }

    @Override
    public Restaurant create(CreateRestaurantInputDto input) {
        List<OpeningHours> openingHours = input.openingHours() == null
                ? List.of()
                : input.openingHours().stream().map(openingHoursFactory::create).toList();

        return Restaurant.builder()
                .name(input.name())
                .cnpj(input.cnpj())
                .cuisineType(input.cuisineType())
                .ownerId(input.ownerId())
                .address(addressFactory.create(input.address()))
                .openingHours(openingHours)
                .build();
    }

    @Override
    public Restaurant restore(
            Long id,
            String name,
            String cnpj,
            String cuisineType,
            Long ownerId,
            Address address,
            List<OpeningHours> openingHours
    ) {
        return Restaurant.builder()
                .id(id)
                .name(name)
                .cnpj(cnpj)
                .cuisineType(cuisineType)
                .ownerId(ownerId)
                .address(address)
                .openingHours(openingHours)
                .build();
    }
}
