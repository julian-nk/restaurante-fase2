package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.ChangePasswordInputDto;
import br.com.fiap.restaurante.core.dto.RequesterDto;

public interface ChangeAppUserPasswordUseCase {

    /**
     * Troca a senha do usuário. Só o próprio usuário troca a senha, inclusive quando quem chama é administrador,
     * e precisa informar a senha atual.
     */
    void execute(ChangePasswordInputDto input, RequesterDto requester);
}
