package br.com.fiap.restaurante.infra.database.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
@Entity
@Table(name = "customer")
public class CustomerEntity {

    @Id
    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Column(name = "created_on", nullable = false, updatable = false)
    private OffsetDateTime createdOn;

    protected CustomerEntity() {
    }
}
