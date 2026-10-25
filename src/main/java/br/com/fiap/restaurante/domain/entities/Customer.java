package br.com.fiap.restaurante.domain.entities;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

/**
 * Representa o cliente um cliente no app, que fará os pedidos.
 */
@Entity
@Table(name = "customer")
public class Customer {

    // PK compartilhada com person (person_id)
    @Id
    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Column(name = "created_on", nullable = false, updatable = false)
    private OffsetDateTime createdOn;

    protected Customer() {
    }

    public Customer(Long personId) {
        this.personId = personId;
    }

    @PrePersist
    void onCreate() {
        if (createdOn == null) {
            createdOn = OffsetDateTime.now();
        }
    }

    public Long getPersonId() {
        return personId;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }
}
