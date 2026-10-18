package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Error600
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.mdMedium
import kotlinx.coroutines.delay

private const val LATE_ORDER_MINUTES = 30

/** Live count of the minutes the order has been with the agent; turns red once it is late. */
@Composable
fun AssignedElapsedTime(assignedAtMillis: Long, modifier: Modifier = Modifier) {
    // Clamp at zero in case the device clock is behind the server time
    fun elapsedMillis() = (System.currentTimeMillis() - assignedAtMillis).coerceAtLeast(0)

    val elapsedMinutes by produceState(elapsedMillis() / 60_000, assignedAtMillis) {
        while (true) {
            val elapsed = elapsedMillis()
            value = elapsed / 60_000
            // Wake up right when the next minute is reached
            delay(60_000 - elapsed % 60_000)
        }
    }
    val isLate = elapsedMinutes > LATE_ORDER_MINUTES

    InfoRow(
        icon = R.drawable.ic_history,
        label = stringResource(R.string.order_minutes_with_you, elapsedMinutes.toString()),
        textStyle = mdMedium.copy(color = if (isLate) Error600 else Gray800),
        iconTint = if (isLate) Error600 else Gray500,
        modifier = modifier
    )
}
