package com.ovasta.logisticsapp.presentation.profile.presentation.charge

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.CenteredTextAppBar
import com.ovasta.logisticsapp.base.Error500
import com.ovasta.logisticsapp.base.Gray100
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray600
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.StatusCanceled
import com.ovasta.logisticsapp.base.StatusDelivered
import com.ovasta.logisticsapp.base.StatusPending
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseScreen
import com.ovasta.logisticsapp.base.components.sharedComposable.LocalNavigator
import com.ovasta.logisticsapp.base.ext.createImageCaptureUri
import com.ovasta.logisticsapp.base.lgSemiBold
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.base.xsRegular
import com.ovasta.logisticsapp.presentation.profile.data.model.TopUpRequest
import com.ovasta.logisticsapp.presentation.profile.data.model.TopUpStatus
import com.ovasta.logisticsapp.presentation.profile.presentation.components.CreatedAtLines
import com.ovasta.logisticsapp.presentation.profile.presentation.components.EmptyListMessage
import com.ovasta.logisticsapp.presentation.profile.presentation.components.ListSectionHeader
import com.ovasta.logisticsapp.presentation.profile.presentation.components.LoadMoreEffect
import com.ovasta.logisticsapp.presentation.profile.presentation.components.LoadingMoreIndicator
import com.ovasta.logisticsapp.presentation.profile.presentation.components.NewTopUpBottomSheet
import com.ovasta.logisticsapp.presentation.profile.presentation.components.formatAmount
import com.ovasta.logisticsapp.presentation.profile.presentation.components.walletCardSurface

@Composable
fun ChargeScreen(viewModel: ChargeViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    val navigator = LocalNavigator.current
    val context = LocalContext.current

    // The new top-up form is saveable, like the camera file below, because the camera / gallery
    // app can push this screen out of memory
    var isTopUpSheetVisible by rememberSaveable { mutableStateOf(false) }
    var amountText by rememberSaveable { mutableStateOf("") }
    var proofImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    // File the camera app writes the captured photo into
    var cameraImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    fun closeTopUpSheet() {
        isTopUpSheetVisible = false
        amountText = ""
        proofImageUri = null
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { image ->
            if (image != null) proofImageUri = image
        }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
            if (saved) proofImageUri = cameraImageUri
        }

    // The ViewModel outlives the screen, so refresh on every visit
    LaunchedEffect(Unit) {
        viewModel.loadTopUpRequests()
    }

    LaunchedEffect(viewState.isTopUpSent) {
        if (viewState.isTopUpSent) {
            closeTopUpSheet()
            viewModel.onTopUpSentHandled()
        }
    }

    BaseScreen(viewModel = viewModel) {
        ChargeContent(
            viewState = viewState,
            onBackClick = { navigator.pop() },
            onRefresh = viewModel::loadTopUpRequests,
            onLoadMore = viewModel::loadNextPage,
            onNewTopUpClick = { isTopUpSheetVisible = true }
        )

        if (isTopUpSheetVisible) {
            NewTopUpBottomSheet(
                amountText = amountText,
                onAmountChange = { amountText = it },
                proofImage = proofImageUri,
                onCameraClick = {
                    val captureUri = context.createImageCaptureUri()
                    cameraImageUri = captureUri
                    // No camera app on the device: nothing gets attached
                    runCatching { cameraLauncher.launch(captureUri) }
                },
                onGalleryClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemoveImage = { proofImageUri = null },
                onSubmit = viewModel::createTopUpRequest,
                onDismiss = { closeTopUpSheet() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargeContent(
    viewState: ChargeViewState,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onNewTopUpClick: () -> Unit
) {
    val listState = rememberLazyListState()
    LoadMoreEffect(
        listState = listState,
        itemCount = viewState.topUpRequests?.size ?: 0,
        hasNextPage = viewState.hasNextPage,
        onLoadMore = onLoadMore
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenteredTextAppBar(
                title = stringResource(R.string.charge),
                onBackButtonPressed = onBackClick
            )
        },
        bottomBar = { NewTopUpBar(onClick = onNewTopUpClick) }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = viewState.isLoading,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Gray100)
        ) {
            val requests = viewState.topUpRequests
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
            ) {
                // Before the first load returns only the refresh indicator shows
                if (requests != null) {
                    item(key = "header") {
                        ListSectionHeader(
                            title = stringResource(R.string.top_up_requests),
                            count = viewState.totalCount
                        )
                    }

                    if (requests.isEmpty()) {
                        item(key = "empty") {
                            EmptyListMessage(message = stringResource(R.string.charge_placeholder))
                        }
                    } else {
                        items(requests) { request -> TopUpRequestCard(request = request) }

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
private fun NewTopUpBar(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Base_white)
    ) {
        HorizontalDivider(
            thickness = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
            color = Gray200
        )
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(com.intuit.sdp.R.dimen._16sdp)),
            shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            contentPadding = PaddingValues(
                vertical = dimensionResource(com.intuit.sdp.R.dimen._10sdp),
                horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp)
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null,
                tint = Base_white,
                modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._18sdp))
            )
            Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._6sdp)))
            Text(
                text = stringResource(R.string.new_top_up),
                style = mdMedium.copy(color = Base_white)
            )
        }
    }
}

