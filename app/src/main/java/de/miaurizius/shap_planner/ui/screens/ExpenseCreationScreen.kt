package de.miaurizius.shap_planner.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.miaurizius.shap_planner.entities.Account
import de.miaurizius.shap_planner.viewmodels.ExpenseCreationViewModel

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseCreationScreen(
    account: Account,
    viewModel: ExpenseCreationViewModel,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var totalAmountStr by remember { mutableStateOf("") }
    val attachmentUris = remember { mutableStateListOf<android.net.Uri>() }
    val userShares = remember { mutableStateMapOf<java.util.UUID, String>() }
    val users by viewModel.users.collectAsState()

    // Real-time calculation logic
    val totalCents = (totalAmountStr.replace(",", ".").toDoubleOrNull() ?: 0.0) * 100
    val distributedCents = userShares.values.sumOf {
        (it.replace(",", ".").toDoubleOrNull() ?: 0.0) * 100
    }.toLong()

    val diff = totalCents.toLong() - distributedCents
    val isAmountMatched = totalCents > 0 && diff == 0L

    // File Picker for multiple files (Images & PDFs)
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        attachmentUris.addAll(uris)
    }

    LaunchedEffect(Unit) { viewModel.loadUsers() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Expense") },
                navigationIcon = { IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info
            item {
                Spacer(modifier = Modifier.height(8.dp))
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = totalAmountStr,
                            onValueChange = { totalAmountStr = it },
                            label = { Text("Total Amount (€) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Optional Description & Multi-File Attachments
            item {
                Text("Additional Info", style = MaterialTheme.typography.titleMedium)
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Attachments (${attachmentUris.size})", style = MaterialTheme.typography.labelLarge)
                        attachmentUris.forEach { uri ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Description, "File", modifier = Modifier.size(20.dp))
                                Text(" Document attached", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                                IconButton(onClick = { attachmentUris.remove(uri) }) {
                                    Icon(Icons.Default.Delete, "Remove", tint = Color.Red)
                                }
                            }
                        }

                        Button(onClick = { launcher.launch("*/*") }) {
                            Text("Select Files (Images, PDF)")
                        }
                    }
                }
            }

            // Split Details & Validation Message
            item {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Split Details *", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = {
                            if (totalCents > 0) {
                                val share = String.format("%.2f", (totalCents / users.size) / 100.0)
                                users.forEach { userShares[it.id] = share }
                            }
                        }) { Text("Split Equally") }
                    }

                    // VALIDATION MESSAGE
                    if (totalCents > 0) {
                        val statusText = when {
                            diff > 0 -> "Remaining: ${String.format("%.2f", diff / 100.0)} €"
                            diff < 0 -> "Over-allocated: ${String.format("%.2f", Math.abs(diff) / 100.0)} €"
                            else -> "Amount matched! ✓"
                        }
                        val statusColor = if (diff == 0L) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error

                        Surface(
                            color = statusColor.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = statusText,
                                color = statusColor,
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            items(users) { user ->
                UserShareInputItem(
                    userName = user.name,
                    amount = userShares[user.id] ?: "",
                    onAmountChange = { userShares[user.id] = it }
                )
            }

            // Save Actions
            item {
                Button(
                    onClick = {
                        viewModel.saveExpense(
                            account = account,
                            title = title,
                            description = description,
                            amountCents = totalCents.toInt(),
                            shares = userShares.mapValues { (it.value.replace(",",".").toDoubleOrNull() ?: 0.0).toInt() * 100 }.filter { it.value > 0 },
                            attachments = attachmentUris.map { it.toString() }
                        )
                        onSaved()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = title.isNotBlank() && isAmountMatched // STRICT VALIDATION
                ) {
                    Text("Save Expense")
                }

                if (totalAmountStr.isNotBlank() && !isAmountMatched) {
                    Text(
                        "Sum of shares must equal total amount to save.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun UserShareInputItem(userName: String, amount: String, onAmountChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(userName.take(1).uppercase(), style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(userName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)

            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                modifier = Modifier.width(100.dp),
                placeholder = { Text("0.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("€")
        }
    }
}