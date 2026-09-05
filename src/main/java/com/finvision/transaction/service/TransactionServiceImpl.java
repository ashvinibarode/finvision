package com.finvision.transaction.service;

import com.finvision.budget.entity.Budget;
import com.finvision.budget.repository.BudgetRepository;
import com.finvision.category.entity.Category;
import com.finvision.category.entity.CategoryType;
import com.finvision.category.repository.CategoryRepository;
import com.finvision.common.exception.ResourceNotFoundException;
import com.finvision.notification.entity.NotificationType;
import com.finvision.notification.service.NotificationService;
import com.finvision.transaction.dto.TransactionFilterRequest;
import com.finvision.transaction.dto.TransactionRequest;
import com.finvision.transaction.dto.TransactionResponse;
import com.finvision.transaction.entity.Transaction;
import com.finvision.transaction.repository.TransactionRepository;
import com.finvision.user.entity.User;
import com.finvision.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final NotificationService notificationService;


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

        // Check budget notifications
        checkBudgetNotification(
                user,
                category,
                transaction.getTransactionDate()
        );

        // Check low balance notification
        checkLowBalanceNotification(user);

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

        // Check budget notifications
        checkBudgetNotification(
                user,
                category,
                transaction.getTransactionDate()
        );

        // Check low balance notification
        checkLowBalanceNotification(user);

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
                    "%"
                            + filter.getSearch()
                            .trim()
                            .toLowerCase()
                            + "%";

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


    // BUDGET NOTIFICATION

    private void checkBudgetNotification(
            User user,
            Category category,
            LocalDate transactionDate) {

        // Only expense categories can affect budgets

        if (category.getType() != CategoryType.EXPENSE) {
            return;
        }


        // First day of transaction month

        LocalDate month =
                transactionDate.withDayOfMonth(1);


        // Find budget for this category and month

        Budget budget =
                budgetRepository
                        .findByUserAndCategoryAndMonth(
                                user,
                                category,
                                month
                        )
                        .orElse(null);

        if (budget == null) {
            return;
        }


        // Calculate monthly spending

        LocalDate startDate = month;

        LocalDate endDate =
                month.plusMonths(1);

        BigDecimal spentAmount =
                budgetRepository.calculateCategorySpending(
                        user,
                        category,
                        startDate,
                        endDate
                );

        if (spentAmount == null) {
            spentAmount = BigDecimal.ZERO;
        }


        BigDecimal budgetAmount =
                budget.getAmount();


        // BUDGET EXCEEDED

        if (spentAmount.compareTo(budgetAmount) > 0) {

            String message =
                    "Your "
                            + category.getName()
                            + " budget has been exceeded. "
                            + "Spent: ₹"
                            + spentAmount
                            + " / Budget: ₹"
                            + budgetAmount;

            notificationService.createNotification(
                    user,
                    "Budget Exceeded",
                    message,
                    NotificationType.BUDGET_EXCEEDED
            );

            return;
        }


        // BUDGET WARNING - 80%

        BigDecimal warningLimit =
                budgetAmount
                        .multiply(
                                BigDecimal.valueOf(80)
                        )
                        .divide(
                                BigDecimal.valueOf(100)
                        );

        if (spentAmount.compareTo(warningLimit) >= 0) {

            String message =
                    "Your "
                            + category.getName()
                            + " budget is almost exhausted. "
                            + "Spent: ₹"
                            + spentAmount
                            + " / Budget: ₹"
                            + budgetAmount;

            notificationService.createNotification(
                    user,
                    "Budget Warning",
                    message,
                    NotificationType.BUDGET_WARNING
            );
        }
    }


    // LOW BALANCE NOTIFICATION

    private void checkLowBalanceNotification(
            User user) {

        BigDecimal totalIncome =
                transactionRepository.getTotalIncome(
                        user,
                        CategoryType.INCOME
                );

        BigDecimal totalExpense =
                transactionRepository.getTotalExpense(
                        user,
                        CategoryType.EXPENSE
                );


        if (totalIncome == null) {
            totalIncome = BigDecimal.ZERO;
        }

        if (totalExpense == null) {
            totalExpense = BigDecimal.ZERO;
        }


        // No income means low balance
        // notification should not be generated

        if (totalIncome.compareTo(
                BigDecimal.ZERO) <= 0) {

            return;
        }


        BigDecimal balance =
                totalIncome.subtract(totalExpense);


        // 10% of total income

        BigDecimal warningLimit =
                totalIncome
                        .multiply(
                                BigDecimal.valueOf(10)
                        )
                        .divide(
                                BigDecimal.valueOf(100)
                        );


        // LOW BALANCE

        if (balance.compareTo(warningLimit) <= 0) {

            String message =
                    "Your available balance is low. "
                            + "Current balance: ₹"
                            + balance;

            notificationService.createNotification(
                    user,
                    "Low Balance",
                    message,
                    NotificationType.LOW_BALANCE
            );
        }
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