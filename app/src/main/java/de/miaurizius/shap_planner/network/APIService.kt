package de.miaurizius.shap_planner.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class LoginRequest(val username: String, val password: String)
data class LoginUser(val id: String, val username: String, val role: String, val avatarUrl: String?)
data class LoginResponse(val access_token: String, val refresh_token: String, val user: LoginUser, val wgName: String)

data class RefreshRequest(val refresh_token: String)
data class RefreshResponse(val access_token: String, val refresh_token: String)

interface APIService {
    @POST("api/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @POST("api/refresh")
    suspend fun refresh(@Body req: RefreshRequest): Response<RefreshResponse>

    @GET("api/ping")
    suspend fun ping(@Header("Authorization") token: String): Response<Map<String, String>>
}