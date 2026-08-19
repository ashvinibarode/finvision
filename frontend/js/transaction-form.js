let categories = [];


// PAGE LOAD

document.addEventListener(
    "DOMContentLoaded",
    initializeTransactionForm
);


// INITIALIZE FORM

async function initializeTransactionForm() {

    try {

        categories = await getCategories();

        populateCategoryDropdown();

        setDefaultDate();

    } catch (error) {

        console.error(
            "Error loading transaction form:",
            error
        );

        showMessage(
            "Unable to load categories",
            true
        );
    }
}


// CATEGORY DROPDOWN

function populateCategoryDropdown() {

    const categorySelect =
        document.getElementById("category");

    categorySelect.innerHTML =
        `<option value="">
            Select Category
        </option>`;

    categories.forEach(category => {

        const option =
            document.createElement("option");

        option.value = category.id;

        option.textContent =
            `${category.name} (${category.type})`;

        categorySelect.appendChild(option);

    });
}


// DEFAULT DATE

function setDefaultDate() {

    const dateInput =
        document.getElementById(
            "transactionDate"
        );

    const today =
        new Date()
            .toISOString()
            .split("T")[0];

    dateInput.value = today;
}


// FORM SUBMIT

document
    .getElementById("transactionForm")
    .addEventListener(
        "submit",
        handleTransactionSubmit
    );


// HANDLE SUBMIT

async function handleTransactionSubmit(event) {

    event.preventDefault();


    const title =
        document
            .getElementById("title")
            .value
            .trim();


    const description =
        document
            .getElementById("description")
            .value
            .trim();


    const amount =
        Number(
            document
                .getElementById("amount")
                .value
        );


    const categoryId =
        Number(
            document
                .getElementById("category")
                .value
        );


    const transactionDate =
        document
            .getElementById("transactionDate")
            .value;


    // FRONTEND VALIDATION

    if (!title) {

        showMessage(
            "Transaction title is required",
            true
        );

        return;
    }


    if (!amount || amount <= 0) {

        showMessage(
            "Amount must be greater than 0",
            true
        );

        return;
    }


    if (!categoryId) {

        showMessage(
            "Please select a category",
            true
        );

        return;
    }


    if (!transactionDate) {

        showMessage(
            "Please select transaction date",
            true
        );

        return;
    }


    // REQUEST OBJECT

    const transaction = {

        title: document
            .getElementById("title")
            .value
            .trim(),

        description: document
            .getElementById("description")
            .value
            .trim(),

        amount: Number(
            document.getElementById("amount").value
        ),

        categoryId: Number(
            document.getElementById("category").value
        ),

        transactionDate: document
            .getElementById("transactionDate")
            .value
    };


    try {

        showMessage(
            "Saving transaction..."
        );


        await createTransaction(
            transaction
        );


        showMessage(
            "Transaction created successfully"
        );


        setTimeout(() => {

            window.location.href =
                "transactions.html";

        }, 800);


    } catch (error) {

        console.error(
            "Transaction creation error:",
            error
        );


        showMessage(
            error.message ||
            "Unable to create transaction",
            true
        );
    }
}


// MESSAGE

function showMessage(
    message,
    isError = false
) {

    const messageElement =
        document.getElementById(
            "formMessage"
        );


    messageElement.textContent =
        message;


    messageElement.style.marginTop =
        "15px";
}


// CANCEL BUTTON

document
    .getElementById("cancelBtn")
    .addEventListener(
        "click",
        () => {

            window.location.href =
                "transactions.html";

        }
    );


// LOGOUT

const logoutBtn =
    document.getElementById("logoutBtn");


if (logoutBtn) {

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