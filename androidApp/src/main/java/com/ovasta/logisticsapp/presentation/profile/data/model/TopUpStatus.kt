package com.ovasta.logisticsapp.presentation.profile.data.model

import androidx.annotation.StringRes
import com.ovasta.logisticsapp.R

/** The [TopUpRequest.status] ids sent by the backend. */
enum class TopUpStatus(val id: Int, @StringRes val labelRes: Int) {
    PENDING(1, R.string.top_up_status_pending),
    APPROVED(2, R.string.top_up_status_approved),
    REJECTED(3, R.string.top_up_status_rejected);

    companion object {
        fun fromId(id: Int?): TopUpStatus? = entries.firstOrNull { it.id == id }
    }
}
