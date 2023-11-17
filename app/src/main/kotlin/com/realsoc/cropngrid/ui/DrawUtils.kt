package com.realsoc.cropngrid.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawTransform
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.realsoc.cropngrid.ui.models.CoordinateSystem
import com.realsoc.cropngrid.ui.models.GridParameters
import com.realsoc.cropngrid.ui.models.Transformation
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

// Classes
data class Vector(val x: Float, val y: Float) {
    operator fun unaryMinus() = Vector(-x, -y)
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
    operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)

    companion object {
        val NULL = Vector(0f, 0f)
        val X = Vector(1f, 0f)
        val Y = Vector(0f, 1f)
    }
}

data class Point(val x: Float, val y: Float) {
    companion object {
        val ZERO = Point(0f, 0f)
    }
}

// Functions


fun cosDeg(angle: Float): Float = cos(angle.toRad()).toFloat()
fun sinDeg(angle: Float): Float = sin(angle.toRad()).toFloat()

fun canvasTransformation(coordinateSystem: CoordinateSystem): DrawTransform.() -> Unit {
    return {
        translate(coordinateSystem.transformation.translation)
        rotate(coordinateSystem.transformation.rotation, coordinateSystem.pivot)
        scale(coordinateSystem.transformation.scale, coordinateSystem.pivot)
    }
}

// Extensions

val Context.testBitmap: Bitmap
    get() {
        return BitmapFactory.decodeStream(resources.assets.open("test_image.png"))
    }

fun DrawTransform.translate(vector: Vector) {
    translate(vector.x, vector.y)
}

fun DrawTransform.rotate(degrees: Float, pivot: Point) {
    rotate(degrees, pivot.toOffset())
}

fun DrawTransform.scale(scale: Float, pivot: Point) {
    scale(scale, pivot.toOffset())
}


fun Offset.toPoint(): Point = Point(x, y)
fun Offset.toVector(): Vector = Vector(x, y)

fun Point.toOffset(): Offset = Offset(x, y)
fun Vector.toOffset(): Offset = Offset(x, y)

fun Rect.scale(scale: Float, pivot: Point): Rect {
    val vectorToTopLeft = pivot vectorTo topLeft.toPoint()
    val newTopLeft = pivot + (vectorToTopLeft * scale)
    val newSize = size * scale
    return Rect(newTopLeft.toOffset(), newSize)
}

infix fun Point.vectorTo(that: Point): Vector = that - this
operator fun Point.minus(other: Point) = Vector(x - other.x, y - other.y)
operator fun Point.plus(vector: Vector) = Point(x + vector.x, y + vector.y)
operator fun Point.minus(vector: Vector) = Point(x - vector.x, y - vector.y)
operator fun Vector.plus(point: Point): Point = point + this
operator fun Vector.times(scalar: Float) = Vector(x * scalar, y * scalar)
operator fun Float.times(vector: Vector) = Vector(this * vector.x, this * vector.y)
operator fun Vector.div(scalar: Float): Vector = Vector(x / scalar, y / scalar)

private fun Offset.rotate(angle: Float) = Offset(
    x * cosDeg(angle) - y * sinDeg(angle),
    x * sinDeg(angle) + y * cosDeg(angle)
)

fun Point.rotate(pivot: Point, angle: Float) = Point(
    (x - pivot.x) * cosDeg(angle) - (y - pivot.y) * sinDeg(angle) + pivot.x,
    (x - pivot.x) * sinDeg(angle) + (y - pivot.y) * cosDeg(angle) + pivot.y
)

fun Vector.rotate(angle: Float) = Vector(
    x * cosDeg(angle) - y * sinDeg(angle),
    x * sinDeg(angle) + y * cosDeg(angle)
)

fun Float.toRad(): Double = Math.toRadians(toDouble())

fun Point.transform(
    pivot: Point,
    translation: Vector = Vector(0f, 0f),
    scale: Float = 1f,
    rotation: Float = 0f
): Point = (pivot vectorTo this).rotate(rotation) * scale + translation + pivot

fun Offset.transform(
    pivot: Offset = Offset(0f, 0f), translate: Offset = Offset(0f, 0f), scale: Float = 1f, rotation:
    Float = 0f
): Offset {
    return (this - pivot).rotate(rotation) * scale + translate + pivot
}

fun Offset.transform(pivot: Offset = Offset(0f, 0f), transformation: Transformation = Transformation()): Offset {
    return transform(
        pivot, transformation.translation.toOffset(), transformation.scale, transformation
            .rotation
    )
}


