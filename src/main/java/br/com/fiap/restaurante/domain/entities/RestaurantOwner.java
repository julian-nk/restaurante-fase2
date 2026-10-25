package br.com.fiap.restaurante.domain.entities;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "restaurant_owner")
public class RestaurantOwner {

    // PK compartilhada com person (person_id)
    @Id
    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Column(name = "created_on", nullable = false, updatable = false)
    private OffsetDateTime createdOn;

    @Column(name = "business_email", nullable = false, length = 255)
    private String businessEmail;

    protected RestaurantOwner() {
    }

    public RestaurantOwner(Long personId, String businessEmail) {
        this.personId = personId;
        this.businessEmail = businessEmail;
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

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void updateBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }
}
