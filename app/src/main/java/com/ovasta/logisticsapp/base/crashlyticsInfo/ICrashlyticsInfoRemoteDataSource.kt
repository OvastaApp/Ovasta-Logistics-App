package com.ovasta.logisticsapp.base.crashlyticsInfo

import com.ovasta.logisticsapp.data.User

interface ICrashlyticsInfoRemoteDataSource {
   suspend fun setUserInfo(user: User?)
}