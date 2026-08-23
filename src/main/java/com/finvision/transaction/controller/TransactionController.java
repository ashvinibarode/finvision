package com.finvision.transaction.controller;

import com.finvision.category.entity.CategoryType;
import com.finvision.transaction.dto.TransactionFilterRequest;
import com.finvision.transaction.dto.TransactionRequest;
import com.finvision.transaction.dto.TransactionResponse;
import com.finvision.transaction.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
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

    // ADD TRANSACTION
    @PostMapping
    public ResponseEntity<TransactionResponse> addTransaction(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        transactionService.addTransaction(
                                authentication.getName(),
                                request
                        )
                );
    }



    // GET TRANSACTIONS
    // SEARCH + CATEGORY + DATE + PAGINATION


    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getTransactions(

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            Long categoryId,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate toDate,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(
                    defaultValue = "transactionDate"
            )
            String sortBy,

            @RequestParam(
                    defaultValue = "desc"
            )
            String direction,

            Authentication authentication) {


        TransactionFilterRequest filter =
                new TransactionFilterRequest();

        filter.setSearch(search);
        filter.setCategoryId(categoryId);
        filter.setFromDate(fromDate);
        filter.setToDate(toDate);


        return ResponseEntity.ok(
                transactionService.getTransactions(
                        authentication.getName(),
                        page,
                        size,
                        sortBy,
                        direction,
                        filter
                )
        );
    }



    // GET TRANSACTION BY ID


    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.getTransactionById(
                        id,
                        authentication.getName()
                )
        );
    }



    // UPDATE TRANSACTION


    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.updateTransaction(
                        id,
                        authentication.getName(),
                        request
                )
        );
    }



    // DELETE TRANSACTION


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id,
            Authentication authentication) {

        transactionService.deleteTransaction(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }



    // GET BY CATEGORY TYPE


    @GetMapping("/type/{type}")
    public ResponseEntity<List<TransactionResponse>>
    getTransactionsByType(
            @PathVariable CategoryType type,
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByType(
                        authentication.getName(),
                        type
                )
        );
    }



    // GET BY CATEGORY


    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TransactionResponse>>
    getTransactionsByCategory(
            @PathVariable Long categoryId,
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByCategory(
                        authentication.getName(),
                        categoryId
                )
        );
    }



    // SEARCH


    @GetMapping("/search")
    public ResponseEntity<List<TransactionResponse>>
    searchTransactions(
            @RequestParam String keyword,
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.searchTransactions(
                        authentication.getName(),
                        keyword
                )
        );
    }



    // DATE RANGE


    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionResponse>>
    getTransactionsBetweenDates(

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate endDate,

            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.getTransactionsBetweenDates(
                        authentication.getName(),
                        startDate,
                        endDate
                )
        );
    }



    // PAGINATED TRANSACTIONS


    @GetMapping("/page")
    public ResponseEntity<Page<TransactionResponse>>
    getTransactionsPage(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(
                    defaultValue = "transactionDate"
            )
            String sortBy,

            @RequestParam(
                    defaultValue = "desc"
            )
            String direction,

            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.getTransactions(
                        authentication.getName(),
                        page,
                        size,
                        sortBy,
                        direction,
                        new TransactionFilterRequest()
                )
        );
    }
}