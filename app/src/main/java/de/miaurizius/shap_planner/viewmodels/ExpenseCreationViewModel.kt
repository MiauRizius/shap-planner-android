package de.miaurizius.shap_planner.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.miaurizius.shap_planner.TokenStorage
import de.miaurizius.shap_planner.entities.Account
import de.miaurizius.shap_planner.entities.Expense
import de.miaurizius.shap_planner.entities.ExpenseDao
import de.miaurizius.shap_planner.entities.ExpenseShare
import de.miaurizius.shap_planner.entities.ExpenseShareDao
import de.miaurizius.shap_planner.entities.User
import de.miaurizius.shap_planner.entities.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ExpenseCreationViewModel(
    private val userDao: UserDao,
    private val expenseDao: ExpenseDao,
    private val shareDao: ExpenseShareDao,
    private val tokenStorage: TokenStorage
) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    fun loadUsers() {
        viewModelScope.launch {
            userDao.getAllUsers().collect { _users.value = it }
        }
    }

    fun saveExpense(
        account: Account,
        title: String,
        description: String,
        amountCents: Int,
        shares: Map<UUID, Int>,
        attachments: List<String>
    ) {
//        viewModelScope.launch {
//            val expenseId = UUID.randomUUID()
//            val newExpense = Expense(
//                id = expenseId,
//                payer_id = account.id,
//                amount = amountCents,
//                title = title,
//                description = description,
//                attachments = if (attachments.isEmpty()) null else attachments,
//                created_at = (System.currentTimeMillis() / 1000).toInt(),
//                last_updated_at = 0
//            )
//
//            expenseDao.insertExpense(newExpense)
//
//            shares.forEach { (userId, shareCents) ->
//                shareDao.insertShare(
//                    ExpenseShare(UUID.randomUUID(), expenseId, userId, shareCents)
//                )
//            }
//
//            // API POST Request
//
//        }
    }
}