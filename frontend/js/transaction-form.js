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


        // IMPORTANT:
        // api.js contains getTransaction(),
        // not getTransactionById()

        const transaction =
            await getTransaction(id);


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


    if (!categorySelect) {
        return;
    }


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


    if (!dateInput) {
        return;
    }


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


    const amountValue =
        document
            .getElementById("amount")
            .value
            .trim();


    const amount =
        Number(amountValue);


    const categoryValue =
        document
            .getElementById("category")
            .value;


    const categoryId =
        Number(categoryValue);


    const transactionDate =
        document
            .getElementById(
                "transactionDate"
            )
            .value;


    const submitButton =
        document.getElementById(
            "submitBtn"
        );



    // FRONTEND VALIDATION



    // 1. TITLE VALIDATION

    if (!title) {

        showMessage(
            "Transaction title is required",
            true
        );

        return;
    }


    if (title.length > 100) {

        showMessage(
            "Transaction title cannot exceed 100 characters",
            true
        );

        return;
    }


    // 2. DESCRIPTION VALIDATION

    if (description.length > 500) {

        showMessage(
            "Description cannot exceed 500 characters",
            true
        );

        return;
    }


    // 3. AMOUNT REQUIRED

    if (!amountValue) {

        showMessage(
            "Transaction amount is required",
            true
        );

        return;
    }


    // 4. AMOUNT NUMBER VALIDATION

    if (!Number.isFinite(amount)) {

        showMessage(
            "Please enter a valid amount",
            true
        );

        return;
    }


    // 5. AMOUNT POSITIVE VALIDATION

    if (amount <= 0) {

        showMessage(
            "Amount must be greater than 0",
            true
        );

        return;
    }


    // 6. AMOUNT DECIMAL VALIDATION

    if (!/^\d+(\.\d{1,2})?$/.test(amountValue)) {

        showMessage(
            "Amount can have maximum 2 decimal places",
            true
        );

        return;
    }


    // 7. CATEGORY VALIDATION

    if (!categoryValue || !categoryId) {

        showMessage(
            "Please select a category",
            true
        );

        return;
    }


    // 8. DATE REQUIRED VALIDATION

    if (!transactionDate) {

        showMessage(
            "Please select transaction date",
            true
        );

        return;
    }


    // 9. DATE FORMAT VALIDATION

    const selectedDate =
        new Date(
            transactionDate + "T00:00:00"
        );


    if (
        Number.isNaN(
            selectedDate.getTime()
        )
    ) {

        showMessage(
            "Please select a valid transaction date",
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

        // Disable button
        // Prevent duplicate submission

        if (submitButton) {

            submitButton.disabled = true;
        }



        // EDIT MODE


        if (editingTransactionId) {

            showMessage(
                "Updating transaction..."
            );


            // IMPORTANT:
            // saveTransaction(transaction, id)
            // performs PUT request

            await saveTransaction(
                transaction,
                editingTransactionId
            );


            showMessage(
                "Transaction updated successfully"
            );

        }



        // CREATE MODE


        else {

            showMessage(
                "Saving transaction..."
            );


            // IMPORTANT:
            // saveTransaction(transaction)
            // performs POST request

            await saveTransaction(
                transaction
            );


            showMessage(
                "Transaction created successfully"
            );
        }



        // REDIRECT


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


        // Re-enable button
        // if API request failed

        if (submitButton) {

            submitButton.disabled = false;
        }
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


    // Error/success state

    if (isError) {

        messageElement.classList.add(
            "error-message"
        );

    } else {

        messageElement.classList.remove(
            "error-message"
        );
    }
}



// CANCEL BUTTON


const cancelBtn =
    document.getElementById(
        "cancelBtn"
    );


if (cancelBtn) {

    cancelBtn.addEventListener(
        "click",
        () => {

            window.location.href =
                "transactions.html";

        }
    );
}



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