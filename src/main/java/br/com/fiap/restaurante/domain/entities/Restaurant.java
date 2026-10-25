package br.com.fiap.restaurante.domain.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "restaurant")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    // Somente dígitos (validado pela constraint ck_restaurant_cnpj no DB)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(nullable = false, length = 14, unique = true)
    private String cnpj;

    @Column(name = "cuisine_type", nullable = false, length = 50)
    private String cuisineType;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "address_id", nullable = false, unique = true)
    private Long addressId;

    protected Restaurant() {
    }

    public Restaurant(String name, String cnpj, String cuisineType, Long ownerId, Long addressId) {
        this.name = name;
        this.cnpj = cnpj;
        this.cuisineType = cuisineType;
        this.ownerId = ownerId;
        this.addressId = addressId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void update(String name, String cuisineType, Long ownerId) {
        this.name = name;
        this.cuisineType = cuisineType;
        this.ownerId = ownerId;
    }
}
