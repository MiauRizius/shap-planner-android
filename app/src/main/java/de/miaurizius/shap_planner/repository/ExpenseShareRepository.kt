package de.miaurizius.shap_planner.repository

import de.miaurizius.shap_planner.entities.ExpenseShare
import de.miaurizius.shap_planner.entities.ExpenseShareDao
import de.miaurizius.shap_planner.network.APIService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.UUID

class ExpenseShareRepository(
    private val dao: ExpenseShareDao,
    private val api: APIService
) {
    fun getShares(token: String, forceRefresh: Boolean = false): Flow<Resource<List<ExpenseShare>>> = flow {
        val cachedData = dao.getAllShares().first()
        emit(Resource.Loading(cachedData))

        if(cachedData.isEmpty() || forceRefresh) {
            try {
                val response = api.sharesGet("Bearer $token")
                if(response.isSuccessful) {
                    val remoteShare = response.body()?.shares ?: emptyList()
                    remoteShare.forEach {
                        dao.insertShare(it)
                    }
                }
            } catch(e: Exception) {
                emit(Resource.Error("Network Error: ${e.localizedMessage}", cachedData))
            }
        }
        dao.getAllShares().collect { emit(Resource.Success(it)) }
    }

    fun getShareById(token: String, shareId: UUID, forceRefresh: Boolean = false): Flow<Resource<ExpenseShare>> = flow {
        val cached = dao.getShareById(shareId).first()
        emit(Resource.Loading(cached))
        if(cached == null || forceRefresh) {
            try {
                val response = api.shareGet("Bearer $token", shareId)
                if(response.isSuccessful) {
                    response.body()?.share?.let { remoteShare -> dao.insertShare(remoteShare) }
                }
            } catch(e: Exception) {
                emit(Resource.Error("Network-Error: ${e.localizedMessage}", cached))
            }
        }
        dao.getShareById(shareId).collect { share ->
            if(share != null) emit(Resource.Success(share))
            else emit(Resource.Error("Share nicht gefunden", null))
        }
    }
}