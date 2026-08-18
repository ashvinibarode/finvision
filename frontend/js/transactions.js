let currentPage = 0;

const pageSize = 10;



// PAGE LOAD


document.addEventListener(
    "DOMContentLoaded",
    () => {

        loadTransactions();

        setupPagination();

        setupLogout();

        setupAddTransaction();

    }
);



// LOAD TRANSACTIONS

async function loadTransactions() {

    try {

        const data =
            await getTransactions(
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



// RENDER TRANSACTIONS


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
                    ${transaction.title || "-"}
                </td>

                <td>
                    ${transaction.category || "-"}
                </td>

                <td>
                    ${formatCurrency(
                        transaction.amount
                    )}
                </td>

                <td>
                    ${transaction.transactionDate || "-"}
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


// PAGINATION


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

        const totalPages =
            data.totalPages || 0;


        pageInfo.textContent =
            totalPages > 0
                ? `Page ${data.number + 1} of ${totalPages}`
                : "Page 0 of 0";
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



// PAGINATION EVENT LISTENERS


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

                if (!nextBtn.disabled) {

                    currentPage++;

                    loadTransactions();
                }

            }
        );
    }
}



// DELETE TRANSACTION


async function deleteTransaction(id) {

    const confirmed =
        confirm(
            "Are you sure you want to delete this transaction?"
        );


    if (!confirmed) {
        return;
    }


    try {

        await deleteTransactionApi(id);


        alert(
            "Transaction deleted successfully"
        );


        /*
         * If the current page becomes empty
         * after deleting the last transaction,
         * move back to the previous page.
         */

        const data =
            await getTransactions(
                currentPage,
                pageSize
            );


        if (
            data &&
            data.content &&
            data.content.length === 0 &&
            currentPage > 0
        ) {

            currentPage--;
        }


        await loadTransactions();


    } catch (error) {

        console.error(
            "Delete transaction error:",
            error
        );


        alert(
            error.message ||
            "Unable to delete transaction"
        );
    }
}



// EDIT TRANSACTION


function editTransaction(id) {

    window.location.href =
        `transaction-form.html?id=${id}`;
}



// ADD TRANSACTION

function setupAddTransaction() {

    const addTransactionBtn =
        document.getElementById(
            "addTransactionBtn"
        );


    if (!addTransactionBtn) {
        return;
    }


    addTransactionBtn.addEventListener(
        "click",
        () => {

            window.location.href =
                "transaction-form.html";

        }
    );
}



// LOGOUT


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



// CURRENCY FORMATTER


function formatCurrency(amount) {

    return new Intl.NumberFormat(
        "en-IN",
        {
            style: "currency",
            currency: "INR"
        }
    ).format(amount || 0);
}