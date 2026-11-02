package br.com.fiap.restaurante.infra.database.jpa.entity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppUserTypeIdTest {

    @Test
    void deveConsiderarIdsIguaisComoIguais() {
        AppUserTypeId id1 = new AppUserTypeId(1L, 2L);
        AppUserTypeId id2 = new AppUserTypeId(1L, 2L);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void deveConsiderarIdsDiferentesComoDiferentes() {
        AppUserTypeId id1 = new AppUserTypeId(1L, 2L);
        AppUserTypeId id2 = new AppUserTypeId(1L, 3L);

        assertNotEquals(id1, id2);
    }

    @Test
    void deveSerIgualAEleMesmo() {
        AppUserTypeId id = new AppUserTypeId(1L, 2L);

        assertEquals(id, id);
    }

    @Test
    void naoDeveSerIgualANullOuOutroTipo() {
        AppUserTypeId id = new AppUserTypeId(1L, 2L);

        assertNotEquals(null, id);
        assertNotEquals("texto", id);
    }

    @Test
    void deveCalcularHashCodeParaIdsNulos() {
        AppUserTypeId id1 = new AppUserTypeId(null, null);
        AppUserTypeId id2 = new AppUserTypeId(null, null);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }
}