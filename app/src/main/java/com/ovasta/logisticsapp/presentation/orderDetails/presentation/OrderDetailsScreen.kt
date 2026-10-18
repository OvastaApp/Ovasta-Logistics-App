package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.CenteredTextAppBar
import com.ovasta.logisticsapp.base.Error500
import com.ovasta.logisticsapp.base.Gray100
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray600
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.StatusDelivered
import com.ovasta.logisticsapp.base.StatusPending
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseScreen
import com.ovasta.logisticsapp.base.constants.LocalConstants
import com.ovasta.logisticsapp.base.components.sharedComposable.ConfirmDialog
import com.ovasta.logisticsapp.base.components.sharedComposable.LocalNavigator
import com.ovasta.logisticsapp.base.ext.createImageCaptureUri
import com.ovasta.logisticsapp.base.ext.formatDecimal
import com.ovasta.logisticsapp.base.ext.makePhoneCall
import com.ovasta.logisticsapp.base.ext.navigateToLocationClick
import com.ovasta.logisticsapp.base.ext.orDefault
import com.ovasta.logisticsapp.base.lgSemiBold
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.mdSemiBold
import com.ovasta.logisticsapp.base.smMedium
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.base.smSemiBold
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.presentation.home.presentation.components.StatusTag
import com.ovasta.logisticsapp.presentation.home.data.model.FirebaseProduct
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.OrderSteps
import com.ovasta.logisticsapp.presentation.orderDetails.presentation.components.EditProductBottomSheet
import com.ovasta.logisticsapp.presentation.orderDetails.presentation.components.OptionsBottomSheet
import com.ovasta.logisticsapp.presentation.orderDetails.presentation.components.SheetOption
import com.ovasta.logisticsapp.presentation.orderDetails.presentation.components.PickProductBottomSheet
import com.ovasta.logisticsapp.presentation.orderDetails.presentation.components.ReceiveAmountBottomSheet

