package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.smNormal
import androidx.compose.ui.text.font.FontWeight
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import java.util.Locale

@Composable
fun PartnerStatisticsSection(
    statistics: PartnerStatistics
) {
    var isExpanded by remember { mutableStateOf(true) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "expandIconRotation"
    )

    Card(
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
        colors = CardDefaults.cardColors(containerColor = Base_white),
        border = BorderStroke(
            width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
            color = Gray200
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(com.intuit.sdp.R.dimen._12sdp))
        ) {
            // Header - Always visible
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_price),
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._18sdp))
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                    Column {
                        Text(
                            text = stringResource(R.string.statistics),
                            style = smNormal.copy(color = Gray800, fontWeight = FontWeight.SemiBold)
                        )
                        // Collapsed summary - show net profit (profit - withdrawals)
                        if (!isExpanded) {
                            val netProfit = (statistics.deliveryProfitSum ?: 0.0) - (statistics.withdrawTransactionsSum ?: 0.0)
                            val netColor = if (netProfit >= 0) Color(0xFF4CAF50) else Color(0xFFE57373)
                            Text(
                                text = String.format(
                                    stringResource(R.string.price_currency),
                                    formatAmount(netProfit)
                                ),
                                style = xsMedium.copy(color = netColor)
                            )
                        }
                    }
                }

                Icon(
                    painter = painterResource(R.drawable.ic_black_arrow_down),
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = Gray500,
                    modifier = Modifier
                        .size(dimensionResource(com.intuit.sdp.R.dimen._20sdp))
                        .rotate(rotationAngle)
                )
            }

            // Expandable content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = tween(300),
                    expandFrom = Alignment.Top
                ) + fadeIn(animationSpec = tween(300)),
                exit = shrinkVertically(
                    animationSpec = tween(300),
                    shrinkTowards = Alignment.Top
                ) + fadeOut(animationSpec = tween(300))
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                    modifier = Modifier.padding(top = dimensionResource(com.intuit.sdp.R.dimen._12sdp))
                ) {
                    // Row with Delivery Profit and Withdrawals side by side
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = R.drawable.ic_delivery_fees,
                            iconTint = Color(0xFF4CAF50),
                            label = stringResource(R.string.delivery_profit),
                            value = String.format(
                                stringResource(R.string.price_currency),
                                formatAmount(statistics.deliveryProfitSum)
                            ),
                            containerColor = Color(0xFFF0F9F0),
                            borderColor = Color(0xFFC8E6C9)
                        )

                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = R.drawable.ic_price,
                            iconTint = Color(0xFFE57373),
                            label = stringResource(R.string.total_withdrawals),
                            value = String.format(
                                stringResource(R.string.price_currency),
                                formatAmount(statistics.withdrawTransactionsSum)
                            ),
                            containerColor = Color(0xFFFFF8F0),
                            borderColor = Color(0xFFFFE0B2)
                        )
                    }

                    // Orders progress card - only show when target is set
                    val targetCount = statistics.targetOrdersCount ?: 0
                    if (targetCount > 0) {
                        OrdersProgressCard(
                            ordersCount = statistics.ordersCount ?: 0,
                            targetOrdersCount = targetCount,
                            targetEndDate = statistics.targetEndDate
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: Int,
    iconTint: Color,
    label: String,
    value: String,
    containerColor: Color,
    borderColor: Color
) {
    Card(
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(
            width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
            color = borderColor
        ),
        modifier = modifier.fillMaxHeight()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(com.intuit.sdp.R.dimen._10sdp),
                    vertical = dimensionResource(com.intuit.sdp.R.dimen._8sdp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._18sdp))
            )
            Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = xsMedium.copy(color = Gray500),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = value,
                    style = smNormal.copy(color = Gray800),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun OrdersProgressCard(
    ordersCount: Int,
    targetOrdersCount: Int,
    targetEndDate: String?
) {
    val progress = if (targetOrdersCount > 0) {
        (ordersCount.toFloat() / targetOrdersCount.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800),
        label = "progressAnimation"
    )

    val progressColor = when {
        progress >= 1.0f -> Color(0xFF2E7D32)   // Dark green - target achieved
        progress >= 0.75f -> Color(0xFF4CAF50)   // Green - almost there
        progress >= 0.50f -> Color(0xFFFFA726)   // Orange - halfway
        progress >= 0.25f -> Color(0xFFFF7043)   // Deep orange - still early
        else -> Color(0xFFEF5350)                // Red - just started
    }

    Card(
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
        colors = CardDefaults.cardColors(containerColor = Base_white),
        border = BorderStroke(
            width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
            color = Gray200
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(com.intuit.sdp.R.dimen._16sdp))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delivery_truck),
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._20sdp))
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                    Text(
                        text = stringResource(R.string.orders_progress),
                        style = mdMedium.copy(color = Gray800),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                Text(
                    text = String.format(
                        Locale.ENGLISH,
                        stringResource(R.string.orders_progress_count),
                        ordersCount,
                        targetOrdersCount
                    ),
                    style = smNormal.copy(color = progressColor),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
                    .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
                    .background(progressColor.copy(alpha = 0.15f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
                        .background(progressColor)
                )
            }

            if (!targetEndDate.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                Text(
                    text = stringResource(R.string.target_end_date, targetEndDate),
                    style = xsMedium.copy(color = Gray500),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun formatAmount(value: Double?): String {
    if (value == null) return "0"
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        String.format(Locale.US, "%.2f", value)
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun PartnerStatisticsSectionPreview() {
    PartnerStatisticsSection(
        statistics = PartnerStatistics(
            withdrawTransactionsSum = 1500.0,
            deliveryProfitSum = 750.0,
            ordersCount = 45,
            targetOrdersCount = 100,
            targetEndDate = "2024-12-31"
        )
    )
}