@Composable
private fun TopUpRequestCard(request: TopUpRequest) {
    val status = TopUpStatus.fromId(request.status)
    Column(
        modifier = Modifier
            .walletCardSurface()
            .padding(dimensionResource(com.intuit.sdp.R.dimen._12sdp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.price_currency, formatAmount(request.amount ?: 0.0)),
                style = lgSemiBold.copy(color = Primary)
            )
            if (status != null) TopUpStatusTag(status = status)
        }

        request.createdAt?.let { createdAt ->
            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
            CreatedAtLines(createdAt = createdAt)
        }

        // The reviewer approved a different amount than requested
        request.adjustedAmount?.takeIf { status != TopUpStatus.REJECTED }?.let { adjustedAmount ->
            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
            Text(
                text = stringResource(
                    R.string.approved_amount,
                    stringResource(R.string.price_currency, formatAmount(adjustedAmount))
                ),
                style = xsMedium.copy(color = StatusDelivered)
            )
            request.adjustmentReason?.takeIf { it.isNotBlank() }?.let { reason ->
                Text(text = reason, style = xsMedium.copy(color = Gray600))
            }
        }

        if (status == TopUpStatus.REJECTED) {
            request.rejectionReason?.takeIf { it.isNotBlank() }?.let { reason ->
                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
                Text(
                    text = stringResource(R.string.rejection_reason, reason),
                    style = xsMedium.copy(color = Error500)
                )
            }
        }
    }
}

@Composable
private fun TopUpStatusTag(status: TopUpStatus) {
    val color = when (status) {
        TopUpStatus.PENDING -> StatusPending
        TopUpStatus.APPROVED -> StatusDelivered
        TopUpStatus.REJECTED -> StatusCanceled
    }
    Text(
        text = stringResource(status.labelRes),
        style = xsMedium.copy(color = color),
        modifier = Modifier
            .background(
                color.copy(alpha = 0.1f),
                RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._16sdp))
            )
            .padding(
                horizontal = dimensionResource(com.intuit.sdp.R.dimen._10sdp),
                vertical = dimensionResource(com.intuit.sdp.R.dimen._4sdp)
            )
    )
}

@Preview(showSystemUi = true, showBackground = true, locale = "ar")
@Composable
fun ChargeContentPreview() {
    ChargeContent(
        viewState = ChargeViewState(
            totalCount = 3,
            topUpRequests = listOf(
                TopUpRequest(
                    id = 1,
                    amount = 100.5,
                    status = TopUpStatus.PENDING.id,
                    createdAt = "2026-10-31T10:15:00.000000Z"
                ),
                TopUpRequest(
                    id = 2,
                    amount = 500.0,
                    adjustedAmount = 450.0,
                    adjustmentReason = "Transfer fees deducted",
                    status = TopUpStatus.APPROVED.id,
                    createdAt = "2026-10-28 18:40:00"
                ),
                TopUpRequest(
                    id = 3,
                    amount = 250.0,
                    status = TopUpStatus.REJECTED.id,
                    rejectionReason = "The transfer receipt is not clear",
                    createdAt = "2026-10-25 12:00:00"
                ),
            )
        ),
        onBackClick = {},
        onRefresh = {},
        onLoadMore = {},
        onNewTopUpClick = {}
    )
}
