package de.miaurizius.shap_planner.ui

import androidx.compose.runtime.Composable
import de.miaurizius.shap_planner.entities.Account
import de.miaurizius.shap_planner.network.SessionState
import de.miaurizius.shap_planner.ui.screens.AccountSelectionScreen
import de.miaurizius.shap_planner.ui.screens.DashboardScreen
import de.miaurizius.shap_planner.ui.screens.LoginScreen

@Composable
fun AppContent(
    isLoggedIn: Boolean,
    accountList: List<Account>,
    selectedAccount: Account?,
    showLoginForNewAccount: Boolean,
    onLogin: (String, String, String) -> Unit,
    onSelectAccount: (Account) -> Unit,
    onLogoutAccount: () -> Unit,
    onAddAccountClick: () -> Unit,
    onDeleteAccount: () -> Unit,
    sessionState: SessionState,
    onValidateSession: () -> Unit,
    onSessionInvalid: () -> Unit
) {
    when {
        showLoginForNewAccount -> LoginScreen(onLogin)
        accountList.isEmpty() -> LoginScreen(onLogin)
        selectedAccount != null -> DashboardScreen(
            account = selectedAccount,
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