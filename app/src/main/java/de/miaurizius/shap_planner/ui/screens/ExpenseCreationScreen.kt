package de.miaurizius.shap_planner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.miaurizius.shap_planner.entities.Account
import de.miaurizius.shap_planner.viewmodels.ExpenseCreationViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpenseCreationScreen(
    account: Account,
    viewModel: ExpenseCreationViewModel,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    val users by viewModel.users.collectAsState()
    val selectedUsers = remember { mutableStateListOf<UUID>() }

    LaunchedEffect(Unit) { viewModel.loadUsers() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("New Expense") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("What was bought?") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = amountStr,
                onValueChange = { amountStr = it },
                label = { Text("Amount in €") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Who participated?", style = MaterialTheme.typography.titleMedium)

            FlowRow(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                users.forEach { user ->
                    FilterChip(
                        selected = selectedUsers.contains(user.id),
                        onClick = {
                            if (selectedUsers.contains(user.id)) selectedUsers.remove(user.id)
                            else selectedUsers.add(user.id)
                        },
                        label = { Text(user.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val cents = (amountStr.replace(",", ".").toDoubleOrNull() ?: 0.0) * 100
                    viewModel.saveExpense(account, title, cents.toInt(), selectedUsers.toList())
                    onSaved()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && amountStr.isNotBlank() && selectedUsers.isNotEmpty()
            ) {
                Text("Save")
            }

            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel")
            }
        }
    }
}