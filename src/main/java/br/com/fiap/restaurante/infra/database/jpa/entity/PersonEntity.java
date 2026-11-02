package br.com.fiap.restaurante.infra.database.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@AllArgsConstructor
@Entity
@Table(name = "person")
public class PersonEntity {

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

    protected PersonEntity() {
    }

}
