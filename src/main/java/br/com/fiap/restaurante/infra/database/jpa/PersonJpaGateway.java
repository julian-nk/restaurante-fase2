package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.infra.database.jpa.repository.AddressRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.PersonRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class PersonJpaGateway extends JpaGatewaySupport implements PersonGateway {

    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;

    public PersonJpaGateway(PersonRepository personRepository, AddressRepository addressRepository) {
        this.personRepository = personRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public boolean existsById(Long id) {
        return call(() -> personRepository.existsById(id));
    }

    @Override
    public Optional<Long> findIdByCpf(String cpf) {
        return call(() -> personRepository.findIdByCpf(cpf));
    }

    @Override
    public boolean isEmailUsedByAnotherPerson(String email, Long personId) {
        return call(() -> personId == null
                ? personRepository.existsByEmailIgnoreCase(email)
                : personRepository.existsByEmailIgnoreCaseAndIdNot(email, personId));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        run(() -> personRepository.findById(id).ifPresent(entity -> {
            personRepository.delete(entity);
            if (entity.getAddressId() != null) {
                addressRepository.deleteById(entity.getAddressId());
            }
            personRepository.flush();
        }));
    }
}
