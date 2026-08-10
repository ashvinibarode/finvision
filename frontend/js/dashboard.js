document.addEventListener("DOMContentLoaded", loadDashboard);

async function loadDashboard() {

    try {

        const data = await getDashboardData();

        if (!data) {
            return;
        }

        updateSummary(data);

        createIncomeExpenseChart(data);

        createCategoryChart(data);

        updateRecentTransactions(data);

    } catch (error) {

        console.error("Dashboard error:", error);

        alert("Unable to load dashboard");


        function updateSummary(data) {

            document.getElementById("totalIncome").textContent =
                formatCurrency(data.totalIncome);

            document.getElementById("totalExpense").textContent =
                formatCurrency(data.totalExpense);

            document.getElementById("balance").textContent =
                formatCurrency(data.balance);

            document.getElementById("totalTransactions").textContent =
                data.totalTransactions;
        }


        function formatCurrency(amount) {

            return new Intl.NumberFormat("en-IN", {
                style: "currency",
                currency: "INR"
            }).format(amount);
        }


        function createIncomeExpenseChart(data) {

            const ctx =
                document.getElementById("incomeExpenseChart");

            new Chart(ctx, {

                type: "bar",

                data: {

                    labels: ["Income", "Expense"],

                    datasets: [
                        {
                            label: "Amount",

                            data: [
                                data.totalIncome,
                                data.totalExpense
                            ]
                        }
                    ]

                },

                options: {

                    responsive: true,

                    plugins: {
                        legend: {
                            display: false
                        }
                    }

                }

            });
        }

        function createCategoryChart(data) {

            const categories =
                data.topExpenseCategories.map(
                    item => item.category
                );

            const amounts =
                data.topExpenseCategories.map(
                    item => item.amount
                );

            const ctx =
                document.getElementById("categoryExpenseChart");

            new Chart(ctx, {

                type: "doughnut",

                data: {

                    labels: categories,

                    datasets: [
                        {
                            label: "Expenses",
                            data: amounts
                        }
                    ]

                },

                options: {
                    responsive: true
                }

            });
        }
          function updateRecentTransactions(data) {

              const table =
                  document.getElementById("recentTransactions");

              table.innerHTML = "";

              data.recentTransactions.forEach(transaction => {

                  const row = document.createElement("tr");

                  row.innerHTML = `
                      <td>${transaction.title}</td>
                      <td>${transaction.category}</td>
                      <td>${formatCurrency(transaction.amount)}</td>
                      <td>${transaction.transactionDate}</td>
                  `;

                  table.appendChild(row);
              });
          }
    }
}