package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

import com.ovasta.logisticsapp.presentation.orderDetails.presentation.statusbootmsheets.EditTaskStatusBottomSheetDialog
import com.ovasta.logisticsapp.presentation.orderDetails.presentation.statusbootmsheets.ReasonsBottomSheetDialog
import com.ovasta.logisticsapp.presentation.orderDetails.presentation.statusbootmsheets.StatusDialogType
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.CenteredTextAppBar
import com.ovasta.logisticsapp.base.Error700
import com.ovasta.logisticsapp.base.Gray100
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray300
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.Primary50
import com.ovasta.logisticsapp.base.Primary500
import com.ovasta.logisticsapp.base.Primary700
import com.ovasta.logisticsapp.base.Success50
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseDialog
import com.ovasta.logisticsapp.base.ext.orDefault
import com.ovasta.logisticsapp.base.lgSemiBold
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.mdSemiBold
import com.ovasta.logisticsapp.base.setOnClick
import com.ovasta.logisticsapp.base.smMedium
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.base.xsRegular
import com.ovasta.logisticsapp.presentation.orderDetails.data.BundleComponentModel
import com.ovasta.logisticsapp.presentation.orderDetails.data.ProductModel
import com.ovasta.logisticsapp.presentation.orderDetails.data.ProductUnitModel
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.DropOfTaskDetailsModel


