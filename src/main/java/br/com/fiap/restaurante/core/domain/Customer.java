package br.com.fiap.restaurante.core.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class Customer extends Person {

    private OffsetDateTime createdOn;

    @Builder
    public Customer(String cpf, Long id, String name, String email, Address address, OffsetDateTime createdOn) {
        super(cpf, id, name, email, address);
        this.createdOn = DomainValidation.requireNonNull(createdOn, "data de cadastro");
    }
}
