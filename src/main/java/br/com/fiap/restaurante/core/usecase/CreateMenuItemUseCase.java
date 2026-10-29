package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.CreateMenuItemInputDto;
import br.com.fiap.restaurante.core.dto.MenuItemOutputDto;

public interface CreateMenuItemUseCase {

    MenuItemOutputDto execute(CreateMenuItemInputDto input);
}
