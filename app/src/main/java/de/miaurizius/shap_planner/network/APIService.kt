package de.miaurizius.shap_planner.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val username: String, val password: String)
data class LoginUser(val id: String, val username: String, val role: String, val avatarUrl: String?)
data class LoginResponse(val access_token: String, val refresh_token: String, val user: LoginUser, val wgName: String)

interface APIService {
    @POST("api/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @POST("api/refresh")
    suspend fun refresh(@Body req: Map<String, String>): Response<Map<String, String>>
}