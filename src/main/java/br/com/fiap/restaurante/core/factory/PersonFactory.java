package br.com.fiap.restaurante.core.factory;

import br.com.fiap.restaurante.core.domain.Address;
import br.com.fiap.restaurante.core.domain.Person;
import br.com.fiap.restaurante.core.dto.CreatePersonInputDto;

public interface PersonFactory {

    Person create(CreatePersonInputDto input);

    /** @param address pode ser nulo */
    Person restore(Long id, String name, String email, String cpf, Address address);
}
