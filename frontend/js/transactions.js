let currentPage = 0;

const pageSize = 10;

let categories = [];

let currentFilters = {};



// PAGE LOAD


document.addEventListener(
    "DOMContentLoaded",
    async () => {

        await loadCategories();

        await loadTransactions();

    }
);



// LOAD CATEGORIES


async function loadCategories() {

    try {

        const data =
            await getCategories();


        if (!data) {
            return;
        }


        categories = data;


        populateCategoryFilter(
            data
        );

    } catch (error) {

        console.error(
            "Category loading error:",
            error
        );

    }
}



// POPULATE CATEGORY FILTER


function populateCategoryFilter(
    categories
) {

    const select =
        document.getElementById(
            "categoryFilter"
        );


    if (!select) {
        return;
    }


    select.innerHTML =
        `<option value="">
            All Categories
        </option>`;


    categories.forEach(
        category => {

            const option =
                document.createElement(
                    "option"
                );


            option.value =
                category.id;


            option.textContent =
                category.name;


            select.appendChild(
                option
            );

        }
    );
}



// LOAD TRANSACTIONS


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


        showTransactionMessage(
            error.message ||
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
                document.createElement(
                    "tr"
                );


            row.innerHTML = `
                <td>
                    ${escapeHtml(
                        transaction.title
                    )}
                </td>

                <td>
                    ${escapeHtml(
                        transaction.category || "-"
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


            tableBody.appendChild(
                row
            );

        }
    );
}



// PAGINATION


function updatePagination(data) {

    const pageInfo =
        document.getElementById(
            "pageInfo"
        );


    if (pageInfo) {

        const totalPages =
            data.totalPages;


        pageInfo.textContent =
            totalPages === 0
                ? "No pages"
                : `Page ${
                    data.number + 1
                } of ${
                    totalPages
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
            data.first ||
            data.totalPages === 0;
    }


    if (nextBtn) {

        nextBtn.disabled =
            data.last ||
            data.totalPages === 0;
    }
}



// PREVIOUS PAGE


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



// NEXT PAGE


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



// APPLY FILTERS


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


            const search =
                searchInput
                    ? searchInput.value.trim()
                    : "";


            const categoryId =
                categoryFilter
                    ? categoryFilter.value
                    : "";


            const fromDateValue =
                fromDate
                    ? fromDate.value
                    : "";


            const toDateValue =
                toDate
                    ? toDate.value
                    : "";



            // SEARCH VALIDATION


            if (search.length > 100) {

                alert(
                    "Search cannot exceed 100 characters"
                );

                return;
            }



            // DATE VALIDATION


            if (
                fromDateValue &&
                toDateValue &&
                fromDateValue > toDateValue
            ) {

                alert(
                    "From date cannot be after To date"
                );

                return;
            }



            // SET FILTERS


            currentFilters = {

                search: search,

                categoryId: categoryId,

                fromDate:
                    fromDateValue,

                toDate:
                    toDateValue
            };


            // Always start from
            // first page after filtering

            currentPage = 0;


            loadTransactions();

        }
    );
}



// CLEAR FILTERS


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

                searchInput.value =
                    "";

            }


            if (categoryFilter) {

                categoryFilter.value =
                    "";

            }


            if (fromDate) {

                fromDate.value =
                    "";

            }


            if (toDate) {

                toDate.value =
                    "";

            }


            currentFilters = {};

            currentPage = 0;


            loadTransactions();

        }
    );
}



// DELETE TRANSACTION


async function deleteTransaction(
    id
) {

    const confirmed =
        confirm(
            "Are you sure you want to delete this transaction?"
        );


    if (!confirmed) {
        return;
    }


    const token =
        localStorage.getItem(
            "token"
        );


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


        /*
         * Delete ke baad agar current page
         * empty ho jaye to previous page par
         * move karenge.
         */

        if (
            currentPage > 0 &&
            dataIsLastPageAfterDelete()
        ) {

            currentPage--;

        }


        await loadTransactions();


    } catch (error) {

        console.error(
            "Delete error:",
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



// CURRENCY


function formatCurrency(amount) {

    return new Intl.NumberFormat(
        "en-IN",
        {
            style: "currency",
            currency: "INR"
        }
    ).format(amount);
}



// HTML ESCAPE


function escapeHtml(value) {

    if (value === null ||
        value === undefined) {

        return "";
    }


    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}



// TRANSACTION MESSAGE


function showTransactionMessage(
    message
) {

    const tableBody =
        document.getElementById(
            "transactionTableBody"
        );


    if (!tableBody) {
        return;
    }


    tableBody.innerHTML = `
        <tr>
            <td colspan="5">
                ${escapeHtml(message)}
            </td>
        </tr>
    `;
}



// DELETE PAGE HELPER


function dataIsLastPageAfterDelete() {



    return false;
}