package br.com.fiap.restaurante.infra.security;

import br.com.fiap.restaurante.core.domain.UserTypeEnum;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class ApiAccessRules {

    /**
     * Rotas abertas, sem login: a documentação da API e a página de erro do Spring.
     */
    public static final List<String> PUBLIC_PATHS = List.of(
            "/my-docs",
            "/my-docs/**",
            "/api-docs",
            "/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/error"
    );


    public static final List<Rule> RULES = List.of(
            anyUser(HttpMethod.GET, "/api/restaurants/**"),
            anyUser(HttpMethod.GET, "/api/menu-items/**"),

            // Cadastro, alteração e exclusão de restaurantes e pratos: dono de restaurante ou administrador
            onlyTypes(null, "/api/restaurants/**", UserTypeEnum.ADMIN, UserTypeEnum.RESTAURANT_OWNER),
            onlyTypes(null, "/api/menu-items/**", UserTypeEnum.ADMIN, UserTypeEnum.RESTAURANT_OWNER),

            // Troca de senha: qualquer usuário autenticado chega ao caso de uso, que só aceita o próprio usuário
            // logado e confere a senha atual
            anyUser(HttpMethod.PATCH, "/api/app-users/*/password"),

            // Demais endpoints (tipos de usuário e associações, usuários, pessoas, clientes e donos): administrador
            onlyTypes(null, "/api/**", UserTypeEnum.ADMIN)
    );

    private ApiAccessRules() {
    }

    private static Rule anyUser(HttpMethod method, String pattern) {
        return new Rule(method, pattern, List.of());
    }

    private static Rule onlyTypes(HttpMethod method, String pattern, UserTypeEnum... userTypeCodes) {
        return new Rule(method, pattern, List.of(userTypeCodes));
    }

    /**
     * Regra que vale para a requisição, ou vazio se nenhuma casar (as rotas públicas não têm regra).
     *
     * @param path caminho da requisição; também aceita o modelo do OpenAPI, como {@code /api/menu-items/{id}}
     */
    public static Optional<Rule> ruleFor(HttpMethod method, String path) {
        return RULES.stream().filter(rule -> rule.matches(method, path)).findFirst();
    }

    public static String roleOf(String userTypeCode) {
        return userTypeCode.toUpperCase(Locale.ROOT);
    }
}
