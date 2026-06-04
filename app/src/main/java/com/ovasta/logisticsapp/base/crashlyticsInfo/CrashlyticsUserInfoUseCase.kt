package com.ovasta.logisticsapp.base.crashlyticsInfo

import com.ovasta.logisticsapp.data.User

class CrashlyticsUserInfoUseCase(
    private val crashlyticsRemoteDataSource: ICrashlyticsInfoRemoteDataSource
) {
    suspend operator fun invoke(user: User?) {
        crashlyticsRemoteDataSource.setUserInfo(user = user)
    }
}