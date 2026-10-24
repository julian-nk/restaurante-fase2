package br.com.fiap.restaurante.adapters.gateways;

import br.com.fiap.restaurante.domain.entities.AppUserType;
import br.com.fiap.restaurante.domain.entities.AppUserTypeId;
import br.com.fiap.restaurante.domain.entities.UserType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppUserTypeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public boolean appUserExists(Long appUserId) {
        Number count = (Number) entityManager
                .createNativeQuery("""
                    SELECT COUNT(*)
                    FROM app_user
                    WHERE id = :appUserId
                    """)
                .setParameter("appUserId", appUserId)
                .getSingleResult();

        return count.longValue() > 0;
    }

    public List<UserType> findUserTypesByAppUserId(Long appUserId) {
        return entityManager
                .createNativeQuery("""
                    SELECT ut.*
                    FROM user_type ut
                    INNER JOIN app_user_type aut
                        ON aut.user_type_id = ut.id
                    WHERE aut.app_user_id = :appUserId
                    """, UserType.class)
                .setParameter("appUserId", appUserId)
                .getResultList();
    }

    @Transactional
    public void associate(Long appUserId, Long userTypeId) {
        entityManager.createNativeQuery("""
            INSERT INTO app_user_type (app_user_id, user_type_id)
            VALUES (:appUserId, :userTypeId)
            ON CONFLICT (app_user_id, user_type_id) DO NOTHING
            """)
                .setParameter("appUserId", appUserId)
                .setParameter("userTypeId", userTypeId)
                .executeUpdate();
    }

    @Transactional
    public void remove(Long appUserId, Long userTypeId) {
        entityManager.createNativeQuery("""
            DELETE FROM app_user_type
            WHERE app_user_id = :appUserId
              AND user_type_id = :userTypeId
            """)
                .setParameter("appUserId", appUserId)
                .setParameter("userTypeId", userTypeId)
                .executeUpdate();
    }
}
