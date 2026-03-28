package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.presentation.home.data.model.TaskStatus

@Composable
fun StatusTag(
    statusId: Int,
    statusName: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (statusId) {
        TaskStatus.Cancelled.id -> Pair(Color(0xFFFFF4E6), Color(0xFFFF9800))
        TaskStatus.Completed.id -> Pair(Color(0xFFE8F5E9), Color(0xFF4CAF50))
        TaskStatus.InProgress.id -> Pair(Color(0xFFE3F2FD), Color(0xFF2196F3))
        TaskStatus.Pending.id -> Pair(Color(0xFFFFEBEE), Color(0xFFF44336))
        else -> Pair(Color(0xFFF5F5F5), Color(0xFF9E9E9E))
    }

    Row(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._16sdp))
            )
            .padding(
                horizontal = dimensionResource(com.intuit.sdp.R.dimen._10sdp),
                vertical = dimensionResource(com.intuit.sdp.R.dimen._4sdp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(com.intuit.sdp.R.dimen._6sdp))
                .background(textColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
        Text(
            text = statusName,
            style = xsMedium.copy(color = textColor)
        )
    }
}


@Preview
@Composable
fun StatusTagPreview() {
    StatusTag(
        statusId = TaskStatus.InProgress.id,
        statusName = "In Progress"
    )
}