@Composable
fun DropOfOrderDetailsScreen(
    viewState: DropOfOrderDetailsViewState,
    onAction: (DropOfOrderDetailsAction) -> Unit
) {

    DropOfOrderDetailsContent(
        viewState = viewState,
        onAction = onAction
    )


//    val state = viewState.statusDialogType
    when (val state = viewState.statusDialogType) {
        is StatusDialogType.StatusType -> {
            EditTaskStatusBottomSheetDialog(
                dialogState = state.dialogState,
                onDismiss = {
                    onAction(DropOfOrderDetailsAction.DismissStatusDialogs)
                },
                onSelectTaskStatus = {
                    onAction(DropOfOrderDetailsAction.OnSelectTaskStatus(it))
                }
            )
        }

        is StatusDialogType.ReasonType -> {
            ReasonsBottomSheetDialog(
                dialogState = state.dialogState,
                onDismiss = {
                    onAction(DropOfOrderDetailsAction.DismissStatusDialogs)
                }, onBackToTaskStatus = {
                    onAction(DropOfOrderDetailsAction.BackToTaskStatus)
                },
                onConfirm = { selectedReason ->
                    if (state.dialogState.isRequestDelay) {
                        onAction(
                            DropOfOrderDetailsAction.OnDelaySameDay(selectedReason)
                        )
                    } else {
                        onAction(
                            DropOfOrderDetailsAction.OnFailTask(selectedReason)
                        )
                    }
                }
            )
        }

        is StatusDialogType.None -> {}
    }


    if (viewState.showAddProductsDialog) {

        BaseDialog(
            icon = painterResource(R.drawable.logo),
            title = stringResource(R.string.add_products),
            message = stringResource(R.string.are_you_sure_you_want_to_add_products),
            dismissOnClickOutside = true,
            onDismiss = { onAction(DropOfOrderDetailsAction.DismissAddProductsDialog) },
            primaryButtonText = stringResource(R.string.yes),
            secondaryButtonText = stringResource(R.string.no),
            onPrimaryClick = { onAction(DropOfOrderDetailsAction.OnConfirmClick) },
            onSecondaryClick = { onAction(DropOfOrderDetailsAction.DismissAddProductsDialog) }
        )
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DropOfOrderDetailsContent(
    viewState: DropOfOrderDetailsViewState,
    onAction: (DropOfOrderDetailsAction) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray100),
        topBar = {
            CenteredTextAppBar(
                stringResource(R.string.task_details),
                onBackButtonPressed = { onAction(DropOfOrderDetailsAction.OnBackPressed) }
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
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .setOnClick {

                            }
                            .fillMaxWidth()
                            .background(Base_white)
                            .padding(
                                horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp),
                                vertical = dimensionResource(com.intuit.sdp.R.dimen._12sdp)
                            )
                    ) {

                        Column(
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = stringResource(R.string.required_to_be_received),
                                style = smMedium.copy(
                                    color = Gray500
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                modifier = Modifier.padding(top = dimensionResource(com.intuit.sdp.R.dimen._2sdp)),
                                text = "${viewState.taskDetails?.toBeCollectedAmount.orDefault()}",
                                style = lgSemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
                            horizontalAlignment = Alignment.End
                        ) {

                            Text(
                                text = "#${viewState.taskDetails?.vendorId.orDefault()}",
                                style = xsMedium
                            )

                            Row(
                                modifier = Modifier
                                    .padding(top = dimensionResource(com.intuit.sdp.R.dimen._4sdp))
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._4sdp))
                            ) {
                                viewState.taskDetails?.tags.orEmpty().forEach { tag ->
                                    DropOffPaymentTag(text = tag)
                                }
                            }
                        }
                        Image(
                            modifier = Modifier.setOnClick {
                                onAction(DropOfOrderDetailsAction.OnInfoClick)
                            },
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = null
                        )
                    }
                }

                item {
                    if (true) {
                        Column(
                            modifier = Modifier
                                .setOnClick {
                                    onAction(DropOfOrderDetailsAction.NavigateToUpSellingProductsScreen)
                                }
                                .fillMaxWidth()
                                .padding(
                                    horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp),
                                    vertical = dimensionResource(com.intuit.sdp.R.dimen._16sdp)
                                )
                                .background(
                                    color = Error700,
                                    shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
                                )
                        ) {
                            Text(
                                modifier = Modifier.padding(
                                    vertical = dimensionResource(com.intuit.sdp.R.dimen._8sdp),
                                    horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp)
                                ),
                                text = stringResource(R.string.view_deleted_products),
                                style = xsRegular,
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(
                                        RoundedCornerShape(
                                            bottomEnd = dimensionResource(com.intuit.sdp.R.dimen._8sdp),
                                            bottomStart = dimensionResource(com.intuit.sdp.R.dimen._8sdp)

                                        )
                                    )
                                    .border(
                                        width = dimensionResource(com.intuit.sdp.R.dimen._2sdp),
                                        shape = RoundedCornerShape(
                                            bottomEnd = dimensionResource(com.intuit.sdp.R.dimen._8sdp),
                                            bottomStart = dimensionResource(com.intuit.sdp.R.dimen._8sdp)

                                        ),
                                        color = Error700
                                    )
                                    .background(Base_white)
                                    .padding(
                                        horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp),
                                        vertical = dimensionResource(com.intuit.sdp.R.dimen._12sdp)
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Image(
                                    painter = painterResource(R.drawable.ic_add_extra_product),
                                    contentDescription = null
                                )

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                                ) {
                                    Text(
                                        text = stringResource(R.string.deleted_products),
                                        style = mdMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        modifier = Modifier
                                            .padding(top = dimensionResource(com.intuit.sdp.R.dimen._8sdp))
                                            .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._16sdp)))
                                            .background(color = Success50)
                                            .padding(
                                                vertical = dimensionResource(com.intuit.sdp.R.dimen._2sdp),
                                                horizontal = dimensionResource(com.intuit.sdp.R.dimen._8sdp)
                                            ),
                                        text = stringResource(R.string.products_you_can_add_again),
                                        style = xsMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }


                                Text(
                                    modifier = Modifier.align(Alignment.Top),
                                    text = buildAnnotatedString {

                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.Black,
                                                fontSize = dimensionResource(com.intuit.ssp.R.dimen._16ssp).value.sp,
                                                fontWeight = FontWeight(700)
                                            )
                                        ) {
                                            append(
                                                viewState.taskDetails?.upsellingItemsCount.toString()
                                            )
                                        }

                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.Black,
                                                fontSize = dimensionResource(com.intuit.ssp.R.dimen._16ssp).value.sp,
                                                fontWeight = FontWeight(400)
                                            )
                                        ) { append(stringResource(R.string.product)) }

                                    },
                                )

                            }

                        }
                    }
                }

                viewState.categoryToProducts.keys.forEachIndexed { categoryIndex, categoryName ->
                    stickyHeader {
                        Title(
                            categoryName = categoryName,
                            productsCount = viewState.categoryToProducts[categoryName]?.size.orDefault()
                        )
                    }

                    itemsIndexed(viewState.categoryToProducts[categoryName].orEmpty()) { productIndex, product ->
                        product.units?.forEachIndexed { unitIndex, unit ->
                            Product(
                                productIndex = productIndex,
                                product = product,
                                unitIndex = unitIndex,
                                unit = unit,
                                currency = viewState.currency,
                            )
                        }
                    }
                }

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Base_white)
                    .border(width = dimensionResource(com.intuit.sdp.R.dimen._1sdp), color = Gray200)
                    .padding(dimensionResource(com.intuit.sdp.R.dimen._16sdp))
            ) {

                if (viewState.isSaveButtonAppear) {
                    androidx.compose.material3.Button(
                        modifier = Modifier.weight(1f),
                        onClick = { onAction(DropOfOrderDetailsAction.OnSaveUpSellingClick) },
                        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Primary500
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            style = mdMedium.copy(color = Base_white),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { onAction(DropOfOrderDetailsAction.OnReceiveAmountClick) },
                        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Primary500
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                modifier = Modifier.weight(1f),
                                text = stringResource(R.string.receive_amount),
                                style = mdMedium.copy(color = Base_white),
                                textAlign = TextAlign.Start,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "${viewState.taskDetails?.toBeCollectedAmount.orDefault()} ${viewState.currency}",
                                style = mdMedium.copy(color = Base_white),
                                textAlign = TextAlign.Start,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                        }
                    }

                    if (viewState.showUpdateButton) {
                        OutlinedButton(
                            modifier = Modifier.padding(start = dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                            onClick = { onAction(DropOfOrderDetailsAction.OnChangeStatusClick) },
                            border = BorderStroke(
                                width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                                color = Gray300
                            ),
                            shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Base_white

                            )
                        ) {
                            Text(
                                text = stringResource(R.string.change),
                                style = mdMedium
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun Bundle(
    index: Int,
    product: ProductModel,
    currency: String,
    showPlusClick: Boolean,
    showMinusClick: Boolean,
    onPlusClick: (Int, ProductModel) -> Unit,
    onMinusClick: (Int, ProductModel) -> Unit,
) {

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Base_white)
            .border(width = dimensionResource(com.intuit.sdp.R.dimen._1sdp), color = Gray200)
            .padding(
                horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp),
                vertical = dimensionResource(com.intuit.sdp.R.dimen._12sdp)
            ),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._60sdp)),
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = null,
            )

            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = Gray800,
                                fontSize = dimensionResource(com.intuit.ssp.R.dimen._18ssp).value.sp,
                                fontWeight = FontWeight(700)
                            )
                        ) { append(product.price.toString()) }

                        withStyle(
                            SpanStyle(
                                color = Gray800,
                                fontSize = dimensionResource(com.intuit.ssp.R.dimen._14ssp).value.sp,
                                fontWeight = FontWeight(400)
                            )
                        ) { append(" $currency") }

                        /*withStyle(
                            SpanStyle(
                                color = Gray500,
                                fontSize = dimensionResource(com.intuit.sdp.R.dimen._14ssp).value.sp,
                                fontWeight = FontWeight(400),
                                textDecoration = TextDecoration.LineThrough
                            )
                        ) { append(" ${item.price} $currency") }*/
                    }
                )

                Text(
                    modifier = Modifier.padding(top = dimensionResource(com.intuit.sdp.R.dimen._2sdp)),
                    text = "${product.name}",
                    style = smMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }

        if (expanded) {
            product.bundleComponents?.forEachIndexed { index, componentModel ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "${componentModel.quantity}. ",
                        style = xsMedium
                    )

                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = dimensionResource(com.intuit.sdp.R.dimen._4sdp)),
                        text = "${componentModel.productName} - ${componentModel.unitName}",
                        style = xsMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun Title(
    categoryName: String,
    productsCount: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray100)
            .padding(
                horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp),
                vertical = dimensionResource(com.intuit.sdp.R.dimen._8sdp)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = categoryName,
            style = mdSemiBold
        )

        Text(
            text = "${productsCount} ${stringResource(R.string.product)}",
            style = mdSemiBold
        )

    }
}

