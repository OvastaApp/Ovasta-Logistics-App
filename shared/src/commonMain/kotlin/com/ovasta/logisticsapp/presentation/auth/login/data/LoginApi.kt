package com.ovasta.logisticsapp.presentation.auth.login.data

import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.data.User
import com.ovasta.logisticsapp.data.remote.postJson
import com.ovasta.logisticsapp.presentation.auth.login.data.model.LoginRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body

class LoginApi(private val client: HttpClient) {

    suspend fun login(login: LoginRequest): ApiResponse<User> =
        client.postJson("login", login).body()
}
