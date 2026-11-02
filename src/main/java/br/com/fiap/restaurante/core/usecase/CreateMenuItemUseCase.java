package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.CreateMenuItemInputDto;
import br.com.fiap.restaurante.core.dto.MenuItemOutputDto;
import br.com.fiap.restaurante.core.dto.RequesterDto;

public interface CreateMenuItemUseCase {

    MenuItemOutputDto execute(CreateMenuItemInputDto input, RequesterDto requester);
}
