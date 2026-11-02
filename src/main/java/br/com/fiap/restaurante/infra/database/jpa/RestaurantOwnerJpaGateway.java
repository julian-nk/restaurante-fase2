package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.Restaurant;
import br.com.fiap.restaurante.core.domain.RestaurantOwner;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
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
import java.util.stream.Collectors;

@Component
public class RestaurantOwnerJpaGateway extends JpaGatewaySupport implements RestaurantOwnerGateway {

    private final RestaurantOwnerRepository restaurantOwnerRepository;
    private final PersonPersistence personPersistence;
    private final RestaurantOwnerMapper mapper;
    private final RestaurantGateway restaurantGateway;

    public RestaurantOwnerJpaGateway(
            RestaurantOwnerRepository restaurantOwnerRepository,
            PersonPersistence personPersistence,
            RestaurantOwnerMapper mapper,
            RestaurantGateway restaurantGateway
    ) {
        this.restaurantOwnerRepository = restaurantOwnerRepository;
        this.personPersistence = personPersistence;
        this.mapper = mapper;
        this.restaurantGateway = restaurantGateway;
    }

    private List<Restaurant> restaurantsOf(Long ownerId) {
        return restaurantGateway.findByOwnerIds(List.of(ownerId));
    }

    @Override
    @Transactional
    public RestaurantOwner save(RestaurantOwner restaurantOwner) {
        return call(() -> {
            PersonRecord person = personPersistence.save(restaurantOwner);
            RestaurantOwnerEntity saved = restaurantOwnerRepository.save(
                    mapper.map(person.person().getId(), restaurantOwner));
            restaurantOwnerRepository.flush();
            return mapper.map(person.person(), person.address(), saved, restaurantsOf(saved.getPersonId()));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RestaurantOwner> findById(Long id) {
        return call(() -> restaurantOwnerRepository.findById(id).flatMap(role -> personPersistence.findById(id)
                                                                                                  .map(person -> mapper.map(
                                                                                                          person.person(),
                                                                                                          person.address(),
                                                                                                          role,
                                                                                                          restaurantsOf(
                                                                                                                  id)))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantOwner> findAll() {
        return call(() -> {
            List<RestaurantOwnerEntity> roles = restaurantOwnerRepository.findAll();
            List<Long> ownerIds = roles.stream().map(RestaurantOwnerEntity::getPersonId).toList();
            Map<Long, PersonRecord> persons = personPersistence.findAllById(ownerIds);
            Map<Long, List<Restaurant>> restaurants = restaurantGateway.findByOwnerIds(ownerIds).stream()
                                                                       .collect(Collectors.groupingBy(
                                                                               Restaurant::getOwnerId));

            return roles.stream()
                        .map(role -> {
                            PersonRecord person = persons.get(role.getPersonId());
                            return mapper.map(person.person(), person.address(), role,
                                              restaurants.getOrDefault(role.getPersonId(), List.of()));
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
