package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.domain.PasswordPolicy;
import br.com.fiap.restaurante.core.dto.AppUserOutputDto;
import br.com.fiap.restaurante.core.dto.CreateAppUserInputDto;
import br.com.fiap.restaurante.core.exception.LoginAlreadyInUseException;
import br.com.fiap.restaurante.core.exception.PersonAlreadyHasUserException;
import br.com.fiap.restaurante.core.exception.PersonNotFoundException;
import br.com.fiap.restaurante.core.factory.AppUserFactory;
import br.com.fiap.restaurante.core.gateway.AppUserGateway;
import br.com.fiap.restaurante.core.gateway.PasswordEncoderGateway;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.usecase.CreateAppUserUseCase;

public class CreateAppUserUseCaseImpl implements CreateAppUserUseCase {

    private final AppUserGateway appUserGateway;
    private final PersonGateway personGateway;
    private final PasswordEncoderGateway passwordEncoder;
    private final AppUserFactory appUserFactory;

    public CreateAppUserUseCaseImpl(
            AppUserGateway appUserGateway,
            PersonGateway personGateway,
            PasswordEncoderGateway passwordEncoder,
            AppUserFactory appUserFactory
    ) {
        this.appUserGateway = appUserGateway;
        this.personGateway = personGateway;
        this.passwordEncoder = passwordEncoder;
        this.appUserFactory = appUserFactory;
    }

    @Override
    public AppUserOutputDto execute(CreateAppUserInputDto input) {
        PasswordPolicy.validate(input.password());
        AppUser appUser = appUserFactory.create(input, passwordEncoder.encode(input.password()));

        if (!personGateway.existsById(appUser.getPersonId())) {
            throw new PersonNotFoundException(appUser.getPersonId());
        }
        if (appUserGateway.existsByPersonId(appUser.getPersonId())) {
            throw new PersonAlreadyHasUserException(appUser.getPersonId());
        }
        if (appUserGateway.existsByLogin(appUser.getLogin())) {
            throw new LoginAlreadyInUseException(appUser.getLogin());
        }

        return AppUserOutputDto.from(appUserGateway.save(appUser));
    }
}
