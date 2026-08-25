let budgets = [];

document.addEventListener(
    "DOMContentLoaded",
    loadBudgets
);



// LOAD BUDGETS


async function loadBudgets() {

    try {

        budgets = await getBudgets();

        if (!budgets) {
            return;
        }

        await renderBudgets();

    } catch (error) {

        console.error(error);

        showBudgetMessage(
            error.message,
            true
        );
    }
}



// RENDER BUDGETS


async function renderBudgets() {

    const container =
        document.getElementById(
            "budgetContainer"
        );


    if (!container) {
        return;
    }


    container.innerHTML = "";


    if (budgets.length === 0) {

        container.innerHTML = `
            <div class="empty-state">
                <h3>No budgets found</h3>

                <p>
                    Create your first monthly budget.
                </p>
            </div>
        `;

        return;
    }


    for (const budget of budgets) {

        try {

            const analytics =
                await getBudgetAnalytics(
                    budget.id
                );


            const card =
                document.createElement("div");

            card.className =
                "budget-card";


            card.innerHTML = `

                <div class="budget-header">

                    <div>

                        <h3>
                            ${analytics.categoryName}
                        </h3>

                        <p>
                            ${formatMonth(
                                analytics.month
                            )}
                        </p>

                    </div>


                    <span class="budget-status">

                        ${
                            analytics.overBudget
                                ? "Over Budget"
                                : "On Track"
                        }

                    </span>

                </div>


                <div class="budget-amount">

                    <span>
                        Budget
                    </span>

                    <strong>
                        ₹${formatAmount(
                            analytics.budgetAmount
                        )}
                    </strong>

                </div>


                <div class="budget-details">

                    <div>

                        <span>
                            Spent
                        </span>

                        <strong>
                            ₹${formatAmount(
                                analytics.spentAmount
                            )}
                        </strong>

                    </div>


                    <div>

                        <span>
                            Remaining
                        </span>

                        <strong>
                            ₹${formatAmount(
                                analytics.remainingAmount
                            )}
                        </strong>

                    </div>

                </div>


                <div class="progress-container">

                    <div
                        class="progress-bar"
                        style="
                            width: ${Math.min(
                                analytics.utilizationPercentage,
                                100
                            )}%;
                        "
                    ></div>

                </div>


                <p class="utilization">

                    ${analytics.utilizationPercentage}%
                    used

                </p>


                <div class="budget-actions">

                    <button
                        type="button"
                        onclick="editBudget(${budget.id})"
                    >
                        Edit
                    </button>


                    <button
                        type="button"
                        onclick="deleteBudget(${budget.id})"
                    >
                        Delete
                    </button>

                </div>

            `;


            container.appendChild(card);


        } catch (error) {

            console.error(
                `Failed to load analytics for budget ${budget.id}`,
                error
            );

        }
    }
}



// DELETE BUDGET


async function deleteBudget(id) {

    const confirmed =
        confirm(
            "Are you sure you want to delete this budget?"
        );


    if (!confirmed) {
        return;
    }


    try {

        await deleteBudgetApi(id);


        showBudgetMessage(
            "Budget deleted successfully.",
            false
        );


        await loadBudgets();


    } catch (error) {

        console.error(error);


        showBudgetMessage(
            error.message,
            true
        );
    }
}



// EDIT BUDGET


function editBudget(id) {



    console.log(
        "Edit budget:",
        id
    );
}



// FORMAT AMOUNT


function formatAmount(amount) {

    return Number(amount)
        .toLocaleString(
            "en-IN",
            {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }
        );
}



// FORMAT MONTH


function formatMonth(dateString) {

    const date =
        new Date(dateString);


    return date.toLocaleDateString(
        "en-IN",
        {
            month: "long",
            year: "numeric"
        }
    );
}



// SHOW MESSAGE


function showBudgetMessage(
    message,
    isError = false
) {

    const element =
        document.getElementById(
            "budgetMessage"
        );


    if (!element) {
        return;
    }


    element.textContent =
        message;


    element.className =
        isError
            ? "error-message"
            : "success-message";
}