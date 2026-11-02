package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.dto.LoginInputDto;
import br.com.fiap.restaurante.core.exception.DefaultUserTypeNotFoundException;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.exception.LoginAlreadyInUseException;
import br.com.fiap.restaurante.core.exception.PersonAlreadyHasUserException;
import br.com.fiap.restaurante.core.factory.impl.AppUserFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.UserTypeFactoryImpl;
import br.com.fiap.restaurante.core.gateway.AppUserGateway;
import br.com.fiap.restaurante.core.gateway.PasswordEncoderGateway;
import br.com.fiap.restaurante.core.gateway.UserTypeGateway;
import br.com.fiap.restaurante.core.usecase.impl.PersonAppUserService;
import br.com.fiap.restaurante.core.usecase.impl.PersonAppUserService.PendingAppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PersonAppUserServiceTest {

    private static final Long CUSTOMER_TYPE = 3L;
    private static final Long OWNER_TYPE = 4L;

    private final AppUserFactoryImpl appUserFactory = new AppUserFactoryImpl();
    private AppUserGateway appUserGateway;
    private UserTypeGateway userTypeGateway;
    private PasswordEncoderGateway passwordEncoder;
    private PersonAppUserService service;

    @BeforeEach
    void setUp() {
        appUserGateway = mock(AppUserGateway.class);
        userTypeGateway = mock(UserTypeGateway.class);
        passwordEncoder = mock(PasswordEncoderGateway.class);
        when(passwordEncoder.encode(any())).thenAnswer(invocation -> "hash:" + invocation.getArgument(0));
        when(appUserGateway.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        UserTypeFactoryImpl userTypes = new UserTypeFactoryImpl();
        when(userTypeGateway.findByCode("customer"))
                .thenReturn(Optional.of(userTypes.restore(CUSTOMER_TYPE, "Cliente", "customer")));
        when(userTypeGateway.findByCode("restaurant_owner"))
                .thenReturn(Optional.of(userTypes.restore(OWNER_TYPE, "Dono de Restaurante", "restaurant_owner")));
        service = new PersonAppUserService(appUserGateway, userTypeGateway, passwordEncoder, appUserFactory);
    }

    private AppUser userOfPerson7(Long... userTypeIds) {
        return appUserFactory.restore(1L, 7L, "maria", "hash:senhaAtual1", Set.of(userTypeIds));
    }

    private AppUser savedUser() {
        ArgumentCaptor<AppUser> saved = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserGateway).save(saved.capture());
        return saved.getValue();
    }

    @Test
    void shouldFindTheIdsOfTheRoleUserTypes() {
        assertEquals(List.of(CUSTOMER_TYPE, OWNER_TYPE),
                     List.copyOf(service.requireUserTypes(List.of("customer", "restaurant_owner"))));
    }

    @Test
    void shouldRefuseWhenTheRoleUserTypeIsMissing() {
        when(userTypeGateway.findByCode("customer")).thenReturn(Optional.empty());

        DefaultUserTypeNotFoundException exception = assertThrows(DefaultUserTypeNotFoundException.class,
                () -> service.requireUserTypes(List.of("customer")));
        assertEquals("userType.defaultNotFound", exception.getCode());
        assertEquals(409, exception.getHttpStatus());
    }

    @Test
    void shouldCreateTheUserOfANewPersonOnlyAfterThePersonIsSaved() {
        PendingAppUser pending = service.prepare(null, new LoginInputDto(" Maria.Silva ", "senha1234"),
                                                 Set.of(CUSTOMER_TYPE));
        verify(appUserGateway, never()).save(any());

        pending.save(7L);

        AppUser user = savedUser();
        assertEquals(7L, user.getPersonId());
        assertEquals("maria.silva", user.getLogin());
        assertEquals("hash:senha1234", user.getPasswordHash());
        assertEquals(Set.of(CUSTOMER_TYPE), user.getUserTypeIds());
    }

    @Test
    void shouldRequireLoginAndPasswordWhenThePersonHasNoUser() {
        assertThrows(InvalidFieldException.class, () -> service.prepare(null, null, Set.of()));
        assertThrows(InvalidFieldException.class, () -> service.prepare(null, new LoginInputDto(" ", null), Set.of()));
        assertThrows(InvalidFieldException.class,
                () -> service.prepare(null, new LoginInputDto("maria", null), Set.of()));
        assertThrows(InvalidFieldException.class,
                () -> service.prepare(null, new LoginInputDto(null, "senha1234"), Set.of()));
        // pessoa já cadastrada, mas que ainda não tem usuário
        assertThrows(InvalidFieldException.class, () -> service.prepare(7L, null, Set.of(CUSTOMER_TYPE)));
    }

    @Test
    void shouldValidateTheLoginBeforeHashingThePassword() {
        assertThrows(InvalidFieldException.class,
                () -> service.prepare(null, new LoginInputDto("maria", "123"), Set.of()));
        assertThrows(InvalidFieldException.class,
                () -> service.prepare(null, new LoginInputDto("maria silva", "senha1234"), Set.of()));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void shouldRejectLoginInUse() {
        when(appUserGateway.existsByLogin("maria")).thenReturn(true);

        assertThrows(LoginAlreadyInUseException.class,
                () -> service.prepare(null, new LoginInputDto("Maria", "senha1234"), Set.of()));
    }

    @Test
    void shouldGiveTheUserTypesToTheUserThePersonAlreadyHas() {
        when(appUserGateway.findByPersonId(7L)).thenReturn(Optional.of(userOfPerson7(CUSTOMER_TYPE)));

        service.prepare(7L, null, Set.of(CUSTOMER_TYPE, OWNER_TYPE)).save(7L);

        assertEquals(Set.of(CUSTOMER_TYPE, OWNER_TYPE), savedUser().getUserTypeIds());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void shouldNotSaveTheUserWhenItAlreadyHasTheUserTypes() {
        when(appUserGateway.findByPersonId(7L)).thenReturn(Optional.of(userOfPerson7(CUSTOMER_TYPE)));

        service.prepare(7L, new LoginInputDto(null, " "), Set.of(CUSTOMER_TYPE)).save(7L);

        verify(appUserGateway, never()).save(any());
    }

    @Test
    void shouldRejectLoginForPersonThatAlreadyHasUser() {
        when(appUserGateway.findByPersonId(7L)).thenReturn(Optional.of(userOfPerson7()));

        assertThrows(PersonAlreadyHasUserException.class,
                () -> service.prepare(7L, new LoginInputDto("maria2", "senha1234"), Set.of(CUSTOMER_TYPE)));
        assertThrows(PersonAlreadyHasUserException.class,
                () -> service.prepare(7L, new LoginInputDto(null, "senha1234"), Set.of(CUSTOMER_TYPE)));
    }

    @Test
    void shouldRemoveTheUserTypeWhenThePersonLosesTheRole() {
        when(appUserGateway.findByPersonId(7L)).thenReturn(Optional.of(userOfPerson7(CUSTOMER_TYPE, OWNER_TYPE)));

        service.removeUserType(7L, "customer");

        assertEquals(Set.of(OWNER_TYPE), savedUser().getUserTypeIds());
    }

    @Test
    void shouldHaveNothingToRemoveWithoutUserOrUserType() {
        service.removeUserType(7L, "customer"); // a pessoa não tem usuário

        when(appUserGateway.findByPersonId(7L)).thenReturn(Optional.of(userOfPerson7(OWNER_TYPE)));
        service.removeUserType(7L, "customer"); // o usuário não tem o tipo

        when(userTypeGateway.findByCode("customer")).thenReturn(Optional.empty());
        service.removeUserType(7L, "customer"); // o tipo nem existe mais

        verify(appUserGateway, never()).save(any());
    }
}
