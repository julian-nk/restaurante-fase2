package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.RequesterDto;

public interface DeleteMenuItemUseCase {

    void execute(Long id, RequesterDto requester);
}
