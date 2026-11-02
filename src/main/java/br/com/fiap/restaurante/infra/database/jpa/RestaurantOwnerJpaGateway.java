package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.RestaurantOwner;
import br.com.fiap.restaurante.core.gateway.RestaurantOwnerGateway;
import br.com.fiap.restaurante.infra.database.jpa.PersonPersistence.PersonRecord;
import br.com.fiap.restaurante.infra.database.jpa.entity.RestaurantOwnerEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.RestaurantOwnerRepository;
import br.com.fiap.restaurante.infra.database.mapper.RestaurantOwnerMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Grava e lê o papel junto com os dados da pessoa (via {@link PersonPersistence}), já que o domínio trata
 * {@link RestaurantOwner} como uma pessoa.
 */
@Component
public class RestaurantOwnerJpaGateway extends JpaGatewaySupport implements RestaurantOwnerGateway {

    private final RestaurantOwnerRepository restaurantOwnerRepository;
    private final PersonPersistence personPersistence;
    private final RestaurantOwnerMapper mapper;

    public RestaurantOwnerJpaGateway(
            RestaurantOwnerRepository restaurantOwnerRepository,
            PersonPersistence personPersistence,
            RestaurantOwnerMapper mapper
    ) {
        this.restaurantOwnerRepository = restaurantOwnerRepository;
        this.personPersistence = personPersistence;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public RestaurantOwner save(RestaurantOwner restaurantOwner) {
        return call(() -> {
            PersonRecord person = personPersistence.save(restaurantOwner);
            RestaurantOwnerEntity saved = restaurantOwnerRepository.save(mapper.map(person.person().getId(), restaurantOwner));
            restaurantOwnerRepository.flush();
            return mapper.map(person.person(), person.address(), saved);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RestaurantOwner> findById(Long id) {
        return call(() -> restaurantOwnerRepository.findById(id).flatMap(role -> personPersistence.findById(id)
                .map(person -> mapper.map(person.person(), person.address(), role))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantOwner> findAll() {
        return call(() -> {
            List<RestaurantOwnerEntity> roles = restaurantOwnerRepository.findAll();
            Map<Long, PersonRecord> persons = personPersistence.findAllById(
                    roles.stream().map(RestaurantOwnerEntity::getPersonId).toList());

            return roles.stream()
                    .map(role -> {
                        PersonRecord person = persons.get(role.getPersonId());
                        return mapper.map(person.person(), person.address(), role);
                    })
                    .toList();
        });
    }

    @Override
    public boolean existsById(Long id) {
        return call(() -> restaurantOwnerRepository.existsById(id));
    }

    @Override
    public void deleteById(Long id) {
        run(() -> restaurantOwnerRepository.deleteById(id));
    }
}