@Composable
fun DropOfOrderDetailsScreen(
    viewModel: TaskDetailsViewModel, taskId: Int
) {
    val viewState by viewModel.viewState.collectAsState()
    val navigator = LocalNavigator.current

    // Index of the product currently being edited (null = sheet closed).
    var editingProductIndex by remember { mutableStateOf<Int?>(null) }
    // Index of the product currently being picked (null = sheet closed).
    var pickingProductIndex by remember { mutableStateOf<Int?>(null) }
    // Index of the product pending delete confirmation (null = dialog closed).
    var deletingProductIndex by remember { mutableStateOf<Int?>(null) }
    // Whether the change-status confirmation dialog is showing.
    var showChangeStatusDialog by remember { mutableStateOf(false) }
    // Whether the receive-amount (deliver order) sheet is showing.
    var showReceiveAmountSheet by remember { mutableStateOf(false) }
    // Whether the edit-options (request edit / upload receipt) sheet is showing.
    var showEditOptionsSheet by remember { mutableStateOf(false) }
    // Whether the image-source (camera / gallery) sheet is showing.
    var showImageSourceSheet by remember { mutableStateOf(false) }
    // What the image being picked / captured is for (null = not picking). Saveable, like the
    // camera file below, because the camera / gallery app can push this screen out of memory.
    var pendingImageUpload by rememberSaveable { mutableStateOf<OrderImageUpload?>(null) }
    // File the camera app writes the captured photo into.
    var cameraImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    fun uploadImage(image: Uri) {
        when (pendingImageUpload) {
            OrderImageUpload.EDIT_REQUEST -> viewModel.requestOrderEdit(image)
            OrderImageUpload.RECEIPT -> viewModel.uploadOrderReceipt(image)
            null -> Unit
        }
        pendingImageUpload = null
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { image ->
            if (image != null) uploadImage(image) else pendingImageUpload = null
        }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
            val image = cameraImageUri
            if (saved && image != null) uploadImage(image) else pendingImageUpload = null
        }

    BackHandler(enabled = true, onBack = { navigator.pop() })

    LaunchedEffect(Unit) {
        viewModel.getTaskDetails(taskId = taskId)
    }

    // Once the money is collected and the order marked delivered, leave the details screen.
    LaunchedEffect(viewState.isOrderDelivered) {
        if (viewState.isOrderDelivered) navigator.pop()
    }

    BaseScreen(viewModel = viewModel) {
        OrderDetailsContent(
            viewState = viewState,
            onBackClick = { navigator.pop() },
            onProductClick = { index -> editingProductIndex = index },
            onPickClick = { index -> pickingProductIndex = index },
            onDeleteClick = { index -> deletingProductIndex = index },
            onChangeStatusClick = { showChangeStatusDialog = true },
            onDeliverClick = { showReceiveAmountSheet = true },
            onEditClick = { showEditOptionsSheet = true }
        )

        // Both edit options need an image: pick what it's for, then where it comes from.
        if (showEditOptionsSheet) {
            fun chooseImageFor(upload: OrderImageUpload) {
                pendingImageUpload = upload
                showEditOptionsSheet = false
                showImageSourceSheet = true
            }
            OptionsBottomSheet(
                title = R.string.edit_order,
                options = buildList {
                    // Already editable (edit request approved): nothing left to request
                    if (!viewState.task.isEditable) {
                        add(SheetOption(R.drawable.ic_edit_task, R.string.request_edit) {
                            chooseImageFor(OrderImageUpload.EDIT_REQUEST)
                        })
                    }
                    add(SheetOption(R.drawable.ic_image, R.string.upload_receipt) {
                        chooseImageFor(OrderImageUpload.RECEIPT)
                    })
                },
                onDismiss = { showEditOptionsSheet = false }
            )
        }

        if (showImageSourceSheet) {
            OptionsBottomSheet(
                title = R.string.add_image,
                options = listOf(
                    SheetOption(R.drawable.ic_camera, R.string.camera) {
                        showImageSourceSheet = false
                        val captureUri = context.createImageCaptureUri()
                        cameraImageUri = captureUri
                        // No camera app on the device: drop the pending upload
                        runCatching { cameraLauncher.launch(captureUri) }
                            .onFailure { pendingImageUpload = null }
                    },
                    SheetOption(R.drawable.ic_image, R.string.gallery) {
                        showImageSourceSheet = false
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                ),
                onDismiss = {
                    showImageSourceSheet = false
                    pendingImageUpload = null
                }
            )
        }

        if (showReceiveAmountSheet) {
            ReceiveAmountBottomSheet(
                totalAmount = viewState.task.totalPrice.toDouble(),
                onConfirm = { receivedAmount ->
                    viewModel.deliverOrder(receivedAmount)
                    showReceiveAmountSheet = false
                },
                onDismiss = { showReceiveAmountSheet = false }
            )
        }

        if (showChangeStatusDialog) {
            ConfirmDialog(
                title = stringResource(R.string.change_order_status),
                message = stringResource(R.string.confirm_change_status_message),
                onPrimaryClick = {
                    viewModel.changeOrderStatusToOnTheWay()
                    showChangeStatusDialog = false
                },
                onDismiss = { showChangeStatusDialog = false }
            )
        }

        editingProductIndex?.let { index ->
            val product = viewState.task.products.getOrNull(index)
            if (product != null) {
                EditProductBottomSheet(
                    product = product,
                    availableSources = viewState.availableSources,
                    isSourcesLoading = viewState.isSourcesLoading,
                    onLoadSources = { viewModel.loadProductSources(product) },
                    onSave = { newPrice, newQuantity, newSource ->
                        viewModel.updateProduct(index, newPrice, newQuantity, newSource)
                        editingProductIndex = null
                    },
                    onDismiss = { editingProductIndex = null },
                    // The editable-order API only changes price and quantity
                    showSource = !viewState.task.isEditable
                )
            }
        }

        pickingProductIndex?.let { index ->
            val product = viewState.task.products.getOrNull(index)
            if (product != null) {
                PickProductBottomSheet(
                    product = product,
                    onConfirm = { foundQuantity ->
                        viewModel.markProductPicked(index, foundQuantity)
                        pickingProductIndex = null
                    },
                    onDismiss = { pickingProductIndex = null }
                )
            }
        }

        deletingProductIndex?.let { index ->
            val product = viewState.task.products.getOrNull(index)
            if (product != null) {
                ConfirmDialog(
                    title = stringResource(R.string.delete_product),
                    message = stringResource(
                        R.string.delete_product_message,
                        product.name.orDefault(stringResource(R.string.unknown_product)),
                        (product.quantity ?: 0.0).formatDecimal()
                    ),
                    onPrimaryClick = {
                        viewModel.deleteProduct(index)
                        deletingProductIndex = null
                    },
                    onDismiss = { deletingProductIndex = null }
                )
            }
        }
    }
}

/** What a picked / captured image is uploaded as. */
private enum class OrderImageUpload {
    /** `sales-orders/{id}/allow-edits` — asks for the order to be made editable. */
    EDIT_REQUEST,

    /** `sales-orders/{id}/images` — attaches the receipt to the order. */
    RECEIPT
}

/** Pick-status filter applied to the products list. */
private enum class ProductPickFilter(@StringRes val labelRes: Int) {
    ALL(R.string.filter_all),
    NOT_PICKED(R.string.filter_not_picked),
    PARTIALLY_PICKED(R.string.filter_partially_picked);

    fun matches(product: FirebaseProduct): Boolean = when (this) {
        ALL -> true
        NOT_PICKED -> product.pickedQuantity == null
        PARTIALLY_PICKED -> {
            val picked = product.pickedQuantity
            picked != null && picked < (product.quantity ?: 0.0)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OrderDetailsContent(
    viewState: TaskDetailsViewState,
    onBackClick: () -> Unit,
    onProductClick: (Int) -> Unit,
    onPickClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onChangeStatusClick: () -> Unit,
    onDeliverClick: () -> Unit,
    onEditClick: () -> Unit,
    actionsEnabled: Boolean = LocalConstants.ORDER_ACTIONS_ENABLED,
) {
    val task = viewState.task
    var selectedFilter by rememberSaveable { mutableStateOf(ProductPickFilter.ALL) }
    val listState = rememberLazyListState()

    // List item index of the sticky filter row (0 = order header, 1 = section title).
    val filterHeaderIndex = 2
    // Changing the filter can shrink the list below the current scroll offset, which would make
    // the pinned filter row drift down from the top edge. Snap it back so the filters stay stuck
    // and the newly filtered products start right underneath.
    LaunchedEffect(selectedFilter) {
        if (listState.firstVisibleItemIndex >= filterHeaderIndex) {
            listState.scrollToItem(filterHeaderIndex)
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray100),
        topBar = {
            CenteredTextAppBar(
                title = stringResource(R.string.order_details),
                onBackButtonPressed = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Gray100)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    horizontal = dimensionResource(com.intuit.sdp.R.dimen._12sdp),
                    vertical = dimensionResource(com.intuit.sdp.R.dimen._12sdp)
                ),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
            ) {
                item { OrderHeader(task = task) }

                item { ProductsSectionHeader(count = task.products.size) }

                if (task.products.isEmpty()) {
                    item { EmptyProductsCard() }
                } else {
                    // Pinned to the top edge while the products scroll underneath.
                    // Hidden in the view-only release, so the list always shows ALL products.
                    if (actionsEnabled) {
                        stickyHeader(key = "product_filters") {
                            ProductFilterRow(
                                selected = selectedFilter,
                                onSelect = { selectedFilter = it },
                                modifier = Modifier
                                    .background(Gray100)
                                    .padding(vertical = dimensionResource(com.intuit.sdp.R.dimen._4sdp))
                            )
                        }
                    }

                    // Keep each product's original index so edit / delete / pick actions still
                    // map to the right entry in the flat list, even when filtered or grouped.
                    val filtered = task.products.withIndex()
                        .filter { selectedFilter.matches(it.value) }

                    if (filtered.isEmpty()) {
                        item { EmptyProductsCard(messageRes = R.string.no_products_match_filter) }
                    } else {
                        // Group products by source.
                        val groups = filtered
                            .groupBy { it.value.source?.takeIf { s -> s.isNotBlank() } }
                            .toList()
                            // Named sources first (in encounter order), "Other" group last.
                            .sortedBy { (source, _) -> source == null }

                        groups.forEach { (source, entries) ->
                            item(key = "header_${source ?: ""}") {
                                CategoryHeader(source = source, count = entries.size)
                            }
                            items(entries, key = { it.index }) { entry ->
                                ProductCard(
                                    product = entry.value,
                                    // Edit / delete are also allowed once an edit request is approved
                                    showEditActions = actionsEnabled || task.isEditable,
                                    showPickAction = actionsEnabled,
                                    onClick = { onProductClick(entry.index) },
                                    onPickClick = { onPickClick(entry.index) },
                                    onDeleteClick = { onDeleteClick(entry.index) }
                                )
                            }
                        }
                    }
                }
            }

            // Bottom action bar. Which action it offers depends on where the order is in its
            // lifecycle:
            //  - Assigned: "change order status" (to on-the-way), locked until the user has acted
            //    on every remaining product — picked fully/partially (pickedQuantity set) or
            //    deleted it (removed from the list).
            //  - On the way (Picked): "deliver order" — opens the receive-amount sheet.
            //  - Delivered / Canceled: no action.
            // While order actions are disabled (view-only release) picking is unavailable, so
            // "deliver order" is the only action and is offered at any active step.
            val currentStep = OrderSteps.fromStatusId(task.statusId)
            val isDeliverStep = currentStep == OrderSteps.Picked || !actionsEnabled
            val isFinished = currentStep == OrderSteps.Delivered || currentStep == OrderSteps.Canceled
            val canChangeStatus = task.products.isNotEmpty() &&
                task.products.all { it.pickedQuantity != null }
            if (!isFinished) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Base_white)
                ) {
                    HorizontalDivider(
                        thickness = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                        color = Gray200
                    )
                    if (!isDeliverStep && !canChangeStatus) {
                        Text(
                            text = stringResource(R.string.change_status_locked_hint),
                            style = xsMedium.copy(color = Gray500),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = dimensionResource(com.intuit.sdp.R.dimen._8sdp),
                                    start = dimensionResource(com.intuit.sdp.R.dimen._16sdp),
                                    end = dimensionResource(com.intuit.sdp.R.dimen._16sdp)
                                )
                        )
                    }
                    val buttonEnabled = isDeliverStep || canChangeStatus
                    val buttonShape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
                    val buttonPadding = PaddingValues(
                        vertical = dimensionResource(com.intuit.sdp.R.dimen._10sdp),
                        horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(com.intuit.sdp.R.dimen._16sdp)),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            // Takes the full width alone, or 3:2 next to the edit button
                            modifier = Modifier.weight(3f),
                            onClick = if (isDeliverStep) onDeliverClick else onChangeStatusClick,
                            enabled = buttonEnabled,
                            shape = buttonShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary,
                                disabledContainerColor = Gray200
                            ),
                            contentPadding = buttonPadding
                        ) {
                            Text(
                                text = stringResource(
                                    if (isDeliverStep) R.string.deliver_order else R.string.change_order_status
                                ),
                                style = mdMedium.copy(color = if (buttonEnabled) Base_white else Gray500)
                            )
                        }

                        // "Edit" sits beside "deliver order" and opens the edit-options sheet
                        if (isDeliverStep) {
                            OutlinedButton(
                                modifier = Modifier.weight(2f),
                                onClick = onEditClick,
                                shape = buttonShape,
                                contentPadding = buttonPadding
                            ) {
                                Text(
                                    text = stringResource(R.string.edit),
                                    style = mdMedium.copy(color = Primary),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Shared card look used by the header and each product row (matches [TaskCard]). */
@Composable
private fun Modifier.cardSurface(): Modifier {
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

/** Formats a price with up to 2 decimals and no trailing zeros (250.0 -> "250", 20.25 -> "20.25"). */
private fun formatPrice(value: Double): String = value.formatDecimal(maxDecimals = 2)

@Composable
private fun ProductsSectionHeader(count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.products_list),
            style = mdSemiBold
        )
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

/**
 * Group label for a set of products sharing the same [source]. Shows the source name (or a
 * generic "Other" label when the product has no source) followed by a count pill.
 */
@Composable
private fun CategoryHeader(source: String?, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(com.intuit.sdp.R.dimen._4sdp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_count),
            contentDescription = null,
            tint = Gray500,
            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._16sdp))
        )
        Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._6sdp)))
        Text(
            text = source ?: stringResource(R.string.other_products),
            style = smSemiBold.copy(color = Gray800),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
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

/** Selectable pick-status filter pills shown above the products list. */
@Composable
private fun ProductFilterRow(
    selected: ProductPickFilter,
    onSelect: (ProductPickFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._6sdp))
    ) {
        ProductPickFilter.entries.forEach { filter ->
            val isSelected = filter == selected
            val shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._16sdp))
            Text(
                text = stringResource(filter.labelRes),
                style = xsMedium.copy(color = if (isSelected) Base_white else Gray600),
                maxLines = 1,
                modifier = Modifier
                    .clip(shape)
                    .background(if (isSelected) Primary else Base_white, shape)
                    .border(
                        width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                        color = if (isSelected) Primary else Gray200,
                        shape = shape
                    )
                    .clickable { onSelect(filter) }
                    .padding(
                        horizontal = dimensionResource(com.intuit.sdp.R.dimen._10sdp),
                        vertical = dimensionResource(com.intuit.sdp.R.dimen._6sdp)
                    )
            )
        }
    }
}

