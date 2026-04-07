package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.CenteredTextAppBar
import com.ovasta.logisticsapp.base.Gray100
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray300
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray600
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.Primary500
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseScreen
import com.ovasta.logisticsapp.base.components.sharedComposable.LocalNavigator
import com.ovasta.logisticsapp.base.ext.orDefault
import com.ovasta.logisticsapp.base.lgSemiBold
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.mdSemiBold
import com.ovasta.logisticsapp.base.setOnClick
import com.ovasta.logisticsapp.base.smMedium
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.presentation.home.data.model.FirebaseProduct
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask

@Composable
fun DropOfOrderDetailsScreen(
    viewModel: TaskDetailsViewModel, taskId: Int
) {
    val viewState by viewModel.viewState.collectAsState()
    val navigator = LocalNavigator.current

    // Handle system back button/gesture - placed first to intercept early
    BackHandler(enabled = true, onBack = {
        navigator.pop()
    })

    LaunchedEffect(Unit) {
        viewModel.getTaskDetails(taskId = taskId)
    }

    BaseScreen(
        viewModel = viewModel
    ) {
//        OrderDetailsContent(
//            task = viewState.task,
//            onBackClick = { navigator.pop() }
//        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OrderDetailsContent(
    viewState: TaskDetailsViewState,
    onAction: (TaskDetailsAction) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray100),
        topBar = {
            CenteredTextAppBar(
                stringResource(R.string.task_details),
                onBackButtonPressed = { onAction(TaskDetailsAction.OnBackPressed) }
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
                            androidx.compose.material.Text(
                                text = stringResource(R.string.required_to_be_received),
                                style = smMedium.copy(
                                    color = Gray500
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                modifier = Modifier.padding(top = dimensionResource(com.intuit.sdp.R.dimen._2sdp)),
                                text = "${viewState.task.totalPrice.orDefault()}",
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
                                text = "#${viewState.task.taskId.orDefault()}",
                                style = xsMedium
                            )
                        }

                        Image(
                            modifier = Modifier.setOnClick {
                                onAction(TaskDetailsAction.OnInfoClick)
                            },
                            painter = painterResource(R.drawable.ic_info),
                            contentDescription = null
                        )

                    }
                }


                viewState.categoryToProducts.keys.forEachIndexed { categoryIndex, categoryName ->
                    stickyHeader {
                        Title(
                            categoryName = categoryName,
                            productsCount = viewState.categoryToProducts[categoryName]?.size.orDefault()
                        )
                    }

                    itemsIndexed(viewState.task.products) { productIndex, product ->
                        Product(
                            product = product,
                        )
                    }
                }
            }

            // Move the buttons Row here, outside LazyColumn
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Base_white)
                    .border(width = dimensionResource(com.intuit.sdp.R.dimen._1sdp), color = Gray200)
                    .padding(dimensionResource(com.intuit.sdp.R.dimen._16sdp))
            ) {

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { },
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
                            text = "${viewState.task.totalPrice.orDefault()} ${viewState.currency}",
                            style = mdMedium.copy(color = Base_white),
                            textAlign = TextAlign.Start,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                    }
                }

                if (true) {
                    OutlinedButton(
                        modifier = Modifier.padding(start = dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                        onClick = { },
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
    product: FirebaseProduct
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
                painter = rememberAsyncImagePainter(product.imageUrl.orDefault()),
                contentDescription = null,
            )
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
                    ) { append(product.totalPrice.toString()) }

                    withStyle(
                        SpanStyle(
                            color = Gray800,
                            fontSize = dimensionResource(com.intuit.ssp.R.dimen._14ssp).value.sp,
                            fontWeight = FontWeight(400)
                        )
                    ) { append(stringResource(R.string.price_currency)) }
                }
            )

            Text(
                modifier = Modifier.padding(top = dimensionResource(com.intuit.sdp.R.dimen._2sdp)),
                text = product.name.orDefault(),
                style = xsMedium.copy(color = Gray600),
            )

            Text(
                modifier = Modifier.padding(top = dimensionResource(com.intuit.sdp.R.dimen._2sdp)),
                text = product.name.orDefault(),
                style = smMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

        }



        Text(
            modifier = Modifier
                .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._4sdp)),
            text = product.quantity.toString(),
            color = Color.Black,
            fontSize = dimensionResource(com.intuit.ssp.R.dimen._14ssp).value.sp,
            fontWeight = FontWeight(700),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrderDetailsContentPreview() {
    OrderDetailsContent(
        viewState = TaskDetailsViewState(
            task = HomeTask(
                taskId = 1,
                clientLang = 30.0444,
                clientLat = 31.2357,
                totalPrice = 250f,
                products = listOf(
                    FirebaseProduct(
                        name = "Product 1",
                        totalPrice = 100.0,
                        quantity = 2,
                        imageUrl = "https://via.placeholder.com/150"
                    ),
                    FirebaseProduct(
                        name = "Product 3",
                        totalPrice = 150.0,
                        quantity = 3,
                        imageUrl = "https://via.placeholder.com/150"
                    )
                )
            ),
            categoryToProducts = mapOf(
                "Category 1" to listOf(
                    FirebaseProduct(
                        name = "Product 1",
                        totalPrice = 100.0,
                        quantity = 2,
                        imageUrl = "https://via.placeholder.com/150"
                    )
                ),
                "Category 2" to listOf(
                    FirebaseProduct(
                        name = "Product 3",
                        totalPrice = 150.0,
                        quantity = 3,
                        imageUrl = "https://via.placeholder.com/150"
                    )
                )
            ),
            currency = "EGP"
        ),
        onAction = {}
    )
}
