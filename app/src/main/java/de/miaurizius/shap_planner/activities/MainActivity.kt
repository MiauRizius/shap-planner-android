package de.miaurizius.shap_planner.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.unit.dp
import de.miaurizius.shap_planner.UserPreferences
import de.miaurizius.shap_planner.entities.Account
import de.miaurizius.shap_planner.room.AppDatabase
import de.miaurizius.shap_planner.ui.theme.ShapPlannerTheme
import de.miaurizius.shap_planner.viewmodels.LoginViewModel
import de.miaurizius.shap_planner.viewmodels.MainViewModel
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        val prefs = UserPreferences(this)
        val loginViewModel = LoginViewModel(prefs)

        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.accountDao()
        val mainViewModel = MainViewModel(dao)

        setContent {
            ShapPlannerTheme {
                val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()
                val accountList by mainViewModel.accounts.collectAsState()
                val selectedAccount = mainViewModel.selectedAccount

                when {
                    !isLoggedIn || accountList.isEmpty() -> {
                        LoginScreen { serverUrl, username, password -> loginViewModel.login(serverUrl, username, password, mainViewModel) }
                    }

                    selectedAccount != null -> {
                        DashboardScreen(
                            account = selectedAccount,
                            onBack = { mainViewModel.logoutFromAccount() }
                        )
                    }

                    else -> {
                        AccountSelectionScreen(
                            accounts = accountList,
                            onAccountClick = { account ->
                                mainViewModel.selectAccount(account)
                            },
                            onAddAccountClick = {
                                loginViewModel.logout()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AccountSelectionScreen(accounts: List<Account>, onAccountClick: (Account) -> Unit, onAddAccountClick: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Wähle einen Account", style = MaterialTheme.typography.headlineSmall)
        }

        items(accounts) { account ->
            Card(modifier = Modifier.fillMaxWidth().clickable{ onAccountClick(account) }) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).background(Color.Gray, shape = CircleShape))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = account.name, fontWeight = FontWeight.Bold)
                        Text(text = account.wgName, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onAddAccountClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Anderen Account hinzufügen")
            }
        }
    }
}

@Composable
fun LoginScreen(onLogin: (String, String, String) -> Unit) {
    var serverUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp).statusBarsPadding().navigationBarsPadding()) {
        Text("Bitte anmelden")
        Spacer(modifier = Modifier.height(8.dp))

        //Home-Server
        TextField(
            value = serverUrl,
            onValueChange = { serverUrl = it },
            label = { Text("Server-URL") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        //Username
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nutzername") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        //Password
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Passwort") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { if(serverUrl.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) onLogin(
            serverUrl,
            username,
            password
        ) }) {
            Text("Login")
        }
    }
}

@Composable
fun DashboardScreen(account: Account, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Hallo, ${account.name}!", style = MaterialTheme.typography.headlineMedium)
                Text(text = "WG: ${account.wgName}", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            }
            Button(onClick = onBack) {
                Text("Wechseln")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Text("Hier kommen bald deine WG-Kosten hin 🚀")
        }
    }
}