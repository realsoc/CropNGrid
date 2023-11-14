package com.realsoc.cropngrid.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.realsoc.cropngrid.R

@Composable
fun ColumnScope.DialogButtons(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    enabled: Boolean = true
) {
    Row(Modifier.align(Alignment.End)) {
        TextButton(onClick = onDismissRequest, enabled = enabled) {
            Text(stringResource(R.string.cancel))
        }
        Button(onClick = onConfirm, enabled = enabled) {
            Text(stringResource(R.string.confirm))
        }
    }
}