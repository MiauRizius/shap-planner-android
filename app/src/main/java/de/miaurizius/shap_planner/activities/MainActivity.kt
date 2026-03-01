package de.miaurizius.shap_planner.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import de.miaurizius.shap_planner.TokenStorage
import de.miaurizius.shap_planner.UserPreferences
import de.miaurizius.shap_planner.room.AppDatabase
import de.miaurizius.shap_planner.ui.AppContent
import de.miaurizius.shap_planner.ui.theme.ShapPlannerTheme
import de.miaurizius.shap_planner.viewmodels.LoginViewModel
import de.miaurizius.shap_planner.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = UserPreferences(this)
        val loginViewModel = LoginViewModel(prefs, applicationContext)
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.accountDao()
        val tokenStorage = TokenStorage(applicationContext)
        val mainViewModel = MainViewModel(dao, tokenStorage)


        setContent {
            ShapPlannerTheme {
                val accountList by mainViewModel.accounts.collectAsState()
                val selectedAccount = mainViewModel.selectedAccount
                val showLoginForNewAccount = remember { mutableStateOf(false) }
                val expenses by mainViewModel.expenses.collectAsState()

                BackHandler(enabled = showLoginForNewAccount.value && accountList.isNotEmpty()) {
                    showLoginForNewAccount.value = false
                }

                AppContent(
                    accountList = accountList,
                    selectedAccount = selectedAccount,
                    showLoginForNewAccount = showLoginForNewAccount.value,
                    onLogin = { server, user, pass ->
                        loginViewModel.login(server, user, pass, mainViewModel)
                        showLoginForNewAccount.value = false
                    },
                    onSelectAccount = { mainViewModel.selectAccount(it) },
                    onLogoutAccount = { mainViewModel.logoutFromAccount() },
                    onAddAccountClick = { showLoginForNewAccount.value = true },
                    onDeleteAccount = { mainViewModel.deleteAccount(selectedAccount!!) },
                    sessionState = mainViewModel.sessionState,
                    onValidateSession = { mainViewModel.validateSession(selectedAccount!!) },
                    onSessionInvalid = { mainViewModel.logoutFromAccount() },
                    expenses = expenses,
                    onExpenseClick = { expense -> println("Clicked: ${expense.title}") },
                    viewModel = mainViewModel
                )
            }
        }
    }
}