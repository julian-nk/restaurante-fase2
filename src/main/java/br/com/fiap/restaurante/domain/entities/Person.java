package br.com.fiap.restaurante.domain.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    // Somente dígitos (validado pela constraint ck_customer_cpf)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(nullable = false, length = 11, unique = true)
    private String cpf;

    @Column(name = "address_id")
    private Long addressId;

    protected Person() {
    }

    public Person(String name, String email, String cpf, Long addressId) {
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.addressId = addressId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void update(String name, String email, Long addressId) {
        this.name = name;
        this.email = email;
        this.addressId = addressId;
    }
}
