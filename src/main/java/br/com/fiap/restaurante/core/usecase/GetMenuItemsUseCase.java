package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.MenuItemOutputDto;

import java.util.List;

public interface GetMenuItemsUseCase {

    List<MenuItemOutputDto> execute();
}
