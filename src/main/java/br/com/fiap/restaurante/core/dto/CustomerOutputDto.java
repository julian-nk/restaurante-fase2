package br.com.fiap.restaurante.core.dto;

import br.com.fiap.restaurante.core.domain.Customer;

import java.time.OffsetDateTime;

public record CustomerOutputDto(
        Long id,
        String name,
        String email,
        String cpf,
        AddressOutputDto address,
        OffsetDateTime createdOn
) {

    public static CustomerOutputDto from(Customer customer) {
        return new CustomerOutputDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getCpf(),
                AddressOutputDto.from(customer.getAddress()),
                customer.getCreatedOn()
        );
    }
}
