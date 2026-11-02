package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.domain.PasswordPolicy;
import br.com.fiap.restaurante.core.dto.ChangePasswordInputDto;
import br.com.fiap.restaurante.core.dto.RequesterDto;
import br.com.fiap.restaurante.core.exception.InvalidPasswordException;
import br.com.fiap.restaurante.core.exception.PasswordChangeNotAllowedException;
import br.com.fiap.restaurante.core.gateway.AppUserGateway;
import br.com.fiap.restaurante.core.gateway.PasswordEncoderGateway;
import br.com.fiap.restaurante.core.usecase.ChangeAppUserPasswordUseCase;

public class ChangeAppUserPasswordUseCaseImpl implements ChangeAppUserPasswordUseCase {

    private final AppUserGateway appUserGateway;
    private final PasswordEncoderGateway passwordEncoder;

    public ChangeAppUserPasswordUseCaseImpl(AppUserGateway appUserGateway, PasswordEncoderGateway passwordEncoder) {
        this.appUserGateway = appUserGateway;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void execute(ChangePasswordInputDto input, RequesterDto requester) {
        // Cada pessoa tem no máximo um usuário, então o usuário logado é o que tem a pessoa dele. Um ID que não é
        // o do usuário logado recebe a mesma resposta exista ele ou não, para não revelar quais usuários existem.
        AppUser appUser = appUserGateway.findById(input.appUserId())
                .filter(user -> user.getPersonId().equals(requester.personId()))
                .orElseThrow(PasswordChangeNotAllowedException::new);

        if (input.currentPassword() == null
                || !passwordEncoder.matches(input.currentPassword(), appUser.getPasswordHash())) {
            throw new InvalidPasswordException();
        }

        PasswordPolicy.validate(input.newPassword());
        appUser.changePasswordHash(passwordEncoder.encode(input.newPassword()));

        appUserGateway.save(appUser);
    }
}