fun DrawScope.drawTextOverlay(
    text: String,
    textMeasurer: TextMeasurer,
    textColor: Color,
    backgroundColor: Color
) {
    val size = this.size

    val textLayoutResult: TextLayoutResult =
        textMeasurer.measure(
            text = AnnotatedString(text),
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        )

    val middle = Offset(size.width / 2, size.height / 2)
    val textOffset = Offset(textLayoutResult.size.width / 2f , textLayoutResult.size.height /2f)
    drawCircle(color = backgroundColor, radius = max(textLayoutResult.size.height.toFloat(), textLayoutResult.size.width.toFloat()))
    drawText(textLayoutResult, textColor, topLeft = middle - textOffset)
}


// Grid

fun DrawScope.drawRatioOverlay(
    gridCoordinates: Rect,
    gridParameters: GridParameters,
    textMeasurer: TextMeasurer
) {
    val horizontalStep = gridCoordinates.width / gridParameters.columnNumber
    val verticalStep = gridCoordinates.height / gridParameters.rowNumber

    translate(left = gridCoordinates.left, top = gridCoordinates.top) {
        val ratioOverlayColor = Color.White.copy(alpha = 0.3f)

        val (width, height) = when (gridParameters.ratioMode) {
            GridParameters.RatioMode.RatioForImage -> gridCoordinates.width to gridCoordinates.height
            GridParameters.RatioMode.RatioForItem -> horizontalStep to verticalStep
        }

        drawRect(ratioOverlayColor, topLeft = Offset(0f, 0f), Size(width, height))


        val textLayoutResult: TextLayoutResult =
            textMeasurer.measure(
                text = AnnotatedString(gridParameters.ratioAsString),
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            )

        val middle = Offset(width / 2, height / 2)
        val offset = Offset(textLayoutResult.size.width / 2f, textLayoutResult.size.height /2f)
        drawText(textLayoutResult, Color.White, topLeft = middle - offset)
    }
}

fun calculateGridArea(
    gridParameters: GridParameters,
    maxWidth: Float,
    maxHeight: Float,
    margin: Int = 0
): Rect {
    val limitingSide = java.lang.Float.min(maxWidth, maxHeight) - margin * 2

    val middleHorizontal = maxWidth / 2
    val middleVertical = maxHeight / 2

    val (width, height) = gridParameters.getWidthAndHeight(limitingSide)

    val start = middleHorizontal - width / 2
    val top = middleVertical - height / 2

    return Rect(Offset(start, top), Size(width, height))
}

/**
 * Draw the grid area which is an black overlay, a centered focused (transparent) area and a grid
 *
 * @param gridParameters
 * @param gridCoordinates
 */
fun DrawScope.drawGridArea(
    gridCoordinates: Rect,
    gridParameters: GridParameters
) {
    val windowBackground = Color.Black.copy(alpha = 0.6f)

    clipRect(
        left = gridCoordinates.left,
        right = gridCoordinates.right,
        top = gridCoordinates.top,
        bottom = gridCoordinates.bottom,
        clipOp = ClipOp.Difference
    ) {
        drawRect(windowBackground)
    }
    drawGrid(gridCoordinates, gridParameters)
}

/**
 * Draw a grid on a DrawScope
 *
 * @param gridCoordinates
 * @param gridParameters
 */
fun DrawScope.drawGrid(gridCoordinates: Rect, gridParameters: GridParameters) {
    val horizontalStep = gridCoordinates.width / gridParameters.columnNumber
    val verticalStep = gridCoordinates.height / gridParameters.rowNumber

    translate(left = gridCoordinates.left, top = gridCoordinates.top) {

        for (j in 0..gridParameters.rowNumber) {
            drawLine(Color.White, Offset(0f, j * verticalStep), Offset(gridCoordinates.width, j * verticalStep))
        }
        for (i in 0..gridParameters.columnNumber) {
            drawLine(Color.White, Offset(i * horizontalStep, 0f), Offset(i * horizontalStep, gridCoordinates.height))
        }
    }
}

/**
 * Get the crop grid as a list of cell (Rect)
 */
fun getCropGrid(gridCoordinates: Rect, gridParameters: GridParameters): List<List<Rect>> {
    val horizontalStep = gridCoordinates.width / gridParameters.columnNumber
    val verticalStep = gridCoordinates.height / gridParameters.rowNumber

    return (0..<gridParameters.rowNumber).map { rowNumber ->
        (0..<gridParameters.columnNumber).map { columnNumber ->
            Rect(
                gridCoordinates.left + columnNumber * horizontalStep,
                gridCoordinates.top + rowNumber * verticalStep,
                gridCoordinates.left + (columnNumber + 1) * horizontalStep,
                gridCoordinates.top + (rowNumber + 1) * verticalStep
            )
        }
    }
}