const API_BASE_URL = "http://localhost:8080/api";


// ==================== LOGIN ====================

const loginForm = document.getElementById("loginForm");

if (loginForm) {

    loginForm.addEventListener("submit", async (event) => {

        event.preventDefault();

        const email =
            document.getElementById("email").value;

        const password =
            document.getElementById("password").value;

        const errorMessage =
            document.getElementById("errorMessage");

        try {

            const response = await fetch(
                `${API_BASE_URL}/auth/login`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({
                        email: email,
                        password: password
                    })
                }
            );

            const data = await response.json();

            if (!response.ok) {

                errorMessage.textContent =
                    data.message || "Login failed";

                return;
            }

            localStorage.setItem(
                "token",
                data.token
            );

            window.location.href = "index.html";

        } catch (error) {

            console.error(error);

            errorMessage.textContent =
                "Unable to connect to server";
        }
    });
}


// ==================== REGISTER ====================

const registerForm =
    document.getElementById("registerForm");

if (registerForm) {

    registerForm.addEventListener(
        "submit",
        async (event) => {

            event.preventDefault();

            const firstName =
                document.getElementById("firstName").value;

            const lastName =
                document.getElementById("lastName").value;

            const email =
                document.getElementById("email").value;

            const password =
                document.getElementById("password").value;

            const phone =
                document.getElementById("phone").value;

            const errorMessage =
                document.getElementById("errorMessage");

            try {

                const response = await fetch(
                    `${API_BASE_URL}/auth/register`,
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body: JSON.stringify({
                            firstName: firstName,
                            lastName: lastName,
                            email: email,
                            password: password,
                            phone: phone
                        })
                    }
                );

                const data = await response.json();

                if (!response.ok) {

                    errorMessage.textContent =
                        data.message ||
                        "Registration failed";

                    return;
                }

                window.location.href =
                    "login.html";

            } catch (error) {

                console.error(error);

                errorMessage.textContent =
                    "Unable to connect to server";
            }
        }
    );
}