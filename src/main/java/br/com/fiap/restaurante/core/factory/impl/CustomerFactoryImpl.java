package br.com.fiap.restaurante.core.factory.impl;

import br.com.fiap.restaurante.core.domain.Customer;
import br.com.fiap.restaurante.core.factory.CustomerFactory;

import java.time.OffsetDateTime;

public class CustomerFactoryImpl implements CustomerFactory {

    @Override
    public Customer create(Long personId) {
        return Customer.builder()
                .personId(personId)
                .createdOn(OffsetDateTime.now())
                .build();
    }

    @Override
    public Customer restore(Long personId, OffsetDateTime createdOn) {
        return Customer.builder()
                .personId(personId)
                .createdOn(createdOn)
                .build();
    }
}
