package com.realsoc.cropngrid.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints


// From https://stackoverflow.com/a/73357119/7503613
interface DimensionScope {
    var size: Size
}

// Snapshot state so composables reading the size recompose when the layout pass updates it
class DimensionScopeImpl : DimensionScope {
    override var size: Size by mutableStateOf(Size.Zero)
}

@Composable
fun DimensionLayout(
    modifier: Modifier = Modifier,
    content: @Composable DimensionScope.() -> Unit
) {
    val dimensionScope = remember{DimensionScopeImpl()}

    Layout(
        modifier = modifier,
        // 🔥 since we invoke it here it will have Size.Zero
        // on Composition then will have size value below
        content = { dimensionScope.content() }
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val placeables = measurables.map { measurable: Measurable ->
            measurable.measure(constraints)
        }

        val maxWidth = placeables.maxOf { it.width }
        val maxHeight = placeables.maxOf { it.height }

        dimensionScope.size = Size(maxWidth.toFloat(), maxHeight.toFloat())

        layout(maxWidth, maxHeight) {
            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, 0)
            }
        }
    }
}