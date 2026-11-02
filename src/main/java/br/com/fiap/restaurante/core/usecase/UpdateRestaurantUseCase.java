package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.RequesterDto;
import br.com.fiap.restaurante.core.dto.RestaurantOutputDto;
import br.com.fiap.restaurante.core.dto.UpdateRestaurantInputDto;

public interface UpdateRestaurantUseCase {

    RestaurantOutputDto execute(UpdateRestaurantInputDto input, RequesterDto requester);
}
