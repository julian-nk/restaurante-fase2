package br.com.fiap.restaurante.core.factory;

import br.com.fiap.restaurante.core.domain.RestaurantOwner;
import br.com.fiap.restaurante.core.dto.RestaurantOwnerInputDto;

import java.time.OffsetDateTime;

public interface RestaurantOwnerFactory {

    RestaurantOwner create(RestaurantOwnerInputDto input);

    RestaurantOwner restore(Long personId, OffsetDateTime createdOn, String businessEmail);
}
