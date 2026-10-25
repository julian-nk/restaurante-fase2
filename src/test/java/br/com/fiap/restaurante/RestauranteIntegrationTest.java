
package br.com.fiap.restaurante;

import br.com.fiap.restaurante.adapters.gateways.UserTypeRepository;
import br.com.fiap.restaurante.domain.entities.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RestauranteIntegrationTest {

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Test
    void shouldSaveAndRetrieveUserType() {
        // Arrange
        UserType userType = new UserType(
                "Cliente de integração",
                "integration_customer"
        );

        // Act
        UserType savedUserType = userTypeRepository.save(userType);

        UserType retrievedUserType = userTypeRepository
                .findById(savedUserType.getId())
                .orElseThrow();

        // Assert
        assertNotNull(savedUserType.getId());
        assertEquals("Cliente de integração", retrievedUserType.getName());
        assertEquals("integration_customer", retrievedUserType.getCode());
    }

    @Test
    void shouldCheckIfUserTypeCodeExists() {
        // Arrange
        UserType userType = new UserType(
                "Administrador de integração",
                "integration_admin"
        );

        userTypeRepository.save(userType);

        // Act
        boolean exists = userTypeRepository.existsByCode("integration_admin");
        boolean doesNotExist = userTypeRepository.existsByCode("nonexistent_code");

        // Assert
        assertTrue(exists);
        assertFalse(doesNotExist);
    }
}
