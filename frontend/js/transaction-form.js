let categories = [];
let editingTransactionId = null;



// PAGE LOAD


document.addEventListener(
    "DOMContentLoaded",
    initializeTransactionForm
);



// INITIALIZE FORM


async function initializeTransactionForm() {

    try {

        categories = await getCategories();

        if (!categories) {
            return;
        }

        populateCategoryDropdown();


        const params =
            new URLSearchParams(
                window.location.search
            );

        const id = params.get("id");


        if (id) {

            editingTransactionId =
                Number(id);

            await loadTransactionForEdit(
                editingTransactionId
            );

        } else {

            setDefaultDate();

        }

    } catch (error) {

        console.error(
            "Form initialization error:",
            error
        );

        showMessage(
            error.message ||
            "Unable to initialize form",
            true
        );
    }
}



// LOAD TRANSACTION FOR EDIT


async function loadTransactionForEdit(id) {

    try {

        showMessage(
            "Loading transaction..."
        );


        const transaction =
            await getTransactionById(id);


        if (!transaction) {
            return;
        }


        // Change page title

        document.getElementById(
            "formTitle"
        ).textContent =
            "Edit Transaction";


        // Change submit button

        document.getElementById(
            "submitBtn"
        ).textContent =
            "Update Transaction";


        // Fill title

        document.getElementById(
            "title"
        ).value =
            transaction.title || "";


        // Fill description

        document.getElementById(
            "description"
        ).value =
            transaction.description || "";


        // Fill amount

        document.getElementById(
            "amount"
        ).value =
            transaction.amount ?? "";


        // Fill date

        document.getElementById(
            "transactionDate"
        ).value =
            transaction.transactionDate || "";


        // Fill category

        document.getElementById(
            "category"
        ).value =
            transaction.categoryId ?? "";


        showMessage("");

    } catch (error) {

        console.error(
            "Transaction loading error:",
            error
        );

        showMessage(
            error.message ||
            "Unable to load transaction",
            true
        );
    }
}



// CATEGORY DROPDOWN


function populateCategoryDropdown() {

    const categorySelect =
        document.getElementById(
            "category"
        );


    categorySelect.innerHTML = `
        <option value="">
            Select Category
        </option>
    `;


    categories.forEach(category => {

        const option =
            document.createElement(
                "option"
            );


        option.value =
            category.id;


        option.textContent =
            `${category.name} (${category.type})`;


        categorySelect.appendChild(
            option
        );
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


    // GET FORM VALUES

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
            .getElementById(
                "transactionDate"
            )
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

        title: title,

        description: description,

        amount: amount,

        categoryId: categoryId,

        transactionDate: transactionDate
    };


    try {

        // ========================
        // EDIT MODE
        // ========================

        if (editingTransactionId) {

            showMessage(
                "Updating transaction..."
            );


            await updateTransaction(
                editingTransactionId,
                transaction
            );


            showMessage(
                "Transaction updated successfully"
            );


        }

        // ========================
        // CREATE MODE
        // ========================

        else {

            showMessage(
                "Saving transaction..."
            );


            await createTransaction(
                transaction
            );


            showMessage(
                "Transaction created successfully"
            );
        }


        // ========================
        // REDIRECT
        // ========================

        setTimeout(() => {

            window.location.href =
                "transactions.html";

        }, 800);


    } catch (error) {

        console.error(
            "Transaction save/update error:",
            error
        );


        showMessage(
            error.message ||
            "Unable to save transaction",
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


    if (!messageElement) {
        return;
    }


    messageElement.textContent =
        message;


    messageElement.style.marginTop =
        "15px";


    messageElement.style.display =
        message
            ? "block"
            : "none";
}



// CANCEL BUTTON
// ===============================

document
    .getElementById("cancelBtn")
    .addEventListener(
        "click",
        () => {

            window.location.href =
                "transactions.html";

        }
    );


// ===============================
// LOGOUT
const logoutBtn =
    document.getElementById(
        "logoutBtn"
    );


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