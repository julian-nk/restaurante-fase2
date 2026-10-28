package br.com.fiap.restaurante.core.factory;

import br.com.fiap.restaurante.core.domain.Address;
import br.com.fiap.restaurante.core.domain.OpeningHours;
import br.com.fiap.restaurante.core.domain.Restaurant;
import br.com.fiap.restaurante.core.dto.CreateRestaurantInputDto;

import java.util.List;

public interface RestaurantFactory {

    /** Cria o restaurante com seu endereço e horários de funcionamento. */
    Restaurant create(CreateRestaurantInputDto input);

    Restaurant restore(
            Long id,
            String name,
            String cnpj,
            String cuisineType,
            Long ownerId,
            Address address,
            List<OpeningHours> openingHours
    );
}
