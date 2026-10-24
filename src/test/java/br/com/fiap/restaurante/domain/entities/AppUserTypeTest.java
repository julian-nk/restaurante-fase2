package br.com.fiap.restaurante.domain.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppUserTypeTest {

    @Test
    void deveCriarAssociacaoComOsIdsInformados() {
        AppUserType association = new AppUserType(1L, 2L);

        assertEquals(1L, association.getAppUserId());
        assertEquals(2L, association.getUserTypeId());
    }

    @Test
    void devePermitirIdsNulosNoConstrutor() {
        AppUserType association = new AppUserType(null, null);

        assertNull(association.getAppUserId());
        assertNull(association.getUserTypeId());
    }
}