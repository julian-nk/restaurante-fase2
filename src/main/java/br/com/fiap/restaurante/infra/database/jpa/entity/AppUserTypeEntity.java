package br.com.fiap.restaurante.infra.database.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Entity
@Table(name = "app_user_type")
@IdClass(AppUserTypeId.class)
public class AppUserTypeEntity {

    @Id
    @Column(name = "app_user_id", nullable = false)
    private Long appUserId;

    @Id
    @Column(name = "user_type_id", nullable = false)
    private Long userTypeId;

    protected AppUserTypeEntity() {
    }
}
