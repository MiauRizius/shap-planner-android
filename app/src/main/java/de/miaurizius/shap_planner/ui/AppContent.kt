package de.miaurizius.shap_planner.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import de.miaurizius.shap_planner.entities.Account
import de.miaurizius.shap_planner.entities.Expense
import de.miaurizius.shap_planner.network.SessionState
import de.miaurizius.shap_planner.ui.screens.AccountSelectionScreen
import de.miaurizius.shap_planner.ui.screens.DashboardScreen
import de.miaurizius.shap_planner.ui.screens.ExpenseCreationScreen
import de.miaurizius.shap_planner.ui.screens.ExpenseDetailScreen
import de.miaurizius.shap_planner.ui.screens.LoginScreen
import de.miaurizius.shap_planner.viewmodels.ExpenseCreationViewModel
import de.miaurizius.shap_planner.viewmodels.ExpenseDetailViewModel
import de.miaurizius.shap_planner.viewmodels.MainViewModel

@Composable
fun AppContent(
    // Login
    accountList: List<Account>,
    selectedAccount: Account?,
    showLoginForNewAccount: Boolean,
    onLogin: (String, String, String) -> Unit,

    // Expenses
    onExpenseClick: (Expense) -> Unit,

    // Account
    onSelectAccount: (Account) -> Unit,
    onLogoutAccount: () -> Unit,
    onAddAccountClick: () -> Unit,
    onDeleteAccount: () -> Unit,

    // Session
    sessionState: SessionState,
    onValidateSession: () -> Unit,
    onSessionInvalid: () -> Unit,

    //Important
    viewModel: MainViewModel,
    detailViewModel: ExpenseDetailViewModel,
    creationViewModel: ExpenseCreationViewModel
) {
    var selectedExpense by remember { mutableStateOf<Expense?>(null) }
    var showAddExpenseScreen by remember { mutableStateOf(false) }

    when {
        showAddExpenseScreen -> {
            ExpenseCreationScreen(
                account = selectedAccount!!,
                viewModel = creationViewModel,
                onBack = { showAddExpenseScreen = false },
                onSaved = { showAddExpenseScreen = false },
            )
        }
        selectedExpense != null -> {
            ExpenseDetailScreen(
                expense = selectedExpense!!,
                account = selectedAccount!!,
                viewModel = detailViewModel,
                onBack = { selectedExpense = null }
            )
        }
        showLoginForNewAccount -> LoginScreen(onLogin)
        accountList.isEmpty() -> LoginScreen(onLogin)
        selectedAccount != null -> DashboardScreen(
            // Data and regarding Methods
            account = selectedAccount,
            onExpenseClick = { selectedExpense = it },

            // Default Methods
            mainViewModel = viewModel,
            onBack = onLogoutAccount,
            onDelete = onDeleteAccount,
            sessionState = sessionState,
            onValidate = onValidateSession,
            onSessionInvalid = onSessionInvalid,
            onAddExpenseClick = { showAddExpenseScreen = true },
        )
        else -> AccountSelectionScreen(
            accounts = accountList,
            onAccountClick = onSelectAccount,
            onAddAccountClick = onAddAccountClick
        )
    }
}