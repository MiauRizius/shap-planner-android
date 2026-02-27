package de.miaurizius.shap_planner

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenStorage(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "wg_token_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveTokens(accountId: String, accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString("access_$accountId", accessToken)
            .putString("refresh_$accountId", refreshToken)
            .apply()
    }

    fun getAccess(accountId: String): String? = prefs.getString("access_$accountId", null)
    fun getRefresh(accountId: String): String? = prefs.getString("refresh_$accountId", null)

    fun clearTokens(accountId: String) {
        prefs.edit()
            .remove("access_$accountId")
            .remove("refresh_$accountId")
            .apply()
    }


}