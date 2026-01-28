package com.ovasta.logisticsapp.base.components.sharedComposable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray300
import com.ovasta.logisticsapp.base.Gray600
import com.ovasta.logisticsapp.base.Gray900
import com.ovasta.logisticsapp.base.Primary50
import com.ovasta.logisticsapp.base.White
import com.ovasta.logisticsapp.base.mdRegular
import com.ovasta.logisticsapp.base.mdSemiBold
import kotlin.collections.filter
import kotlin.collections.forEach
import kotlin.let
import kotlin.text.contains
import kotlin.text.isEmpty


data class MultiSelectItem(
    val id: Int = 0,
    val title: String,
    var isSelected: Boolean = false,
    val stringId: String = "",
)
@Composable
fun ListWithSearchBottomSheet(
    title: String,
    listItems: List<MultiSelectItem>,
    lastSelectedItem: MultiSelectItem? = null,
    showSearch:Boolean=true,
    onConfirmClicked: (MultiSelectItem) -> Unit,
    onCloseClicked: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf(lastSelectedItem) }

    val isEnabled by remember(selectedOption, lastSelectedItem) {
        derivedStateOf {
            selectedOption?.id != lastSelectedItem?.id
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = dimensionResource(com.intuit.sdp.R.dimen._400sdp))
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(
                        start = dimensionResource(com.intuit.sdp.R.dimen._16sdp),
                        end = dimensionResource(com.intuit.sdp.R.dimen._16sdp),
                        bottom = dimensionResource(com.intuit.sdp.R.dimen._22sdp),
                        top = dimensionResource(com.intuit.sdp.R.dimen._16sdp)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title, style = mdSemiBold
                )
                Icon(
                    modifier = Modifier
                        .clickable {
                            onCloseClicked()
                        },
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close icon"
                )
            }
            if (showSearch) SearchTextField { searchQuery = it }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                item {
                    OptionsList(listItems, selectedOption, searchQuery) {
                        selectedOption = it
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
                color = Gray200,
                thickness = dimensionResource(com.intuit.sdp.R.dimen._1sdp)
            )


            ConfirmButton(
                isEnabled = isEnabled,
                onClick = {
                    selectedOption?.let { option -> onConfirmClicked(option) }
                }
            )
        }
    }
}

@Composable
fun OptionsList(
    options: List<MultiSelectItem>,
    selectItem: MultiSelectItem?,
    filterText: String,
    onSelectOption: (MultiSelectItem) -> Unit
) {
    var selectedOption by remember { mutableStateOf(selectItem) }
    val filteredItems = if (filterText.isEmpty()) options else options.filter {
        it.title.contains(filterText, ignoreCase = true)
    }

    filteredItems.forEach { option ->
        Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(com.intuit.sdp.R.dimen._44sdp))
                .padding(
                    start = dimensionResource(com.intuit.sdp.R.dimen._16sdp),
                    end = dimensionResource(com.intuit.sdp.R.dimen._16sdp)
                )
                .background(
                    if (selectedOption?.id == option.id) Primary50 else White,
                    shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
                )
                .border(
                    dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                    color = if (selectedOption?.id == option.id) Primary50 else Gray200,
                    RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
                )
                .clickable {
                    selectedOption = option
                    onSelectOption(option)
                }
                .padding(vertical = dimensionResource(com.intuit.sdp.R.dimen._10sdp))
                .testTag("listItem_${option.title}"),
        verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = option.title, style = mdRegular.copy(color = Gray900), modifier = Modifier.padding(start = dimensionResource(com.intuit.sdp.R.dimen._14sdp)))
            Spacer(modifier = Modifier.weight(1f))
            RadioButton(
                selected = selectedOption?.id == option.id,
                onClick = {
                    selectedOption = option
                    onSelectOption(option)
                },
                colors = RadioButtonDefaults.colors(
                    selectedColor = Gray600,
                    unselectedColor = Gray300
                )
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetView(
    lastSelectedItem: MultiSelectItem?, onSelectOption: (MultiSelectItem?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        containerColor = White,
        sheetState = sheetState,
        onDismissRequest = {
            onSelectOption(null)
        },
        content = {
            ListWithSearchBottomSheet(
                title = "Select an Option",
                listItems = listOf(
                    MultiSelectItem(1, "Option 1"),
                    MultiSelectItem(2, "Option 2"),
                    MultiSelectItem(3, "Option 2"),
                    MultiSelectItem(4, "Option 2"),
                    MultiSelectItem(5, "Option 3"),
                    MultiSelectItem(6, "Option 3"),
                    MultiSelectItem(7, "Option 3"),
                    MultiSelectItem(8, "Option 3"),
                    MultiSelectItem(9, "Option 3"),
                    MultiSelectItem(10, "Option 3"),
                    MultiSelectItem(11, "Option 3"),
                    MultiSelectItem(12, "Option 3"),
                    MultiSelectItem(13, "Option 3"),
                ),
                lastSelectedItem = lastSelectedItem,
                onConfirmClicked = { selectedOption ->
                    onSelectOption(selectedOption)
                },
                onCloseClicked = {
                    onSelectOption(null)
                }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewListWithSearchBottomSheet() {
    ListWithSearchBottomSheet(
        title = "Select an Option", listItems = listOf(
            MultiSelectItem(1, "Option 1"),
            MultiSelectItem(2, "Option 2"),
            MultiSelectItem(3, "Option 3")
        ), onConfirmClicked = { selectedOption ->

        }, onCloseClicked = {

        }
    )
}