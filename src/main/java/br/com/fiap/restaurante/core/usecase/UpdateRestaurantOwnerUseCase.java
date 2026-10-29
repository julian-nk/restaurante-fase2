package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.RestaurantOwnerOutputDto;
import br.com.fiap.restaurante.core.dto.UpdateRestaurantOwnerInputDto;

public interface UpdateRestaurantOwnerUseCase {

    RestaurantOwnerOutputDto execute(UpdateRestaurantOwnerInputDto input);
}
