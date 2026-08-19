const API_BASE_URL = "http://localhost:8080/api";



// AUTHENTICATION HELPER


function getAuthHeaders() {

    const token = localStorage.getItem("token");

    return {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
    };
}



// HANDLE UNAUTHORIZED REQUEST


function handleUnauthorized(response) {

    if (response.status === 401 || response.status === 403) {

        localStorage.removeItem("token");

        window.location.href = "login.html";

        return true;
    }

    return false;
}



// DASHBOARD


async function getDashboardData() {

    const token = localStorage.getItem("token");

    if (!token) {

        window.location.href = "login.html";

        return null;
    }


    const response = await fetch(
        `${API_BASE_URL}/dashboard`,
        {
            method: "GET",
            headers: getAuthHeaders()
        }
    );


    if (handleUnauthorized(response)) {
        return null;
    }


    if (!response.ok) {

        throw new Error(
            "Failed to fetch dashboard data"
        );
    }


    return await response.json();
}



// TRANSACTIONS - GET PAGINATED


async function getTransactions(
    page = 0,
    size = 10
) {

    const token =
        localStorage.getItem("token");


    if (!token) {

        window.location.href =
            "login.html";

        return null;
    }


    const response = await fetch(
        `${API_BASE_URL}/transactions?page=${page}&size=${size}`,
        {
            method: "GET",

            headers: getAuthHeaders()
        }
    );


    if (handleUnauthorized(response)) {
        return null;
    }


    if (!response.ok) {

        throw new Error(
            "Failed to fetch transactions"
        );
    }


    return await response.json();
}



// CATEGORIES - GET ALL

async function getCategories() {

    const token = localStorage.getItem("token");

    const response = await fetch(
        `${API_BASE_URL}/categories`,
        {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("token")}`,
                "Content-Type": "application/json"
            }
        }
    );

    if (response.status === 401 || response.status === 403) {

        localStorage.removeItem("token");
        window.location.href = "login.html";

        return [];
    }

    if (!response.ok) {
        throw new Error("Failed to fetch categories");
    }

    const data = await response.json();

    // Different possible backend response formats
    if (Array.isArray(data)) {
        return data;
    }

    if (Array.isArray(data.content)) {
        return data.content;
    }

    if (Array.isArray(data.categories)) {
        return data.categories;
    }

    return [];
}


// TRANSACTION - GET BY ID

async function getTransaction(id) {

    const token =
        localStorage.getItem("token");


    if (!token) {

        window.location.href =
            "login.html";

        return null;
    }


    const response = await fetch(
        `${API_BASE_URL}/transactions/${id}`,
        {
            method: "GET",

            headers: getAuthHeaders()
        }
    );


    if (handleUnauthorized(response)) {
        return null;
    }


    if (!response.ok) {

        throw new Error(
            "Failed to fetch transaction"
        );
    }


    return await response.json();
}


//create transaction
async function createTransaction(transaction) {

    const token = localStorage.getItem("token");

    if (!token) {
        window.location.href = "login.html";
        return;
    }

    const response = await fetch(
        `${API_BASE_URL}/transactions`,
        {
            method: "POST",

            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },

            body: JSON.stringify(transaction)
        }
    );

    if (!response.ok) {

        const errorText = await response.text();

        console.error(
            "Transaction API Error:",
            response.status,
            errorText
        );

        throw new Error(
            `Transaction failed (${response.status})`
        );
    }

    return await response.json();
}

// TRANSACTION - CREATE / UPDATE

async function saveTransaction(
    transaction,
    id = null
) {

    const token =
        localStorage.getItem("token");


    if (!token) {

        window.location.href =
            "login.html";

        return null;
    }


    const url = id
        ? `${API_BASE_URL}/transactions/${id}`
        : `${API_BASE_URL}/transactions`;


    const method = id
        ? "PUT"
        : "POST";


    const response = await fetch(
        url,
        {
            method: method,

            headers: getAuthHeaders(),

            body: JSON.stringify(transaction)
        }
    );


    if (handleUnauthorized(response)) {
        return null;
    }


    if (!response.ok) {

        const error =
            await response.json()
                .catch(() => null);


        throw new Error(
            error?.message ||
            "Unable to save transaction"
        );
    }


    return await response.json();
}



// TRANSACTION - DELETE


async function deleteTransactionApi(id) {

    const token =
        localStorage.getItem("token");


    if (!token) {

        window.location.href =
            "login.html";

        return null;
    }


    const response = await fetch(
        `${API_BASE_URL}/transactions/${id}`,
        {
            method: "DELETE",

            headers: getAuthHeaders()
        }
    );


    if (handleUnauthorized(response)) {
        return null;
    }


    if (!response.ok) {

        const error =
            await response.json()
                .catch(() => null);


        throw new Error(
            error?.message ||
            "Failed to delete transaction"
        );
    }


    return true;
}