@Composable
private fun Product(
    productIndex: Int,
    product: ProductModel,
    unitIndex: Int,
    unit: ProductUnitModel,
    currency: String,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Base_white)
            .border(width = dimensionResource(com.intuit.sdp.R.dimen._1sdp), color = Gray200)
            .padding(
                horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp),
                vertical = dimensionResource(com.intuit.sdp.R.dimen._12sdp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._60sdp)),
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = null,
            )

            /*Text(
                modifier = Modifier
                    .padding(top = dimensionResource(com.intuit.sdp.R.dimen._2sdp))
                    .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._16sdp)))
                    .background(Success50)
                    .padding(
                        horizontal = dimensionResource(com.intuit.sdp.R.dimen._8sdp),
                        vertical = dimensionResource(com.intuit.sdp.R.dimen._2sdp)
                    ),
                text = "item.status",
                color = Success500
            )*/
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = Gray800,
                            fontSize = dimensionResource(com.intuit.ssp.R.dimen._18ssp).value.sp,
                            fontWeight = FontWeight(700)
                        )
                    ) { append(unit.price.toString()) }

                    withStyle(
                        SpanStyle(
                            color = Gray800,
                            fontSize = dimensionResource(com.intuit.ssp.R.dimen._14ssp).value.sp,
                            fontWeight = FontWeight(400)
                        )
                    ) { append(" $currency") }

                    /*withStyle(
                        SpanStyle(
                            color = Gray500,
                            fontSize = dimensionResource(com.intuit.sdp.R.dimen._14ssp).value.sp,
                            fontWeight = FontWeight(400),
                            textDecoration = TextDecoration.LineThrough
                        )
                    ) { append(" ${item.price} $currency") }*/
                }
            )

            Text(
                modifier = Modifier.padding(top = dimensionResource(com.intuit.sdp.R.dimen._2sdp)),
                text = "${product.name} ${unit.name}",
                style = smMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

        }
    }
}

