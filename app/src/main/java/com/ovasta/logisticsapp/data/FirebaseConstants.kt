package com.ovasta.logisticsapp.data

import org.koin.core.component.KoinComponent

object FirebaseConstants : KoinComponent {
    const val FIRESTORE_ROOT_ORDERS_NAME: String = "orders_eg"
    const val FIRESTORE_ROOT_WORKERS_NAME: String = "workers"
    const val FIRESTORE_WORKER_ORDERS_NAME: String = "orders"
    const val FIRESTORE_PRODUCTS_NAME: String = "products"

}