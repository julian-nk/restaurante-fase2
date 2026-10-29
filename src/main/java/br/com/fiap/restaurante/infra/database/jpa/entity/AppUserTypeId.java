package br.com.fiap.restaurante.infra.database.jpa.entity;

import java.io.Serializable;
import java.util.Objects;

public class AppUserTypeId implements Serializable {

    private Long appUserId;
    private Long userTypeId;

    public AppUserTypeId() {
    }

    public AppUserTypeId(Long appUserId, Long userTypeId) {
        this.appUserId = appUserId;
        this.userTypeId = userTypeId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AppUserTypeId other)) return false;

        return Objects.equals(appUserId, other.appUserId)
                && Objects.equals(userTypeId, other.userTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appUserId, userTypeId);
    }
}