@Composable
private fun DropOffPaymentTag(text: String) {
    androidx.compose.material3.Text(
        modifier = Modifier
            .background(
                color = Primary50, shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._16sdp))
            )
            .padding(
                horizontal = dimensionResource(com.intuit.sdp.R.dimen._10sdp),
                vertical = dimensionResource(com.intuit.sdp.R.dimen._2sdp)
            ),
        text = text,
        style = smMedium.copy(color = Primary700),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}


@Preview(showBackground = true)
@Composable
private fun DropOfOrderDetailsScreenPreview() {

    val tempImage =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRXB62tjHoc2ryZVERvGz_rYDqcy3kNdwZpdf6u-zE9eMllD8ik25CDvsL7Pud137Odqs8&usqp=CAU"

    val productList = listOf(
        ProductModel(
            id = 1,
            name = "Coca Cola",
            price = 25.0,
            category = "Drinks",
            type = "simple",
            availableBasicUnitCount = 12,
            units = listOf(),
            unit = "",
            image = tempImage
        ),

        ProductModel(
            id = 1,
            name = "Coca Cola",
            price = 25.0,
            category = "Drinks",
            type = "simple",
            availableBasicUnitCount = 12,
            units = listOf(),
            unit = "",
            image = tempImage
        ),

        ProductModel(
            id = 1,
            name = "Coca Cola",
            price = 25.0,
            category = "Drinks",
            type = "simple",
            availableBasicUnitCount = 12,
            units = listOf(),
            unit = "",
            image = tempImage
        ),

        ProductModel(
            id = 1,
            name = "Coca Cola",
            price = 25.0,
            category = "Drinks",
            type = "simple",
            availableBasicUnitCount = 12,
            units = listOf(),
            unit = "",
            image = tempImage
        ),

        ProductModel(
            id = 1,
            name = "Coca Cola",
            price = 25.0,
            category = "Drinks",
            type = "simple",
            availableBasicUnitCount = 12,
            units = listOf(),
            unit = "",
            image = tempImage
        )

    )



    DropOfOrderDetailsContent(
        viewState = DropOfOrderDetailsViewState(
            categoryToProducts = productList.groupBy { it.category }
        ),
        onAction = {}
    )

}

