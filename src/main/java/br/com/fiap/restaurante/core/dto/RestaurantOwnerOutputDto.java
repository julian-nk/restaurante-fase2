package br.com.fiap.restaurante.core.dto;

import br.com.fiap.restaurante.core.domain.RestaurantOwner;

import java.time.OffsetDateTime;

public record RestaurantOwnerOutputDto(
        Long id,
        String name,
        String email,
        String cpf,
        AddressOutputDto address,
        String businessEmail,
        OffsetDateTime createdOn
) {

    public static RestaurantOwnerOutputDto from(RestaurantOwner owner) {
        return new RestaurantOwnerOutputDto(
                owner.getId(),
                owner.getName(),
                owner.getEmail(),
                owner.getCpf(),
                AddressOutputDto.from(owner.getAddress()),
                owner.getBusinessEmail(),
                owner.getCreatedOn()
        );
    }
}
