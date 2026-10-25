
package br.com.fiap.restaurante;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.*;

class RestauranteApplicationTest {

    @Test
    void shouldCallSpringApplicationRun() {
        try (MockedStatic<SpringApplication> mocked =
                     mockStatic(SpringApplication.class)) {

            String[] args = {"--spring.main.web-application-type=none"};

            RestauranteApplication.main(args);

            mocked.verify(() ->
                    SpringApplication.run(RestauranteApplication.class, args)
            );
        }
    }
}
