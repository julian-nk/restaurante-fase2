package br.com.fiap.restaurante;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Apenas o pacote {@code infra} é escaneado: o {@code core} não tem anotações do Spring (seus beans
 * são criados em {@code infra.config}) e o pacote {@code old} guarda a versão anterior só como referência.
 */
@SpringBootApplication(scanBasePackages = "br.com.fiap.restaurante.infra")
public class RestauranteApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestauranteApplication.class, args);
    }

}
