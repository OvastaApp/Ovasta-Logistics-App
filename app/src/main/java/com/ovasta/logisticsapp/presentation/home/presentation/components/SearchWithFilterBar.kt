package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.components.sharedComposable.SearchBox

@Composable
fun SearchWithFilterBar(
    searchKey: String,
    onSearchKeyChange: (String) -> Unit,
    onSearchTriggered: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
    ) {
        SearchBox(
            modifier = Modifier
                .weight(1f)
                .testTag("searchBox"),
            searchKey,
            hint = stringResource(R.string.find_order),
            onSearchKeyChange = onSearchKeyChange,
            onSearchTriggered = onSearchTriggered,
            keyboardType = KeyboardType.Number
        )
    }
}


@Preview
@Composable
fun SearchWithFilterBarPreview() {
    SearchWithFilterBar(
        searchKey = "",
        onSearchKeyChange = {},
        onSearchTriggered = {}
    )
}