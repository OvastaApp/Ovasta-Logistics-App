package com.ovasta.logisticsapp.base

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.dimensionResource
import androidx.compose.runtime.Composable
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.ui.theme.BLACK

val smMedium: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray700,
        fontSize = dimensionResource(id = com.intuit.ssp.R.dimen._14ssp).value.sp,
        fontFamily = FontFamily(Font(R.font.medium_new))
    )

val mdRegular: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray500,
        fontSize = dimensionResource(id = com.intuit.ssp.R.dimen._16ssp).value.sp,
        fontFamily = FontFamily(Font(R.font.regular_new)),
    )


val xlSemiBold: TextStyle
    @Composable
    get() = TextStyle(
        color = BLACK,
        fontSize = dimensionResource(id = com.intuit.ssp.R.dimen._20ssp).value.sp,
        fontFamily = FontFamily(Font(R.font.semibold_new))
    )

val mdMedium: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray600,
        fontFamily = FontFamily(Font(R.font.medium_new)),
        fontSize = dimensionResource(id = com.intuit.ssp.R.dimen._16ssp).value.sp,
    )

val smNormal: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray800,
        fontFamily = FontFamily(Font(R.font.regular_new)),
        fontSize = dimensionResource(id = com.intuit.ssp.R.dimen._14ssp).value.sp,
    )

val smSemiBold: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray800,
        fontFamily = FontFamily(Font(R.font.semibold_new)),
        fontSize = dimensionResource(id = com.intuit.ssp.R.dimen._14ssp).value.sp,
    )

val mdSemiBold: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray800,
        fontFamily = FontFamily(Font(R.font.semibold_new)),
        fontSize = dimensionResource(id = com.intuit.ssp.R.dimen._16ssp).value.sp,
    )

val lgSemiBold: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray800,
        fontFamily = FontFamily(Font(R.font.semibold_new)),
        fontSize = dimensionResource(id = com.intuit.ssp.R.dimen._18ssp).value.sp,
    )

val xsMedium: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray500,
        fontFamily = FontFamily(Font(R.font.medium_new)),
        fontSize = dimensionResource(id = com.intuit.ssp.R.dimen._12ssp).value.sp,
    )

val xsRegular: TextStyle
    @Composable
    get() = TextStyle(
        color = Base_white,
        fontFamily = FontFamily(Font(R.font.regular_new)),
        fontSize = dimensionResource(id = com.intuit.ssp.R.dimen._12ssp).value.sp,
    )


val lgMedium: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray900,
        fontFamily = FontFamily(Font(R.font.medium_new)),
        fontSize = dimensionResource(id =com.intuit.ssp.R.dimen._18ssp).value.sp,
    )

