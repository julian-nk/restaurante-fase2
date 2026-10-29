package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.CreateRestaurantOwnerInputDto;
import br.com.fiap.restaurante.core.dto.RestaurantOwnerOutputDto;

public interface RegisterRestaurantOwnerUseCase {

    RestaurantOwnerOutputDto execute(CreateRestaurantOwnerInputDto input);
}
