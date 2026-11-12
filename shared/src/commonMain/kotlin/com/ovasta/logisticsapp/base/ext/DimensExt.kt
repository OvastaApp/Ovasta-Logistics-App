package com.ovasta.logisticsapp.base.ext

import androidx.compose.runtime.Composable
import com.ovasta.logisticsapp.base.platform.smallestScreenWidthDp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Replaces the sdp/ssp libraries (Android-only dimension resources) with the same scaling:
// sizes are designed for a 300dp-wide screen and grow with the smallest screen width,
// in steps of 30dp from 300dp to 1080dp. E.g. 8.sdp is 9.6dp on a 360dp-wide phone.

private const val BASE_WIDTH_DP = 300
private const val MAX_WIDTH_DP = 1080
private const val WIDTH_STEP_DP = 30

/** The sdp/ssp scale for a screen whose smallest width is [smallestWidthDp]. */
internal fun sdpScale(smallestWidthDp: Int): Float {
    val bucket = (smallestWidthDp / WIDTH_STEP_DP * WIDTH_STEP_DP).coerceIn(BASE_WIDTH_DP, MAX_WIDTH_DP)
    return bucket.toFloat() / BASE_WIDTH_DP
}

@Composable
private fun currentSdpScale(): Float = sdpScale(smallestScreenWidthDp())

/** A size in dp that scales with the screen width, like the sdp library's `_Nsdp`. */
val Int.sdp: Dp
    @Composable get() = (this * currentSdpScale()).dp

/** A text size in sp that scales with the screen width, like the ssp library's `_Nssp`. */
val Int.ssp: TextUnit
    @Composable get() = (this * currentSdpScale()).sp
