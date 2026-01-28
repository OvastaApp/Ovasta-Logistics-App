package com.ovasta.logisticsapp.presentation.auth.login.data

import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.data.User
import com.ovasta.logisticsapp.presentation.auth.login.data.model.LoginRequest
import retrofit2.http.*

interface LoginApi {
    @POST("login")
    suspend fun login(
        @Body login: LoginRequest
    ): ApiResponse<User>
}