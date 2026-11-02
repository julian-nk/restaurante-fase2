package br.com.fiap.restaurante.core.factory.impl;

import br.com.fiap.restaurante.core.domain.Address;
import br.com.fiap.restaurante.core.domain.RestaurantOwner;
import br.com.fiap.restaurante.core.dto.CreateRestaurantOwnerInputDto;
import br.com.fiap.restaurante.core.factory.AddressFactory;
import br.com.fiap.restaurante.core.factory.RestaurantOwnerFactory;

import java.time.OffsetDateTime;
import java.util.ArrayList;

public class RestaurantOwnerFactoryImpl implements RestaurantOwnerFactory {

    private final AddressFactory addressFactory;

    public RestaurantOwnerFactoryImpl(AddressFactory addressFactory) {
        this.addressFactory = addressFactory;
    }

    @Override
    public RestaurantOwner create(Long personId, CreateRestaurantOwnerInputDto input) {
        return RestaurantOwner.builder()
                .id(personId)
                .name(input.name())
                .email(input.email())
                .cpf(input.cpf())
                .address(input.address() == null ? null : addressFactory.create(input.address()))
                .createdOn(OffsetDateTime.now())
                .businessEmail(input.businessEmail())
                .ownedRestaurants(new ArrayList<>())
                .build();
    }

    @Override
    public RestaurantOwner restore(
            Long id,
            String name,
            String email,
            String cpf,
            Address address,
            OffsetDateTime createdOn,
            String businessEmail
    ) {
        return RestaurantOwner.builder()
                .id(id)
                .name(name)
                .email(email)
                .cpf(cpf)
                .address(address)
                .createdOn(createdOn)
                .businessEmail(businessEmail)
                .ownedRestaurants(new ArrayList<>())
                .build();
    }
}
