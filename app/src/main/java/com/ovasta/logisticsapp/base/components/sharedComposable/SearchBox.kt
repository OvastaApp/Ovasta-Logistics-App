package com.ovasta.logisticsapp.base.components.sharedComposable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.mdRegular


@Composable
fun SearchBox(
    modifier: Modifier = Modifier,
    searchKey: String,
    hint:String,
    onSearchKeyChange: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    keyboardType:KeyboardType?=null
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
        color = Base_white,
        border = BorderStroke(dimensionResource(com.intuit.sdp.R.dimen._1sdp), Gray200),
        shadowElevation = dimensionResource(com.intuit.sdp.R.dimen._1sdp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(com.intuit.sdp.R.dimen._14sdp),
                    vertical = dimensionResource(com.intuit.sdp.R.dimen._10sdp)
                ), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "بحث",
                tint = Color.Unspecified,
                modifier = Modifier.clickable {
                    onSearchTriggered()
                    keyboardController?.hide() })
            Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimensionResource(com.intuit.sdp.R.dimen._4sdp))
            ) {
                BasicTextField(
                    value = searchKey,
                    onValueChange = { onSearchKeyChange(it)
                        if (it.isEmpty()) {
                            keyboardController?.hide()
                        }},
                    singleLine = true,
                    textStyle = mdRegular.copy(
                        color = Gray500,
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction =ImeAction.Search, keyboardType = keyboardType ?:KeyboardType.Number),
                    keyboardActions = KeyboardActions(onSearch = {
                        onSearchTriggered()
                        keyboardController?.hide() }),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box {
                            if (searchKey.isEmpty()) {
                                Text(
                                    text = hint,
                                    style = mdRegular.copy(color = Gray500),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.CenterEnd)
                                )
                            }
                            innerTextField()
                        }
                    })
            }
        }
    }
}

@Preview(showBackground=true,locale="ar")
@Composable
fun PreviewSearchBox(){
    SearchBox(Modifier,"","search",{},{})
}