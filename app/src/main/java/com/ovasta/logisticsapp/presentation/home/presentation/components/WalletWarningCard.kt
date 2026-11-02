package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Error300
import com.ovasta.logisticsapp.base.Error50
import com.ovasta.logisticsapp.base.Error600
import com.ovasta.logisticsapp.base.Error700
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.base.ext.sdp

/** Warning about the courier's wallet (e.g. a low balance), sent by `home` as [message]. */
@Composable
fun WalletWarningCard(message: String) {
    Card(
        shape = RoundedCornerShape(8.sdp),
        colors = CardDefaults.cardColors(containerColor = Error50),
        border = BorderStroke(
            width = 1.sdp,
            color = Error300
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = null,
                tint = Error600,
                modifier = Modifier.size(20.sdp)
            )
            Spacer(modifier = Modifier.width(10.sdp))
            Text(
                text = message,
                style = xsMedium.copy(color = Error700)
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun WalletWarningCardPreview() {
    WalletWarningCard(message = "رصيد المحفظة منخفض، يرجى الشحن لاستقبال طلبات جديدة")
}
