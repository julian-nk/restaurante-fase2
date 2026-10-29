package br.com.fiap.restaurante.core.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Address {

    static final int STREET_MAX_LENGTH = 150;
    static final int NUMBER_MAX_LENGTH = 10;
    static final int COMPLEMENT_MAX_LENGTH = 100;
    static final int NEIGHBORHOOD_MAX_LENGTH = 100;
    static final int CITY_MAX_LENGTH = 100;
    static final int ZIP_CODE_LENGTH = 8;

    private Long id;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private StatesEnum state;
    private String zipCode;

    @Builder
    public Address(Long id, String street, String number, String complement, String neighborhood, String city,
                   StatesEnum state, String zipCode) {
        this.id = id;
        apply(street, number, complement, neighborhood, city, state, zipCode);
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
        apply(street, number, complement, neighborhood, city, state, zipCode);
    }

    private void apply(
            String street,
            String number,
            String complement,
            String neighborhood,
            String city,
            StatesEnum state,
            String zipCode
    ) {
        String validStreet = DomainValidation.requireText(street, STREET_MAX_LENGTH, "logradouro");
        String validNumber = DomainValidation.requireText(number, NUMBER_MAX_LENGTH, "número");
        String validComplement = DomainValidation.optionalText(complement, COMPLEMENT_MAX_LENGTH, "complemento");
        String validNeighborhood = DomainValidation.requireText(neighborhood, NEIGHBORHOOD_MAX_LENGTH, "bairro");
        String validCity = DomainValidation.requireText(city, CITY_MAX_LENGTH, "cidade");
        StatesEnum validState = DomainValidation.requireNonNull(state, "UF");
        String validZipCode = DomainValidation.requireDigits(zipCode, ZIP_CODE_LENGTH, "CEP");

        this.street = validStreet;
        this.number = validNumber;
        this.complement = validComplement;
        this.neighborhood = validNeighborhood;
        this.city = validCity;
        this.state = validState;
        this.zipCode = validZipCode;
    }
}
