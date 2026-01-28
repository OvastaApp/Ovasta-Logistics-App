package com.ovasta.logisticsapp.base.components.sharedComposable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray100
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.Primary200
import com.ovasta.logisticsapp.base.Primary500
import com.ovasta.logisticsapp.base.mdRegular
import kotlin.text.isEmpty

@Composable
fun ConfirmButton(isEnabled: Boolean, onClick: () -> Unit) {
    Button(
        enabled = isEnabled,
        onClick = onClick,
        modifier = Modifier
            .padding(dimensionResource(com.intuit.sdp.R.dimen._16sdp))
            .fillMaxWidth()
            .height(dimensionResource(com.intuit.sdp.R.dimen._44sdp))
            .testTag("confirmButton"),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary500,
            disabledContainerColor = Primary200,
            contentColor = Base_white,
            disabledContentColor = Base_white
        ),
        shape = RoundedCornerShape(dimensionResource(id = com.intuit.sdp.R.dimen._8sdp)),
    ) {
        Text(
            text = stringResource(id = R.string.confirm),
            color = Color.White,
            style = mdRegular
        )
    }
}

@Composable
fun SearchTextField(onSearchValueChange: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray100)
            .padding(dimensionResource(com.intuit.sdp.R.dimen._16sdp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
                )
                .border(
                    width = dimensionResource(com.intuit.ssp.R.dimen._1ssp),
                    color = Gray200,
                    shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(com.intuit.sdp.R.dimen._44sdp))
            ) {
                Image(
                    modifier = Modifier
                        .padding(
                            start = dimensionResource(com.intuit.sdp.R.dimen._13sdp),
                            end = dimensionResource(com.intuit.sdp.R.dimen._8sdp)
                        )
                        .size(dimensionResource(com.intuit.sdp.R.dimen._18sdp)),
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Search icon"
                )

                BasicTextField(
                    modifier = Modifier.testTag("searchTextField"),
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        onSearchValueChange(it)
                    },
                    singleLine = true,
                    textStyle = mdRegular.copy(color = if (searchQuery.isEmpty()) Gray500 else Gray800),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimensionResource(com.intuit.sdp.R.dimen._44sdp)),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.search_by_task_id),
                                    style = mdRegular.copy(color = Gray500)
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}
