let currentStartDate = null;
let currentEndDate = null;


document.addEventListener(
    "DOMContentLoaded",
    initializeReports
);


function initializeReports() {

    const today =
        new Date();

    const firstDay =
        new Date(
            today.getFullYear(),
            today.getMonth(),
            1
        );


    document.getElementById(
        "startDate"
    ).value =
        formatDateForInput(firstDay);


    document.getElementById(
        "endDate"
    ).value =
        formatDateForInput(today);


    document
        .getElementById("generateReportBtn")
        .addEventListener(
            "click",
            generateReport
        );


    document
        .getElementById("downloadPdfBtn")
        .addEventListener(
            "click",
            downloadReportPdf
        );
}

async function generateReport() {

    const startDate =
        document.getElementById(
            "startDate"
        ).value;

    const endDate =
        document.getElementById(
            "endDate"
        ).value;


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


        currentStartDate =
            startDate;

        currentEndDate =
            endDate;


        displayReport(report);

        showReportMessage(
            "Report generated successfully."
        );


    } catch (error) {

        console.error(error);

        showReportMessage(
            error.message,
            true
        );
    }
}

function displayReport(report) {

    document.getElementById(
        "totalIncome"
    ).textContent =
        formatCurrency(
            report.totalIncome
        );


    document.getElementById(
        "totalExpense"
    ).textContent =
        formatCurrency(
            report.totalExpense
        );


    document.getElementById(
        "balance"
    ).textContent =
        formatCurrency(
            report.balance
        );


    document.getElementById(
        "totalTransactions"
    ).textContent =
        report.totalTransactions;


    document.getElementById(
        "reportPeriod"
    ).textContent =
        report.month;
}

async function downloadReportPdf() {

    if (!currentStartDate ||
        !currentEndDate) {

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
            error.message,
            true
        );
    }
}

function formatCurrency(amount) {

    return Number(amount)
        .toLocaleString(
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


    return `${year}-${month}-${day}`;
}


function showReportMessage(
    message,
    isError = false
) {

    const element =
        document.getElementById(
            "reportMessage"
        );

    element.textContent =
        message;

    element.className =
        isError
            ? "error-message"
            : "success-message";
}