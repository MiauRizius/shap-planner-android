package de.miaurizius.shap_planner.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.miaurizius.shap_planner.UserPreferences
import de.miaurizius.shap_planner.entities.Account
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class LoginViewModel(private val prefs: UserPreferences) : ViewModel() {

    val isLoggedIn = prefs.isLoggedInFlow.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val lastUserId = prefs.lastUserLoginFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun login(serverUrl: String, username: String, password: String, viewModel: MainViewModel) {
        val uuid = UUID.randomUUID();
        val acc = Account(uuid, username, "Pfadi-WG", null, serverUrl) //TODO: get data from backend
        viewModel.addAccount(acc)
        println("Logged in as ${username} in ${serverUrl}")
        viewModelScope.launch { prefs.saveLogin(uuid.toString()) }
    }

    fun logout() {
        viewModelScope.launch { prefs.clearLogin() }
    }

}