package com.ovasta.logisticsapp.presentation.profile.data.model

import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.top_up_status_approved
import com.ovasta.logisticsapp.shared.resources.top_up_status_pending
import com.ovasta.logisticsapp.shared.resources.top_up_status_rejected
import org.jetbrains.compose.resources.StringResource

/** The [TopUpRequest.status] ids sent by the backend. */
enum class TopUpStatus(val id: Int, val labelRes: StringResource) {
    PENDING(1, Res.string.top_up_status_pending),
    APPROVED(2, Res.string.top_up_status_approved),
    REJECTED(3, Res.string.top_up_status_rejected);

    companion object {
        fun fromId(id: Int?): TopUpStatus? = entries.firstOrNull { it.id == id }
    }
}
