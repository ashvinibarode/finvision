let currentPage = 0;

const pageSize = 10;


// Page Load


document.addEventListener(
    "DOMContentLoaded",
    () => {

        loadTransactions();

        setupPagination();

        setupLogout();

    }
);



// Load Transactions


async function loadTransactions() {

    try {

        const data = await getTransactions(
            currentPage,
            pageSize
        );


        if (!data) {
            return;
        }


        renderTransactions(data);

        updatePagination(data);


    } catch (error) {

        console.error(
            "Transaction loading error:",
            error
        );

        alert(
            "Unable to load transactions"
        );

    }

}



// Render Transactions


function renderTransactions(data) {

    const tableBody =
        document.getElementById(
            "transactionTableBody"
        );


    if (!tableBody) {
        return;
    }


    tableBody.innerHTML = "";


    if (
        !data.content ||
        data.content.length === 0
    ) {

        tableBody.innerHTML = `
            <tr>
                <td colspan="5">
                    No transactions found
                </td>
            </tr>
        `;

        return;
    }


    data.content.forEach(
        transaction => {

            const row =
                document.createElement("tr");


            row.innerHTML = `

                <td>
                    ${transaction.title}
                </td>

                <td>
                    ${transaction.category}
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

                    <button
                        type="button"
                        onclick="editTransaction(${transaction.id})">
                        Edit
                    </button>

                    <button
                        type="button"
                        onclick="deleteTransaction(${transaction.id})">
                        Delete
                    </button>

                </td>

            `;


            tableBody.appendChild(row);

        }
    );

}


// ===============================
// Pagination
// ===============================

function updatePagination(data) {

    const pageInfo =
        document.getElementById(
            "pageInfo"
        );


    const previousBtn =
        document.getElementById(
            "previousBtn"
        );


    const nextBtn =
        document.getElementById(
            "nextBtn"
        );


    if (pageInfo) {

        pageInfo.textContent =
            `Page ${data.number + 1} of ${data.totalPages}`;

    }


    if (previousBtn) {

        previousBtn.disabled =
            data.first;

    }


    if (nextBtn) {

        nextBtn.disabled =
            data.last;

    }

}


// ===============================
// Pagination Event Listeners
// ===============================

function setupPagination() {

    const previousBtn =
        document.getElementById(
            "previousBtn"
        );


    const nextBtn =
        document.getElementById(
            "nextBtn"
        );


    if (previousBtn) {

        previousBtn.addEventListener(
            "click",
            () => {

                if (currentPage > 0) {

                    currentPage--;

                    loadTransactions();

                }

            }
        );

    }


    if (nextBtn) {

        nextBtn.addEventListener(
            "click",
            () => {

                currentPage++;

                loadTransactions();

            }
        );

    }

}


// ===============================
// Delete Transaction
// ===============================

async function deleteTransaction(id) {

    const confirmed =
        confirm(
            "Are you sure you want to delete this transaction?"
        );


    if (!confirmed) {
        return;
    }


    const token =
        localStorage.getItem("token");


    if (!token) {

        window.location.href =
            "login.html";

        return;

    }


    try {

        const response =
            await fetch(
                `${API_BASE_URL}/transactions/${id}`,
                {
                    method: "DELETE",

                    headers: {
                        "Authorization":
                            `Bearer ${token}`
                    }
                }
            );


        if (
            response.status === 401 ||
            response.status === 403
        ) {

            localStorage.removeItem(
                "token"
            );

            window.location.href =
                "login.html";

            return;

        }


        if (!response.ok) {

            throw new Error(
                "Failed to delete transaction"
            );

        }


        alert(
            "Transaction deleted successfully"
        );


        await loadTransactions();


    } catch (error) {

        console.error(
            "Delete transaction error:",
            error
        );


        alert(
            "Unable to delete transaction"
        );

    }

}



// Edit Transaction


function editTransaction(id) {

    window.location.href =
        `transaction-form.html?id=${id}`;

}



// Logout


function setupLogout() {

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