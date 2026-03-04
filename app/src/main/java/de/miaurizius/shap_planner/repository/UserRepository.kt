package de.miaurizius.shap_planner.repository

import de.miaurizius.shap_planner.entities.User
import de.miaurizius.shap_planner.entities.UserDao
import de.miaurizius.shap_planner.network.APIService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.UUID

class UserRepository(
    private val dao: UserDao,
    private val api: APIService
) {
    fun getUser(token: String, userId: UUID, forceRefresh: Boolean = false): Flow<Resource<User>> = flow {
        val cached = dao.getUserById(userId).first()
        emit(Resource.Loading(cached))
        if(cached == null || forceRefresh) {
            try {
                val response = api.userinfo("Bearer $token", userId)
                if(response.isSuccessful) {
                    println("Body: ${response.body()}")
                    response.body()?.let { remoteUser -> dao.insertUser(remoteUser) }
                }
            } catch(e: Exception) {
                emit(Resource.Error("Network-Error: ${e.localizedMessage}", cached))
            }
        }
        dao.getUserById(userId).collect { user -> if(user != null) emit(Resource.Success(user)) else emit(
            Resource.Error("User nicht gefunden", null)) }
    }
}