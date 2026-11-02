package com.ovasta.logisticsapp.data.setting.model

enum class OrderStatusType(val status: Int) {
    NEW(1),
    IN_PROGRESS(2),
    READY(3),
    PENDING(4),
    ON_THE_WAY(5),
    DELIVERED(6),
    FAILED(7),
    DELAYED(8),
    PARTIAL_DELIVERED(9),
    CANCELED(12)
}