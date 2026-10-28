package br.com.fiap.restaurante.core.factory;

import br.com.fiap.restaurante.core.domain.Customer;

import java.time.OffsetDateTime;

public interface CustomerFactory {

    Customer create(Long personId);

    Customer restore(Long personId, OffsetDateTime createdOn);
}
