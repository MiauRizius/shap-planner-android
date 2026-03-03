package de.miaurizius.shap_planner.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(onLogin: (String, String, String) -> Unit, onBack: (() -> Unit)? = null) {

    if (onBack != null) {
        BackHandler {
            onBack()
        }
    }

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
            label = { Text("Server-Domain") }
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