package com.ovasta.logisticsapp.base.services

import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.presentation.home.data.DeviceLocation
import com.ovasta.logisticsapp.presentation.home.data.GeoPoint
import com.ovasta.logisticsapp.presentation.home.data.IHomeRepository
import com.ovasta.logisticsapp.presentation.home.data.model.AssignedDeliveryTask
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask
import com.ovasta.logisticsapp.presentation.home.data.model.HomeData
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.OrderSteps
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatus
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonElement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LocationTrackerTest {

    /** Records sendLocation calls; [failNext] makes writes throw, [hangNext] makes them never finish. */
    private class FakeHomeRepository : IHomeRepository {
        val sent = mutableListOf<GeoPoint>()
        var failNext = 0
        var hangNext = 0

        override suspend fun sendLocation(lat: Double, long: Double) {
            if (hangNext > 0) { hangNext--; awaitCancellation() }
            if (failNext > 0) { failNext--; error("offline") }
            sent += GeoPoint(lat, long)
        }

        override suspend fun getAssignedOrders(userId: Int, userType: Int, districtId: Int): Flow<List<HomeTask>> = TODO()
        override suspend fun listenToNewDeliveryTasks(userId: Int, districtId: Int): Flow<List<DeliveryTask>> = TODO()
        override suspend fun startLocationTracking() = TODO()
        override suspend fun stopLocationTracking() = TODO()
        override suspend fun changePartnerStatus(isOnline: Boolean, lat: Double?, long: Double?) = TODO()
        override suspend fun getPartnerStatus(): ApiResponse<PartnerStatus> = TODO()
        override suspend fun getPartnerStatistics(month: Int, year: Int): ApiResponse<PartnerStatistics> = TODO()
        override suspend fun getHome(): ApiResponse<HomeData?> = TODO()
        override suspend fun getAssignedDeliveryOrders(): List<AssignedDeliveryTask> = TODO()
        override suspend fun startSalesOrder(orderId: Int) = TODO()
        override suspend fun changeOrderStatus(orderId: Int, status: OrderSteps): ApiResponse<JsonElement> = TODO()
        override suspend fun createDeliveryOrder(deliveryPrice: Double, receiverMobile: String?, toAddress: String?, note: String?) = TODO()
        override suspend fun updateDeliveryOrderFees(orderId: Int, deliveryFees: Double) = TODO()
    }

    private val deviceLocation = object : DeviceLocation {
        override suspend fun currentLocation(): GeoPoint? = null
        override fun isLocationEnabled() = true
        override fun hasLocationPermission() = true
    }

    private val repository = FakeHomeRepository()
    private val monitor = LocationTrackingMonitor(deviceLocation)
    private val tracker = LocationTracker(repository, monitor)

    // About 1.1 m per 0.00001 degree of latitude
    private val start = GeoPoint(30.000000, 31.000000)
    private val fiveMetersNorth = GeoPoint(30.000045, 31.000000)
    private val twentyMetersNorth = GeoPoint(30.000180, 31.000000)

    @Test
    fun `logs the first fix and then only moves of 10 m or more`() = runTest {
        val shown = mutableListOf<GeoPoint>()

        tracker.track(flowOf(start, fiveMetersNorth, twentyMetersNorth)) { shown += it }

        assertEquals(listOf(start, twentyMetersNorth), repository.sent)
        assertEquals(listOf(start, fiveMetersNorth, twentyMetersNorth), shown)
        assertNull(monitor.currentBlockReason())
    }

    @Test
    fun `a failed write is retried on the next fix and blocks order actions meanwhile`() = runTest {
        repository.failNext = 1

        tracker.track(flowOf(start))
        assertEquals(LocationBlockReason.LOGGING_NOT_WORKING, monitor.currentBlockReason())

        // Still nothing logged, so even a fix 5 m away is written
        tracker.track(flowOf(fiveMetersNorth))
        assertEquals(listOf(fiveMetersNorth), repository.sent)
        assertNull(monitor.currentBlockReason())
    }

    @Test
    fun `a write that doesn't finish within the timeout counts as failed`() = runTest {
        repository.hangNext = 1

        tracker.track(flowOf(start, fiveMetersNorth))

        // The hung first write didn't count, so the next fix is written even though it is close
        assertEquals(listOf(fiveMetersNorth), repository.sent)
    }

    @Test
    fun `tracking that never started blocks order actions`() {
        assertEquals(LocationBlockReason.TRACKING_OFF, monitor.currentBlockReason())
    }

    @Test
    fun `haversine distance matches known values`() {
        assertEquals(20.0, distanceMeters(start, twentyMetersNorth), 0.5)
        assertEquals(0.0, distanceMeters(start, start), 0.0)
        // Cairo to Alexandria, about 179 km
        assertEquals(179_000.0, distanceMeters(GeoPoint(30.0444, 31.2357), GeoPoint(31.2001, 29.9187)), 2_000.0)
    }
}
