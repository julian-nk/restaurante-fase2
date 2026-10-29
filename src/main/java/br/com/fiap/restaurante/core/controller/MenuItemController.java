package br.com.fiap.restaurante.core.controller;

import br.com.fiap.restaurante.core.dto.CreateMenuItemInputDto;
import br.com.fiap.restaurante.core.dto.MenuItemOutputDto;
import br.com.fiap.restaurante.core.dto.UpdateMenuItemInputDto;
import br.com.fiap.restaurante.core.usecase.CreateMenuItemUseCase;
import br.com.fiap.restaurante.core.usecase.DeleteMenuItemUseCase;
import br.com.fiap.restaurante.core.usecase.GetMenuItemByIdUseCase;
import br.com.fiap.restaurante.core.usecase.GetMenuItemsByRestaurantIdUseCase;
import br.com.fiap.restaurante.core.usecase.GetMenuItemsUseCase;
import br.com.fiap.restaurante.core.usecase.UpdateMenuItemUseCase;

import java.util.List;

public class MenuItemController {

    private final CreateMenuItemUseCase createMenuItemUseCase;
    private final GetMenuItemsUseCase getMenuItemsUseCase;
    private final GetMenuItemByIdUseCase getMenuItemByIdUseCase;
    private final GetMenuItemsByRestaurantIdUseCase getMenuItemsByRestaurantIdUseCase;
    private final UpdateMenuItemUseCase updateMenuItemUseCase;
    private final DeleteMenuItemUseCase deleteMenuItemUseCase;

    public MenuItemController(
            CreateMenuItemUseCase createMenuItemUseCase,
            GetMenuItemsUseCase getMenuItemsUseCase,
            GetMenuItemByIdUseCase getMenuItemByIdUseCase,
            GetMenuItemsByRestaurantIdUseCase getMenuItemsByRestaurantIdUseCase,
            UpdateMenuItemUseCase updateMenuItemUseCase,
            DeleteMenuItemUseCase deleteMenuItemUseCase
    ) {
        this.createMenuItemUseCase = createMenuItemUseCase;
        this.getMenuItemsUseCase = getMenuItemsUseCase;
        this.getMenuItemByIdUseCase = getMenuItemByIdUseCase;
        this.getMenuItemsByRestaurantIdUseCase = getMenuItemsByRestaurantIdUseCase;
        this.updateMenuItemUseCase = updateMenuItemUseCase;
        this.deleteMenuItemUseCase = deleteMenuItemUseCase;
    }

    public MenuItemOutputDto create(CreateMenuItemInputDto input) {
        return createMenuItemUseCase.execute(input);
    }

    public List<MenuItemOutputDto> findAll() {
        return getMenuItemsUseCase.execute();
    }

    public MenuItemOutputDto findById(Long id) {
        return getMenuItemByIdUseCase.execute(id);
    }

    public List<MenuItemOutputDto> findByRestaurantId(Long restaurantId) {
        return getMenuItemsByRestaurantIdUseCase.execute(restaurantId);
    }

    public MenuItemOutputDto update(UpdateMenuItemInputDto input) {
        return updateMenuItemUseCase.execute(input);
    }

    public void delete(Long id) {
        deleteMenuItemUseCase.execute(id);
    }
}
