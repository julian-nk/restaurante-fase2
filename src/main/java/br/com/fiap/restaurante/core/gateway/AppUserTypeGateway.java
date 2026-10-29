package br.com.fiap.restaurante.core.gateway;

import br.com.fiap.restaurante.core.domain.UserType;

import java.util.List;

public interface AppUserTypeGateway {

    void associate(Long appUserId, Long userTypeId);

    List<UserType> findUserTypesByAppUserId(Long appUserId);

    void remove(Long appUserId, Long userTypeId);
}
