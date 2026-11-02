package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.ChangePasswordInputDto;

public interface ChangeAppUserPasswordUseCase {

    void execute(ChangePasswordInputDto input);
}
