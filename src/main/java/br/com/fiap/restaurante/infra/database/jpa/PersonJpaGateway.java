package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.Person;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.infra.database.jpa.PersonPersistence.PersonRecord;
import br.com.fiap.restaurante.infra.database.jpa.repository.AddressRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.PersonRepository;
import br.com.fiap.restaurante.infra.database.mapper.PersonMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class PersonJpaGateway extends JpaGatewaySupport implements PersonGateway {

    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;
    private final PersonPersistence personPersistence;
    private final PersonMapper mapper;

    public PersonJpaGateway(
            PersonRepository personRepository,
            AddressRepository addressRepository,
            PersonPersistence personPersistence,
            PersonMapper mapper
    ) {
        this.personRepository = personRepository;
        this.addressRepository = addressRepository;
        this.personPersistence = personPersistence;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Person save(Person person) {
        return call(() -> {
            PersonRecord saved = personPersistence.save(person);
            personRepository.flush();
            return mapper.map(saved.person(), saved.address());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Person> findById(Long id) {
        return call(() -> personPersistence.findById(id).map(stored -> mapper.map(stored.person(), stored.address())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return call(() -> personPersistence.findAll().stream()
                .map(stored -> mapper.map(stored.person(), stored.address()))
                .toList());
    }

    @Override
    public boolean existsById(Long id) {
        return call(() -> personRepository.existsById(id));
    }

    @Override
    public Optional<Long> findIdByCpf(String cpf) {
        return call(() -> personRepository.findIdByCpf(cpf));
    }

    @Override
    public boolean isEmailUsedByAnotherPerson(String email, Long personId) {
        return call(() -> personId == null
                ? personRepository.existsByEmailIgnoreCase(email)
                : personRepository.existsByEmailIgnoreCaseAndIdNot(email, personId));
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
