package de.miaurizius.shap_planner

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

object UserPreferencesKeys {
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val LAST_USER_ID = stringPreferencesKey("last_user_id")
}

class UserPreferences(private val context: Context) {

    //Stave status
    suspend fun saveLogin(userId: String) {
        context.dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.IS_LOGGED_IN] = true
            prefs[UserPreferencesKeys.LAST_USER_ID] = userId;
        }
    }

    //Logout
    suspend fun clearLogin() {
        context.dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.IS_LOGGED_IN] = false
            prefs[UserPreferencesKeys.LAST_USER_ID] = ""
        }
    }

    //Get state
    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[UserPreferencesKeys.IS_LOGGED_IN] ?: false }
    val lastUserLoginFlow: Flow<String?> = context.dataStore.data.map { prefs -> prefs[UserPreferencesKeys.LAST_USER_ID] }
}