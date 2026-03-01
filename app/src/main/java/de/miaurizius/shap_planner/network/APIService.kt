package de.miaurizius.shap_planner.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import java.util.UUID

interface APIService {
    // Account
    @POST("api/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>
    @POST("api/refresh")
    suspend fun refresh(@Body req: RefreshRequest): Response<RefreshResponse>
    @GET("api/ping")
    suspend fun ping(@Header("Authorization") token: String): Response<Map<String, String>>

    // Expenses
    @GET("api/expenses")
    suspend fun expenseGet(@Header("Authorization") token: String): Response<ExpenseResponse>
    @POST("api/expenses")
    suspend fun expenseCreate(@Header("Authorization") token: String)
    @PUT("api/expenses")
    suspend fun expenseUpdate(@Header("Authorization") token: String)
    @DELETE("api/expenses")
    suspend fun expenseDelete(@Header("Authorization") token: String)

    // User
    @GET("api/userinfo")
    suspend fun userinfo(@Header("Authorization") token: String, @Query("id") userId: UUID)
}