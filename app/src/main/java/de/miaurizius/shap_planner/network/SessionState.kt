package de.miaurizius.shap_planner.network

sealed class SessionState {
    object Loading : SessionState()
    object Valid : SessionState()
    object Invalid : SessionState()
    data class Error(val message: String) : SessionState()
}