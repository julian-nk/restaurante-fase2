package br.com.fiap.restaurante.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user_type")
@IdClass(AppUserTypeId.class)
public class AppUserType {

    @Id
    @Column(name = "app_user_id", nullable = false)
    private Long appUserId;

    @Id
    @Column(name = "user_type_id", nullable = false)
    private Long userTypeId;

    protected AppUserType() {
    }

    public AppUserType(Long appUserId, Long userTypeId) {
        this.appUserId = appUserId;
        this.userTypeId = userTypeId;
    }

    public Long getAppUserId() {
        return appUserId;
    }

    public Long getUserTypeId() {
        return userTypeId;
    }
}