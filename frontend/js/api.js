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



// CHECK LOGIN


function checkAuthentication() {

    const token = localStorage.getItem("token");

    if (!token) {

        window.location.href = "login.html";

        return false;
    }

    return true;
}



// DASHBOARD


async function getDashboardData() {

    if (!checkAuthentication()) {
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



// TRANSACTIONS - GET PAGINATED + FILTERED


async function getTransactions(
    page = 0,
    size = 10,
    filters = {}
) {

    if (!checkAuthentication()) {
        return null;
    }


    const params =
        new URLSearchParams();


    // Pagination

    params.append(
        "page",
        page
    );

    params.append(
        "size",
        size
    );


    // Search

    if (
        filters.search &&
        filters.search.trim() !== ""
    ) {

        params.append(
            "search",
            filters.search.trim()
        );
    }


    // Category

    if (
        filters.categoryId &&
        filters.categoryId !== ""
    ) {

        params.append(
            "categoryId",
            filters.categoryId
        );
    }


    // From Date

    if (
        filters.fromDate &&
        filters.fromDate !== ""
    ) {

        params.append(
            "fromDate",
            filters.fromDate
        );
    }


    // To Date

    if (
        filters.toDate &&
        filters.toDate !== ""
    ) {

        params.append(
            "toDate",
            filters.toDate
        );
    }


    const response = await fetch(
        `${API_BASE_URL}/transactions?${params.toString()}`,
        {
            method: "GET",

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
            "Failed to fetch transactions"
        );
    }


    return await response.json();
}



// TRANSACTION - GET BY ID


async function getTransaction(id) {

    if (!checkAuthentication()) {
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

        const error =
            await response.json()
                .catch(() => null);


        throw new Error(
            error?.message ||
            "Failed to fetch transaction"
        );
    }


    return await response.json();
}



// CATEGORIES - GET ALL


async function getCategories() {

    if (!checkAuthentication()) {
        return [];
    }


    const response = await fetch(
        `${API_BASE_URL}/categories`,
        {
            method: "GET",

            headers: getAuthHeaders()
        }
    );


    if (handleUnauthorized(response)) {
        return [];
    }


    if (!response.ok) {

        const error =
            await response.json()
                .catch(() => null);


        throw new Error(
            error?.message ||
            "Failed to fetch categories"
        );
    }


    const data =
        await response.json();


    // Backend returns List<CategoryResponse>

    if (Array.isArray(data)) {
        return data;
    }


    // Fallback if backend returns paginated response

    if (Array.isArray(data.content)) {
        return data.content;
    }


    // Fallback if backend wraps categories

    if (Array.isArray(data.categories)) {
        return data.categories;
    }


    return [];
}



// TRANSACTION - CREATE / UPDATE


async function saveTransaction(
    transaction,
    id = null
) {

    if (!checkAuthentication()) {
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
            `Unable to ${
                id ? "update" : "create"
            } transaction`
        );
    }


    return await response.json();
}



// TRANSACTION - DELETE


async function deleteTransactionApi(id) {

    if (!checkAuthentication()) {
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

//budget

async function getBudgets() {

    if (!checkAuthentication()) {
        return null;
    }


    const response = await fetch(
        `${API_BASE_URL}/budgets`,
        {
            method: "GET",
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
            "Failed to fetch budgets"
        );
    }


    return await response.json();
}




//analytics

async function getBudgetAnalytics(id) {

    if (!checkAuthentication()) {
        return null;
    }


    const response = await fetch(
        `${API_BASE_URL}/budgets/${id}/analytics`,
        {
            method: "GET",
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
            "Failed to fetch budget analytics"
        );
    }


    return await response.json();
}



// BUDGET - CREATE / UPDATE

async function saveBudget(
    budget,
    id = null
) {

    if (!checkAuthentication()) {
        return null;
    }


    const url = id
        ? `${API_BASE_URL}/budgets/${id}`
        : `${API_BASE_URL}/budgets`;


    const method = id
        ? "PUT"
        : "POST";


    const response = await fetch(
        url,
        {
            method: method,

            headers: getAuthHeaders(),

            body: JSON.stringify(budget)
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
            `Unable to ${
                id ? "update" : "create"
            } budget`
        );
    }


    return await response.json();
}


async function getBudgetById(id) {

    if (!checkAuthentication()) {
        return null;
    }


    const response = await fetch(
        `${API_BASE_URL}/budgets/${id}`,
        {
            method: "GET",
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
            "Failed to fetch budget"
        );
    }


    return await response.json();
}



// BUDGET - DELETE


async function deleteBudgetApi(id) {

    if (!checkAuthentication()) {
        return null;
    }


    const response = await fetch(
        `${API_BASE_URL}/budgets/${id}`,
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
            "Failed to delete budget"
        );
    }


    return true;
}
//report
async function getReport(
    startDate,
    endDate
) {

    const token =
        localStorage.getItem("token");

    const params = new URLSearchParams({
        startDate: startDate,
        endDate: endDate
    });

    const response = await fetch(
        `${API_BASE_URL}/reports/summary?${params}`,
        {
            method: "GET",

            headers: {
                "Authorization":
                    `Bearer ${token}`,

                "Content-Type":
                    "application/json"
            }
        }
    );

    const data =
        await response.json();

    if (!response.ok) {

        throw new Error(
            data.message ||
            "Failed to generate report"
        );
    }

    return data;
}

//pdf download

async function downloadPdfReport(
    startDate,
    endDate
) {

    const token =
        localStorage.getItem("token");

    const params = new URLSearchParams({
        startDate: startDate,
        endDate: endDate
    });

    const response = await fetch(
        `${API_BASE_URL}/reports/pdf?${params}`,
        {
            method: "GET",

            headers: {
                "Authorization":
                    `Bearer ${token}`
            }
        }
    );

    if (!response.ok) {

        throw new Error(
            "Failed to download PDF report"
        );
    }

    const blob =
        await response.blob();

    const url =
        window.URL.createObjectURL(blob);

    const link =
        document.createElement("a");

    link.href = url;

    link.download =
        "finvision-report.pdf";

    document.body.appendChild(link);

    link.click();

    link.remove();

    window.URL.revokeObjectURL(url);
}