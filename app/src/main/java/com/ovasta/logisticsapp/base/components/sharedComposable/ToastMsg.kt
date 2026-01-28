package com.ovasta.logisticsapp.base.components.sharedComposable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.base.Gray25
import com.ovasta.logisticsapp.base.Gray700
import com.ovasta.logisticsapp.base.xsMedium

@Composable
fun ToastMsg(
    text: String,
    hasIcon: Boolean? = false, backgroundColor: Color = Gray700,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._16sdp)))
            .background(backgroundColor)
            .padding(
                vertical = dimensionResource(com.intuit.sdp.R.dimen._4sdp),
                horizontal = dimensionResource(com.intuit.sdp.R.dimen._12sdp)
            ), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, style = xsMedium.copy(color = Gray25, textAlign = TextAlign.Center))
        Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._6sdp)))
        if(hasIcon==true){
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(
                dimensionResource(com.intuit.sdp.R.dimen._8sdp)
            ))
        }
    }


}

@Preview(showBackground = true)
@Composable
fun ShowToast(){
    ToastMsg("تم دفع 3,000 ج.م بالمحفظة")
}