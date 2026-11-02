package br.com.fiap.restaurante.core.controller;

import br.com.fiap.restaurante.core.dto.CreateRestaurantOwnerInputDto;
import br.com.fiap.restaurante.core.dto.RestaurantOwnerOutputDto;
import br.com.fiap.restaurante.core.dto.UpdateRestaurantOwnerInputDto;
import br.com.fiap.restaurante.core.usecase.DeleteRestaurantOwnerUseCase;
import br.com.fiap.restaurante.core.usecase.GetRestaurantOwnerByIdUseCase;
import br.com.fiap.restaurante.core.usecase.GetRestaurantOwnersUseCase;
import br.com.fiap.restaurante.core.usecase.RegisterRestaurantOwnerUseCase;
import br.com.fiap.restaurante.core.usecase.UpdateRestaurantOwnerUseCase;

import java.util.List;

public class RestaurantOwnerController {

    private final RegisterRestaurantOwnerUseCase registerRestaurantOwnerUseCase;
    private final GetRestaurantOwnersUseCase getRestaurantOwnersUseCase;
    private final GetRestaurantOwnerByIdUseCase getRestaurantOwnerByIdUseCase;
    private final UpdateRestaurantOwnerUseCase updateRestaurantOwnerUseCase;
    private final DeleteRestaurantOwnerUseCase deleteRestaurantOwnerUseCase;

    public RestaurantOwnerController(
            RegisterRestaurantOwnerUseCase registerRestaurantOwnerUseCase,
            GetRestaurantOwnersUseCase getRestaurantOwnersUseCase,
            GetRestaurantOwnerByIdUseCase getRestaurantOwnerByIdUseCase,
            UpdateRestaurantOwnerUseCase updateRestaurantOwnerUseCase,
            DeleteRestaurantOwnerUseCase deleteRestaurantOwnerUseCase
    ) {
        this.registerRestaurantOwnerUseCase = registerRestaurantOwnerUseCase;
        this.getRestaurantOwnersUseCase = getRestaurantOwnersUseCase;
        this.getRestaurantOwnerByIdUseCase = getRestaurantOwnerByIdUseCase;
        this.updateRestaurantOwnerUseCase = updateRestaurantOwnerUseCase;
        this.deleteRestaurantOwnerUseCase = deleteRestaurantOwnerUseCase;
    }

    public RestaurantOwnerOutputDto register(CreateRestaurantOwnerInputDto input) {
        return registerRestaurantOwnerUseCase.execute(input);
    }

    public List<RestaurantOwnerOutputDto> findAll() {
        return getRestaurantOwnersUseCase.execute();
    }

    public RestaurantOwnerOutputDto findById(Long id) {
        return getRestaurantOwnerByIdUseCase.execute(id);
    }

    public RestaurantOwnerOutputDto update(UpdateRestaurantOwnerInputDto input) {
        return updateRestaurantOwnerUseCase.execute(input);
    }

    public void delete(Long id) {
        deleteRestaurantOwnerUseCase.execute(id);
    }
}
