package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.gateway.AppUserTypeGateway;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserTypeEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserTypeId;
import br.com.fiap.restaurante.infra.database.jpa.repository.AppUserTypeRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.UserTypeRepository;
import br.com.fiap.restaurante.infra.database.mapper.UserTypeMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppUserTypeJpaGateway extends JpaGatewaySupport implements AppUserTypeGateway {

    private final AppUserTypeRepository appUserTypeRepository;
    private final UserTypeRepository userTypeRepository;
    private final UserTypeMapper userTypeMapper;

    public AppUserTypeJpaGateway(
            AppUserTypeRepository appUserTypeRepository,
            UserTypeRepository userTypeRepository,
            UserTypeMapper userTypeMapper
    ) {
        this.appUserTypeRepository = appUserTypeRepository;
        this.userTypeRepository = userTypeRepository;
        this.userTypeMapper = userTypeMapper;
    }

    /**
     * Como a entidade já chega com o id preenchido, o save faz merge: só insere se a associação ainda não existir.
     * Associar duas vezes, portanto, não gera erro.
     */
    @Override
    public void associate(Long appUserId, Long userTypeId) {
        run(() -> appUserTypeRepository.save(new AppUserTypeEntity(appUserId, userTypeId)));
    }

    @Override
    public List<UserType> findUserTypesByAppUserId(Long appUserId) {
        return call(() -> userTypeRepository.findByAppUserId(appUserId)
                .stream()
                .map(userTypeMapper::map)
                .toList());
    }

    /** Não gera erro se a associação não existir. */
    @Override
    public void remove(Long appUserId, Long userTypeId) {
        run(() -> appUserTypeRepository.deleteById(new AppUserTypeId(appUserId, userTypeId)));
    }
}
