package com.ovasta.logisticsapp.presentation.profile.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray900
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.components.sharedComposable.ConfirmButton
import com.ovasta.logisticsapp.base.lgMedium
import com.ovasta.logisticsapp.base.local.AppLanguage
import com.ovasta.logisticsapp.base.mdMedium
import kotlinx.coroutines.launch

/**
 * Lets the agent pick the app language. The pick is only applied on Confirm, so the agent
 * can move between options first.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageBottomSheet(
    currentLanguage: AppLanguage,
    onConfirm: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var selectedLanguage by rememberSaveable(currentLanguage) { mutableStateOf(currentLanguage) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Base_white
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.language),
                    style = lgMedium.copy(color = Gray900),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))

                AppLanguage.entries.forEach { language ->
                    LanguageOptionRow(
                        language = language,
                        isSelected = language == selectedLanguage,
                        onClick = { selectedLanguage = language }
                    )
                }
            }

            ConfirmButton(isEnabled = selectedLanguage != currentLanguage) {
                // Close the sheet before applying, as applying recreates the screen
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    onConfirm(selectedLanguage)
                }
            }
        }
    }
}

@Composable
private fun LanguageOptionRow(
    language: AppLanguage,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(
                width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                color = if (isSelected) Primary else Gray200,
                shape = shape
            )
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .padding(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            // The whole row handles the click
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = Primary)
        )
        Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
        Text(
            text = stringResource(language.nameRes),
            style = mdMedium.copy(color = Gray900),
            modifier = Modifier.weight(1f)
        )
    }
}
