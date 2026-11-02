package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.Restaurant;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.infra.database.jpa.entity.AddressEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.OpeningHoursEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.RestaurantEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.AddressRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.OpeningHoursRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.RestaurantRepository;
import br.com.fiap.restaurante.infra.database.mapper.AddressMapper;
import br.com.fiap.restaurante.infra.database.mapper.RestaurantMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RestaurantJpaGateway extends JpaGatewaySupport implements RestaurantGateway {

    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;
    private final OpeningHoursRepository openingHoursRepository;
    private final RestaurantMapper restaurantMapper;
    private final AddressMapper addressMapper;

    public RestaurantJpaGateway(
            RestaurantRepository restaurantRepository,
            AddressRepository addressRepository,
            OpeningHoursRepository openingHoursRepository,
            RestaurantMapper restaurantMapper,
            AddressMapper addressMapper
    ) {
        this.restaurantRepository = restaurantRepository;
        this.addressRepository = addressRepository;
        this.openingHoursRepository = openingHoursRepository;
        this.restaurantMapper = restaurantMapper;
        this.addressMapper = addressMapper;
    }

    @Override
    @Transactional
    public Restaurant save(Restaurant restaurant) {
        return call(() -> {
            AddressEntity address = addressRepository.save(addressMapper.map(restaurant.getAddress()));
            RestaurantEntity saved = restaurantRepository.save(restaurantMapper.map(restaurant, address.getId()));

            // os horários são substituídos por inteiro
            openingHoursRepository.deleteAllByRestaurantId(saved.getId());
            List<OpeningHoursEntity> openingHours = openingHoursRepository.saveAll(
                    restaurantMapper.map(restaurant.getOpeningHours(), saved.getId()));
            restaurantRepository.flush();

            return restaurantMapper.map(saved, address, openingHours);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Restaurant> findById(Long id) {
        return call(() -> restaurantRepository.findById(id).map(entity -> restaurantMapper.map(
                entity,
                addressRepository.findById(entity.getAddressId()).orElseThrow(),
                openingHoursRepository.findByRestaurantId(entity.getId()))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> findAll() {
        return call(() -> {
            List<RestaurantEntity> restaurants = restaurantRepository.findAll();
            if (restaurants.isEmpty()) {
                return List.of();
            }

            Map<Long, AddressEntity> addresses = addressRepository
                    .findAllById(restaurants.stream().map(RestaurantEntity::getAddressId).toList())
                    .stream()
                    .collect(Collectors.toMap(AddressEntity::getId, Function.identity()));
            Map<Long, List<OpeningHoursEntity>> openingHours = openingHoursRepository
                    .findByRestaurantIdIn(restaurants.stream().map(RestaurantEntity::getId).toList())
                    .stream()
                    .collect(Collectors.groupingBy(OpeningHoursEntity::getRestaurantId));

            return restaurants.stream()
                    .map(entity -> restaurantMapper.map(
                            entity,
                            addresses.get(entity.getAddressId()),
                            openingHours.getOrDefault(entity.getId(), List.of())))
                    .toList();
        });
    }

    @Override
    public boolean existsById(Long id) {
        return call(() -> restaurantRepository.existsById(id));
    }

    @Override
    public boolean existsByCnpj(String cnpj) {
        return call(() -> restaurantRepository.existsByCnpj(cnpj));
    }

    @Override
    public boolean existsByOwnerId(Long ownerId) {
        return call(() -> restaurantRepository.existsByOwnerId(ownerId));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        run(() -> restaurantRepository.findById(id).ifPresent(entity -> {
            openingHoursRepository.deleteAllByRestaurantId(entity.getId());
            restaurantRepository.delete(entity);
            addressRepository.deleteById(entity.getAddressId());
            restaurantRepository.flush();
        }));
    }
}
