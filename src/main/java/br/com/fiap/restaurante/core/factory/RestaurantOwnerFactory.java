package br.com.fiap.restaurante.core.factory;

import br.com.fiap.restaurante.core.domain.Address;
import br.com.fiap.restaurante.core.domain.RestaurantOwner;
import br.com.fiap.restaurante.core.dto.CreateRestaurantOwnerInputDto;

import java.time.OffsetDateTime;

public interface RestaurantOwnerFactory {

    /**
     * Cria o dono de restaurante a partir dos dados do cadastro.
     *
     * @param personId id da pessoa já cadastrada com o mesmo CPF (que é cliente), ou nulo para uma pessoa nova
     */
    RestaurantOwner create(Long personId, CreateRestaurantOwnerInputDto input);

    /** Reconstrói um dono já existente, a partir dos dados da pessoa e do papel gravados no banco. */
    RestaurantOwner restore(
            Long id,
            String name,
            String email,
            String cpf,
            Address address,
            OffsetDateTime createdOn,
            String businessEmail
    );
}
