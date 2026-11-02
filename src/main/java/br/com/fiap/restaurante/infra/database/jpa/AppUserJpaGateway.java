package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.gateway.AppUserGateway;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserTypeEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.AppUserRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.AppUserTypeRepository;
import br.com.fiap.restaurante.infra.database.mapper.AppUserMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Grava e lê o usuário junto com os tipos dele: a tabela app_user guarda o usuário e a app_user_type, os
 * tipos. Ler sempre os tipos é o que permite gravar o usuário de volta sem perder nenhum.
 */
@Component
public class AppUserJpaGateway extends JpaGatewaySupport implements AppUserGateway {

    private final AppUserRepository appUserRepository;
    private final AppUserTypeRepository appUserTypeRepository;
    private final AppUserMapper mapper;

    public AppUserJpaGateway(
            AppUserRepository appUserRepository,
            AppUserTypeRepository appUserTypeRepository,
            AppUserMapper mapper
    ) {
        this.appUserRepository = appUserRepository;
        this.appUserTypeRepository = appUserTypeRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public AppUser save(AppUser appUser) {
        return call(() -> {
            AppUserEntity saved = appUserRepository.save(mapper.map(appUser));
            syncUserTypes(saved.getId(), appUser.getUserTypeIds());
            appUserRepository.flush();
            return mapper.map(saved, appUser.getUserTypeIds());
        });
    }

    /** Deixa a app_user_type igual aos tipos do usuário: apaga as associações que saíram e grava as novas. */
    private void syncUserTypes(Long appUserId, Set<Long> userTypeIds) {
        List<AppUserTypeEntity> stored = appUserTypeRepository.findByAppUserId(appUserId);
        Set<Long> storedIds = stored.stream().map(AppUserTypeEntity::getUserTypeId).collect(Collectors.toSet());

        List<AppUserTypeEntity> removed = stored.stream()
                .filter(association -> !userTypeIds.contains(association.getUserTypeId()))
                .toList();
        List<AppUserTypeEntity> added = userTypeIds.stream()
                .filter(userTypeId -> !storedIds.contains(userTypeId))
                .map(userTypeId -> new AppUserTypeEntity(appUserId, userTypeId))
                .toList();

        if (!removed.isEmpty()) {
            appUserTypeRepository.deleteAll(removed);
        }
        if (!added.isEmpty()) {
            appUserTypeRepository.saveAll(added);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppUser> findById(Long id) {
        return call(() -> appUserRepository.findById(id)
                .map(entity -> mapper.map(entity, userTypeIdsOf(appUserTypeRepository.findByAppUserId(id)))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppUser> findAll() {
        return call(() -> {
            List<AppUserEntity> users = appUserRepository.findAll();
            if (users.isEmpty()) {
                return List.of();
            }
            Map<Long, List<Long>> userTypeIdsByUser = appUserTypeRepository
                    .findByAppUserIdIn(users.stream().map(AppUserEntity::getId).toList()).stream()
                    .collect(Collectors.groupingBy(AppUserTypeEntity::getAppUserId,
                            Collectors.mapping(AppUserTypeEntity::getUserTypeId, Collectors.toList())));

            return users.stream()
                    .map(user -> mapper.map(user, userTypeIdsByUser.getOrDefault(user.getId(), List.of())))
                    .toList();
        });
    }

    private static List<Long> userTypeIdsOf(List<AppUserTypeEntity> associations) {
        return associations.stream().map(AppUserTypeEntity::getUserTypeId).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return call(() -> appUserRepository.existsById(id));
    }

    @Override
    public boolean existsByPersonId(Long personId) {
        return call(() -> appUserRepository.existsByPersonId(personId));
    }

    @Override
    public boolean existsByLogin(String login) {
        return call(() -> appUserRepository.existsByLoginIgnoreCase(login));
    }

    /** O banco remove em cascata as associações com os tipos. */
    @Override
    public void deleteById(Long id) {
        run(() -> appUserRepository.deleteById(id));
    }
}
