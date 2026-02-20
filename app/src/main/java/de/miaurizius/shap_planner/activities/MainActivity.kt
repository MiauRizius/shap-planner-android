package de.miaurizius.shap_planner.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.miaurizius.shap_planner.UserPreferences
import de.miaurizius.shap_planner.ui.theme.ShapPlannerTheme
import de.miaurizius.shap_planner.viewmodels.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        val prefs = UserPreferences(this)
        val viewModel = LoginViewModel(prefs)

        setContent {
            ShapPlannerTheme {
                val isLoggedIn by viewModel.isLoggedIn.collectAsState()
                if(isLoggedIn) MainScreen()
                else LoginScreen { userId -> viewModel.login(userId) }
            }
        }
    }
}

@Composable
fun MainScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Willkommen zurück!")
        Button(onClick = { /* TODO: Logout */ }) {
            Text("Logout")
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