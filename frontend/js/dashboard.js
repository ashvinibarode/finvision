document.addEventListener(
    "DOMContentLoaded",
    loadDashboard
);


async function loadDashboard() {

    try {

        const data = await getDashboardData();

        if (!data) {
            return;
        }

        updateSummary(data);

        createIncomeExpenseChart(data);

        createCategoryChart(data);

        updateRecentTransactions(data);

        updateTotalBudgets(data);

    } catch (error) {

        console.error(
            "Dashboard error:",
            error
        );

        alert(
            "Unable to load dashboard"
        );
    }
}



   //SUMMARY


function updateSummary(data) {

    document.getElementById(
        "totalIncome"
    ).textContent =
        formatCurrency(
            data.totalIncome
        );


    document.getElementById(
        "totalExpense"
    ).textContent =
        formatCurrency(
            data.totalExpense
        );


    document.getElementById(
        "balance"
    ).textContent =
        formatCurrency(
            data.balance
        );


    document.getElementById(
        "totalTransactions"
    ).textContent =
        data.totalTransactions ?? 0;
}



  // TOTAL BUDGETS


function updateTotalBudgets(data) {

    document.getElementById(
        "totalBudgets"
    ).textContent =
        data.totalBudgets ?? 0;
}



  // CURRENCY


function formatCurrency(amount) {

    return new Intl.NumberFormat(
        "en-IN",
        {
            style: "currency",
            currency: "INR"
        }
    ).format(amount ?? 0);
}



  // INCOME VS EXPENSE


function createIncomeExpenseChart(data) {

    const canvas =
        document.getElementById(
            "incomeExpenseChart"
        );

    if (!canvas) {
        return;
    }

    new Chart(canvas, {

        type: "bar",

        data: {

            labels: [
                "Income",
                "Expense"
            ],

            datasets: [
                {
                    label: "Amount",

                    data: [
                        data.totalIncome ?? 0,
                        data.totalExpense ?? 0
                    ]
                }
            ]
        },

        options: {

            responsive: true,

            maintainAspectRatio: false,

            plugins: {

                legend: {
                    display: false
                }
            },

            scales: {

                y: {
                    beginAtZero: true
                }
            }
        }
    });
}



  // CATEGORY EXPENSE CHART


function createCategoryChart(data) {

    const canvas =
        document.getElementById(
            "categoryExpenseChart"
        );

    if (!canvas) {
        return;
    }

    const categories =
        data.topExpenseCategories ?? [];


    const labels =
        categories.map(
            item => item.category
        );


    const amounts =
        categories.map(
            item => item.amount
        );


    new Chart(canvas, {

        type: "doughnut",

        data: {

            labels: labels,

            datasets: [
                {
                    label: "Expenses",

                    data: amounts
                }
            ]
        },

        options: {

            responsive: true,

            maintainAspectRatio: false
        }
    });
}



  // RECENT TRANSACTIONS


function updateRecentTransactions(data) {

    const table =
        document.getElementById(
            "recentTransactions"
        );

    if (!table) {
        return;
    }


    table.innerHTML = "";


    const transactions =
        data.recentTransactions ?? [];


    if (transactions.length === 0) {

        table.innerHTML = `
            <tr>
                <td colspan="4">
                    No recent transactions found.
                </td>
            </tr>
        `;

        return;
    }


    transactions.forEach(
        transaction => {

            const row =
                document.createElement("tr");


            row.innerHTML = `
                <td>
                    ${escapeHtml(
                        transaction.title
                    )}
                </td>

                <td>
                    ${escapeHtml(
                        transaction.category
                    )}
                </td>

                <td>
                    ${formatCurrency(
                        transaction.amount
                    )}
                </td>

                <td>
                    ${transaction.transactionDate}
                </td>
            `;


            table.appendChild(row);
        }
    );
}



  // HTML ESCAPE


function escapeHtml(value) {

    const div =
        document.createElement("div");

    div.textContent =
        value ?? "";

    return div.innerHTML;
}



  // LOGOUT


document.addEventListener(
    "DOMContentLoaded",
    () => {

        const logoutBtn =
            document.getElementById(
                "logoutBtn"
            );


        if (!logoutBtn) {
            return;
        }


        logoutBtn.addEventListener(
            "click",
            () => {

                localStorage.removeItem(
                    "token"
                );

                window.location.href =
                    "login.html";
            }
        );
    }
);