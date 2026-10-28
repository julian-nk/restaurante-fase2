package br.com.fiap.restaurante.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class Person {

    private String cpf;
    private Long id;
    private String name;
    private String email;
    private Address address;

    public void update(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void defineAddress(
            Address address
    ) {
        if (this.address == null) {
            this.address = address;
        } else {
            this.address.update(address.getStreet(), address.getNumber(), address.getComplement(),
                                address.getNeighborhood(), address.getCity(), address.getState(), address.getZipCode());
        }
    }

    public void removeAddress() {
        address = null;
    }
}
