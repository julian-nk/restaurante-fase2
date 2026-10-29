package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.Person;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.infra.database.jpa.entity.AddressEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.PersonEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.AddressRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.PersonRepository;
import br.com.fiap.restaurante.infra.database.mapper.AddressMapper;
import br.com.fiap.restaurante.infra.database.mapper.PersonMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PersonJpaGateway extends JpaGatewaySupport implements PersonGateway {

    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;
    private final PersonMapper personMapper;
    private final AddressMapper addressMapper;

    public PersonJpaGateway(
            PersonRepository personRepository,
            AddressRepository addressRepository,
            PersonMapper personMapper,
            AddressMapper addressMapper
    ) {
        this.personRepository = personRepository;
        this.addressRepository = addressRepository;
        this.personMapper = personMapper;
        this.addressMapper = addressMapper;
    }

    @Override
    @Transactional
    public Person save(Person person) {
        return call(() -> {
            Long previousAddressId = person.getId() == null
                    ? null
                    : personRepository.findById(person.getId()).map(PersonEntity::getAddressId).orElse(null);

            AddressEntity address = person.getAddress() == null
                    ? null
                    : addressRepository.save(addressMapper.map(person.getAddress()));
            Long addressId = address == null ? null : address.getId();

            PersonEntity saved = personRepository.save(personMapper.map(person, addressId));

            // o endereço antigo não pertence a mais ninguém
            if (previousAddressId != null && !previousAddressId.equals(addressId)) {
                addressRepository.deleteById(previousAddressId);
            }
            personRepository.flush();

            return personMapper.map(saved, address);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Person> findById(Long id) {
        return call(() -> personRepository.findById(id).map(entity -> personMapper.map(
                entity,
                entity.getAddressId() == null ? null : addressRepository.findById(entity.getAddressId()).orElse(null))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return call(() -> {
            List<PersonEntity> persons = personRepository.findAll();
            List<Long> addressIds = persons.stream().map(PersonEntity::getAddressId).filter(Objects::nonNull).toList();
            Map<Long, AddressEntity> addresses = addressRepository.findAllById(addressIds).stream()
                    .collect(Collectors.toMap(AddressEntity::getId, Function.identity()));

            return persons.stream()
                    .map(entity -> personMapper.map(entity, addresses.get(entity.getAddressId())))
                    .toList();
        });
    }

    @Override
    public boolean existsById(Long id) {
        return call(() -> personRepository.existsById(id));
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return call(() -> personRepository.existsByCpf(cpf));
    }

    @Override
    public boolean existsByEmail(String email) {
        return call(() -> personRepository.existsByEmailIgnoreCase(email));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        run(() -> personRepository.findById(id).ifPresent(entity -> {
            personRepository.delete(entity);
            if (entity.getAddressId() != null) {
                addressRepository.deleteById(entity.getAddressId());
            }
            personRepository.flush();
        }));
    }
}
