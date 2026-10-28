package br.com.fiap.restaurante.core.factory.impl;

import br.com.fiap.restaurante.core.domain.RestaurantOwner;
import br.com.fiap.restaurante.core.dto.RestaurantOwnerInputDto;
import br.com.fiap.restaurante.core.factory.RestaurantOwnerFactory;

import java.time.OffsetDateTime;
import java.util.ArrayList;

public class RestaurantOwnerFactoryImpl implements RestaurantOwnerFactory {

    @Override
    public RestaurantOwner create(RestaurantOwnerInputDto input) {
        return RestaurantOwner.builder()
                .personId(input.personId())
                .createdOn(OffsetDateTime.now())
                .businessEmail(input.businessEmail())
                .restaurants(new ArrayList<>())
                .build();
    }

    @Override
    public RestaurantOwner restore(Long personId, OffsetDateTime createdOn, String businessEmail) {
        return RestaurantOwner.builder()
                .personId(personId)
                .createdOn(createdOn)
                .businessEmail(businessEmail)
                .restaurants(new ArrayList<>())
                .build();
    }
}
