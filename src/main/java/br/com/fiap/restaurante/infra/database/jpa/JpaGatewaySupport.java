package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.exception.RepositoryAccessException;
import br.com.fiap.restaurante.core.exception.SystemBaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Classe base dos gateways JPA. Ela existe para que nenhuma exceção do Spring Data ou do Hibernate
 * vaze para o core.
 *
 * <p><b>Atenção com métodos {@code @Transactional}.</b> A tradução só alcança o que acontece enquanto a
 * lambda está rodando. Se o próprio método do gateway for transacional, o commit acontece depois que ele
 * retorna, já fora do {@code try}, e um erro de constraint que só aparecesse nesse momento passaria direto.
 * Nesses casos, chame {@code flush()} dentro da lambda para forçar o SQL a rodar ali mesmo, como faz o
 * {@link PersonJpaGateway#deleteById(Long)}.
 */
public abstract class JpaGatewaySupport {

    /**
     * Usa a classe concreta, para que o log mostre qual gateway falhou, e não esta classe base.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Executa uma operação no repositório e devolve o resultado, traduzindo qualquer falha para o core.
     *
     * @param action a operação, passada como lambda para que rode dentro do {@code try}
     * @param <T>    o tipo do resultado, inferido pela própria lambda
     * @return o que a operação devolveu
     * @throws RepositoryAccessException se o acesso aos dados falhar
     * @throws SystemBaseException       se a própria operação lançar uma exceção do core, repassada sem
     *                                   alteração
     */
    protected <T> T call(Supplier<T> action) {
        try {
            return action.get();
        } catch (SystemBaseException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("Erro ao acessar o repositório de dados", e);
            throw new RepositoryAccessException(e);
        }
    }

    /**
     * Versão do {@link #call(Supplier)} para operações que não devolvem nada, como apagar ou associar.
     *
     * <p>Ela só adapta o {@code Runnable} para um {@code Supplier} que devolve {@code null}, então as regras
     * de tradução são exatamente as mesmas:
     *
     * <pre>{@code
     * run(() -> personRepository.deleteById(id));
     * }</pre>
     *
     * @param action a operação a executar
     * @throws RepositoryAccessException se o acesso aos dados falhar
     */
    protected void run(Runnable action) {
        call(() -> {
            action.run();
            return null;
        });
    }
}
