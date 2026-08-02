package com.finvision.transaction.controller;

import com.finvision.category.entity.CategoryType;

import com.finvision.transaction.dto.TransactionRequest;
import com.finvision.transaction.dto.TransactionResponse;
import com.finvision.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> addTransaction(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.addTransaction(
                        authentication.getName(),
                        request));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.getAllTransactions(
                        authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.getTransactionById(
                        id,
                        authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.updateTransaction(
                        id,
                        authentication.getName(),
                        request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id,
            Authentication authentication) {

        transactionService.deleteTransaction(
                id,
                authentication.getName());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByType(
            @PathVariable CategoryType type,
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByType(
                        authentication.getName(),
                        type));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByCategory(
            @PathVariable Long categoryId,
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByCategory(
                        authentication.getName(),
                        categoryId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TransactionResponse>> searchTransactions(
            @RequestParam String keyword,
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.searchTransactions(
                        authentication.getName(),
                        keyword));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionResponse>> getTransactionsBetweenDates(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.getTransactionsBetweenDates(
                        authentication.getName(),
                        startDate,
                        endDate));
    }
}