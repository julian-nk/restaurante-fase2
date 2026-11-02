package br.com.fiap.restaurante.infra.database.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
@Entity
@Table(name = "restaurant_owner")
public class RestaurantOwnerEntity {

    // PK compartilhada com person (person_id)
    @Id
    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Column(name = "created_on", nullable = false, updatable = false)
    private OffsetDateTime createdOn;

    @Column(name = "business_email", nullable = false, length = 255)
    private String businessEmail;

    protected RestaurantOwnerEntity() {
    }

}
