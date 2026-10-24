package br.com.fiap.restaurante.adapters.gateways;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.interfaces.IAppUserTypeGateway;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppUserTypeGatewayImpl implements IAppUserTypeGateway {

    private final AppUserTypeRepository repository;

    public AppUserTypeGatewayImpl(AppUserTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void associate(Long appUserId, Long userTypeId) {
        repository.associate(appUserId, userTypeId);
    }

    @Override
    public List<UserType> findUserTypesByAppUserId(Long appUserId) {
        return repository.findUserTypesByAppUserId(appUserId);
    }

    @Override
    public void remove(Long appUserId, Long userTypeId) {
        repository.remove(appUserId, userTypeId);
    }

    @Override
    public boolean appUserExists(Long appUserId) {
        return repository.appUserExists(appUserId);
    }
}