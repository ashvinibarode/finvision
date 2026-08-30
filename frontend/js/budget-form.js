let categories = [];

let editingBudgetId = null;


document.addEventListener(
    "DOMContentLoaded",
    initializeBudgetForm
);


async function initializeBudgetForm() {

    try {

        categories =
            await getCategories();

        populateCategories();


        const params =
            new URLSearchParams(
                window.location.search
            );

        const id =
            params.get("id");


        if (id) {

            editingBudgetId =
                Number(id);

            await loadBudget(
                editingBudgetId
            );

        }

    } catch (error) {

        console.error(error);

        showMessage(
            error.message,
            true
        );
    }
}

function populateCategories() {

    const select =
        document.getElementById(
            "category"
        );

    select.innerHTML = `
        <option value="">
            Select Category
        </option>
    `;


    categories
        .filter(
            category =>
                category.type === "EXPENSE"
        )
        .forEach(category => {

            const option =
                document.createElement(
                    "option"
                );

            option.value =
                category.id;

            option.textContent =
                category.name;

            select.appendChild(option);

        });
}

async function loadBudget(id) {

    const budget =
        await getBudgetById(id);


    document.getElementById(
        "formTitle"
    ).textContent =
        "Edit Budget";


    document.getElementById(
        "submitBtn"
    ).textContent =
        "Update Budget";


    document.getElementById(
        "category"
    ).value =
        budget.categoryId;


    document.getElementById(
        "amount"
    ).value =
        budget.amount;


    document.getElementById(
        "month"
    ).value =
        budget.month.substring(0, 7);
}

document
    .getElementById("budgetForm")
    .addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();


            const categoryId =
                Number(
                    document.getElementById(
                        "category"
                    ).value
                );


            const amount =
                Number(
                    document.getElementById(
                        "amount"
                    ).value
                );


            const monthValue =
                document.getElementById(
                    "month"
                ).value;


            if (!categoryId) {

                showMessage(
                    "Please select a category",
                    true
                );

                return;
            }


            if (!amount || amount <= 0) {

                showMessage(
                    "Budget amount must be greater than 0",
                    true
                );

                return;
            }


            if (!monthValue) {

                showMessage(
                    "Please select a month",
                    true
                );

                return;
            }


            const budget = {

                categoryId: categoryId,

                amount: amount,

                month:
                    `${monthValue}-01`
            };


            try {

                if (editingBudgetId) {

                    await saveBudget(
                        budget,
                        editingBudgetId
                    );

                    showMessage(
                        "Budget updated successfully"
                    );

                } else {

                    await saveBudget(
                        budget
                    );

                    showMessage(
                        "Budget created successfully"
                    );
                }
                setTimeout(() => {

                    window.location.href =
                        "budget.html";

                }, 800);


            } catch (error) {

                console.error(error);

                showMessage(
                    error.message ||
                    "Budget operation failed",
                    true
                );
            }
        }
    );

    function showMessage(
        message,
        isError = false
    ) {

        const element =
            document.getElementById(
                "formMessage"
            );

        element.textContent = message;
    }


    document
        .getElementById("cancelBtn")
        .addEventListener(
            "click",
            () => {

                window.location.href =
                    "budget.html";

            }
        );