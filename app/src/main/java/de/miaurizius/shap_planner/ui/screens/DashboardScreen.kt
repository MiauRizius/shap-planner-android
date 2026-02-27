package de.miaurizius.shap_planner.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.miaurizius.shap_planner.entities.Account
import de.miaurizius.shap_planner.network.SessionState

@Composable
fun DashboardScreen(
    account: Account,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    sessionState: SessionState,
    onValidate: () -> Unit,
    onSessionInvalid: () -> Unit) {

    LaunchedEffect(Unit) { onValidate() }

    when (sessionState) {
        SessionState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        SessionState.Valid -> {
            BackHandler {
                onBack()
            }
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
                        Text(
                            text = "Hallo, ${account.name}!",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "WG: ${account.wgName}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                    Button(onClick = onBack) {
                        Text("Wechseln")
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                Button(onClick = onDelete) {
                    Text("Löschen")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Hier kommen bald deine WG-Kosten hin 🚀")
                }
            }
        }

        SessionState.Invalid -> {
            LaunchedEffect(Unit) {
                onSessionInvalid()
            }
        }

        is SessionState.Error -> {
            Text("Server error")
        }
    }
}