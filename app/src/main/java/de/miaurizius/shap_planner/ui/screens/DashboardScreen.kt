package de.miaurizius.shap_planner.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.miaurizius.shap_planner.entities.Account
import de.miaurizius.shap_planner.entities.Expense
import de.miaurizius.shap_planner.network.SessionState
import de.miaurizius.shap_planner.viewmodels.MainViewModel

@Composable
fun DashboardScreen(
    // Data and regarding Methods
    account: Account,
    expenses: List<Expense>,
    onExpenseClick: (Expense) -> Unit,

    // Default Methods
    mainViewModel: MainViewModel,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    sessionState: SessionState,
    onValidate: () -> Unit,
    onSessionInvalid: () -> Unit) {

    LaunchedEffect(Unit) { onValidate() }
    mainViewModel.loadExpenses(account)
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
                // Header
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

                Text("WG-Kosten", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(expenses) { expense ->
                        ExpenseItem(expense = expense, onClick = { onExpenseClick(expense) })
                    }
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

@Composable
fun ExpenseItem(expense: Expense, onClick: () -> Unit) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .clickable{onClick()},
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = expense.title, style = MaterialTheme.typography.bodyLarge)
            Text(text = expense.amount.toString()+"€", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}