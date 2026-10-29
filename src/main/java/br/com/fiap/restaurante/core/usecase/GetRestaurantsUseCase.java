package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.RestaurantOutputDto;

import java.util.List;

public interface GetRestaurantsUseCase {

    List<RestaurantOutputDto> execute();
}
