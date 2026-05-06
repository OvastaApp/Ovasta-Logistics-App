package com.ovasta.logisticsapp.presentation.home.data

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_DISTRICT_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_ONLINE_DRIVERS_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_ORDERS_NAME
import com.ovasta.logisticsapp.presentation.home.data.model.ChangeStatusRequest
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.SellerTask
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest

class HomeServerRemoteDataSource(private val homeApi: HomeApi) : IHomeServerRemoteDataSource {

    override suspend fun changePartnerStatus(isOnline: Boolean) {
        val changeStatusRequest = ChangeStatusRequest(isOnline = isOnline)
        homeApi.changePartnerStatus(changeStatusRequest)
    }

    override suspend fun getPartnerStatus() = homeApi.getPartnerStatus()

    override suspend fun getPartnerStatistics(
        month: Int,
        year: Int,
    ) = homeApi.getPartnerStatistics(month, year)
}