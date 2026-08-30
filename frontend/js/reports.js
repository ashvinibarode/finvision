let currentStartDate = null;
let currentEndDate = null;
let categoryExpenseChart = null;

document.addEventListener("DOMContentLoaded", initializeReports);

function initializeReports() {

    const startDateInput =
        document.getElementById("startDate");

    const endDateInput =
        document.getElementById("endDate");

    const generateButton =
        document.getElementById("generateReportBtn");

    const downloadButton =
        document.getElementById("downloadPdfBtn");

    if (!startDateInput ||
        !endDateInput ||
        !generateButton ||
        !downloadButton) {

        console.error("Report elements not found.");
        return;
    }

    const today = new Date();

    const firstDay = new Date(
        today.getFullYear(),
        today.getMonth(),
        1
    );

    startDateInput.value =
        formatDateForInput(firstDay);

    endDateInput.value =
        formatDateForInput(today);

    generateButton.addEventListener(
        "click",
        generateReport
    );

    downloadButton.addEventListener(
        "click",
        downloadReportPdf
    );
}


async function generateReport() {

    const startDate =
        document.getElementById("startDate").value;

    const endDate =
        document.getElementById("endDate").value;

    if (!startDate || !endDate) {

        showReportMessage(
            "Please select both dates.",
            true
        );

        return;
    }

    if (startDate > endDate) {

        showReportMessage(
            "Start date cannot be after end date.",
            true
        );

        return;
    }

    try {

        const report =
            await getReport(
                startDate,
                endDate
            );

        currentStartDate = startDate;
        currentEndDate = endDate;

        displayReport(report);

        showReportMessage(
            "Report generated successfully."
        );

    } catch (error) {

        console.error(error);

        showReportMessage(
            error.message ||
            "Failed to generate report.",
            true
        );
    }
}


function displayReport(report) {

    document.getElementById(
        "totalIncome"
    ).textContent =
        formatCurrency(report.totalIncome);

    document.getElementById(
        "totalExpense"
    ).textContent =
        formatCurrency(report.totalExpense);

    document.getElementById(
        "balance"
    ).textContent =
        formatCurrency(report.balance);

    document.getElementById(
        "totalTransactions"
    ).textContent =
        report.totalTransactions;

    document.getElementById(
        "reportPeriod"
    ).textContent =
        report.month;

    displayCategoryExpenseChart(
        report.categoryExpenses || []
    );
}


function displayCategoryExpenseChart(
    categoryExpenses
) {

    const canvas =
        document.getElementById(
            "categoryExpenseChart"
        );

    if (!canvas) {
        return;
    }

    if (categoryExpenseChart) {

        categoryExpenseChart.destroy();

        categoryExpenseChart = null;
    }

    if (
        !categoryExpenses ||
        categoryExpenses.length === 0
    ) {
        return;
    }

    const labels =
        categoryExpenses.map(
            item => item.categoryName
        );

    const amounts =
        categoryExpenses.map(
            item => Number(item.amount)
        );

    categoryExpenseChart =
        new Chart(
            canvas,
            {
                type: "doughnut",

                data: {
                    labels: labels,

                    datasets: [
                        {
                            label:
                                "Category Expenses",

                            data: amounts
                        }
                    ]
                },

                options: {
                    responsive: true,

                    maintainAspectRatio:
                        false,

                    plugins: {
                        legend: {
                            position: "bottom"
                        }
                    }
                }
            }
        );
}


async function downloadReportPdf() {

    if (
        !currentStartDate ||
        !currentEndDate
    ) {

        showReportMessage(
            "Generate a report first.",
            true
        );

        return;
    }

    try {

        await downloadPdfReport(
            currentStartDate,
            currentEndDate
        );

        showReportMessage(
            "PDF downloaded successfully."
        );

    } catch (error) {

        console.error(error);

        showReportMessage(
            error.message ||
            "Failed to download PDF.",
            true
        );
    }
}


function formatCurrency(amount) {

    return Number(amount).toLocaleString(
        "en-IN",
        {
            style: "currency",
            currency: "INR"
        }
    );
}


function formatDateForInput(date) {

    const year =
        date.getFullYear();

    const month =
        String(
            date.getMonth() + 1
        ).padStart(2, "0");

    const day =
        String(
            date.getDate()
        ).padStart(2, "0");

    return year + "-" + month + "-" + day;
}


function showReportMessage(
    message,
    isError = false
) {

    const element =
        document.getElementById(
            "reportMessage"
        );

    if (!element) {
        return;
    }

    element.textContent = message;

    element.className =
        isError
            ? "error-message"
            : "success-message";
}

function escapeHtml(value) {

    const div =
        document.createElement("div");

    div.textContent =
        value ?? "";

    return div.innerHTML;
}


function displayReport(report) {

    document.getElementById(
        "totalIncome"
    ).textContent =
        formatCurrency(report.totalIncome);


    document.getElementById(
        "totalExpense"
    ).textContent =
        formatCurrency(report.totalExpense);


    document.getElementById(
        "balance"
    ).textContent =
        formatCurrency(report.balance);


    document.getElementById(
        "totalTransactions"
    ).textContent =
        report.totalTransactions;


    document.getElementById(
        "reportPeriod"
    ).textContent =
        report.month;


    displayCategoryExpenseChart(
        report.categoryExpenses
    );


    displayReportTransactions(
        report.transactions
    );
}

function displayReportTransactions(
    transactions
) {

    const tbody =
        document.getElementById(
            "reportTransactionsBody"
        );


    tbody.innerHTML = "";


    if (!transactions ||
        transactions.length === 0) {

        tbody.innerHTML = `
            <tr>
                <td colspan="5">
                    No transactions found
                    for the selected period.
                </td>
            </tr>
        `;

        return;
    }


    transactions.forEach(transaction => {

        const row =
            document.createElement("tr");


        row.innerHTML = `
            <td>
                ${escapeHtml(transaction.title)}
            </td>

            <td>
                ${escapeHtml(
                    transaction.categoryName
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

            <td>
                ${escapeHtml(
                    transaction.type
                )}
            </td>
        `;


        tbody.appendChild(row);

    });

}