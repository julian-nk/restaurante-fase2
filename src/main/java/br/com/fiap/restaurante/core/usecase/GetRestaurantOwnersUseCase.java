package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.RestaurantOwnerOutputDto;

import java.util.List;

public interface GetRestaurantOwnersUseCase {

    List<RestaurantOwnerOutputDto> execute();
}
