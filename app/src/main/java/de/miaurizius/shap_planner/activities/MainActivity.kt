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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
                LaunchedEffect(Unit) {
                    mainViewModel.addAccount(Account(UUID.randomUUID(), "MiauRizius", "Pfadi-WG"))
                }

                val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()
                val accountList by mainViewModel.accounts.collectAsState() // Lädt aus Room

                if (isLoggedIn) {
                    if (accountList.isEmpty()) {
                        // Zeige Button "Ersten Account erstellen"
                    } else {
                        AccountSelectionScreen(
                            accounts = accountList,
                            onAccountClick = { account ->
                                mainViewModel.selectAccount(account)
                            }
                        )
                    }
                } else {
                    LoginScreen { userId -> loginViewModel.login(userId) }
                }
            }
        }
    }
}

@Composable
fun AccountSelectionScreen(accounts: List<Account>, onAccountClick: (Account) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
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
    }
}

@Composable
fun LoginScreen(onLogin: (String) -> Unit) {
    var userId by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Bitte anmelden")
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("User ID") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { if(userId.isNotEmpty()) onLogin(userId) }) {
            Text("Login")
        }
    }
}