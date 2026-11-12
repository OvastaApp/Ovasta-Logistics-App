package com.ovasta.logisticsapp.base.components.sharedComposable

import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.confirm
import com.ovasta.logisticsapp.shared.resources.ic_search
import com.ovasta.logisticsapp.shared.resources.search_by_task_id

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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray100
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.mdRegular
import kotlin.text.isEmpty
import com.ovasta.logisticsapp.base.ext.sdp

@Composable
fun ConfirmButton(isEnabled: Boolean, onClick: () -> Unit) {
    Button(
        enabled = isEnabled,
        onClick = onClick,
        modifier = Modifier
            .padding(16.sdp)
            .fillMaxWidth()
            .height(44.sdp)
            .testTag("confirmButton"),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            disabledContainerColor = Primary.copy(alpha = 0.4f),
            contentColor = Base_white,
            disabledContentColor = Base_white.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(8.sdp),
    ) {
        Text(
            text = stringResource(Res.string.confirm),
            color = if (isEnabled) Base_white else Base_white.copy(alpha = 0.7f),
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
            .padding(16.sdp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.sdp)
                )
                .border(
                    width = 1.sdp,
                    color = Gray200,
                    shape = RoundedCornerShape(8.sdp)
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.sdp)
            ) {
                Image(
                    modifier = Modifier
                        .padding(
                            start = 13.sdp,
                            end = 8.sdp
                        )
                        .size(18.sdp),
                    painter = painterResource(Res.drawable.ic_search),
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
                                .height(44.sdp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = stringResource(Res.string.search_by_task_id),
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
