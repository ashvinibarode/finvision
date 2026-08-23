package com.finvision.transaction.service.impl;

import com.finvision.category.entity.CategoryType;
import com.finvision.category.entity.Category;
import com.finvision.category.repository.CategoryRepository;
import com.finvision.common.exception.ResourceNotFoundException;
import com.finvision.transaction.dto.TransactionFilterRequest;
import com.finvision.transaction.dto.TransactionRequest;
import com.finvision.transaction.dto.TransactionResponse;
import com.finvision.transaction.entity.Transaction;
import com.finvision.transaction.repository.TransactionRepository;
import com.finvision.transaction.service.TransactionService;
import com.finvision.user.entity.User;
import com.finvision.user.repository.UserRepository;


import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;



    // ADD TRANSACTION


    @Override
    public TransactionResponse addTransaction(
            String email,
            TransactionRequest request) {

        User user = getUser(email);

        Category category =
                categoryRepository.findAccessibleCategory(
                        request.getCategoryId(),
                        user
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found"
                        )
                );


        Transaction transaction =
                Transaction.builder()
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .amount(request.getAmount())
                        .transactionDate(
                                request.getTransactionDate()
                        )
                        .category(category)
                        .user(user)
                        .build();


        transaction =
                transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }



    // UPDATE TRANSACTION


    @Override
    public TransactionResponse updateTransaction(
            Long id,
            String email,
            TransactionRequest request) {

        User user = getUser(email);

        Transaction transaction =
                transactionRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found"
                                )
                        );


        Category category =
                categoryRepository.findAccessibleCategory(
                        request.getCategoryId(),
                        user
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found"
                        )
                );


        transaction.setTitle(
                request.getTitle()
        );

        transaction.setDescription(
                request.getDescription()
        );

        transaction.setAmount(
                request.getAmount()
        );

        transaction.setCategory(
                category
        );

        transaction.setTransactionDate(
                request.getTransactionDate()
        );


        transaction =
                transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }



    // DELETE TRANSACTION


    @Override
    public void deleteTransaction(
            Long id,
            String email) {

        User user = getUser(email);

        Transaction transaction =
                transactionRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found"
                                )
                        );

        transactionRepository.delete(transaction);
    }



    // GET BY ID


    @Override
    public TransactionResponse getTransactionById(
            Long id,
            String email) {

        User user = getUser(email);

        Transaction transaction =
                transactionRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found"
                                )
                        );

        return mapToResponse(transaction);
    }



    // GET ALL


    @Override
    public List<TransactionResponse> getAllTransactions(
            String email) {

        User user = getUser(email);

        return transactionRepository
                .findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }



    // GET BY CATEGORY TYPE


    @Override
    public List<TransactionResponse> getTransactionsByType(
            String email,
            CategoryType type) {

        User user = getUser(email);

        return transactionRepository
                .findByUserAndCategory_Type(
                        user,
                        type
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }



    // GET BY CATEGORY


    @Override
    public List<TransactionResponse> getTransactionsByCategory(
            String email,
            Long categoryId) {

        User user = getUser(email);

        return transactionRepository
                .findByUserAndCategoryId(
                        user,
                        categoryId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }



    // SEARCH


    @Override
    public List<TransactionResponse> searchTransactions(
            String email,
            String keyword) {

        User user = getUser(email);

        return transactionRepository
                .findByUserAndTitleContainingIgnoreCase(
                        user,
                        keyword
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }



    // DATE RANGE
    @Override
    public List<TransactionResponse> getTransactionsBetweenDates(
            String email,
            LocalDate startDate,
            LocalDate endDate) {

        User user = getUser(email);

        if (startDate != null &&
                endDate != null &&
                startDate.isAfter(endDate)) {

            throw new IllegalArgumentException(
                    "Start date cannot be after end date"
            );
        }

        return transactionRepository
                .findByUserAndTransactionDateBetween(
                        user,
                        startDate,
                        endDate
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }





    // FILTER + PAGINATION + SORT


    @Override
    public Page<TransactionResponse> getTransactions(
            String email,
            int page,
            int size,
            String sortBy,
            String direction,
            TransactionFilterRequest filter) {

        User user = getUser(email);

        // Validate date range
        if (filter != null &&
                filter.getFromDate() != null &&
                filter.getToDate() != null &&
                filter.getFromDate().isAfter(filter.getToDate())) {

            throw new IllegalArgumentException(
                    "From date cannot be after to date"
            );
        }


        // Prevent invalid page/size values
        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }


        // Allow only valid sortable fields
        String validSortBy =
                getValidSortField(sortBy);


        Sort.Direction sortDirection =
                "asc".equalsIgnoreCase(direction)
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;


        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                sortDirection,
                                validSortBy
                        )
                );



        // USER OWNERSHIP


        Specification<Transaction> specification =
                Specification.where(
                        (root, query, criteriaBuilder) ->
                                criteriaBuilder.equal(
                                        root.get("user"),
                                        user
                                )
                );



        // CATEGORY FILTER


        if (filter != null &&
                filter.getCategoryId() != null) {

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    criteriaBuilder.equal(
                                            root.get("category")
                                                    .get("id"),
                                            filter.getCategoryId()
                                    )
                    );
        }



        // FROM DATE FILTER


        if (filter != null &&
                filter.getFromDate() != null) {

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    criteriaBuilder
                                            .greaterThanOrEqualTo(
                                                    root.get(
                                                            "transactionDate"
                                                    ),
                                                    filter.getFromDate()
                                            )
                    );
        }



        // TO DATE FILTER


        if (filter != null &&
                filter.getToDate() != null) {

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    criteriaBuilder
                                            .lessThanOrEqualTo(
                                                    root.get(
                                                            "transactionDate"
                                                    ),
                                                    filter.getToDate()
                                            )
                    );
        }



        // SEARCH FILTER


        if (filter != null &&
                filter.getSearch() != null &&
                !filter.getSearch().isBlank()) {

            String search =
                    "%" +
                            filter.getSearch()
                                    .trim()
                                    .toLowerCase() +
                            "%";


            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    criteriaBuilder.or(

                                            criteriaBuilder.like(
                                                    criteriaBuilder.lower(
                                                            root.get(
                                                                    "title"
                                                            )
                                                    ),
                                                    search
                                            ),

                                            criteriaBuilder.like(
                                                    criteriaBuilder.lower(
                                                            root.get(
                                                                    "description"
                                                            )
                                                    ),
                                                    search
                                            )
                                    )
                    );
        }


        return transactionRepository
                .findAll(
                        specification,
                        pageable
                )
                .map(this::mapToResponse);
    }



    // VALID SORT FIELD


    private String getValidSortField(
            String sortBy) {

        if (sortBy == null ||
                sortBy.isBlank()) {

            return "transactionDate";
        }


        return switch (sortBy) {

            case "title" ->
                    "title";

            case "amount" ->
                    "amount";

            case "transactionDate" ->
                    "transactionDate";

            default ->
                    "transactionDate";
        };
    }



    // GET USER


    private User getUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }



    // MAP ENTITY → RESPONSE


    private TransactionResponse mapToResponse(
            Transaction transaction) {

        return TransactionResponse.builder()
                .id(transaction.getId())
                .title(transaction.getTitle())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .type(
                        transaction
                                .getCategory()
                                .getType()
                )
                .category(
                        transaction
                                .getCategory()
                                .getName()
                )
                .transactionDate(
                        transaction
                                .getTransactionDate()
                )
                .build();
    }
}