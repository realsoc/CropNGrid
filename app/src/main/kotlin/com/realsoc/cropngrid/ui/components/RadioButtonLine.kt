package com.realsoc.cropngrid.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioButtonLine(
    title: String,
    items: List<String>,
    selectedItem: Int,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledSelectedColor: Color = MaterialTheme.colorScheme.onSurface,
    disabledUnselectedColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(modifier) {
        var y by remember { mutableIntStateOf(0) }
        var firstBaseLine by remember { mutableIntStateOf(0) }
        Text(
            title,
            onTextLayout = { textLayoutResult ->
                firstBaseLine = textLayoutResult.firstBaseline.toInt()
            },
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)

                layout(
                    width = placeable.width,
                    height = placeable.height
                ) {
                    placeable.placeRelative(0, y - (placeable.height / 2))
                }
            }
        )
        Spacer(modifier = Modifier.width(5.dp))
        //Trace(y, if (selectedItem == 0) selectedColor else unselectedColor)
        items.forEachIndexed { index, item ->
            val selected by derivedStateOf { selectedItem == index }

            Column(horizontalAlignment = CenterHorizontally) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    // TODO : calculate y position before everything is composed otherwise it will blink
                    // todo but also not very nice
                    RadioButton(
                        selected = selected,
                        onClick = {
                            onItemClick(index)
                        },
                        modifier = Modifier
                            .layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints)
                                y = placeable.height / 2
                                layout(
                                    width = placeable.width,
                                    height = placeable.height
                                ) {
                                    placeable.placeRelative(0, 0)
                                }
                            },
                        colors = RadioButtonDefaults.colors(
                            selectedColor,
                            unselectedColor,
                            disabledSelectedColor,
                            disabledUnselectedColor
                        )
                    )
                    Text(
                        item,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selected) selectedColor else unselectedColor
                    )
                }
            }
            if (index != items.lastIndex) {
                Trace(
                    y, if (index == selectedItem || index == selectedItem - 1) selectedColor else
                        unselectedColor
                )
            }
        }
    }
}

@Composable
private fun Trace(height: Int, color: Color) {
    Spacer(modifier = Modifier
        .width(15.dp)
        .drawWithContent {
            drawLine(
                color,
                start = Offset(0f, height.toFloat()),
                end = Offset(size.width, height.toFloat()),
                strokeWidth = 5f
            )
        }
    )
}

@Preview
@Composable
fun PreviewRadioButtonLine() {
    val selectedRadioButton by remember { mutableIntStateOf(0) }
    val items = listOf("1", "2", "3", "4", "5")
    val onItemClick = { clicked: Int ->
        println("$clicked clicked")
    }
    Row {
        RadioButtonLine("Titre", items, selectedRadioButton, onItemClick)
    }
}