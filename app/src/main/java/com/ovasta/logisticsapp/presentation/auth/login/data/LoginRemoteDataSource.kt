package com.ovasta.logisticsapp.presentation.auth.login.data

import com.google.firebase.auth.FirebaseAuth
import com.ovasta.logisticsapp.data.User
import com.ovasta.logisticsapp.presentation.auth.login.data.model.LoginRequest

class LoginRemoteDataSource(private val loginApi: LoginApi) :
    ILoginRemoteDataSource {

    override suspend fun login(phone: String, password: String, userType: Int): User {
        val loginData = LoginRequest(mobile = phone, password, userType)
        return loginApi.login(login = loginData).data
    }

    override suspend fun authenticateWithFirebase(
        firebaseAuthToken: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        FirebaseAuth.getInstance().signInWithCustomToken(firebaseAuthToken)
            .addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                onFailure()
            }
    }

}
