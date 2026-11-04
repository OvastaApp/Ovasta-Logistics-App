package com.ovasta.logisticsapp.base

import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.medium_new
import com.ovasta.logisticsapp.shared.resources.regular_new
import com.ovasta.logisticsapp.shared.resources.semibold_new

import androidx.compose.ui.text.TextStyle
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import com.ovasta.logisticsapp.ui.theme.BLACK
import com.ovasta.logisticsapp.base.ext.ssp

val smMedium: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray700,
        fontSize = 14.ssp,
        fontFamily = FontFamily(Font(Res.font.medium_new))
    )

val mdRegular: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray500,
        fontSize = 16.ssp,
        fontFamily = FontFamily(Font(Res.font.regular_new)),
    )


val xlSemiBold: TextStyle
    @Composable
    get() = TextStyle(
        color = BLACK,
        fontSize = 20.ssp,
        fontFamily = FontFamily(Font(Res.font.semibold_new))
    )

val mdMedium: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray600,
        fontFamily = FontFamily(Font(Res.font.medium_new)),
        fontSize = 16.ssp,
    )

val smNormal: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray800,
        fontFamily = FontFamily(Font(Res.font.regular_new)),
        fontSize = 14.ssp,
    )

val smSemiBold: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray800,
        fontFamily = FontFamily(Font(Res.font.semibold_new)),
        fontSize = 14.ssp,
    )

val mdSemiBold: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray800,
        fontFamily = FontFamily(Font(Res.font.semibold_new)),
        fontSize = 16.ssp,
    )

val lgSemiBold: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray800,
        fontFamily = FontFamily(Font(Res.font.semibold_new)),
        fontSize = 18.ssp,
    )

val xsMedium: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray500,
        fontFamily = FontFamily(Font(Res.font.medium_new)),
        fontSize = 12.ssp,
    )

val xsRegular: TextStyle
    @Composable
    get() = TextStyle(
        color = Base_white,
        fontFamily = FontFamily(Font(Res.font.regular_new)),
        fontSize = 12.ssp,
    )


val lgMedium: TextStyle
    @Composable
    get() = TextStyle(
        color = Gray900,
        fontFamily = FontFamily(Font(Res.font.medium_new)),
        fontSize = 18.ssp,
    )

