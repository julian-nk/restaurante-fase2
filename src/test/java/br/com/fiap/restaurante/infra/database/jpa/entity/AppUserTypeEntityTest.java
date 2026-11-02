package br.com.fiap.restaurante.infra.database.jpa.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppUserTypeEntityTest {

    @Test
    void deveCriarAssociacaoComOsIdsInformados() {
        AppUserTypeEntity association = new AppUserTypeEntity(1L, 2L);

        assertEquals(1L, association.getAppUserId());
        assertEquals(2L, association.getUserTypeId());
    }

    @Test
    void devePermitirIdsNulosNoConstrutor() {
        AppUserTypeEntity association = new AppUserTypeEntity(null, null);

        assertNull(association.getAppUserId());
        assertNull(association.getUserTypeId());
    }
}