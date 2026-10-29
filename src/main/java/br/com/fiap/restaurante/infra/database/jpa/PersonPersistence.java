package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.Person;
import br.com.fiap.restaurante.infra.database.jpa.entity.AddressEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.PersonEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.AddressRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.PersonRepository;
import br.com.fiap.restaurante.infra.database.mapper.AddressMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Grava e lê os dados de pessoa (a tabela person e o endereço) para os gateways de cliente e de dono de
 * restaurante. Como {@link Person} é abstrata, a pessoa não tem gateway próprio de gravação: ela é sempre
 * gravada e lida junto com um dos papéis.
 *
 * <p>Os métodos não abrem transação nem traduzem exceções; isso fica com os gateways que os chamam.
 */
@Component
public class PersonPersistence {

    /** A linha de person e a do endereço dela, que fica nula quando a pessoa não tem endereço. */
    public record PersonRecord(PersonEntity person, AddressEntity address) {
    }

    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public PersonPersistence(
            PersonRepository personRepository,
            AddressRepository addressRepository,
            AddressMapper addressMapper
    ) {
        this.personRepository = personRepository;
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    /**
     * Grava a pessoa e o endereço: cria a pessoa se ela ainda não tiver id e, se tiver, atualiza. Se o endereço
     * foi trocado ou removido, o antigo é apagado, porque não pertence a mais ninguém.
     */
    public PersonRecord save(Person person) {
        Long previousAddressId = person.getId() == null
                ? null
                : personRepository.findById(person.getId()).map(PersonEntity::getAddressId).orElse(null);

        AddressEntity address = person.getAddress() == null
                ? null
                : addressRepository.save(addressMapper.map(person.getAddress()));
        Long addressId = address == null ? null : address.getId();

        PersonEntity saved = personRepository.save(new PersonEntity(
                person.getId(), person.getName(), person.getEmail(), person.getCpf(), addressId));

        if (previousAddressId != null && !previousAddressId.equals(addressId)) {
            addressRepository.deleteById(previousAddressId);
        }
        return new PersonRecord(saved, address);
    }

    public Optional<PersonRecord> findById(Long id) {
        return personRepository.findById(id).map(person -> new PersonRecord(
                person,
                person.getAddressId() == null ? null : addressRepository.findById(person.getAddressId()).orElse(null)));
    }

    /** Carrega várias pessoas de uma vez, com os endereços buscados numa única consulta. */
    public Map<Long, PersonRecord> findAllById(Collection<Long> ids) {
        List<PersonEntity> persons = personRepository.findAllById(ids);
        List<Long> addressIds = persons.stream().map(PersonEntity::getAddressId).filter(Objects::nonNull).toList();
        Map<Long, AddressEntity> addresses = addressRepository.findAllById(addressIds).stream()
                .collect(Collectors.toMap(AddressEntity::getId, Function.identity()));

        return persons.stream().collect(Collectors.toMap(
                PersonEntity::getId,
                person -> new PersonRecord(person, addresses.get(person.getAddressId()))));
    }
}
