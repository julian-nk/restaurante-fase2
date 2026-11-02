package br.com.fiap.restaurante.core.factory;

import br.com.fiap.restaurante.core.domain.Address;
import br.com.fiap.restaurante.core.domain.Customer;
import br.com.fiap.restaurante.core.dto.CreateCustomerInputDto;

import java.time.OffsetDateTime;

public interface CustomerFactory {

    Customer create(Long personId, CreateCustomerInputDto input);

    Customer restore(Long id, String name, String email, String cpf, Address address, OffsetDateTime createdOn);
}
