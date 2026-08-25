document.addEventListener(
    "DOMContentLoaded",
    loadBudgets
);


async function loadBudgets() {

    try {

        const data =
            await getBudgets();

        if (!data) {
            return;
        }

        renderBudgets(data);

    } catch (error) {

        console.error(
            "Budget error:",
            error
        );

        alert(
            "Unable to load budgets"
        );
    }
}