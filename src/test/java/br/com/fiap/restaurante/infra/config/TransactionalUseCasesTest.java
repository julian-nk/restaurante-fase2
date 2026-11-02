package br.com.fiap.restaurante.infra.config;

import br.com.fiap.restaurante.core.dto.CreatePersonInputDto;
import br.com.fiap.restaurante.core.dto.LoginInputDto;
import br.com.fiap.restaurante.core.dto.PersonOutputDto;
import br.com.fiap.restaurante.core.exception.CustomerNotFoundException;
import br.com.fiap.restaurante.core.usecase.CreatePersonUseCase;
import br.com.fiap.restaurante.core.usecase.DeleteCustomerUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionalUseCasesTest {

    private PlatformTransactionManager transactionManager;
    private TransactionStatus status;

    @BeforeEach
    void setUp() {
        transactionManager = mock(PlatformTransactionManager.class);
        status = mock(TransactionStatus.class);
        when(transactionManager.getTransaction(any())).thenReturn(status);
    }

    @Test
    void shouldRunTheUseCaseInsideATransactionAndReturnItsResult() {
        CreatePersonUseCase target = mock(CreatePersonUseCase.class);
        var input = new CreatePersonInputDto("Ana", "ana@email.com", "39053344705", null);
        var login = new LoginInputDto("ana", "senha1234");
        var output = new PersonOutputDto(1L, "Ana", "ana@email.com", "39053344705", null);
        when(target.execute(input, login)).thenReturn(output);

        CreatePersonUseCase useCase = TransactionalUseCases.wrap(CreatePersonUseCase.class, target, transactionManager);

        assertSame(output, useCase.execute(input, login));
        InOrder order = inOrder(transactionManager, target);
        order.verify(transactionManager).getTransaction(any());
        order.verify(target).execute(input, login);
        order.verify(transactionManager).commit(status);
    }

    @Test
    void shouldRollBackAndRethrowTheExceptionOfTheUseCaseUnchanged() {
        DeleteCustomerUseCase target = mock(DeleteCustomerUseCase.class);
        CustomerNotFoundException failure = new CustomerNotFoundException(1L);
        doThrow(failure).when(target).execute(1L);

        DeleteCustomerUseCase useCase = TransactionalUseCases.wrap(DeleteCustomerUseCase.class, target, transactionManager);

        assertSame(failure, assertThrows(CustomerNotFoundException.class, () -> useCase.execute(1L)));
        verify(transactionManager).rollback(status);
        verify(transactionManager, never()).commit(any());
    }

    @Test
    void shouldNotOpenATransactionForTheMethodsOfObject() {
        DeleteCustomerUseCase useCase = TransactionalUseCases.wrap(
                DeleteCustomerUseCase.class, mock(DeleteCustomerUseCase.class), transactionManager);

        assertNotNull(useCase.toString());
        verifyNoInteractions(transactionManager);
    }
}
