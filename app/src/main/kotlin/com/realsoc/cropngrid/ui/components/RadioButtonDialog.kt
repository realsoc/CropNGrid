package com.realsoc.cropngrid.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun RadioButtonDialog(
    title: String,
    defaultSelectedItem: Int,
    items: List<String>,
    onConfirm: (position: Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = modifier) {
            Column(modifier = Modifier.padding(16.dp)){
                var selectedItem by remember { mutableIntStateOf(defaultSelectedItem) }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Column(Modifier.fillMaxWidth()) {
                    items.forEachIndexed { index, item ->
                        Row {
                            RadioButton(selected = index == selectedItem, onClick = { selectedItem = index })
                            Text(item, Modifier.align(CenterVertically))
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Dismiss")
                    }
                    Button(onClick = {onConfirm(selectedItem)}) {
                        Text("Confirm")
                    }
                }
            }

        }
    }
}

@Preview
@Composable
fun RadioButtonDialogPreview() {
    val items = listOf("Element 1", "Element 2", "Element 3")
    val defaultSelected = 2
    RadioButtonDialog(
        "Elements",
        defaultSelectedItem = defaultSelected,
        items = items,
        onConfirm = { println("$it selected") },
        onDismiss = { println("Dismissed") })
}