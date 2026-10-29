package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.RestaurantOwner;
import br.com.fiap.restaurante.core.gateway.RestaurantOwnerGateway;
import br.com.fiap.restaurante.infra.database.jpa.repository.RestaurantOwnerRepository;
import br.com.fiap.restaurante.infra.database.mapper.RestaurantOwnerMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RestaurantOwnerJpaGateway extends JpaGatewaySupport implements RestaurantOwnerGateway {

    private final RestaurantOwnerRepository restaurantOwnerRepository;
    private final RestaurantOwnerMapper mapper;

    public RestaurantOwnerJpaGateway(RestaurantOwnerRepository restaurantOwnerRepository, RestaurantOwnerMapper mapper) {
        this.restaurantOwnerRepository = restaurantOwnerRepository;
        this.mapper = mapper;
    }

    @Override
    public RestaurantOwner save(RestaurantOwner owner) {
        return call(() -> mapper.map(restaurantOwnerRepository.save(mapper.map(owner))));
    }

    @Override
    public Optional<RestaurantOwner> findById(Long personId) {
        return call(() -> restaurantOwnerRepository.findById(personId).map(mapper::map));
    }

    @Override
    public List<RestaurantOwner> findAll() {
        return call(() -> restaurantOwnerRepository.findAll().stream().map(mapper::map).toList());
    }

    @Override
    public boolean existsById(Long personId) {
        return call(() -> restaurantOwnerRepository.existsById(personId));
    }

    @Override
    public void deleteById(Long personId) {
        run(() -> restaurantOwnerRepository.deleteById(personId));
    }
}
