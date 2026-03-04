package de.miaurizius.shap_planner.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.miaurizius.shap_planner.entities.Account
import de.miaurizius.shap_planner.entities.Expense
import de.miaurizius.shap_planner.viewmodels.ExpenseDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
    expense: Expense,
    account: Account,
    viewModel: ExpenseDetailViewModel,
    onBack: () -> Unit
) {
    val shares by viewModel.sharesWithUser.collectAsState()

    LaunchedEffect(expense) {
        viewModel.loadExpenseDetail(account, expense)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(expense.title) },
                navigationIcon = { IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Zurück")
                } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text(
                text = "${expense.amount / 100.0}€",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(expense.description, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Kostenaufteilung", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(shares) { item ->
                    ShareItem(item.user?.name ?: "Unbekannter Nutzer", item.share.share_cents)
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun ShareItem(name: String, amountCents: Int) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(name, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Text(
                text = String.format("%.2f€", amountCents / 100.0),
                color = if (amountCents > 0) Color(0xFF4CAF50) else Color.Gray,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}