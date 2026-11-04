package com.ovasta.logisticsapp.presentation.auth.login.data

import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.data.User
import com.ovasta.logisticsapp.presentation.auth.login.data.model.LoginRequest
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException

class LoginRemoteDataSource(
    private val loginApi: LoginApi,
    private val auth: FirebaseAuth,
) : ILoginRemoteDataSource {

    override suspend fun login(phone: String, password: String, userType: Int, fcmToken: String?):  ApiResponse<User> {
        val loginData = LoginRequest(mobile = phone, password, userType, fcmToken = fcmToken)
        return loginApi.login(login = loginData)
    }

    override suspend fun authenticateWithFirebase(
        firebaseAuthToken: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            auth.signInWithCustomToken(firebaseAuthToken)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            onFailure()
            return
        }
        onSuccess()
    }
}
