package com.ovasta.logisticsapp.presentation.auth.login.data

import com.ovasta.logisticsapp.data.User

interface ILoginRemoteDataSource {
    suspend fun login(phone: String, password: String, userType: Int): User

    suspend fun authenticateWithFirebase(
        firebaseAuthToken: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )
}