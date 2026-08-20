let currentPage = 0;
const pageSize = 10;

let categories = [];


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


function populateCategoryFilter(categories) {

    const select =
        document.getElementById("categoryFilter");

    if (!select) {
        return;
    }

    select.innerHTML =
        `<option value="">All Categories</option>`;

    categories.forEach(category => {

        const option =
            document.createElement("option");

        option.value = category.id;

        option.textContent = category.name;

        select.appendChild(option);

    });
}



// Load Transactions


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


    data.forEach(transaction => {

        const row =
            document.createElement("tr");


        row.innerHTML = `
            <td>${transaction.title}</td>

            <td>${transaction.category}</td>

            <td>${formatCurrency(transaction.amount)}</td>

            <td>${transaction.transactionDate}</td>

            <td>

                <button
                    onclick="editTransaction(${transaction.id})">
                    Edit
                </button>

                <button
                    onclick="deleteTransaction(${transaction.id})">
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
        document.getElementById("pageInfo");

    if (pageInfo) {

        pageInfo.textContent =
            `Page ${data.number + 1} of ${data.totalPages}`;
    }


    document.getElementById(
        "previousBtn"
    ).disabled = data.first;


    document.getElementById(
        "nextBtn"
    ).disabled = data.last;
}


document
    .getElementById("previousBtn")
    .addEventListener(
        "click",
        () => {

            if (currentPage > 0) {

                currentPage--;

                loadTransactions();
            }
        }
    );


document
    .getElementById("nextBtn")
    .addEventListener(
        "click",
        () => {

            currentPage++;

            loadTransactions();
        }
    );



// Delete


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


        if (!response.ok) {

            throw new Error(
                "Failed to delete transaction"
            );
        }


        await loadTransactions();


    } catch (error) {

        console.error(error);

        alert(
            "Unable to delete transaction"
        );
    }
}



// Edit


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