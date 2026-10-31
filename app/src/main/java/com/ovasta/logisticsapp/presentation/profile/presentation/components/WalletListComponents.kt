package com.ovasta.logisticsapp.presentation.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.ext.formatDecimal
import com.ovasta.logisticsapp.base.ext.toDisplayDate
import com.ovasta.logisticsapp.base.ext.toDisplayTime
import com.ovasta.logisticsapp.base.mdSemiBold
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.base.xsRegular

/** Shared card look for the wallet and charge list rows (matches the order details cards). */
@Composable
fun Modifier.walletCardSurface(): Modifier {
    val shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
    return this
        .fillMaxWidth()
        .clip(shape)
        .background(Base_white, shape)
        .border(
            width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
            color = Gray200,
            shape = shape
        )
}

/** List title followed by a count pill, shown above the wallet transactions / top-up requests. */
@Composable
fun ListSectionHeader(title: String, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, style = mdSemiBold)
        if (count > 0) {
            Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._6sdp)))
            Text(
                text = count.toString(),
                style = xsMedium.copy(color = Primary),
                modifier = Modifier
                    .background(
                        Primary.copy(alpha = 0.1f),
                        RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
                    )
                    .padding(
                        horizontal = dimensionResource(com.intuit.sdp.R.dimen._6sdp),
                        vertical = dimensionResource(com.intuit.sdp.R.dimen._2sdp)
                    )
            )
        }
    }
}

@Composable
fun EmptyListMessage(message: String) {
    Column(
        modifier = Modifier
            .walletCardSurface()
            .padding(dimensionResource(com.intuit.sdp.R.dimen._24sdp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_price),
            contentDescription = null,
            tint = Gray500,
            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._28sdp))
        )
        Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
        Text(
            text = message,
            style = smNormal.copy(color = Gray500),
            textAlign = TextAlign.Center
        )
    }
}

/** A server [createdAt] date shown as the day ("31/10/2026") with the time ("02:05 PM") under it. */
@Composable
fun CreatedAtLines(createdAt: String) {
    Text(text = createdAt.toDisplayDate(), style = xsRegular.copy(color = Gray500))
    createdAt.toDisplayTime()?.let { time ->
        Text(text = time, style = xsRegular.copy(color = Gray500))
    }
}

// How many items from the end of the list the next page starts loading
private const val LOAD_MORE_THRESHOLD = 3

/**
 * Calls [onLoadMore] once the list in [listState] is scrolled near its end while [hasNextPage].
 * Also re-checked whenever [itemCount] changes, in case a page still doesn't fill the screen.
 */
@Composable
fun LoadMoreEffect(
    listState: LazyListState,
    itemCount: Int,
    hasNextPage: Boolean,
    onLoadMore: () -> Unit
) {
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)
    val isNearEnd by remember(listState) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex >= layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD
        }
    }
    LaunchedEffect(isNearEnd, itemCount, hasNextPage) {
        if (isNearEnd && hasNextPage) currentOnLoadMore()
    }
}

/** Small spinner shown as the last list item while the next page loads. */
@Composable
fun LoadingMoreIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Primary,
            strokeWidth = dimensionResource(com.intuit.sdp.R.dimen._2sdp),
            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._24sdp))
        )
    }
}

/**
 * Formats [amount] with up to 2 decimals (250.0 -> "250", -20.5 -> "-20.5"); [showPlus] adds a
 * "+" to positive amounts. Wrapped in a left-to-right isolate so the sign stays in front of the
 * number inside Arabic text.
 */
fun formatAmount(amount: Double, showPlus: Boolean = false): String {
    val sign = if (showPlus && amount > 0.0) "+" else ""
    return "⁦$sign${amount.formatDecimal(maxDecimals = 2)}⁩"
}
