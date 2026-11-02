package br.com.fiap.restaurante.infra.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * <ul>
 *     <li><b>401</b> ({@code auth.unauthenticated}): sem login, ou login e senha que não conferem;</li>
 *     <li><b>403</b> ({@code auth.forbidden}): login válido, mas nenhum dos tipos do usuário tem permissão para a
 *     rota (ver {@link ApiAccessRules}).</li>
 * </ul>
 */
public class JsonSecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    static final String UNAUTHENTICATED_CODE = "auth.unauthenticated";
    static final String FORBIDDEN_CODE = "auth.forbidden";

    private static final String REALM = "restaurante";

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"" + REALM + "\"");
        write(response, HttpStatus.UNAUTHORIZED, UNAUTHENTICATED_CODE,
              "Informe o login e a senha de um usuário cadastrado (HTTP Basic).");
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception
    ) throws IOException {
        write(response, HttpStatus.FORBIDDEN, FORBIDDEN_CODE,
              "O seu tipo de usuário não tem permissão para esta operação.");
    }

    /** As mensagens são fixas e sem aspas, então o JSON pode ser montado direto, sem serializador. */
    private static void write(HttpServletResponse response, HttpStatus status, String code, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"error\":\"" + status.getReasonPhrase()
                                   + "\",\"code\":\"" + code
                                   + "\",\"message\":\"" + message + "\"}");
    }
}
