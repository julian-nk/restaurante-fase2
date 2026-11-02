package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.domain.PasswordPolicy;
import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.dto.CreateAppUserInputDto;
import br.com.fiap.restaurante.core.dto.LoginInputDto;
import br.com.fiap.restaurante.core.exception.DefaultUserTypeNotFoundException;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.exception.LoginAlreadyInUseException;
import br.com.fiap.restaurante.core.exception.PersonAlreadyHasUserException;
import br.com.fiap.restaurante.core.factory.AppUserFactory;
import br.com.fiap.restaurante.core.gateway.AppUserGateway;
import br.com.fiap.restaurante.core.gateway.PasswordEncoderGateway;
import br.com.fiap.restaurante.core.gateway.UserTypeGateway;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class PersonAppUserService {

    private final AppUserGateway appUserGateway;
    private final UserTypeGateway userTypeGateway;
    private final PasswordEncoderGateway passwordEncoder;
    private final AppUserFactory appUserFactory;

    public PersonAppUserService(
            AppUserGateway appUserGateway,
            UserTypeGateway userTypeGateway,
            PasswordEncoderGateway passwordEncoder,
            AppUserFactory appUserFactory
    ) {
        this.appUserGateway = appUserGateway;
        this.userTypeGateway = userTypeGateway;
        this.passwordEncoder = passwordEncoder;
        this.appUserFactory = appUserFactory;
    }

    @FunctionalInterface
    public interface PendingAppUser {
        void save(Long personId);
    }

    public Set<Long> requireUserTypes(Collection<String> codes) {
        Set<Long> ids = new LinkedHashSet<>();
        for (String code : codes) {
            ids.add(userTypeGateway.findByCode(code)
                                   .map(UserType::getId)
                                   .orElseThrow(() -> new DefaultUserTypeNotFoundException(code)));
        }
        return ids;
    }

    /**
     * Confere o usuário de acesso da pessoa antes de gravar qualquer coisa.
     *
     * <p>Se a pessoa já tem usuário, ele ganha os tipos, e login e senha não podem ser informados. Se ainda não
     * tem (pessoa nova, ou cadastrada antes de o login ser obrigatório), login e senha são obrigatórios, e o
     * usuário é criado já com os tipos.</p>
     *
     * @param personId    a pessoa, ou nulo quando ela ainda vai ser criada
     * @param login       login e senha enviados no cadastro (podem estar vazios)
     * @param userTypeIds tipos que o usuário precisa ter
     * @throws PersonAlreadyHasUserException se a pessoa já tem usuário e um login foi informado
     * @throws InvalidFieldException         se faltar login ou senha, ou se estiverem fora do formato
     * @throws LoginAlreadyInUseException    se o login já pertencer a outro usuário
     */
    public PendingAppUser prepare(Long personId, LoginInputDto login, Set<Long> userTypeIds) {
        boolean loginInformed = login != null && !login.isEmpty();
        Optional<AppUser> existing = personId == null ? Optional.empty() : appUserGateway.findByPersonId(personId);

        if (existing.isPresent()) {
            if (loginInformed) {
                throw new PersonAlreadyHasUserException(personId);
            }
            AppUser user = existing.get();
            return savedPersonId -> {
                boolean changed = false;
                for (Long userTypeId : userTypeIds) {
                    changed |= user.addUserType(userTypeId);
                }
                if (changed) {
                    appUserGateway.save(user);
                }
            };
        }

        if (!loginInformed) {
            throw new InvalidFieldException("Informe o login e a senha: a pessoa ainda não tem usuário de acesso.");
        }
        String normalizedLogin = AppUser.normalizeLogin(login.login());
        PasswordPolicy.validate(login.password());
        if (appUserGateway.existsByLogin(normalizedLogin)) {
            throw new LoginAlreadyInUseException(normalizedLogin);
        }
        String passwordHash = passwordEncoder.encode(login.password());
        return savedPersonId -> {
            AppUser user = appUserFactory.create(
                    new CreateAppUserInputDto(savedPersonId, normalizedLogin, login.password()), passwordHash);
            userTypeIds.forEach(user::addUserType);
            appUserGateway.save(user);
        };
    }

    /**
     * Tira do usuário da pessoa o tipo com esse código, quando ela perde o papel. Se a pessoa não tiver usuário,
     * ou o tipo não existir mais, não há o que tirar.
     */
    public void removeUserType(Long personId, String code) {
        userTypeGateway.findByCode(code).ifPresent(type -> appUserGateway.findByPersonId(personId).ifPresent(user -> {
            if (user.removeUserType(type.getId())) {
                appUserGateway.save(user);
            }
        }));
    }
}
