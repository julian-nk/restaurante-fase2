package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.RestaurantOutputDto;

public interface GetRestaurantByIdUseCase {

    RestaurantOutputDto execute(Long id);
}
