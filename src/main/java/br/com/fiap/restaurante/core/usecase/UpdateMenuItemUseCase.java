package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.MenuItemOutputDto;
import br.com.fiap.restaurante.core.dto.UpdateMenuItemInputDto;

public interface UpdateMenuItemUseCase {

    MenuItemOutputDto execute(UpdateMenuItemInputDto input);
}
