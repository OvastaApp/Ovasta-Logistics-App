package com.ovasta.logisticsapp.base.services

/** Gate for order actions: they may only run while the driver's location is tracked. */
fun interface LocationTrackingGuard {
    /**
     * Returns whether an order action may run now. When it may not, the implementation shows
     * the driver why and how to fix it.
     */
    fun ensureLocationTracking(): Boolean
}
