package com.galimberti.paymentsapi.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.galimberti.paymentsapi.domain.Transaction;
import com.galimberti.paymentsapi.dto.TransactionRequest;
import com.galimberti.paymentsapi.services.TransactionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Transações", description = "Transações de uma categoria")
@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado", content = @Content)
@RestController 
@RequestMapping ("/api/categories/{categoryId}/transactions")
public class TransactionResource {

    @Autowired 
    TransactionService transactionService;

    @Operation(summary = "Cria uma transação")
    @ApiResponse(responseCode = "201", description = "Transação criada")
    @ApiResponse(responseCode = "404", description = "categoria não existe ou é de outro usuário", content = @Content)
    @ApiResponse(responseCode = "400", description = "dados invalidos", content = @Content)
    @PostMapping("")
    public ResponseEntity<Transaction> addTransaction(HttpServletRequest request, @PathVariable("categoryId") Integer categoryId, @Valid @RequestBody TransactionRequest body) {
        int userId = (Integer) request.getAttribute("userId");
        Transaction transaction = transactionService.addTransaction(
            userId, categoryId, body.amount(), body.note(), body.transactionDate());
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @Operation(summary = "Retorna transação por id")
    @ApiResponse(responseCode = "200", description = "Transação")
    @ApiResponse(responseCode = "404", description = "Transação não encontrada", content = @Content)
    @GetMapping ("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(HttpServletRequest request, @PathVariable ("categoryId") Integer categoryId, @PathVariable ("transactionId") Integer transactionId) {
        int userId = (Integer) request.getAttribute("userId");
        Transaction transaction = transactionService.fetchTransactionById(userId, categoryId, transactionId);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @Operation(summary = "Retorna lista de transações por categoria")
    @ApiResponse(responseCode = "200", description = "Lista (pode ser vazia)")
    @GetMapping ("")
    public ResponseEntity<List<Transaction>> getAllTransaction(HttpServletRequest request, @PathVariable ("categoryId") Integer categoryId) {
        int userId = (Integer) request.getAttribute("userId");
        List<Transaction> transactions = transactionService.fetchAllTransactions(userId, categoryId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @Operation(summary = "Altera transação por id")
    @ApiResponse(responseCode = "200", description = "Transação")
    @ApiResponse(responseCode = "400", description = "dados invalidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "Transação não encontrada", content = @Content)
    @PutMapping ("/{transactionId}")
    public ResponseEntity<Map<String, Boolean>> updateTransaction(HttpServletRequest request, @PathVariable ("categoryId") Integer categoryId, @PathVariable ("transactionId") Integer transactionId, @Valid @RequestBody TransactionRequest body) {
        int userId = (Integer) request.getAttribute("userId");
        Transaction transaction = new Transaction(transactionId, categoryId, userId,
            body.amount(), body.note(), body.transactionDate());
        transactionService.updateTransaction(userId, categoryId, transactionId, transaction);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Operation(summary = "Exclui transação por id")
    @ApiResponse(responseCode = "200", description = "Transação")
    @ApiResponse(responseCode = "404", description = "Transação não encontrada", content = @Content)
    @DeleteMapping ("/{transactionId}")
    public ResponseEntity<Map<String, Boolean>> deleteTransaction(HttpServletRequest request, @PathVariable ("categoryId") Integer categoryId, @PathVariable ("transactionId") Integer transactionId) {
        int userId = (Integer) request.getAttribute("userId");
        transactionService.removeTransaction(userId, categoryId, transactionId);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

}
