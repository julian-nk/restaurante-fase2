package br.com.fiap.restaurante.infra.config;

import br.com.fiap.restaurante.core.controller.*;
import br.com.fiap.restaurante.core.factory.*;
import br.com.fiap.restaurante.core.factory.impl.*;
import br.com.fiap.restaurante.core.gateway.*;
import br.com.fiap.restaurante.core.usecase.*;
import br.com.fiap.restaurante.core.usecase.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DependencyInjectionConfiguration {

    // ---------- Factories ----------

    @Bean
    public UserTypeFactory userTypeFactory() {
        return new UserTypeFactoryImpl();
    }

    @Bean
    public MenuItemFactory menuItemFactory() {
        return new MenuItemFactoryImpl();
    }

    @Bean
    public AddressFactory addressFactory() {
        return new AddressFactoryImpl();
    }

    @Bean
    public AppUserFactory appUserFactory() {
        return new AppUserFactoryImpl();
    }

    @Bean
    public CustomerFactory customerFactory(AddressFactory addressFactory) {
        return new CustomerFactoryImpl(addressFactory);
    }

    @Bean
    public RestaurantOwnerFactory restaurantOwnerFactory(AddressFactory addressFactory) {
        return new RestaurantOwnerFactoryImpl(addressFactory);
    }

    @Bean
    public OpeningHoursFactory openingHoursFactory() {
        return new OpeningHoursFactoryImpl();
    }

    @Bean
    public RestaurantFactory restaurantFactory(AddressFactory addressFactory, OpeningHoursFactory openingHoursFactory) {
        return new RestaurantFactoryImpl(addressFactory, openingHoursFactory);
    }

    // ---------- Tipo de usuário ----------

    @Bean
    public CreateUserTypeUseCase createUserTypeUseCase(UserTypeGateway gateway, UserTypeFactory factory) {
        return new CreateUserTypeUseCaseImpl(gateway, factory);
    }

    @Bean
    public GetUserTypesUseCase getUserTypesUseCase(UserTypeGateway gateway) {
        return new GetUserTypesUseCaseImpl(gateway);
    }

    @Bean
    public GetUserTypeByIdUseCase getUserTypeByIdUseCase(UserTypeGateway gateway) {
        return new GetUserTypeByIdUseCaseImpl(gateway);
    }

    @Bean
    public UpdateUserTypeUseCase updateUserTypeUseCase(UserTypeGateway gateway) {
        return new UpdateUserTypeUseCaseImpl(gateway);
    }

    @Bean
    public DeleteUserTypeUseCase deleteUserTypeUseCase(UserTypeGateway gateway) {
        return new DeleteUserTypeUseCaseImpl(gateway);
    }

    @Bean
    public UserTypeController userTypeController(
            CreateUserTypeUseCase createUserTypeUseCase,
            GetUserTypesUseCase getUserTypesUseCase,
            GetUserTypeByIdUseCase getUserTypeByIdUseCase,
            UpdateUserTypeUseCase updateUserTypeUseCase,
            DeleteUserTypeUseCase deleteUserTypeUseCase
    ) {
        return new UserTypeController(
                createUserTypeUseCase,
                getUserTypesUseCase,
                getUserTypeByIdUseCase,
                updateUserTypeUseCase,
                deleteUserTypeUseCase
        );
    }

    // ---------- Associação usuário x tipo ----------

    @Bean
    public AssociateUserTypeUseCase associateUserTypeUseCase(
            AppUserTypeGateway appUserTypeGateway,
            AppUserGateway appUserGateway,
            UserTypeGateway userTypeGateway
    ) {
        return new AssociateUserTypeUseCaseImpl(appUserTypeGateway, appUserGateway, userTypeGateway);
    }

    @Bean
    public GetUserTypesByAppUserUseCase getUserTypesByAppUserUseCase(
            AppUserTypeGateway appUserTypeGateway,
            AppUserGateway appUserGateway
    ) {
        return new GetUserTypesByAppUserUseCaseImpl(appUserTypeGateway, appUserGateway);
    }

    @Bean
    public RemoveUserTypeFromAppUserUseCase removeUserTypeFromAppUserUseCase(
            AppUserTypeGateway appUserTypeGateway,
            AppUserGateway appUserGateway,
            UserTypeGateway userTypeGateway
    ) {
        return new RemoveUserTypeFromAppUserUseCaseImpl(appUserTypeGateway, appUserGateway, userTypeGateway);
    }

    @Bean
    public AppUserTypeController appUserTypeController(
            AssociateUserTypeUseCase associateUserTypeUseCase,
            GetUserTypesByAppUserUseCase getUserTypesByAppUserUseCase,
            RemoveUserTypeFromAppUserUseCase removeUserTypeFromAppUserUseCase
    ) {
        return new AppUserTypeController(
                associateUserTypeUseCase,
                getUserTypesByAppUserUseCase,
                removeUserTypeFromAppUserUseCase
        );
    }

    // ---------- Itens do cardápio ----------

    @Bean
    public CreateMenuItemUseCase createMenuItemUseCase(
            MenuItemGateway gateway,
            RestaurantGateway restaurantGateway,
            MenuItemFactory factory
    ) {
        return new CreateMenuItemUseCaseImpl(gateway, restaurantGateway, factory);
    }

    @Bean
    public GetMenuItemsUseCase getMenuItemsUseCase(MenuItemGateway gateway) {
        return new GetMenuItemsUseCaseImpl(gateway);
    }

    @Bean
    public GetMenuItemByIdUseCase getMenuItemByIdUseCase(MenuItemGateway gateway) {
        return new GetMenuItemByIdUseCaseImpl(gateway);
    }

    @Bean
    public GetMenuItemsByRestaurantIdUseCase getMenuItemsByRestaurantIdUseCase(MenuItemGateway gateway) {
        return new GetMenuItemsByRestaurantIdUseCaseImpl(gateway);
    }

    @Bean
    public UpdateMenuItemUseCase updateMenuItemUseCase(MenuItemGateway gateway) {
        return new UpdateMenuItemUseCaseImpl(gateway);
    }

    @Bean
    public DeleteMenuItemUseCase deleteMenuItemUseCase(MenuItemGateway gateway) {
        return new DeleteMenuItemUseCaseImpl(gateway);
    }

    @Bean
    public MenuItemController menuItemController(
            CreateMenuItemUseCase createMenuItemUseCase,
            GetMenuItemsUseCase getMenuItemsUseCase,
            GetMenuItemByIdUseCase getMenuItemByIdUseCase,
            GetMenuItemsByRestaurantIdUseCase getMenuItemsByRestaurantIdUseCase,
            UpdateMenuItemUseCase updateMenuItemUseCase,
            DeleteMenuItemUseCase deleteMenuItemUseCase
    ) {
        return new MenuItemController(
                createMenuItemUseCase,
                getMenuItemsUseCase,
                getMenuItemByIdUseCase,
                getMenuItemsByRestaurantIdUseCase,
                updateMenuItemUseCase,
                deleteMenuItemUseCase
        );
    }

    // ---------- Usuários ----------

    @Bean
    public CreateAppUserUseCase createAppUserUseCase(
            AppUserGateway appUserGateway,
            PersonGateway personGateway,
            PasswordEncoderGateway passwordEncoder,
            AppUserFactory factory
    ) {
        return new CreateAppUserUseCaseImpl(appUserGateway, personGateway, passwordEncoder, factory);
    }

    @Bean
    public GetAppUsersUseCase getAppUsersUseCase(AppUserGateway gateway) {
        return new GetAppUsersUseCaseImpl(gateway);
    }

    @Bean
    public GetAppUserByIdUseCase getAppUserByIdUseCase(AppUserGateway gateway) {
        return new GetAppUserByIdUseCaseImpl(gateway);
    }

    @Bean
    public ChangeAppUserPasswordUseCase changeAppUserPasswordUseCase(
            AppUserGateway gateway,
            PasswordEncoderGateway passwordEncoder
    ) {
        return new ChangeAppUserPasswordUseCaseImpl(gateway, passwordEncoder);
    }

    @Bean
    public DeleteAppUserUseCase deleteAppUserUseCase(AppUserGateway gateway) {
        return new DeleteAppUserUseCaseImpl(gateway);
    }

    @Bean
    public AppUserController appUserController(
            CreateAppUserUseCase createAppUserUseCase,
            GetAppUsersUseCase getAppUsersUseCase,
            GetAppUserByIdUseCase getAppUserByIdUseCase,
            ChangeAppUserPasswordUseCase changeAppUserPasswordUseCase,
            DeleteAppUserUseCase deleteAppUserUseCase
    ) {
        return new AppUserController(
                createAppUserUseCase,
                getAppUsersUseCase,
                getAppUserByIdUseCase,
                changeAppUserPasswordUseCase,
                deleteAppUserUseCase
        );
    }

    // ---------- Clientes ----------

    @Bean
    public RegisterCustomerUseCase registerCustomerUseCase(
            CustomerGateway customerGateway,
            PersonGateway personGateway,
            CustomerFactory factory
    ) {
        return new RegisterCustomerUseCaseImpl(customerGateway, personGateway, factory);
    }

    @Bean
    public GetCustomersUseCase getCustomersUseCase(CustomerGateway gateway) {
        return new GetCustomersUseCaseImpl(gateway);
    }

    @Bean
    public GetCustomerByIdUseCase getCustomerByIdUseCase(CustomerGateway gateway) {
        return new GetCustomerByIdUseCaseImpl(gateway);
    }

    @Bean
    public UpdateCustomerUseCase updateCustomerUseCase(
            CustomerGateway customerGateway,
            PersonGateway personGateway,
            AddressFactory addressFactory
    ) {
        return new UpdateCustomerUseCaseImpl(customerGateway, personGateway, addressFactory);
    }

    @Bean
    public DeleteCustomerUseCase deleteCustomerUseCase(
            CustomerGateway customerGateway,
            RestaurantOwnerGateway restaurantOwnerGateway,
            PersonGateway personGateway
    ) {
        return new DeleteCustomerUseCaseImpl(customerGateway, restaurantOwnerGateway, personGateway);
    }

    @Bean
    public CustomerController customerController(
            RegisterCustomerUseCase registerCustomerUseCase,
            GetCustomersUseCase getCustomersUseCase,
            GetCustomerByIdUseCase getCustomerByIdUseCase,
            UpdateCustomerUseCase updateCustomerUseCase,
            DeleteCustomerUseCase deleteCustomerUseCase
    ) {
        return new CustomerController(
                registerCustomerUseCase,
                getCustomersUseCase,
                getCustomerByIdUseCase,
                updateCustomerUseCase,
                deleteCustomerUseCase
        );
    }

    // ---------- Donos de restaurante ----------

    @Bean
    public RegisterRestaurantOwnerUseCase registerRestaurantOwnerUseCase(
            RestaurantOwnerGateway restaurantOwnerGateway,
            PersonGateway personGateway,
            RestaurantOwnerFactory factory
    ) {
        return new RegisterRestaurantOwnerUseCaseImpl(restaurantOwnerGateway, personGateway, factory);
    }

    @Bean
    public GetRestaurantOwnersUseCase getRestaurantOwnersUseCase(RestaurantOwnerGateway gateway) {
        return new GetRestaurantOwnersUseCaseImpl(gateway);
    }

    @Bean
    public GetRestaurantOwnerByIdUseCase getRestaurantOwnerByIdUseCase(RestaurantOwnerGateway gateway) {
        return new GetRestaurantOwnerByIdUseCaseImpl(gateway);
    }

    @Bean
    public UpdateRestaurantOwnerUseCase updateRestaurantOwnerUseCase(
            RestaurantOwnerGateway restaurantOwnerGateway,
            PersonGateway personGateway,
            AddressFactory addressFactory
    ) {
        return new UpdateRestaurantOwnerUseCaseImpl(restaurantOwnerGateway, personGateway, addressFactory);
    }

    @Bean
    public DeleteRestaurantOwnerUseCase deleteRestaurantOwnerUseCase(
            RestaurantOwnerGateway restaurantOwnerGateway,
            RestaurantGateway restaurantGateway,
            CustomerGateway customerGateway,
            PersonGateway personGateway
    ) {
        return new DeleteRestaurantOwnerUseCaseImpl(
                restaurantOwnerGateway, restaurantGateway, customerGateway, personGateway);
    }

    @Bean
    public RestaurantOwnerController restaurantOwnerController(
            RegisterRestaurantOwnerUseCase registerRestaurantOwnerUseCase,
            GetRestaurantOwnersUseCase getRestaurantOwnersUseCase,
            GetRestaurantOwnerByIdUseCase getRestaurantOwnerByIdUseCase,
            UpdateRestaurantOwnerUseCase updateRestaurantOwnerUseCase,
            DeleteRestaurantOwnerUseCase deleteRestaurantOwnerUseCase
    ) {
        return new RestaurantOwnerController(
                registerRestaurantOwnerUseCase,
                getRestaurantOwnersUseCase,
                getRestaurantOwnerByIdUseCase,
                updateRestaurantOwnerUseCase,
                deleteRestaurantOwnerUseCase
        );
    }

    // ---------- Restaurantes ----------

    @Bean
    public CreateRestaurantUseCase createRestaurantUseCase(
            RestaurantGateway restaurantGateway,
            RestaurantOwnerGateway restaurantOwnerGateway,
            RestaurantFactory factory
    ) {
        return new CreateRestaurantUseCaseImpl(restaurantGateway, restaurantOwnerGateway, factory);
    }

    @Bean
    public GetRestaurantsUseCase getRestaurantsUseCase(RestaurantGateway gateway) {
        return new GetRestaurantsUseCaseImpl(gateway);
    }

    @Bean
    public GetRestaurantByIdUseCase getRestaurantByIdUseCase(RestaurantGateway gateway) {
        return new GetRestaurantByIdUseCaseImpl(gateway);
    }

    @Bean
    public UpdateRestaurantUseCase updateRestaurantUseCase(
            RestaurantGateway restaurantGateway,
            RestaurantOwnerGateway restaurantOwnerGateway,
            OpeningHoursFactory openingHoursFactory
    ) {
        return new UpdateRestaurantUseCaseImpl(restaurantGateway, restaurantOwnerGateway, openingHoursFactory);
    }

    @Bean
    public DeleteRestaurantUseCase deleteRestaurantUseCase(RestaurantGateway gateway) {
        return new DeleteRestaurantUseCaseImpl(gateway);
    }

    @Bean
    public RestaurantController restaurantController(
            CreateRestaurantUseCase createRestaurantUseCase,
            GetRestaurantsUseCase getRestaurantsUseCase,
            GetRestaurantByIdUseCase getRestaurantByIdUseCase,
            UpdateRestaurantUseCase updateRestaurantUseCase,
            DeleteRestaurantUseCase deleteRestaurantUseCase
    ) {
        return new RestaurantController(
                createRestaurantUseCase,
                getRestaurantsUseCase,
                getRestaurantByIdUseCase,
                updateRestaurantUseCase,
                deleteRestaurantUseCase
        );
    }
}
