package com.ovasta.logisticsapp.presentation.auth.login.data

class LoginRepository(
    private val loginRemoteDataSource: ILoginRemoteDataSource,
) : ILoginRepository {

    override suspend fun login(phone: String, password: String, userType: Int, fcmToken: String?) =
        loginRemoteDataSource.login(phone, password, userType, fcmToken)

    override suspend fun authenticateWithFirebase(
        firebaseAuthToken: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        loginRemoteDataSource.authenticateWithFirebase(firebaseAuthToken, onSuccess, onFailure)
    }

}