package br.com.fiap.restaurante.core.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Address {

    private Long id;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private StatesEnum state;
    private String zipCode;

    public Address(Long id, String street, String number, String complement, String neighborhood, String city,
                   StatesEnum state, String zipCode) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    private static StatesEnum convertState(String state) {
        return StatesEnum.fromValue(state);
    }

    public void update(
            String street,
            String number,
            String complement,
            String neighborhood,
            String city,
            StatesEnum state,
            String zipCode
    ) {
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }
}
