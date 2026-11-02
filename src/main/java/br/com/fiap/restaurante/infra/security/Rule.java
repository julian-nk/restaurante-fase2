package br.com.fiap.restaurante.infra.security;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;

public record Rule(HttpMethod method, String pattern, List<UserTypeEnum> userTypeCodes) {

    public Rule {
        userTypeCodes = List.copyOf(userTypeCodes);
    }

    public boolean allowsAnyAuthenticatedUser() {
        return userTypeCodes.isEmpty();
    }

    /**
     * As roles do Spring Security equivalentes aos tipos de usuário, para o {@code hasAnyRole}.
     */
    public String[] roles() {
        return userTypeCodes.stream().map(u -> ApiAccessRules.roleOf(u.getCode())).toArray(String[]::new);
    }

    boolean matches(HttpMethod requestMethod, String path) {
        if (method != null && !method.equals(requestMethod)) {
            return false;
        }
        PathPattern parsed = PathPatternParser.defaultInstance.parse(pattern);
        return parsed.matches(PathContainer.parsePath(path));
    }
}
