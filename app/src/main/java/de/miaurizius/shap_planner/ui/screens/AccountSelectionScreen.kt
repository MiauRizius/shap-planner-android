package de.miaurizius.shap_planner.ui.screens

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.miaurizius.shap_planner.entities.Account

@OptIn(ExperimentalMaterial3Api::class)
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
            Text("Choose an account", style = MaterialTheme.typography.headlineSmall)
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
                Text("Add account")
            }
        }
    }
}