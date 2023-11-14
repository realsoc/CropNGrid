package com.realsoc.cropngrid.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@Composable
fun CropNGridButton(@StringRes textId: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick,
        modifier = modifier
            .padding(20.dp)) {
        Text(
            stringResource(textId),
            style = MaterialTheme.typography.titleMedium.copy(letterSpacing = TextUnit(1.5f, TextUnitType.Sp))
        )
    }
}