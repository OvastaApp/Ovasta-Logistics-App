package com.ovasta.logisticsapp.data.setting.data

sealed class FetchRemoteConfigResult {
    object Error : FetchRemoteConfigResult()
    object ConnectionError : FetchRemoteConfigResult()
    class UpdateNeeded(val forceUpdateTitle: String, val forceUpdateMessage: String) :
        FetchRemoteConfigResult()
    object UpToDate : FetchRemoteConfigResult()
}