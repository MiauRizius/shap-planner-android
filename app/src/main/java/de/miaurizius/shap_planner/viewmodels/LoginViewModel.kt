package de.miaurizius.shap_planner.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.miaurizius.shap_planner.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginViewModel(private val prefs: UserPreferences) : ViewModel() {

    val isLoggedIn = prefs.isLoggedInFlow.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val lastUserId = prefs.lastUserLoginFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun login(userId: String) {
        viewModelScope.launch { prefs.saveLogin(userId) }
    }

    fun logout() {
        viewModelScope.launch { prefs.clearLogin() }
    }

}