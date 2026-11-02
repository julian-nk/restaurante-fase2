package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
public class Person {

    static final int NAME_MAX_LENGTH = 150;
    private static final int CPF_LENGTH = 11;

    private String cpf;
    private Long id;
    private String name;
    private String email;
    private Address address;

    @Builder(builderMethodName = "personBuilder")
    public Person(String cpf, Long id, String name, String email, Address address) {
        this.cpf = normalizeCpf(cpf);
        this.id = id;
        this.name = DomainValidation.requireText(name, NAME_MAX_LENGTH, "nome");
        this.email = DomainValidation.requireEmail(email, "e-mail");
        this.address = address;
    }

    /**
     * Valida o CPF e devolve só os dígitos, que é como ele é gravado e comparado. Aceita o CPF com ou sem
     * pontuação ("529.982.247-25" ou "52998224725").
     *
     * @param cpf
     * @return
     * @throws InvalidFieldException se o CPF estiver vazio ou não tiver 11 dígitos.
     */
    public static String normalizeCpf(String cpf) {
        return DomainValidation.requireDigits(cpf, CPF_LENGTH, "CPF");
    }

    /**
     * Valida os dois campos antes de alterar qualquer um. O e-mail é guardado em minúsculas.
     */
    public void update(String name, String email) {
        String validName = DomainValidation.requireText(name, NAME_MAX_LENGTH, "nome");
        String validEmail = DomainValidation.requireEmail(email, "e-mail");
        this.name = validName;
        this.email = validEmail;
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

    public void updatePersonalData(String name, String email, Address address) {
        update(name, email);
        if (address == null) {
            removeAddress();
        } else {
            defineAddress(address);
        }
    }

    public void removeAddress() {
        address = null;
    }
}
