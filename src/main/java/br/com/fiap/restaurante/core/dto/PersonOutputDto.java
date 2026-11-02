package br.com.fiap.restaurante.core.dto;

import br.com.fiap.restaurante.core.domain.Person;

public record PersonOutputDto(
        Long id,
        String name,
        String email,
        String cpf,
        AddressOutputDto address
) {

    public static PersonOutputDto from(Person person) {
        return new PersonOutputDto(
                person.getId(),
                person.getName(),
                person.getEmail(),
                person.getCpf(),
                AddressOutputDto.from(person.getAddress())
        );
    }
}
