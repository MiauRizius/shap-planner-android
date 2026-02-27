package de.miaurizius.shap_planner.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.miaurizius.shap_planner.TokenStorage
import de.miaurizius.shap_planner.UserPreferences
import de.miaurizius.shap_planner.entities.Account
import de.miaurizius.shap_planner.network.SessionState
import de.miaurizius.shap_planner.room.AppDatabase
import de.miaurizius.shap_planner.ui.AppContent
import de.miaurizius.shap_planner.ui.theme.ShapPlannerTheme
import de.miaurizius.shap_planner.viewmodels.LoginViewModel
import de.miaurizius.shap_planner.viewmodels.MainViewModel
import java.util.UUID

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
                val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()
                val accountList by mainViewModel.accounts.collectAsState()
                val selectedAccount = mainViewModel.selectedAccount
                val showLoginForNewAccount = remember { mutableStateOf(false) }

                BackHandler(enabled = showLoginForNewAccount.value && accountList.isNotEmpty()) {
                    showLoginForNewAccount.value = false
                }

                AppContent(
                    isLoggedIn = isLoggedIn,
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
                    onSessionInvalid = { mainViewModel.logoutFromAccount() }
                )
            }
        }
    }
}