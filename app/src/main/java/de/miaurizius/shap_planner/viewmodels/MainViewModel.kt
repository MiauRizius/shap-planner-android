package de.miaurizius.shap_planner.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.miaurizius.shap_planner.TokenStorage
import de.miaurizius.shap_planner.entities.Account
import de.miaurizius.shap_planner.entities.AccountDao
import de.miaurizius.shap_planner.network.RefreshRequest
import de.miaurizius.shap_planner.network.RetrofitProvider
import de.miaurizius.shap_planner.network.SessionState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class MainViewModel(private val accountDao: AccountDao, private val tokenStorage: TokenStorage) : ViewModel() {

    val accounts: StateFlow<List<Account>> = accountDao.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var sessionState by mutableStateOf<SessionState>(SessionState.Loading)
        private set

    fun validateSession(account: Account) {
        viewModelScope.launch {
            sessionState = SessionState.Loading
            val api = RetrofitProvider.create(account.serverUrl)

            val accessToken = tokenStorage.getAccess(account.id.toString())
            val refreshToken = tokenStorage.getRefresh(account.id.toString())

            if(accessToken == null || refreshToken == null) {
                sessionState = SessionState.Invalid
                return@launch
            }

            println("Testing with AT $accessToken")
            val pingResponse = api.ping("Bearer $accessToken")

            if(pingResponse.isSuccessful) {
                sessionState = SessionState.Valid
                return@launch
            }

            if(pingResponse.code() == 401) {
                println("Testing with RT $refreshToken")
                val refreshResponse = api.refresh(RefreshRequest(refreshToken))

                if(refreshResponse.isSuccessful) {
                    val newTokens = refreshResponse.body()!!

                    tokenStorage.saveTokens(
                        account.id.toString(),
                        newTokens.accessToken,
                        newTokens.accessToken
                    )

                    sessionState = SessionState.Valid
                    return@launch
                } else {
                    sessionState = SessionState.Invalid
                    return@launch
                }
            }
            sessionState = SessionState.Error("Server error")
        }
    }

    fun addAccount(account: Account) {
        viewModelScope.launch {
            accountDao.insertAccount(account)
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            accountDao.deleteAccount(account)
            tokenStorage.clearTokens(account.id.toString())
            selectedAccount = null
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