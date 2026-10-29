package br.com.fiap.restaurante.core.controller;

import br.com.fiap.restaurante.core.dto.CreateRestaurantInputDto;
import br.com.fiap.restaurante.core.dto.RestaurantOutputDto;
import br.com.fiap.restaurante.core.dto.UpdateRestaurantInputDto;
import br.com.fiap.restaurante.core.usecase.CreateRestaurantUseCase;
import br.com.fiap.restaurante.core.usecase.DeleteRestaurantUseCase;
import br.com.fiap.restaurante.core.usecase.GetRestaurantByIdUseCase;
import br.com.fiap.restaurante.core.usecase.GetRestaurantsUseCase;
import br.com.fiap.restaurante.core.usecase.UpdateRestaurantUseCase;

import java.util.List;

public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;
    private final GetRestaurantsUseCase getRestaurantsUseCase;
    private final GetRestaurantByIdUseCase getRestaurantByIdUseCase;
    private final UpdateRestaurantUseCase updateRestaurantUseCase;
    private final DeleteRestaurantUseCase deleteRestaurantUseCase;

    public RestaurantController(
            CreateRestaurantUseCase createRestaurantUseCase,
            GetRestaurantsUseCase getRestaurantsUseCase,
            GetRestaurantByIdUseCase getRestaurantByIdUseCase,
            UpdateRestaurantUseCase updateRestaurantUseCase,
            DeleteRestaurantUseCase deleteRestaurantUseCase
    ) {
        this.createRestaurantUseCase = createRestaurantUseCase;
        this.getRestaurantsUseCase = getRestaurantsUseCase;
        this.getRestaurantByIdUseCase = getRestaurantByIdUseCase;
        this.updateRestaurantUseCase = updateRestaurantUseCase;
        this.deleteRestaurantUseCase = deleteRestaurantUseCase;
    }

    public RestaurantOutputDto create(CreateRestaurantInputDto input) {
        return createRestaurantUseCase.execute(input);
    }

    public List<RestaurantOutputDto> findAll() {
        return getRestaurantsUseCase.execute();
    }

    public RestaurantOutputDto findById(Long id) {
        return getRestaurantByIdUseCase.execute(id);
    }

    public RestaurantOutputDto update(UpdateRestaurantInputDto input) {
        return updateRestaurantUseCase.execute(input);
    }

    public void delete(Long id) {
        deleteRestaurantUseCase.execute(id);
    }
}
