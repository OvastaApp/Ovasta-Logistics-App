package com.ovasta.logisticsapp.presentation.home.data

import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.presentation.home.data.model.OrderSteps

class HomeRepository(
    private val homeFirebaseRemoteDataSource: IHomeFirebaseRemoteDataSource,
    private val homeServerRemoteDataSource: IHomeServerRemoteDataSource,
    val settingsRepository: ISettingsRepository,
    private val trackingKeepAlive: TrackingKeepAlive,
) : IHomeRepository {
    override suspend fun getAssignedOrders(
        userId: Int,
        userType: Int,
        districtId: Int
    ) = homeFirebaseRemoteDataSource.getAssignedOrders(userId, userType, districtId)

    override suspend fun listenToNewDeliveryTasks(
        userId: Int,
        districtId: Int
    ) = homeFirebaseRemoteDataSource.listenToNewDeliveryTasks(
        userId = userId,
        districtId = districtId
    )

    override suspend fun startLocationTracking() = trackingKeepAlive.start()

    override suspend fun stopLocationTracking() = trackingKeepAlive.stop()

    override suspend fun sendLocation(lat: Double, long: Double) {
        with(settingsRepository.getUseData()) {
            homeFirebaseRemoteDataSource.logLocation(
                userId = this?.deliveryId ?: 0,
                districtId = this?.districtId ?: 0,
                lat,
                long
            )
        }
    }

    override suspend fun changePartnerStatus(isOnline: Boolean, lat: Double?, long: Double?) =
        homeServerRemoteDataSource.changePartnerStatus(isOnline = isOnline, lat = lat, long = long)

    override suspend fun getPartnerStatus() = homeServerRemoteDataSource.getPartnerStatus()

    override suspend fun getPartnerStatistics(
        month: Int,
        year: Int,
    ) = homeServerRemoteDataSource.getPartnerStatistics(month, year)

    override suspend fun getHome() = homeServerRemoteDataSource.getHome()

    override suspend fun getAssignedDeliveryOrders() =
        homeServerRemoteDataSource.getAssignedDeliveryOrders()

    override suspend fun startSalesOrder(orderId: Int) =
        homeServerRemoteDataSource.startSalesOrder(orderId)

    override suspend fun changeOrderStatus(orderId: Int, status: OrderSteps) =
        homeServerRemoteDataSource.changeOrderStatus(orderId, status)

    override suspend fun createDeliveryOrder(
        deliveryPrice: Double,
        receiverMobile: String?,
        toAddress: String?,
        note: String?,
    ) {
        homeServerRemoteDataSource.createDeliveryOrder(
            deliveryPrice = deliveryPrice,
            receiverMobile = receiverMobile,
            toAddress = toAddress,
            note = note,
        )
    }

    override suspend fun updateDeliveryOrderFees(orderId: Int, deliveryFees: Double) =
        homeServerRemoteDataSource.updateDeliveryOrderFees(orderId, deliveryFees)

}