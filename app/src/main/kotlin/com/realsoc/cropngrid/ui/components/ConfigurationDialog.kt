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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.realsoc.cropngrid.R
import com.realsoc.cropngrid.ui.models.GridParameters

@Composable
fun ConfigurationDialog(
    onDismissRequest: () -> Unit,
    gridParameters: GridParameters,
    onGridParameters: (GridParameters) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(modifier = Modifier.padding(20.dp)) {
                var tmpGridParameters by remember { mutableStateOf(gridParameters) }
                Text(stringResource(R.string.grid_parameters), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                GridParametersLayout(
                    gridParameters = tmpGridParameters,
                    onGridParameters = { callback -> tmpGridParameters = callback(tmpGridParameters) },
                )
                Spacer(Modifier.height(10.dp))
                DialogButtons(
                    onDismissRequest = onDismissRequest,
                    onConfirm = {
                        onGridParameters(tmpGridParameters)
                        onDismissRequest()
                    })
            }
        }
    }
}
