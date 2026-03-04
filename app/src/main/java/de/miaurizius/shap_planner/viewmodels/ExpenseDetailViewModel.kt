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
import de.miaurizius.shap_planner.network.RetrofitProvider
import de.miaurizius.shap_planner.repository.ExpenseShareRepository
import de.miaurizius.shap_planner.repository.Resource
import de.miaurizius.shap_planner.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ShareWithUser(
    val share: ExpenseShare,
    val user: User?
)

class ExpenseDetailViewModel(
    private val expenseDao: ExpenseDao,
    private val shareDao: ExpenseShareDao,
    private val userDao: UserDao,
    private val tokenStorage: TokenStorage
) : ViewModel() {
    private val _sharesWithUser = MutableStateFlow<List<ShareWithUser>>(emptyList())
    val sharesWithUser: StateFlow<List<ShareWithUser>> = _sharesWithUser

    fun loadExpenseDetail(account: Account, expense: Expense) {
        viewModelScope.launch {
            val api = RetrofitProvider.create(account.serverUrl)
            val token = tokenStorage.getAccess(account.id.toString()) ?: ""

            val shareRepo = ExpenseShareRepository(shareDao, api)
            val userRepo = UserRepository(userDao, api)

            shareRepo.getSharesByExpenseId(token, expense.id).collect { resource ->
                val shares = resource.data ?: emptyList()
                val combinedList = shares.map { share ->
                    val cachedUser = userDao.getUserById(share.user_id).first()
                    if (cachedUser == null) {
                        val userResource = userRepo.getUser(token, share.user_id).first { it is Resource.Success || it is Resource.Error }
                        ShareWithUser(share, userResource.data)
                    } else {
                        ShareWithUser(share, cachedUser)
                    }
                }
                _sharesWithUser.value = combinedList
            }
        }
    }
}