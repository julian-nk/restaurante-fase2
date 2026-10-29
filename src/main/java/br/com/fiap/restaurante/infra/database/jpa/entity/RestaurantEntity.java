package br.com.fiap.restaurante.infra.database.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@AllArgsConstructor
@Entity
@Table(name = "restaurant")
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(nullable = false, length = 14, unique = true)
    private String cnpj;

    @Column(name = "cuisine_type", nullable = false, length = 50)
    private String cuisineType;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "address_id", nullable = false, unique = true)
    private Long addressId;

    protected RestaurantEntity() {
    }
}
