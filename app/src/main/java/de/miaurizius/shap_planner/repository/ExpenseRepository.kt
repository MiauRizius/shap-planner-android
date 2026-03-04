package de.miaurizius.shap_planner.repository

import de.miaurizius.shap_planner.entities.Expense
import de.miaurizius.shap_planner.entities.ExpenseDao
import de.miaurizius.shap_planner.network.APIService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class ExpenseRepository(
    private val dao: ExpenseDao,
    private val api: APIService
) {
    fun getExpenses(token: String, forceRefresh: Boolean = false): Flow<Resource<List<Expense>>> = flow {
        val cachedExpense = dao.getAllExpenses().first()
        emit(Resource.Loading(cachedExpense))

        if(cachedExpense.isEmpty() || forceRefresh) {
            try {
                val response = api.expensesGet("Bearer $token")
                if(response.isSuccessful) {
                    val remoteExpense = response.body()?.expenses ?: emptyList()
                    remoteExpense.forEach {
                        dao.insertExpense(it)
                    }
                }
            } catch(e: Exception) {
                emit(Resource.Error("Network Error: ${e.localizedMessage}", cachedExpense))
            }
        }
        dao.getAllExpenses().collect { emit(Resource.Success(it)) }
    }
}