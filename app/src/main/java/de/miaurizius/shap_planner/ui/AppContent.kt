package de.miaurizius.shap_planner.ui

import androidx.compose.runtime.Composable
import de.miaurizius.shap_planner.entities.Account
import de.miaurizius.shap_planner.entities.Expense
import de.miaurizius.shap_planner.network.SessionState
import de.miaurizius.shap_planner.ui.screens.AccountSelectionScreen
import de.miaurizius.shap_planner.ui.screens.DashboardScreen
import de.miaurizius.shap_planner.ui.screens.LoginScreen
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
    viewModel: MainViewModel
) {
    when {
        showLoginForNewAccount -> LoginScreen(onLogin)
        accountList.isEmpty() -> LoginScreen(onLogin)
        selectedAccount != null -> DashboardScreen(
            // Data and regarding Methods
            account = selectedAccount,
            onExpenseClick = onExpenseClick,

            // Default Methods
            mainViewModel = viewModel,
            onBack = onLogoutAccount,
            onDelete = onDeleteAccount,
            sessionState = sessionState,
            onValidate = onValidateSession,
            onSessionInvalid = onSessionInvalid
        )
        else -> AccountSelectionScreen(
            accounts = accountList,
            onAccountClick = onSelectAccount,
            onAddAccountClick = onAddAccountClick
        )
    }
}