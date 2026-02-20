package de.miaurizius.shap_planner.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.miaurizius.shap_planner.entities.Account
import de.miaurizius.shap_planner.entities.AccountDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class MainViewModel(private val accountDao: AccountDao) : ViewModel() {

    // Das ist der State, den dein SetContent beobachtet
    val accounts: StateFlow<List<Account>> = accountDao.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Funktion zum Hinzufügen (z.B. nach Login)
    fun addAccount(account: Account) {
        viewModelScope.launch {
            accountDao.insertAccount(account)
        }
    }

    fun selectAccount(account: Account) {}
}