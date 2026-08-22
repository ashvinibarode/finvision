let currentPage = 0;
const pageSize = 10;

let categories = [];
let currentFilters = {};



// Page Load


document.addEventListener(
    "DOMContentLoaded",
    async () => {

        await loadCategories();

        await loadTransactions();

    }
);



// Load Categories


async function loadCategories() {

    try {

        const data = await getCategories();

        if (!data) {
            return;
        }

        categories = data;

        populateCategoryFilter(data);

    } catch (error) {

        console.error(
            "Category loading error:",
            error
        );
    }
}



// Populate Category Filter


function populateCategoryFilter(categories) {

    const select =
        document.getElementById(
            "categoryFilter"
        );

    if (!select) {
        return;
    }

    select.innerHTML =
        `<option value="">All Categories</option>`;


    categories.forEach(category => {

        const option =
            document.createElement("option");

        option.value = category.id;

        option.textContent =
            category.name;

        select.appendChild(option);

    });
}



// Load Transactions


async function loadTransactions() {

    try {

        const data =
            await getTransactions(
                currentPage,
                pageSize,
                currentFilters
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


    // Page object ka actual transaction data
    // data.content ke andar hai

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


    data.content.forEach(transaction => {

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
                    onclick="editTransaction(
                        ${transaction.id}
                    )">
                    Edit
                </button>

                <button
                    onclick="deleteTransaction(
                        ${transaction.id}
                    )">
                    Delete
                </button>

            </td>
        `;


        tableBody.appendChild(row);

    });
}



// Pagination


function updatePagination(data) {

    const pageInfo =
        document.getElementById(
            "pageInfo"
        );


    if (pageInfo) {

        pageInfo.textContent =
            `Page ${
                data.number + 1
            } of ${
                data.totalPages
            }`;
    }


    const previousBtn =
        document.getElementById(
            "previousBtn"
        );


    const nextBtn =
        document.getElementById(
            "nextBtn"
        );


    if (previousBtn) {

        previousBtn.disabled =
            data.first;
    }


    if (nextBtn) {

        nextBtn.disabled =
            data.last;
    }
}



// Previous Page


const previousBtn =
    document.getElementById(
        "previousBtn"
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



// Next Page


const nextBtn =
    document.getElementById(
        "nextBtn"
    );


if (nextBtn) {

    nextBtn.addEventListener(
        "click",
        () => {

            currentPage++;

            loadTransactions();

        }
    );
}



// Apply Filters


const filterBtn =
    document.getElementById(
        "filterBtn"
    );


if (filterBtn) {

    filterBtn.addEventListener(
        "click",
        () => {

            const searchInput =
                document.getElementById(
                    "searchInput"
                );


            const categoryFilter =
                document.getElementById(
                    "categoryFilter"
                );


            const fromDate =
                document.getElementById(
                    "fromDate"
                );


            const toDate =
                document.getElementById(
                    "toDate"
                );


            currentFilters = {

                search:
                    searchInput
                        ? searchInput.value.trim()
                        : "",

                categoryId:
                    categoryFilter
                        ? categoryFilter.value
                        : "",

                fromDate:
                    fromDate
                        ? fromDate.value
                        : "",

                toDate:
                    toDate
                        ? toDate.value
                        : ""
            };


            // Filter change hone par
            // first page se start karo

            currentPage = 0;

            loadTransactions();

        }
    );
}



// Clear Filters


const clearFilterBtn =
    document.getElementById(
        "clearFilterBtn"
    );


if (clearFilterBtn) {

    clearFilterBtn.addEventListener(
        "click",
        () => {

            const searchInput =
                document.getElementById(
                    "searchInput"
                );


            const categoryFilter =
                document.getElementById(
                    "categoryFilter"
                );


            const fromDate =
                document.getElementById(
                    "fromDate"
                );


            const toDate =
                document.getElementById(
                    "toDate"
                );


            if (searchInput) {
                searchInput.value = "";
            }


            if (categoryFilter) {
                categoryFilter.value = "";
            }


            if (fromDate) {
                fromDate.value = "";
            }


            if (toDate) {
                toDate.value = "";
            }


            currentFilters = {};

            currentPage = 0;

            loadTransactions();

        }
    );
}



// Delete Transaction


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


        await loadTransactions();


    } catch (error) {

        console.error(
            "Delete error:",
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



// Add Transaction


const addTransactionBtn =
    document.getElementById(
        "addTransactionBtn"
    );


if (addTransactionBtn) {

    addTransactionBtn.addEventListener(
        "click",
        () => {

            window.location.href =
                "transaction-form.html";

        }
    );
}



// Currency


function formatCurrency(amount) {

    return new Intl.NumberFormat(
        "en-IN",
        {
            style: "currency",
            currency: "INR"
        }
    ).format(amount);
}