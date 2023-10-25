package com.realsoc.image_cropper.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.realsoc.image_cropper.ui.calculateGridArea
import com.realsoc.image_cropper.ui.drawGridArea
import com.realsoc.image_cropper.ui.drawRatioOverlay
import com.realsoc.image_cropper.ui.models.GridParameters
import com.realsoc.image_cropper.ui.models.GridParameters.Companion.RATIO_VALUES
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun GridParametersLayout(
    gridParameters: GridParameters,
    onGridParameters: ((GridParameters) -> GridParameters)-> Unit,
    modifier: Modifier = Modifier
) {
    var showRatioDialog by remember { mutableStateOf(false) }

    val selectedPosition = RATIO_VALUES.indexOfFirst { abs(it.second - gridParameters.ratio) < 0.00001f }

    if (showRatioDialog) {
        RadioButtonDialog(
            title = "Ratio",
            defaultSelectedItem = selectedPosition,
            items = RATIO_VALUES.map { it.first },
            onConfirm = { index ->
                onGridParameters { it.copy(ratio = RATIO_VALUES[index].second) }
                showRatioDialog = false },
            onDismiss = { showRatioDialog = false }
        )
    }
    Column(modifier.padding(16.dp)) {
        Row {
            Text("The ratio ", Modifier.align(CenterVertically))
            Spacer(modifier = Modifier.width(5.dp))
            OutlinedButton(onClick = { showRatioDialog = true }) {
                Text(gridParameters.ratioAsString)
            }
        }
        Column(Modifier.fillMaxWidth()) {
            Row {
                RadioButton(
                    selected = gridParameters.ratioMode == GridParameters.RatioMode.RatioForItem,
                    onClick = { onGridParameters { it.copy(ratioMode = GridParameters.RatioMode.RatioForItem) }}
                )

                Text(
                    "will apply to the cropped item",
                    Modifier.align(CenterVertically)
                )
            }
            Row {
                RadioButton(
                selected = gridParameters.ratioMode == GridParameters.RatioMode.RatioForImage,
                onClick = { onGridParameters { it.copy(ratioMode = GridParameters.RatioMode.RatioForImage) }}
            )
                Text(
                    "will apply to the whole image",
                    Modifier.align(CenterVertically)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        val margin = 100
        val marginDp = LocalDensity.current.run { margin.toDp() }

        DimensionLayout(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)) {
            val rowSizeDp = LocalDensity.current.run { size.width.toDp() }
            val columnSize = LocalDensity.current.run { size.height.toDp() }
            var columnSliderPosition by remember { mutableFloatStateOf(gridParameters.columnNumber.toFloat()) }
            Slider(
                value = gridParameters.columnNumber.toFloat(),
                onValueChange = { newValue ->
                    columnSliderPosition = newValue
                    val columnNumber = newValue.roundToInt()
                    onGridParameters { it.copy(columnNumber = columnNumber)}
                },
                steps = 3,
                valueRange = 1f..5f,
                modifier = Modifier
                    .width(columnSize)
                    .padding(horizontal = marginDp)
                    .graphicsLayer {
                        translationY = -size.height / 2
                    }
            )
            BoxWithConstraints {
                val textMeasurer = rememberTextMeasurer()

                Canvas(Modifier) {
                    val gridArea = calculateGridArea(
                        gridParameters,
                        constraints.maxWidth.toFloat(),
                        constraints.maxHeight.toFloat(),
                        margin
                    )
                    drawRatioOverlay(gridArea, gridParameters, textMeasurer)
                    drawGridArea(gridArea, gridParameters)
                }
            }
            var rowSliderPosition by remember { mutableFloatStateOf(gridParameters.rowNumber.toFloat()) }
            Slider(
                value = gridParameters.rowNumber.toFloat(),
                onValueChange = { newValue ->
                    rowSliderPosition = newValue
                    val rowNumber = newValue.roundToInt()
                    onGridParameters { it.copy(rowNumber = rowNumber)}
                },
                steps = 3,
                valueRange = 1f..5f,
                modifier = Modifier
                    .width(rowSizeDp)
                    .padding(horizontal = marginDp)
                    .graphicsLayer {
                        rotationZ = 90f
                        translationX = size.height / 2
                    }
            )
        }
    }
}

@Composable
fun SliderWithLabel(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    valueToLabel: (Float) -> String,
    steps: Int,
    labelMinWidth: Dp = 24.dp,
    onValueChange: (Float) -> Unit
) {

    Column {

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
        ) {



            val offset = getSliderOffset(
                value = value,
                valueRange = valueRange,
                boxWidth = maxWidth,
                labelWidth = labelMinWidth + 8.dp // Since we use a padding of 4.dp on either sides of the SliderLabel, we need to account for this in our calculation
            )

            SliderLabel(
                label = valueToLabel(value), minWidth = labelMinWidth, modifier = Modifier
                    .padding(start = offset)
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.fillMaxWidth()
        )

    }
}


@Composable
fun SliderLabel(label: String, minWidth: Dp, modifier: Modifier = Modifier) {
    Text(
        label,
        textAlign = TextAlign.Center,
        color = Color.White,
        maxLines = 1,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(4.dp)
            .defaultMinSize(minWidth = minWidth)
    )
}


private fun getSliderOffset(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    boxWidth: Dp,
    labelWidth: Dp
): Dp {

    val coerced = value.coerceIn(valueRange.start, valueRange.endInclusive)
    val positionFraction = calcFraction(valueRange.start, valueRange.endInclusive, coerced)

    return (boxWidth - labelWidth) * positionFraction
}


// Calculate the 0..1 fraction that `pos` value represents between `a` and `b`
private fun calcFraction(a: Float, b: Float, pos: Float) =
    (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)