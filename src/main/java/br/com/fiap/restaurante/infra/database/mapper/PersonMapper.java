package br.com.fiap.restaurante.infra.database.mapper;

import br.com.fiap.restaurante.core.domain.Person;
import br.com.fiap.restaurante.core.factory.PersonFactory;
import br.com.fiap.restaurante.infra.database.jpa.entity.AddressEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.PersonEntity;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    private final PersonFactory personFactory;
    private final AddressMapper addressMapper;

    public PersonMapper(PersonFactory personFactory, AddressMapper addressMapper) {
        this.personFactory = personFactory;
        this.addressMapper = addressMapper;
    }

    /** @param address pode ser nulo (pessoa sem endereço) */
    public Person map(PersonEntity person, AddressEntity address) {
        return personFactory.restore(
                person.getId(),
                person.getName(),
                person.getEmail(),
                person.getCpf(),
                address == null ? null : addressMapper.map(address)
        );
    }
}
