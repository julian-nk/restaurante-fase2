package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.CreateRestaurantInputDto;
import br.com.fiap.restaurante.core.dto.RestaurantOutputDto;

public interface CreateRestaurantUseCase {

    RestaurantOutputDto execute(CreateRestaurantInputDto input);
}