@Preview(showBackground = true)
@Composable
private fun DropOfOrderDetailsScreenPreviewWithEnableUpsellingProductOutOfOrder() {

    val tempImage =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRXB62tjHoc2ryZVERvGz_rYDqcy3kNdwZpdf6u-zE9eMllD8ik25CDvsL7Pud137Odqs8&usqp=CAU"

    val productList = listOf(
        ProductModel(
            id = 1,
            name = "Coca Cola",
            price = 25.0,
            category = "Drinks",
            type = "simple",
            availableBasicUnitCount = 12,
            units = listOf(),
            unit = "",
            image = tempImage
        ),

        ProductModel(
            id = 1,
            name = "Coca Cola",
            price = 25.0,
            category = "Drinks",
            type = "simple",
            availableBasicUnitCount = 12,
            units = listOf(),
            unit = "",
            image = tempImage
        ),

        ProductModel(
            id = 1,
            name = "Coca Cola",
            price = 25.0,
            category = "Drinks",
            type = "simple",
            availableBasicUnitCount = 12,
            units = listOf(),
            unit = "",
            image = tempImage
        ),

        ProductModel(
            id = 1,
            name = "Coca Cola",
            price = 25.0,
            category = "Drinks",
            type = "simple",
            availableBasicUnitCount = 12,
            units = listOf(),
            unit = "",
            image = tempImage
        ),

        ProductModel(
            id = 1,
            name = "Coca Cola",
            price = 25.0,
            category = "Drinks",
            type = "simple",
            availableBasicUnitCount = 12,
            units = listOf(),
            unit = "",
            image = tempImage
        )

    )

    DropOfOrderDetailsContent(
        viewState = DropOfOrderDetailsViewState(
            categoryToProducts = productList.groupBy { it.category },
        ),
        onAction = {}
    )

}


@Preview(showBackground = true)
@Composable
private fun DropOfOrderDetailsScreenWithShowChangeButtonPreview() {

    val tempImage =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRXB62tjHoc2ryZVERvGz_rYDqcy3kNdwZpdf6u-zE9eMllD8ik25CDvsL7Pud137Odqs8&usqp=CAU"

    val productList = listOf(
        ProductModel(
            id = 1,
            name = "Coca Cola",
            price = 25.0,
            category = "Drinks",
            type = ProductModel.Type.PRODUCT.type,
            availableBasicUnitCount = 12,
            units = listOf(
                ProductUnitModel(
                    id = 1,
                    name = "Bottle",
                    price = 12.9,
                    basicUnitCount = 6,
                    quantity = 12,
                    packingUnitId = 1,

                    )
            ),
            unit = "",
            image = tempImage
        ),

        ProductModel(
            id = 1,
            name = "Coca Cola",
            price = 25.0,
            category = "Drinks",
            type = ProductModel.Type.BUNDLE.type,
            image = "",
            availableGroupQuantity = 4,
            bundleComponents = listOf(
                BundleComponentModel(
                    productName = "Name",
                    unitName = "unit",
                    quantity = 4
                ),
                BundleComponentModel(
                    productName = "Name",
                    unitName = "unit",
                    quantity = 4
                ),
                BundleComponentModel(
                    productName = "Name Name Name kjskdj jksdj jhsdk ",
                    unitName = "unit askdjflkjh kajhsdf ",
                    quantity = 4
                )
            )
        )

    )

    DropOfOrderDetailsContent(
        viewState = DropOfOrderDetailsViewState(
            categoryToProducts = productList.groupBy { it.category },
            showUpdateButton = true),
        onAction = {}
    )

}

@Preview(showBackground = true)
@Composable
private fun DropOfOrderDetailsScreenWithErrorPreview() {

    val tempImage =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRXB62tjHoc2ryZVERvGz_rYDqcy3kNdwZpdf6u-zE9eMllD8ik25CDvsL7Pud137Odqs8&usqp=CAU"


    DropOfOrderDetailsScreen(
        viewState = DropOfOrderDetailsViewState(
            showUpdateButton = true,
            taskDetails = DropOfTaskDetailsModel(
                taskId = 1,
                shipmentId = 12,
                vendorId = 5,
                totalAmount = 250.0,
                discounts = 20.0,
                prepaidAmount = 50.0,
                toBeCollectedAmount = 180.0,
                tags = listOf("tag1"),
                products = listOf(),
                upsellingItemsCount = 2,
                hasPendingTransaction = false
            )
        ),
        onAction = {}
    )

}


