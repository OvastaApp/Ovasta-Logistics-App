package com.ovasta.logisticsapp.data.setting.data

import com.maxab.basemodule.core.setting.model.Country
import com.maxab.basemodule.core.setting.model.Language
import com.maxab.basemodule.core.setting.model.RemoteConfigModel

interface ISettingsRemoteDataSource {
    suspend fun logout()

}