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

        validateCategory(category, request);

        Transaction transaction = Transaction.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .amount(request.getAmount())
                .type(request.getType())
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

        validateCategory(category, request);

        transaction.setTitle(request.getTitle());
        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
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
    public List<TransactionResponse> getTransactionsByType(String email,
                                                           CategoryType type) {

        User user = getUser(email);

        return transactionRepository.findByUserAndType(user, type)
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

    // ================= Helper Methods =================

    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    private void validateCategory(Category category,
                                  TransactionRequest request) {

        if (category.getType() != request.getType()) {
            throw new IllegalArgumentException(
                    "Category type does not match transaction type");
        }
    }

    private TransactionResponse mapToResponse(Transaction transaction) {

        return TransactionResponse.builder()
                .id(transaction.getId())
                .title(transaction.getTitle())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .category(transaction.getCategory().getName())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }
}