@Composable
private fun EmptyProductsCard(@StringRes messageRes: Int = R.string.no_products_available) {
    Column(
        modifier = Modifier
            .cardSurface()
            .padding(dimensionResource(com.intuit.sdp.R.dimen._24sdp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_count),
            contentDescription = null,
            tint = Gray500,
            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._28sdp))
        )
        Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
        Text(
            text = stringResource(messageRes),
            style = smNormal.copy(color = Gray500),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun OrderHeader(task: HomeTask) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .cardSurface()
            .padding(dimensionResource(com.intuit.sdp.R.dimen._16sdp))
    ) {
        // Order id + status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "#${task.taskId}", style = mdSemiBold)
            if (task.statusName.isNotBlank()) {
                // Same status badge used on the home task cards, for a consistent look.
                StatusTag(statusId = task.statusId, statusName = task.statusName)
            }
        }

        HeaderDivider()

        // Client data
        Text(text = stringResource(R.string.client_information), style = smMedium.copy(color = Gray500))
        Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
        HeaderInfoRow(R.drawable.ic_profile, task.customerName.orDefault(stringResource(R.string.not_available)))
        Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
        HeaderInfoRow(R.drawable.ic_address, task.customerAddress.orDefault(stringResource(R.string.no_address)))

        Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._10sdp)))

        // Contact actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
        ) {
            HeaderActionButton(
                modifier = Modifier.weight(1f),
                icon = R.drawable.ic_call,
                label = stringResource(R.string.call),
                onClick = { context.makePhoneCall(task.clientPhone) }
            )
            HeaderActionButton(
                modifier = Modifier.weight(1f),
                icon = R.drawable.ic_navigation,
                label = stringResource(R.string.directions),
                onClick = { context.navigateToLocationClick(task.clientLat, task.clientLang) }
            )
        }

        HeaderDivider()

        // Order summary
        SummaryRow(
            label = stringResource(R.string.total_products),
            value = (task.itemsCount.takeIf { it > 0 } ?: task.products.size).toString()
        )
        if (task.deliveryFees > 0f) {
            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._6sdp)))
            SummaryRow(
                label = stringResource(R.string.delivery_fees),
                value = stringResource(
                    R.string.price_currency,
                    formatPrice(task.deliveryFees.toDouble())
                )
            )
        }
        Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._6sdp)))
        SummaryRow(
            label = stringResource(R.string.total_price),
            value = stringResource(
                R.string.price_currency,
                formatPrice(task.totalPrice.toDouble())
            ),
            emphasized = true
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String, emphasized: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (emphasized) smMedium.copy(color = Gray800) else smNormal.copy(color = Gray600)
        )
        Text(
            text = value,
            style = if (emphasized) lgSemiBold.copy(color = Primary) else smMedium.copy(color = Gray800)
        )
    }
}

