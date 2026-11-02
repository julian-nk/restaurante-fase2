package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.MenuItemOutputDto;

public interface GetMenuItemByIdUseCase {

    MenuItemOutputDto execute(Long id);
}
