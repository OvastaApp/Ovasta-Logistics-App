package com.ovasta.logisticsapp.data

import org.koin.core.component.KoinComponent

object FirebaseConstants : KoinComponent {
    const val FIRESTORE_ROOT_ORDERS_NAME: String = "orders"
    const val FIRESTORE_ROOT_ONLINE_DRIVERS_NAME: String = "online_drivers"
    const val FIRESTORE_ROOT_DELIVERY_ORDERS_NAME: String = "delivery_orders"
    const val FIRESTORE_COURIER_ID_NAME: String = "courier_id"
    const val FIRESTORE_ROOT_DISTRICT_NAME: String = "districts"
    const val FIRESTORE_PRODUCTS_NAME: String = "products"

}