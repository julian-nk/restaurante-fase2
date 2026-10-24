package br.com.fiap.restaurante.interfaces;

import br.com.fiap.restaurante.domain.entities.UserType;

import java.util.List;

public interface IAppUserTypeGateway {

    void associate(Long appUserId, Long userTypeId);

    List<UserType> findUserTypesByAppUserId(Long appUserId);

    void remove(Long appUserId, Long userTypeId);

    boolean appUserExists(Long appUserId);

}