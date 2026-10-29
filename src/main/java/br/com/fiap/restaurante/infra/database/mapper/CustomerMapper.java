package br.com.fiap.restaurante.infra.database.mapper;

import br.com.fiap.restaurante.core.domain.Customer;
import br.com.fiap.restaurante.core.factory.CustomerFactory;
import br.com.fiap.restaurante.infra.database.jpa.entity.AddressEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.CustomerEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.PersonEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    private final CustomerFactory customerFactory;
    private final AddressMapper addressMapper;

    public CustomerMapper(CustomerFactory customerFactory, AddressMapper addressMapper) {
        this.customerFactory = customerFactory;
        this.addressMapper = addressMapper;
    }

    /** @param address pode ser nulo (pessoa sem endereço) */
    public Customer map(PersonEntity person, AddressEntity address, CustomerEntity customer) {
        return customerFactory.restore(
                person.getId(),
                person.getName(),
                person.getEmail(),
                person.getCpf(),
                address == null ? null : addressMapper.map(address),
                customer.getCreatedOn()
        );
    }

    /** @param personId o id da pessoa já gravada, que também é o id do cliente */
    public CustomerEntity map(Long personId, Customer customer) {
        return new CustomerEntity(personId, customer.getCreatedOn());
    }
}
