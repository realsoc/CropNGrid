package com.realsoc.cropngrid.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun CropNGridDialog(
    onDismissRequest: () -> Unit,
    @StringRes titleId: Int,
    onConfirmRequest : () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(stringResource(titleId), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                content()
                Spacer(Modifier.height(10.dp))
                DialogButtons(
                    onDismissRequest = onDismissRequest,
                    onConfirm = onConfirmRequest
                )
            }
        }
    }
}