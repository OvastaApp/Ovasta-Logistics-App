package com.ovasta.logisticsapp.presentation.nav

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The order a tapped push notification should open. The platform reports the tap (Android:
 * MainActivity from the tap intent's extras; iOS: the AppDelegate's didReceive) and AppNavHost
 * opens TaskDetails once the driver is logged in.
 */
object PendingOrderLink {
    // Keys and values of the push's data payload (see the backend payload in KMP_MIGRATION_PLAN.md)
    const val KEY_TYPE = "type"
    const val KEY_ORDER_ID = "order_id"
    const val TYPE_NEW_ORDER = "new_order"

    private val _orderId = MutableStateFlow<Int?>(null)
    val orderId: StateFlow<Int?> = _orderId.asStateFlow()

    /**
     * A new order isn't the driver's yet, so its tap only opens the app: Home shows its alert
     * sheet from Firestore. Any other push with an order id opens that order.
     */
    fun onNotificationTapped(type: String?, orderId: String?) {
        if (type == TYPE_NEW_ORDER) return
        orderId?.toIntOrNull()?.let { _orderId.value = it }
    }

    fun consume() {
        _orderId.value = null
    }
}
