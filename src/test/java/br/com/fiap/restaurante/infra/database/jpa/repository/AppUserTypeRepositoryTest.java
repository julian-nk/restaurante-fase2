package br.com.fiap.restaurante.infra.database.jpa.repository;

import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserTypeEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserTypeId;
import br.com.fiap.restaurante.infra.database.jpa.entity.UserTypeEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Executa os repositórios contra o H2 (perfil "test"). Cobre o comportamento que antes vinha do SQL nativo:
 * associar duas vezes não duplica nem falha, remover uma associação inexistente não falha e a consulta
 * devolve só os tipos do usuário informado.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AppUserTypeRepositoryTest {

    @Autowired
    private AppUserTypeRepository appUserTypeRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void deveAssociarSemDuplicarQuandoAssociadoDuasVezes() {
        Long appUserId = createAppUser(1001L, "maria");
        Long userTypeId = createUserType("Cliente", "customer");

        appUserTypeRepository.saveAndFlush(new AppUserTypeEntity(appUserId, userTypeId));
        entityManager.clear();

        assertDoesNotThrow(() -> appUserTypeRepository.saveAndFlush(new AppUserTypeEntity(appUserId, userTypeId)));
        assertEquals(1, userTypeRepository.findByAppUserId(appUserId).size());
    }

    @Test
    void deveRemoverAssociacao() {
        Long appUserId = createAppUser(1001L, "maria");
        Long userTypeId = createUserType("Cliente", "customer");
        AppUserTypeId id = new AppUserTypeId(appUserId, userTypeId);
        appUserTypeRepository.saveAndFlush(new AppUserTypeEntity(appUserId, userTypeId));
        entityManager.clear();

        appUserTypeRepository.deleteById(id);
        appUserTypeRepository.flush();

        assertFalse(appUserTypeRepository.existsById(id));
    }

    @Test
    void naoDeveFalharAoRemoverAssociacaoInexistente() {
        assertDoesNotThrow(() -> appUserTypeRepository.deleteById(new AppUserTypeId(999L, 999L)));
    }

    @Test
    void deveBuscarSomenteOsTiposDoUsuario() {
        Long maria = createAppUser(1001L, "maria");
        Long joao = createAppUser(1002L, "joao");
        Long customer = createUserType("Cliente", "customer");
        Long owner = createUserType("Dono de restaurante", "owner");
        Long admin = createUserType("Administrador", "admin");

        appUserTypeRepository.save(new AppUserTypeEntity(maria, customer));
        appUserTypeRepository.save(new AppUserTypeEntity(maria, owner));
        appUserTypeRepository.save(new AppUserTypeEntity(joao, admin));
        appUserTypeRepository.flush();

        Set<String> codes = userTypeRepository.findByAppUserId(maria).stream()
                .map(UserTypeEntity::getCode)
                .collect(Collectors.toSet());

        assertEquals(Set.of("customer", "owner"), codes);
    }

    private Long createAppUser(Long personId, String login) {
        return appUserRepository.save(new AppUserEntity(null, personId, login, "hash")).getId();
    }

    private Long createUserType(String name, String code) {
        return userTypeRepository.save(new UserTypeEntity(null, name, code)).getId();
    }
}