@Composable
private fun HeaderDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
        thickness = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
        color = Gray200
    )
}

@Composable
private fun HeaderActionButton(
    icon: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._18sdp))
        )
        Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._6sdp)))
        Text(text = label, style = smMedium.copy(color = Primary))
    }
}

@Composable
private fun HeaderInfoRow(icon: Int, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Gray500,
            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._18sdp))
        )
        Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
        Text(text = label, style = smNormal.copy(color = Gray800))
    }
}

@Composable
private fun ProductCard(
    product: FirebaseProduct,
    showEditActions: Boolean,
    showPickAction: Boolean,
    onClick: () -> Unit,
    onPickClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val requiredQuantity = product.quantity ?: 0.0
    val foundQuantity = product.pickedQuantity
    val lineTotal = product.totalPrice ?: ((product.itemPrice ?: 0.0) * requiredQuantity)

    Column(
        modifier = Modifier
            .cardSurface()
            .padding(dimensionResource(com.intuit.sdp.R.dimen._12sdp))
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Image(
                modifier = Modifier
                    .size(dimensionResource(com.intuit.sdp.R.dimen._56sdp))
                    .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                    .background(Gray100)
                    // Zoom into the product so it fills the thumbnail; clip above keeps it in bounds.
                    .graphicsLayer {
                        scaleX = 1.4f
                        scaleY = 1.4f
                    },
                painter = rememberAsyncImagePainter(product.imageUrl.orDefault()),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._10sdp))
            ) {
                Text(
                    text = product.name.orDefault(stringResource(R.string.unknown_product)),
                    style = smMedium.copy(color = Gray800),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
                // Unit price on its own line.
                Text(
                    text = stringResource(
                        R.string.price_currency,
                        formatPrice(product.itemPrice ?: 0.0)
                    ),
                    style = xsMedium.copy(color = Gray600)
                )
                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
                // Quantity highlighted on its own line for visibility.
                Text(
                    text = stringResource(R.string.required_qty, requiredQuantity.formatDecimal()),
                    style = smMedium.copy(color = Gray800)
                )
            }

            // Edit / delete affordances, with the picked-status badge stacked beneath them.
            Column(horizontalAlignment = Alignment.End) {
                if (showEditActions) Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onClick,
                        modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._32sdp))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit_task),
                            contentDescription = stringResource(R.string.edit_product),
                            tint = Primary,
                            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._18sdp))
                        )
                    }
                    Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._32sdp))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = stringResource(R.string.delete_product),
                            tint = Error500,
                            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._18sdp))
                        )
                    }
                }
                if (foundQuantity != null) {
                    Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
                    PickedStatusBadge(
                        requiredQuantity = requiredQuantity,
                        foundQuantity = foundQuantity
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
            thickness = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
            color = Gray200
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.item_total_price),
                    style = xsMedium.copy(color = Gray500)
                )
                Text(
                    text = stringResource(R.string.price_currency, formatPrice(lineTotal)),
                    style = smMedium.copy(color = Gray800)
                )
            }

            if (showPickAction) OutlinedButton(
                onClick = onPickClick,
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                contentPadding = PaddingValues(
                    horizontal = dimensionResource(com.intuit.sdp.R.dimen._12sdp),
                    vertical = dimensionResource(com.intuit.sdp.R.dimen._6sdp)
                )
            ) {
                Text(text = stringResource(R.string.mark_picked), style = xsMedium.copy(color = Primary))
            }
        }
    }
}

