package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.domain.PasswordPolicy;
import br.com.fiap.restaurante.core.dto.ChangePasswordInputDto;
import br.com.fiap.restaurante.core.exception.AppUserNotFoundException;
import br.com.fiap.restaurante.core.exception.InvalidPasswordException;
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
    public void execute(ChangePasswordInputDto input) {
        AppUser appUser = appUserGateway.findById(input.appUserId())
                .orElseThrow(() -> new AppUserNotFoundException(input.appUserId()));

        if (input.currentPassword() == null
                || !passwordEncoder.matches(input.currentPassword(), appUser.getPasswordHash())) {
            throw new InvalidPasswordException();
        }

        PasswordPolicy.validate(input.newPassword());
        appUser.changePasswordHash(passwordEncoder.encode(input.newPassword()));

        appUserGateway.save(appUser);
    }
}
