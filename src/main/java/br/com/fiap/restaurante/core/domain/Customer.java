package br.com.fiap.restaurante.core.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class Customer extends Person {

    private Long personId;
    private OffsetDateTime createdOn;

    @Builder
    public Customer(String cpf, Long id, String name, String email, Address address, Long personId,
                    OffsetDateTime createdOn) {
        super(cpf, id, name, email, address);
        this.personId = personId;
        this.createdOn = createdOn;
    }
}