@Composable
private fun PickedStatusBadge(requiredQuantity: Double, foundQuantity: Double?) {
    if (foundQuantity == null) return
    val isFull = foundQuantity >= requiredQuantity
    val color = if (isFull) StatusDelivered else StatusPending
    val label = if (isFull) {
        stringResource(R.string.status_picked_full)
    } else {
        stringResource(
            R.string.status_picked_missing,
            (requiredQuantity - foundQuantity).formatDecimal()
        )
    }
    Text(
        text = label,
        style = xsMedium.copy(color = color),
        modifier = Modifier
            .background(
                color.copy(alpha = 0.1f),
                RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
            )
            .padding(
                horizontal = dimensionResource(com.intuit.sdp.R.dimen._8sdp),
                vertical = dimensionResource(com.intuit.sdp.R.dimen._4sdp)
            )
    )
}

@Preview(showBackground = true, showSystemUi = true, locale = "ar")
@Composable
private fun OrderDetailsContentPreview() {
    OrderDetailsContent(
        viewState = TaskDetailsViewState(
            task = HomeTask(
                taskId = 12345,
                customerName = "Ahmed Ali",
                clientPhone = "01012345678",
                customerAddress = "Nasr City, Cairo",
                statusName = "Assigned",
                itemsCount = 2,
                totalPrice = 250f,
                deliveryFees = 15f,
                products = listOf(
                    FirebaseProduct(
                        name = "Product 1",
                        itemPrice = 100.0,
                        quantity = 2.0,
                        pickedQuantity = 1.0,
                        totalPrice = 200.0,
                        source = "زيت وسمنه"
                    ),
                    FirebaseProduct(name = "Product 2", itemPrice = 20.25, quantity = 0.5, totalPrice = 10.125),
                )
            ),
            currency = "EGP"
        ),
        onBackClick = {},
        onProductClick = {},
        onPickClick = {},
        onDeleteClick = {},
        onChangeStatusClick = {},
        onDeliverClick = {},
        onEditClick = {}
    )
}
