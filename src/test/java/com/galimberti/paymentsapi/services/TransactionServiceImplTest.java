package com.galimberti.paymentsapi.services;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.galimberti.paymentsapi.domain.Category;
import com.galimberti.paymentsapi.domain.Transaction;
import com.galimberti.paymentsapi.exceptions.PaBadRequestException;
import com.galimberti.paymentsapi.exceptions.PaResourceNotFoundException;
import com.galimberti.paymentsapi.repositories.CategoryRepository;
import com.galimberti.paymentsapi.repositories.TransactionRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    TransactionServiceImpl transactionService;

    @Test
    void deveRejeitarTransacaoEmCategoriaDeOutroUsuario() {
        // Arrange: a categoria 2 não pertence ao usuário 1
        when(categoryRepository.findById(1, 2))
            .thenThrow(new PaResourceNotFoundException("Category not found"));

        // Act + Assert
        assertThrows(PaResourceNotFoundException.class,
            () -> transactionService.addTransaction(1, 2, 7000.0, "Nota", 1577817000000L));

        // Nenhuma transação pode ser gravada
        verify(transactionRepository, never()).create(any(), any(), any(), any(), any());
    }

    @Test
    void deveCriarTransacaoERetornarAPersistida() {
        // Arrange
        Category category = new Category(2, 1, "titulo", "desc", 0.0);
        Transaction transacaoSalva = new Transaction(1001, 2, 1, 8000.0, "nota", 1577817000000L);
        when(categoryRepository.findById(1, 2)).thenReturn(category);
        when(transactionRepository.create(1, 2, 8000.0, "nota", 1577817000000L)).thenReturn(1001);
        when(transactionRepository.findById(1, 2, 1001)).thenReturn(transacaoSalva);

        // Act
        Transaction resultado = transactionService.addTransaction(1, 2, 8000.0, "nota", 1577817000000L);

        // Assert: o service devolve a transação buscada pelo id gerado no create
        assertSame(transacaoSalva, resultado);

        InOrder ordem = inOrder(categoryRepository, transactionRepository);
        ordem.verify(categoryRepository).findById(1, 2);
        ordem.verify(transactionRepository).create(1, 2, 8000.0, "nota", 1577817000000L);
        ordem.verify(transactionRepository).findById(1, 2, 1001);
    }

    @Test
    void deveRetornarTransacaoDoId() {
        // Arrange
		Transaction transacaoReferencia = new Transaction(1001, 2, 1, 8000.0, "nota", 1577817000000L);
		when(transactionRepository.findById(1, 2, 1001)).thenReturn(transacaoReferencia);

        // Act 
		Transaction resultado = transactionService.fetchTransactionById(1, 2, 1001);
		
		// Assert
		assertSame(transacaoReferencia, resultado);
    }

    @Test
    void deveRetornarTodasTransacoesDaCategoria() {
        // Arrange
		Transaction transacao1 = new Transaction(1001, 2, 1, 8000.0, "nota1", 1577817000000L);
		Transaction transacao2 = new Transaction(1002, 2, 1, 9000.0, "nota2", 1577817000001L);
		List<Transaction> transacaoReferencia = List.of(transacao1, transacao2);
		when(transactionRepository.findAll(1, 2)).thenReturn(transacaoReferencia);

        // Act 
		List<Transaction> resultado = transactionService.fetchAllTransactions(1, 2);
		
		// Assert
		assertSame(transacaoReferencia, resultado);
    }
	
	@Test
	void deveRetornarListaVaziaQuandoCategoriaNaoTemTransacoes() {
		when(transactionRepository.findAll(1, 2)).thenReturn(List.of());

		List<Transaction> resultado = transactionService.fetchAllTransactions(1, 2);

		assertTrue(resultado.isEmpty());
	}

    @Test
    void deveRemoverTransacao() {
        // Act
        transactionService.removeTransaction(1, 2, 1001);

        // Assert
        verify(transactionRepository).removeById(1, 2, 1001);
    }

    @Test
    void deveLancarExcecaoAoRemoverTransacaoInexistente() {
        // Arrange
        doThrow(new PaResourceNotFoundException("Transaction not found"))
            .when(transactionRepository).removeById(1, 2, 9999);

        // Act + Assert
        assertThrows(PaResourceNotFoundException.class,
            () -> transactionService.removeTransaction(1, 2, 9999));
    }

    @Test
    void deveAtualizarTransacao() {
        // Arrange
		Transaction transacao = new Transaction(1001, 2, 1, 9000.0, "nota2", 1577817000001L);
		
		// Act
        transactionService.updateTransaction(1, 2, 1001, transacao);

        // Assert
        verify(transactionRepository).update(1, 2, 1001, transacao);
    }

    @Test
    void deveLancarExcecaoAoAtualizarTransacaoInexistente() {
        // Arrange
		Transaction transacao = new Transaction(1001, 2, 1, 9000.0, "nota2", 1577817000001L);
        doThrow(new PaBadRequestException("Invalid request"))
            .when(transactionRepository).update(1, 2, 1001, transacao);

        // Act + Assert
        assertThrows(PaBadRequestException.class,
            () -> transactionService.updateTransaction(1, 2, 1001, transacao));
    }
}
