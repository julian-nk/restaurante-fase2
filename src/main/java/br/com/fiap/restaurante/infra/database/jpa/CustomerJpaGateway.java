package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.Customer;
import br.com.fiap.restaurante.core.gateway.CustomerGateway;
import br.com.fiap.restaurante.infra.database.jpa.repository.CustomerRepository;
import br.com.fiap.restaurante.infra.database.mapper.CustomerMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CustomerJpaGateway extends JpaGatewaySupport implements CustomerGateway {

    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;

    public CustomerJpaGateway(CustomerRepository customerRepository, CustomerMapper mapper) {
        this.customerRepository = customerRepository;
        this.mapper = mapper;
    }

    @Override
    public Customer save(Customer customer) {
        return call(() -> mapper.map(customerRepository.save(mapper.map(customer))));
    }

    @Override
    public Optional<Customer> findById(Long personId) {
        return call(() -> customerRepository.findById(personId).map(mapper::map));
    }

    @Override
    public List<Customer> findAll() {
        return call(() -> customerRepository.findAll().stream().map(mapper::map).toList());
    }

    @Override
    public boolean existsById(Long personId) {
        return call(() -> customerRepository.existsById(personId));
    }

    @Override
    public void deleteById(Long personId) {
        run(() -> customerRepository.deleteById(personId));
    }
}
