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
        println("CachedExpense: $cachedExpense")
        emit(Resource.Loading(cachedExpense))

        if(cachedExpense.isEmpty() || forceRefresh) {
            try {
                val response = api.expenseGet("Bearer $token")
                if(response.isSuccessful) {
                    val remoteExpense = response.body()?.expenses ?: emptyList()
                    println("Fetched expenses: $remoteExpense")
                    remoteExpense.forEach {
                        dao.insertExpense(it)
                        println("Added $it")
                    }
                }
            } catch(e: Exception) {
                emit(Resource.Error("Network Error: ${e.localizedMessage}", cachedExpense))
            }
        }
        dao.getAllExpenses().collect { emit(Resource.Success(it)) }
    }
}