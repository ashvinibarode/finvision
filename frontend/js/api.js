async function getTransactions(page = 0, size = 10) {

    const token = localStorage.getItem("token");

    const response = await fetch(
        `${API_BASE_URL}/transactions?page=${page}&size=${size}`,
        {
            method: "GET",

            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        }
    );

    if (response.status === 401 || response.status === 403) {

        localStorage.removeItem("token");

        window.location.href = "login.html";

        return null;
    }

    if (!response.ok) {
        throw new Error("Failed to fetch transactions");
    }

    return await response.json();
}