package com.finvision.transaction.service.impl;

import com.finvision.category.entity.CategoryType;
import com.finvision.category.entity.Category;
import com.finvision.category.repository.CategoryRepository;
import com.finvision.common.exception.ResourceNotFoundException;
import com.finvision.transaction.dto.TransactionRequest;
import com.finvision.transaction.dto.TransactionResponse;
import com.finvision.transaction.entity.Transaction;
import com.finvision.transaction.repository.TransactionRepository;
import com.finvision.transaction.service.TransactionService;
import com.finvision.user.entity.User;
import com.finvision.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public TransactionResponse addTransaction(String email, TransactionRequest request) {

        User user = getUser(email);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));


        // No validation required
        Transaction transaction = Transaction.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .amount(request.getAmount())
                .transactionDate(request.getTransactionDate())
                .category(category)
                .user(user)
                .build();

        transaction = transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    @Override
    public TransactionResponse updateTransaction(Long id, String email, TransactionRequest request) {

        User user = getUser(email);

        Transaction transaction = transactionRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // No validation required

        transaction.setTitle(request.getTitle());
        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setCategory(category);
        transaction.setTransactionDate(request.getTransactionDate());

        transaction = transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    @Override
    public void deleteTransaction(Long id, String email) {

        User user = getUser(email);

        Transaction transaction = transactionRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        transactionRepository.delete(transaction);
    }

    @Override
    public TransactionResponse getTransactionById(Long id, String email) {

        User user = getUser(email);

        Transaction transaction = transactionRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        return mapToResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getAllTransactions(String email) {

        User user = getUser(email);

        return transactionRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<TransactionResponse> getTransactionsByType(
            String email,
            CategoryType type) {

        User user = getUser(email);

        return transactionRepository.findByUserAndCategory_Type(user, type)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<TransactionResponse> getTransactionsByCategory(String email,
                                                               Long categoryId) {

        User user = getUser(email);

        return transactionRepository.findByUserAndCategoryId(user, categoryId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<TransactionResponse> searchTransactions(String email,
                                                        String keyword) {

        User user = getUser(email);

        return transactionRepository
                .findByUserAndTitleContainingIgnoreCase(user, keyword)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<TransactionResponse> getTransactionsBetweenDates(
            String email,
            LocalDate startDate,
            LocalDate endDate) {

        User user = getUser(email);

        return transactionRepository
                .findByUserAndTransactionDateBetween(user, startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public Page<TransactionResponse> getTransactions(
            String email,
            int page,
            int size,
            String sortBy,
            String direction) {

        User user = getUser(email);

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return transactionRepository.findByUser(user, pageable)
                .map(this::mapToResponse);
    }

    // ================= Helper Methods =================

    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }


    private TransactionResponse mapToResponse(Transaction transaction) {

        return TransactionResponse.builder()
                .id(transaction.getId())
                .title(transaction.getTitle())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .type(transaction.getCategory().getType())
                .category(transaction.getCategory().getName())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }

}