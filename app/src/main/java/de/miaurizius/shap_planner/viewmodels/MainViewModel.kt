package de.miaurizius.shap_planner.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    val accounts: StateFlow<List<Account>> = accountDao.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addAccount(account: Account) {
        viewModelScope.launch {
            accountDao.insertAccount(account)
        }
    }

    var selectedAccount by mutableStateOf<Account?>(null)
        private set

    fun selectAccount(account: Account) {
        selectedAccount = account
    }

    fun logoutFromAccount() {
        selectedAccount = null
    }
}