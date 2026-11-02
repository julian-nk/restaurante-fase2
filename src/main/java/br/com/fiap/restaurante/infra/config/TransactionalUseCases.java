package br.com.fiap.restaurante.infra.config;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Faz cada chamada a um caso de uso rodar inteira numa transação só. Os casos de uso do core não conhecem o
 * Spring; quando um deles grava em mais de um gateway (a pessoa e o usuário de acesso dela, por exemplo), garante a atomicidade da operação.
 * As transações dos gateways participam desta.
 */
final class TransactionalUseCases {

    private TransactionalUseCases() {
    }

    static <T> T wrap(Class<T> useCase, T target, PlatformTransactionManager transactionManager) {
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        Object proxy = Proxy.newProxyInstance(
                useCase.getClassLoader(),
                new Class<?>[]{useCase},
                (self, method, args) -> method.getDeclaringClass() == Object.class
                                        ? invoke(target, method, args)
                                        : transaction.execute(status -> invoke(target, method, args)));
        return useCase.cast(proxy);
    }

    /**
     * Chama o caso de uso e repassa a exceção dele como veio, para o {@code GlobalExceptionHandler} tratá-la.
     */
    private static Object invoke(Object target, Method method, Object[] args) {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if (e.getCause() instanceof Error error) {
                throw error;
            }
            throw new IllegalStateException(e.getCause());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
