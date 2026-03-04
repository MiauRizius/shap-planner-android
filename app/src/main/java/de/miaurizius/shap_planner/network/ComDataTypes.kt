package de.miaurizius.shap_planner.network

import de.miaurizius.shap_planner.entities.Expense
import de.miaurizius.shap_planner.entities.ExpenseShare

// Login
data class LoginRequest(val username: String, val password: String)
data class LoginUser(val id: String, val username: String, val role: String, val avatarUrl: String?)
data class LoginResponse(val access_token: String, val refresh_token: String, val user: LoginUser, val wgName: String)

// Refresh-Tokens
data class RefreshRequest(val refresh_token: String)
data class RefreshResponse(val access_token: String, val refresh_token: String)

// Expenses
data class ExpensesResponse(val expenses: List<Expense>)
data class ExpenseCreationRequest(val expense: Expense, val shares: List<ExpenseShare>)
data class ExpenseCreationResponse(val expense: Expense, val shares: List<ExpenseShare>)

// ExpenseShares
data class ExpenseSharesResponse(val shares: List<ExpenseShare>)
data class ExpenseShareResponse(val share: ExpenseShare)