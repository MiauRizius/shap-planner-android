package de.miaurizius.shap_planner.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.miaurizius.shap_planner.TokenStorage
import de.miaurizius.shap_planner.UserPreferences
import de.miaurizius.shap_planner.entities.Account
import de.miaurizius.shap_planner.network.LoginRequest
import de.miaurizius.shap_planner.network.RetrofitProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.Locale.getDefault
import java.util.UUID

class LoginViewModel(private val prefs: UserPreferences, private val appContext: Context) : ViewModel() {

    private val tokenStorage = TokenStorage(appContext)

    val isLoggedIn = prefs.isLoggedInFlow.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val lastUserId = prefs.lastUserLoginFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun login(serverUrl: String, username: String, password: String, viewModel: MainViewModel) {
        viewModelScope.launch {
            val api = RetrofitProvider.create(serverUrl)

            try {
                val response = withContext(Dispatchers.IO) {
                    api.login(LoginRequest(username.lowercase(getDefault()).trim(), password))
                }

                if(response.isSuccessful) {
                    val body = response.body() ?: run {
                        return@launch
                    }

                    val access = body.access_token
                    val refresh = body.refresh_token

                    tokenStorage.saveTokens(body.user.id, access, refresh)

                    val account = Account(
                        id = UUID.fromString(body.user.id),
                        name = username.trim(),
                        wgName = body.wgName,
                        avatarUrl = null,
                        serverUrl = serverUrl,
                        role = body.user.role
                    )

                    viewModel.addAccount(account)

                    prefs.saveLogin(body.user.id)
                } else {
                    println("Login failed: ${response.code()} ${response.errorBody()?.toString()}")
                }
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun logout() {
        viewModelScope.launch { prefs.clearLogin() }
    }

}