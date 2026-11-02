package br.com.fiap.restaurante.core.dto;

import br.com.fiap.restaurante.core.domain.AppUser;

/** Não expõe o hash da senha. */
public record AppUserOutputDto(Long id, Long personId, String login) {

    public static AppUserOutputDto from(AppUser appUser) {
        return new AppUserOutputDto(appUser.getId(), appUser.getPersonId(), appUser.getLogin());
    }
}
