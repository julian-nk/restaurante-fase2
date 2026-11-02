package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.RestaurantOwnerOutputDto;

public interface GetRestaurantOwnerByIdUseCase {

    RestaurantOwnerOutputDto execute(Long personId);
}
