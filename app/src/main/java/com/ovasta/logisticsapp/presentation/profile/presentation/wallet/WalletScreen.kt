package com.ovasta.logisticsapp.presentation.profile.presentation.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.CenteredTextAppBar
import com.ovasta.logisticsapp.base.Gray100
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.StatusCanceled
import com.ovasta.logisticsapp.base.StatusDelivered
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseScreen
import com.ovasta.logisticsapp.base.components.sharedComposable.LocalNavigator
import com.ovasta.logisticsapp.base.smMedium
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.base.smSemiBold
import com.ovasta.logisticsapp.base.xlSemiBold
import com.ovasta.logisticsapp.base.xsRegular
import com.ovasta.logisticsapp.presentation.home.presentation.components.WalletWarningCard
import com.ovasta.logisticsapp.presentation.profile.data.model.WalletTransaction
import com.ovasta.logisticsapp.presentation.profile.presentation.components.CreatedAtLines
import com.ovasta.logisticsapp.presentation.profile.presentation.components.EmptyListMessage
import com.ovasta.logisticsapp.presentation.profile.presentation.components.ListSectionHeader
import com.ovasta.logisticsapp.presentation.profile.presentation.components.LoadMoreEffect
import com.ovasta.logisticsapp.presentation.profile.presentation.components.LoadingMoreIndicator
import com.ovasta.logisticsapp.presentation.profile.presentation.components.formatAmount
import com.ovasta.logisticsapp.presentation.profile.presentation.components.walletCardSurface

@Composable
fun WalletScreen(viewModel: WalletViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    val navigator = LocalNavigator.current

    // The ViewModel outlives the screen, so refresh on every visit
    LaunchedEffect(Unit) {
        viewModel.loadWallet()
    }

    BaseScreen(viewModel = viewModel) {
        WalletContent(
            viewState = viewState,
            onBackClick = { navigator.pop() },
            onRefresh = viewModel::loadWallet,
            onLoadMore = viewModel::loadNextPage
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletContent(
    viewState: WalletViewState,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()
    LoadMoreEffect(
        listState = listState,
        itemCount = viewState.transactions?.size ?: 0,
        hasNextPage = viewState.hasNextPage,
        onLoadMore = onLoadMore
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenteredTextAppBar(
                title = stringResource(R.string.wallet),
                onBackButtonPressed = onBackClick
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = viewState.isLoading,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Gray100)
        ) {
            val transactions = viewState.transactions
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
            ) {
                // Before the first load returns only the refresh indicator shows
                if (transactions != null) {
                    item(key = "balance") { BalanceCard(balance = viewState.balance) }

                    viewState.walletWarning?.let { warning ->
                        item(key = "wallet_warning") { WalletWarningCard(message = warning) }
                    }

                    item(key = "transactions_header") {
                        ListSectionHeader(
                            title = stringResource(R.string.wallet_transactions),
                            count = viewState.totalCount
                        )
                    }

                    if (transactions.isEmpty()) {
                        item(key = "empty") {
                            EmptyListMessage(message = stringResource(R.string.wallet_placeholder))
                        }
                    } else {
                        items(transactions) { transaction -> TransactionItem(transaction) }

                        if (viewState.isLoadingMore) {
                            item(key = "loading_more") { LoadingMoreIndicator() }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(balance: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))
            .background(Primary)
            .padding(dimensionResource(com.intuit.sdp.R.dimen._16sdp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_price),
            contentDescription = null,
            tint = Base_white,
            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._28sdp))
        )
        Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
        Text(
            text = stringResource(R.string.wallet_balance),
            style = smNormal.copy(color = Base_white.copy(alpha = 0.85f))
        )
        Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
        Text(
            text = stringResource(R.string.price_currency, formatAmount(balance)),
            style = xlSemiBold.copy(color = Base_white)
        )
    }
}

@Composable
private fun TransactionItem(transaction: WalletTransaction) {
    val isCredit = transaction.isCredit
    val color = if (isCredit) StatusDelivered else StatusCanceled
    val amount = transaction.amount ?: 0.0

    Row(
        modifier = Modifier
            .walletCardSurface()
            .padding(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(com.intuit.sdp.R.dimen._36sdp))
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(if (isCredit) R.drawable.ic_add else R.drawable.ic_minus),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._18sdp))
            )
        }

        Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._10sdp)))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.description?.takeIf { it.isNotBlank() }
                    ?: stringResource(if (isCredit) R.string.wallet_credit else R.string.wallet_debit),
                style = smMedium.copy(color = Gray800),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            transaction.createdAt?.let { createdAt ->
                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._2sdp)))
                CreatedAtLines(createdAt = createdAt)
            }
        }

        Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

        Text(
            text = stringResource(R.string.price_currency, formatAmount(amount, showPlus = true)),
            style = smSemiBold.copy(color = color)
        )
    }
}

@Preview(showSystemUi = true, showBackground = true, locale = "ar")
@Composable
fun WalletContentPreview() {
    WalletContent(
        viewState = WalletViewState(
            balance = 180.0,
            walletWarning = "رصيد المحفظة منخفض، يرجى الشحن لاستقبال طلبات جديدة",
            totalCount = 2,
            transactions = listOf(
                    WalletTransaction(
                        id = 1,
                        type = "topup_credit",
                        amount = 100.0,
                        description = "شحن محفظة - طلب #6",
                        createdAt = "2026-10-31T11:47:17.000000Z"
                    ),
                    WalletTransaction(
                        id = 2,
                        type = "delivery_debit",
                        amount = -120.0,
                        description = "استقطاع طلب توصيل #616",
                        deliveryOrderId = 616,
                        createdAt = "2026-10-31T12:13:04.000000Z"
                    ),
                )
        ),
        onBackClick = {},
        onRefresh = {},
        onLoadMore = {}
    )
}
