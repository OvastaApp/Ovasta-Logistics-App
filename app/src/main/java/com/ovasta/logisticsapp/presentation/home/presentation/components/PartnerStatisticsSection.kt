package com.ovasta.logisticsapp.presentation.home.presentation.components

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import com.ovasta.logisticsapp.base.smNormal
import androidx.compose.ui.text.font.FontWeight
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.presentation.home.data.model.Incentives
import com.ovasta.logisticsapp.presentation.home.data.model.Milestones
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import java.text.SimpleDateFormat
import java.util.Locale
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PartnerStatisticsSection(
    statistics: PartnerStatistics,
    selectedMonth: Int = LocalDate.now().monthValue,
    selectedYear: Int = LocalDate.now().year,
    onMonthYearChanged: (month: Int, year: Int) -> Unit = { _, _ -> }
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
                            val netColor = if (netProfit >= 0.0) Color(0xFF4CAF50) else Color(0xFFE57373)
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
                    // Month/Year filter row
                    MonthYearFilter(
                        selectedMonth = selectedMonth,
                        selectedYear = selectedYear,
                        onMonthYearChanged = onMonthYearChanged
                    )

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

                    // Incentives progress card - show milestones from incentives
                    val incentives = statistics.incentives
                    if (incentives != null && incentives.milestones.isNotEmpty()) {
                        IncentivesProgressCard(incentives = incentives)
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MonthYearFilter(
    selectedMonth: Int,
    selectedYear: Int,
    onMonthYearChanged: (month: Int, year: Int) -> Unit
) {
    var monthExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }

    val currentYear = LocalDate.now().year
    val years = (2026..currentYear).toList().reversed()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Month dropdown
        Box(modifier = Modifier.weight(1f)) {
            OutlinedButton(
                onClick = { monthExpanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
            ) {
                Text(
                    text = selectedMonth.toString(),
                    style = xsMedium.copy(color = Gray800),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            DropdownMenu(
                expanded = monthExpanded,
                onDismissRequest = { monthExpanded = false }
            ) {
                (1..12).forEach { month ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = month.toString(),
                                style = xsMedium.copy(
                                    color = if (month == selectedMonth) Primary else Gray800,
                                    fontWeight = if (month == selectedMonth) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        },
                        onClick = {
                            monthExpanded = false
                            onMonthYearChanged(month, selectedYear)
                        }
                    )
                }
            }
        }

        // Year dropdown
        Box(modifier = Modifier.weight(1f)) {
            OutlinedButton(
                onClick = { yearExpanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
            ) {
                Text(
                    text = selectedYear.toString(),
                    style = xsMedium.copy(color = Gray800),
                    maxLines = 1
                )
            }
            DropdownMenu(
                expanded = yearExpanded,
                onDismissRequest = { yearExpanded = false }
            ) {
                years.forEach { year ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = year.toString(),
                                style = xsMedium.copy(
                                    color = if (year == selectedYear) Primary else Gray800,
                                    fontWeight = if (year == selectedYear) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        },
                        onClick = {
                            yearExpanded = false
                            onMonthYearChanged(selectedMonth, year)
                        }
                    )
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
private fun IncentivesProgressCard(
    incentives: Incentives
) {
    val completedOrders = incentives.completedOrders ?: 0

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
                .padding(dimensionResource(com.intuit.sdp.R.dimen._12sdp))
        ) {
            // Header row with icon and title
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
                        modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._18sdp))
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                    Text(
                        text = if (!incentives.month.isNullOrBlank()) {
                            stringResource(R.string.incentives) + " (" + formatMonthDisplay(incentives.month!!) + ")"
                        } else {
                            stringResource(R.string.incentives)
                        },
                        style = smNormal.copy(color = Gray800, fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Show completed orders count
                Text(
                    text = String.format(
                        Locale.ENGLISH,
                        "%,d",
                        completedOrders
                    ) + " " + stringResource(R.string.orders),
                    style = xsMedium.copy(color = Primary, fontWeight = FontWeight.SemiBold),
                    maxLines = 1
                )
            }


            // Current bonus info
            val bonusPercentage = incentives.currentBonusPercentage ?: 0.0
            val bonusAmount = incentives.currentBonusAmount ?: 0.0
            if (bonusPercentage > 0.0 || bonusAmount > 0.0) {
                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.current_bonus),
                        style = xsMedium.copy(color = Gray500),
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (bonusPercentage > 0.0) {
                            Text(
                                text = String.format(Locale.ENGLISH, stringResource(R.string.bonus_percentage), bonusPercentage.toInt()),
                                style = xsMedium.copy(color = Color(0xFF4CAF50), fontWeight = FontWeight.SemiBold),
                                maxLines = 1
                            )
                        }
                        if (bonusPercentage > 0.0 && bonusAmount > 0.0) {
                            Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
                            Text(
                                text = "•",
                                style = xsMedium.copy(color = Gray500)
                            )
                            Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
                        }
                        if (bonusAmount > 0.0) {
                            Text(
                                text = String.format(
                                    stringResource(R.string.price_currency),
                                    formatAmount(bonusAmount)
                                ),
                                style = xsMedium.copy(color = Color(0xFF4CAF50), fontWeight = FontWeight.SemiBold),
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Milestones list - each with its own progress
            if (incentives.milestones.isNotEmpty()) {
                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._6sdp))
                ) {
                    incentives.milestones.forEach { milestone ->
                        MilestoneRow(
                            milestone = milestone,
                            completedOrders = completedOrders
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MilestoneRow(
    milestone: Milestones,
    completedOrders: Int
) {
    val isAchieved = milestone.isAchieved == true
    val targetOrders = milestone.targetOrders ?: 0
    val bonusPercentage = milestone.bonusPercentage ?: 0.0
    val remaining = milestone.remainingOrders ?: 0
    val bonusAmount = milestone.bonusAmount ?: 0.0

    val progress = if (targetOrders > 0) {
        (completedOrders.toFloat() / targetOrders.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600),
        label = "milestoneProgress"
    )

    val progressColor = when {
        isAchieved -> Color(0xFF2E7D32)
        progress >= 0.75f -> Color(0xFF4CAF50)
        progress >= 0.50f -> Color(0xFFFFA726)
        progress >= 0.25f -> Color(0xFFFF7043)
        else -> Color(0xFFEF5350)
    }

    val bgColor = if (isAchieved) Color(0xFFF0F9F0) else Color(0xFFF5F5F5)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
            .background(bgColor)
            .padding(
                horizontal = dimensionResource(com.intuit.sdp.R.dimen._10sdp),
                vertical = dimensionResource(com.intuit.sdp.R.dimen._8sdp)
            )
    ) {
        // Top row: target info and status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Target orders and bonus percentage
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = String.format(
                        Locale.ENGLISH,
                        stringResource(R.string.milestone_target),
                        targetOrders,
                        bonusPercentage.toInt()
                    ),
                    style = xsMedium.copy(
                        color = if (isAchieved) Color(0xFF2E7D32) else Gray800,
                        fontWeight = if (isAchieved) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
            // Status badge
            Text(
                text = if (isAchieved) {
                    stringResource(R.string.achieved)
                } else {
                    String.format(Locale.ENGLISH, stringResource(R.string.remaining_orders), remaining)
                },
                style = xsMedium.copy(
                    color = progressColor,
                    fontWeight = if (isAchieved) FontWeight.SemiBold else FontWeight.Normal
                ),
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._6sdp)))

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(com.intuit.sdp.R.dimen._4sdp))
                .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._2sdp)))
                .background(progressColor.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._2sdp)))
                    .background(progressColor)
            )
        }

        // Bottom row: progress count and bonus amount
        Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._3sdp)))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = String.format(
                    Locale.ENGLISH,
                    stringResource(R.string.orders_progress_count),
                    completedOrders.coerceAtMost(targetOrders),
                    targetOrders
                ),
                style = xsMedium.copy(
                    color = progressColor,
                    fontWeight = FontWeight.Normal
                ),
                maxLines = 1
            )
            if (bonusAmount > 0.0) {
                Text(
                    text = String.format(
                        stringResource(R.string.milestone_bonus),
                        String.format(
                            stringResource(R.string.price_currency),
                            formatAmount(bonusAmount)
                        )
                    ),
                    style = xsMedium.copy(
                        color = if (isAchieved) Color(0xFF2E7D32) else Color(0xFFFFA726),
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

private fun formatMonthDisplay(monthString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("MMMM yyyy", Locale("ar"))
        val date = inputFormat.parse(monthString) ?: return monthString
        outputFormat.format(date)
    } catch (_: Exception) {
        monthString
    }
}

private fun formatAmount(value: Double?): String {
    if (value == null) return "0"
    return if (value == value.toLong().toDouble()) {
        String.format(Locale.ENGLISH, "%,d", value.toLong())
    } else {
        String.format(Locale.ENGLISH, "%,.2f", value)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, locale = "ar")
@Composable
fun PartnerStatisticsSectionPreview() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        PartnerStatisticsSection(
            statistics = PartnerStatistics(
                withdrawTransactionsSum = 1500.0,
                deliveryProfitSum = 750.0,
                ordersCount = 45,
                incentives = Incentives(
                    month = "2026-04",
                    completedOrders = 45,
                    totalDeliveryValue = 5000.0,
                    highestAchievedMilestone = "silver",
                    currentBonusPercentage = 5.0,
                    currentBonusAmount = 250.0,
                    milestones = arrayListOf(
                        Milestones(
                            targetOrders = 30,
                            bonusPercentage = 3.0,
                            realPercentage = 3.0,
                            isAchieved = true,
                            remainingOrders = 0,
                            progressPercentage = 100.0,
                            bonusAmount = 150.0
                        ),
                        Milestones(
                            targetOrders = 50,
                            bonusPercentage = 5.0,
                            realPercentage = 5.0,
                            isAchieved = false,
                            remainingOrders = 5,
                            progressPercentage = 90.0,
                            bonusAmount = 350.0
                        ),
                        Milestones(
                            targetOrders = 100,
                            bonusPercentage = 10.0,
                            realPercentage = 10.0,
                            isAchieved = false,
                            remainingOrders = 55,
                            progressPercentage = 45.0,
                            bonusAmount = 800.0
                        )
                    )
                )
            ),
            selectedMonth = 4,
            selectedYear = 2026
        )
    }
}