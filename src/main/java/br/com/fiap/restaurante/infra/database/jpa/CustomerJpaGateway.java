package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.Customer;
import br.com.fiap.restaurante.core.gateway.CustomerGateway;
import br.com.fiap.restaurante.infra.database.jpa.PersonPersistence.PersonRecord;
import br.com.fiap.restaurante.infra.database.jpa.entity.CustomerEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.CustomerRepository;
import br.com.fiap.restaurante.infra.database.mapper.CustomerMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Grava e lê o papel junto com os dados da pessoa (via {@link PersonPersistence}), já que o domínio trata
 * {@link Customer} como uma pessoa.
 */
@Component
public class CustomerJpaGateway extends JpaGatewaySupport implements CustomerGateway {

    private final CustomerRepository customerRepository;
    private final PersonPersistence personPersistence;
    private final CustomerMapper mapper;

    public CustomerJpaGateway(
            CustomerRepository customerRepository,
            PersonPersistence personPersistence,
            CustomerMapper mapper
    ) {
        this.customerRepository = customerRepository;
        this.personPersistence = personPersistence;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Customer save(Customer customer) {
        return call(() -> {
            PersonRecord person = personPersistence.save(customer);
            CustomerEntity saved = customerRepository.save(mapper.map(person.person().getId(), customer));
            customerRepository.flush();
            return mapper.map(person.person(), person.address(), saved);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findById(Long id) {
        return call(() -> customerRepository.findById(id).flatMap(role -> personPersistence.findById(id)
                .map(person -> mapper.map(person.person(), person.address(), role))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return call(() -> {
            List<CustomerEntity> roles = customerRepository.findAll();
            Map<Long, PersonRecord> persons = personPersistence.findAllById(
                    roles.stream().map(CustomerEntity::getPersonId).toList());

            return roles.stream()
                    .map(role -> {
                        PersonRecord person = persons.get(role.getPersonId());
                        return mapper.map(person.person(), person.address(), role);
                    })
                    .toList();
        });
    }

    @Override
    public boolean existsById(Long id) {
        return call(() -> customerRepository.existsById(id));
    }

    @Override
    public void deleteById(Long id) {
        run(() -> customerRepository.deleteById(id));
    }
}
