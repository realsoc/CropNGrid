package com.realsoc.image_cropper.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateRotateBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawTransform
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.realsoc.image_cropper.MainViewModel
import com.realsoc.image_cropper.frame
import com.realsoc.image_cropper.getBitmap
import com.realsoc.image_cropper.getWidthAndLengthFromRatio
import com.realsoc.image_cropper.minus
import com.realsoc.image_cropper.scale
import com.realsoc.image_cropper.toUri
import kotlinx.coroutines.launch
import java.lang.Float.max
import java.lang.Float.min

val Context.testBitmap: Bitmap
    get() {
        return BitmapFactory.decodeStream(resources.assets.open("test_image.png"))
    }

@Composable
fun LoadBitmap(encodedUri: String?, onLoaded: (Bitmap) -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(encodedUri) {
        // Todo : what if uri null
        with(context) {
            encodedUri?.let { contentResolver.getBitmap(it.toUri()) } ?: testBitmap
        }.let {
            onLoaded(it)
        }
    }
}

fun getOffsetToKeepImageInCropArea(askedOffset: Offset, scale: Float, imageSize: Size, cropArea: Rect) {
    // Todo : calculate delta scale rotation offset to keep image in screen and apply
    // Todo : crop image in column and row for real
    // TODO : show confirmation dialog to crop (do you want to crop this ?)
    // TOdo : create menu with crop, ratio, column and row number, fetch image, cancel
    // todo : test
    // Todo : Animation to show how the image is going to be cut
    // todo : cleean
    // todo : jenkins publi play store
    // todo : publi play store
    // todo : repondre stackoverflow code + gif rectangle
    // todo : article
    val toto = Offset(0f, 0f) - Size(0f, 0f)
}



@Composable
fun CropScreen(encodedUri: String? = null, plusClicked: () -> Unit) {
    val viewModel: MainViewModel = viewModel()

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var gridArea by remember { mutableStateOf(Rect(0f, 0f, 100f, 100f)) }

    var minScale by remember { mutableFloatStateOf(1f) }

    var transformationState by remember { mutableStateOf(TransformationState()) }

    var rowNumberGA by remember { mutableIntStateOf(3) }
    var columnNumberGA by remember { mutableIntStateOf(3) }
    var ratio by remember { mutableFloatStateOf(1f) }

    LoadBitmap(encodedUri = encodedUri) {
        bitmap = it
    }

    // Setup initial state when bitmap and grid area are loaded
    LaunchedEffect(bitmap, gridArea) {
        bitmap?.let { bitmap ->
            // The last scale allowing the image to fit entirely in the grid
            minScale = max(gridArea.width / bitmap.width, gridArea.height / bitmap.height)

            println("Scale is $minScale")
            animateToInitialState(bitmap.frame, gridArea, transformationState) { state, _ ->
                transformationState = state
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
            transformationState = transformationState.applyChange(zoomChange, offsetChange.toVector(), rotationChange, minScale)
        }

        Box(
            Modifier
                .weight(1f)
                .clipToBounds()
                .background(Color.Black)
                .drawWithContent {
                    drawContent()
                    drawGridArea(rowNumberGA, columnNumberGA, ratio) { coordinates: Rect ->
                        gridArea = coordinates
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            val coroutineScope = rememberCoroutineScope()

            BoxWithConstraints(
                Modifier
                    .transformable(state = state)
                    .pointerInput(Unit) {
                        this.detectTapGestures(
                            onDoubleTap = {
                                coroutineScope.launch {
                                    animateToInitialState(
                                        bitmap!!.frame,
                                        gridArea,
                                        transformationState
                                    ) { state, _ -> transformationState = state }
                                }
                            }
                        )
                    }
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                bitmap?.let { bitmap ->
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        clipRect(0f, 0f, constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat()) {
                            withTransform({
                                translate(transformationState.translation)
                                rotate(transformationState.rotation, gridArea.center)
                                scale(transformationState.scale, gridArea.center)
                            }) {
                                drawImage(bitmap.asImageBitmap())
                            }
                        }

                    }
                }
            }
        }

        // Todo : Transform this part in an animated dialog to confirm the crop
        Box(
            Modifier
                .weight(1f)
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.TopStart
        ) {
            val rects = getCropGrid(gridArea, rowNumberGA, columnNumberGA)
            bitmap?.let {
                SaveImage(source = it, areas = rects, transformationState = transformationState, pivot = gridArea
                    .center, gridArea.topLeft)
            }
        }

        // Todo : create a real menu
        Row(
            Modifier
                .height(40.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val coroutineScope = rememberCoroutineScope()
            Button({
                coroutineScope.launch {
                    state.animateRotateBy(90f)
                }
            }) {
                Icon(Icons.Filled.Refresh, "turn")
            }
            Button({
                viewModel.closeCrop()
            }) {
                Icon(Icons.Filled.Close, "close")
            }
            Button({
                plusClicked()
            }) {
                Icon(Icons.Filled.Add, "close")
            }
        }
    }
}

@Composable
fun BoxWithConstraintsScope.ShowRect(modifier: Modifier = Modifier, bitmap: Bitmap, area: Rect, transformationState:
TransformationState, pivot: Offset) {
    val size = Size(area.width, area.height)
    val zero = Offset(0f, 0f)

    Canvas(modifier = modifier
        .fillMaxSize()
    ) {

        clipRect(0f, 0f, area.width, area.height) {
            drawRect(Color.Gray, zero, size)
            withTransform({
                translate(transformationState.translation - area.topLeft.toVector())

                rotate(transformationState.rotation, pivot)
                scale(transformationState.scale, pivot)
            }) {
                drawImage(bitmap.asImageBitmap())
            }
        }
    }
}

fun DrawTransform.translate(vector: Vector) {
    translate(vector.x, vector.y)
}

@Composable
fun SaveImage(
    source: Bitmap,
    areas: List<Rect>,
    transformationState: TransformationState,
    pivot: Offset,
    topLeft: Offset
){
    var transformationStateFinal = transformationState
    var areasFinal = areas
    var topLeftFinal = topLeft


    // Scale around pivot
    if (transformationState.scale != 0f && transformationState.scale < 1f) {
        val newScale = 1 / transformationState.scale
        transformationStateFinal = transformationState.scale(newScale)
        areasFinal = areasFinal.map { it.scale(newScale, pivot) }
        topLeftFinal = topLeftFinal.scale(newScale, pivot)
    }


    for (rect in areasFinal) {
        val (widthDp, heightDp) = with(LocalDensity.current) { rect.width.toDp() to rect.height.toDp() }
        val (topDp, leftDp) = with(LocalDensity.current) {
            (rect.top - topLeftFinal.y).toDp() to (rect.left - topLeftFinal.x).toDp()
        }

        BoxWithConstraints(modifier = Modifier
            .offset(x = leftDp, y = topDp)
            .size(widthDp, heightDp),
            contentAlignment = Alignment.TopStart) {
            ShowRect(bitmap = source, area = rect, transformationState = transformationStateFinal, pivot = pivot)
        }
    }

}


/**
 * Draw the grid area which is an overlay, a grid
 */
fun DrawScope.drawGridArea(
    rowNumber: Int,
    columnNumber: Int,
    ratio: Float,
    onGridCoordinates: (coordinates: Rect) -> Unit
) {
    if (rowNumber <= 0 || columnNumber <= 0 || ratio <= 0f) {
        throw RuntimeException("Row number ($rowNumber), column " +
                "number ($columnNumber) and ratio ($ratio) should be strictly positive")
    }

    val margin = 200
    val windowBackground = Color.Black.copy(alpha = 0.6f)

    val limitingSide = min(size.width, size.height) - margin

    val (width, height) = getWidthAndLengthFromRatio(limitingSide, ratio)

    val middleHorizontal = size.width / 2
    val middleVertical = size.height / 2

    val start = middleHorizontal - width / 2
    val top = middleVertical - height / 2

    val gridCoordinates = Rect(Offset(start, top), Size(width, height))
    onGridCoordinates(gridCoordinates)

    clipRect(
        left = gridCoordinates.left,
        right = gridCoordinates.right,
        top = gridCoordinates.top,
        bottom = gridCoordinates.bottom,
        clipOp = ClipOp.Difference
    ) {
        drawRect(windowBackground)
    }
    drawCropGrid(gridCoordinates, rowNumber, columnNumber)
}

/**
 * Draw a grid on a DrawScope
 *
 * @param gridCoordinates
 * @param rowNumber
 * @param columnNumber
 */
fun DrawScope.drawCropGrid(gridCoordinates: Rect, rowNumber: Int, columnNumber: Int) {
    val horizontalStep = gridCoordinates.width / columnNumber
    val verticalStep = gridCoordinates.height / rowNumber

    translate(left = gridCoordinates.left, top = gridCoordinates.top) {
        for (i in 0..rowNumber) {
            drawLine(Color.White, Offset(i * horizontalStep, 0f), Offset(i * horizontalStep, gridCoordinates.height))
        }
        for (j in 0..columnNumber) {
            drawLine(Color.White, Offset(0f, j * verticalStep), Offset(gridCoordinates.width, j * verticalStep))
        }
    }
}

/**
 * Get the crop grid as a list of cell (Rect)
 */
fun getCropGrid(gridCoordinates: Rect, rowNumber: Int, columnNumber: Int): List<Rect> {
    val horizontalStep = gridCoordinates.width / columnNumber
    val verticalStep = gridCoordinates.height / rowNumber
    val areas = mutableListOf<Rect>()
    for (i in 0 until rowNumber) {
        for (j in 0 until columnNumber) {
                areas.add(Rect(
                    gridCoordinates.left + j * horizontalStep,
                    gridCoordinates.top + i * verticalStep,
                    gridCoordinates.left + (j + 1) * horizontalStep,
                    gridCoordinates.top + (i + 1) * verticalStep))
        }
    }
    return areas
}

data class TransformationState(
    val rotation: Float = 0f,
    val translation: Vector = Vector(0f, 0f),
    val scale: Float = 1f
) {
    fun applyChange(zoomChange: Float, translationChange: Vector, rotationChange: Float, minScale: Float):
            TransformationState {
        return copy(
            scale = max(scale * zoomChange, minScale),
            rotation = (rotation + rotationChange) % 360,
            translation = translation + translationChange
        )
    }

    fun scale(newScale: Float): TransformationState {
        return copy(scale = scale * newScale, rotation = rotation, translation = translation * newScale)
    }
}

/**
 * Animate to the initial state
 */
suspend fun animateToInitialState(
    imageFrame: Rect,
    gridArea: Rect,
    currentValue: TransformationState,
    onNewStateAndVelocity: (TransformationState, TransformationState) -> Unit
) {
    val fitAroundTransformation = getFitAroundTransformation(imageFrame, gridArea)
    animate(
        TransformationStateConverter,
        currentValue,
        targetValue = fitAroundTransformation,//TransformationState(targetRotation, targetTranslation, scaleToFit),
        animationSpec = tween(500),
        block = onNewStateAndVelocity
    )
}

/**
 * Given a scale, returns the vector that let the image well centered on the grid area
 *
 * @param imageFrame representing the image to transform
 * @param gridArea is the area around which the image should be centered
 */
fun getFitAroundTransformation(imageFrame: Rect, gridArea: Rect): TransformationState {
    val pivot = gridArea.center.toPoint()

    val scaleToFit = imageFrame.getScaleToFit(gridArea)

    val newImageFrame = imageFrame.scale(scaleToFit, pivot)

    val deltaSize = newImageFrame.size - gridArea.size

    val topLeftOfCenteredImage = gridArea.topLeft.toPoint() - deltaSize.toVector() / 2f

    val centeringTranslation = newImageFrame.topLeft.toPoint() vectorTo topLeftOfCenteredImage

    return TransformationState(0f, centeringTranslation, scaleToFit)
}



/**
 * Given a scale, returns the vector that let the image well centered on the grid area
 *
 * @param topLeft corner of the grid area
 * @param originalSize of the picture
 * @param scale for what the offset is calculated
 */
fun getFitAroundTransformation(topLeft: Point, originalSize: Size, scale: Float): Vector {
    // The delta that is going to be off the screen for a size = originalSize * scale | delta = originalSize - newSize
    val delta = (originalSize * (1 - scale))
    // Spread the delta on both side and transform the resulting vector with the current scale
    // Only the half of delta should be subtracted to topLeft to have it centered
    val offset = Offset(topLeft.x - delta.width / 2f, topLeft.y - delta.height / 2f)

    return (offset * scale).toVector()

    //return offset.transform(scale = scale).toVector()
    //return  Vector(0f, 0f,)
}

object TransformationStateConverter: TwoWayConverter<TransformationState, AnimationVector4D> {
    override val convertFromVector: (AnimationVector4D) -> TransformationState
        get() = { animationVector4d: AnimationVector4D ->
            TransformationState(
                animationVector4d.v1,
                Vector(animationVector4d.v2, animationVector4d.v3),
                animationVector4d.v4
            )
        }
    override val convertToVector: (TransformationState) -> AnimationVector4D
        get() = { transformationState ->
            AnimationVector4D(
                transformationState.rotation,
                transformationState.translation.x,
                transformationState.translation.y,
                transformationState.scale
            )
        }
}

// BS after here

// TOdo : use it to create an answer to stackoverflow
@Composable
fun BoxWithConstraintsScope.ShowCropped(
    modifier: Modifier = Modifier,
    bitmap: Bitmap,
    transformationState: TransformationState,
    origin: Offset,
    rowNumber: Int,
    columnNumber: Int,
    pivot: Offset
) {
    val size = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
    val zero = Offset(0f, 0f)

    Canvas(modifier = modifier
        .fillMaxSize()
        .align(Alignment.Center)) {

        clipRect(0f, 0f, size.width, size.height) {
            drawRect(Color.Black, zero, size)
            //println("bottom transformation state : $transformationState, pivot $pivot ")
            withTransform({
                translate(transformationState.translation)
                translate(-origin.x, -origin.y)

                rotate(transformationState.rotation, pivot)
                scale(transformationState.scale, pivot)
            }) {
                drawImage(bitmap.asImageBitmap())
            }
            drawCropGrid(Rect(zero, size), rowNumber, columnNumber)

            val topLeft = Offset(0f, 0f).transform(pivot, transformationState) - origin
            val topRight = Offset(bitmap.width.toFloat(), 0f).transform(pivot, transformationState) - origin
            val bottomLeft = Offset(0f, bitmap.height.toFloat()).transform(pivot, transformationState) -
                    origin
            val bottomRight = Offset(bitmap.width.toFloat(), bitmap.height.toFloat()).transform(pivot,
                transformationState) - origin

            drawLine(Color.Red, start = topLeft, end = topRight, strokeWidth = 5f)
            drawLine(Color.Red, start = bottomRight, end = topRight, strokeWidth = 5f)
            drawLine(Color.Red, start = bottomRight, end = bottomLeft, strokeWidth = 5f)
            drawLine(Color.Red, start = topLeft, end = bottomLeft, strokeWidth = 5f)
        }
    }
}

fun Rect.getScaleToFit(other: Rect): Float = max(other.width / width, other.height / height)

/*
@Composable
fun CropScreenSaved(encodedUri: String? = null, plusClicked: () -> Unit) {

    var imageRectangle by remember { mutableStateOf(Rectangle()) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    var cropRect by remember { mutableStateOf(Rect(0f, 0f , 100f, 100f)) }
    val viewModel: MainViewModel = viewModel()

    LaunchedEffect(encodedUri) {
        with(context) {
            encodedUri?.let { contentResolver.getBitmap(it.toUri()) } ?: testBitmap
        }.let {
            bitmap = it
        }
    }

    Column(Modifier.fillMaxSize()) {
        var scale by remember { mutableFloatStateOf(1f) }
        var rotation by remember { mutableFloatStateOf(0f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        // let's create a modifier state to specify how to update our UI state defined above
        val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
            // note: scale goes by factor, not an absolute difference, so we need to multiply it
            // for this example, we don't allow downscaling, so cap it to 1f
            // Check if possible

            scale = Float.max(scale * zoomChange, 1f)
            rotation += rotationChange
            offset += offsetChange
        }
        Box(
            Modifier
                .weight(1f)
                .clipToBounds()
                .background(Color.Black)
                .drawWithContent {
                    drawContent()
                    drawGridArea(3, 3, 1f) { coordinates ->
                        cropRect = coordinates
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            val coroutineScope = rememberCoroutineScope()

            Box(
                Modifier
                    // apply pan offset state as a layout transformation before other modifiers
                    .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                    // add transformable to listen to multitouch transformation events after offset
                    .transformable(state = state)
                    // optional for example: add double click to zoom
                    .pointerInput(Unit) {
                        this.detectTapGestures(
                            onDoubleTap = {
                                coroutineScope.launch {
                                    state.animateZoomBy(4f)
                                }
                            }
                        )
                    }
                    .fillMaxSize()
                    .border(1.dp, Color.Green),
                contentAlignment = Alignment.Center
            ) {
                bitmap?.let { bitmap ->
                    Image(bitmap = bitmap.asImageBitmap(),
                        "Bitmap",
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                rotationZ = rotation
                            }
                            .onGloballyPositioned {
                                val topLeft = it.positionInParent()
                                val size = it.size * scale
                                val center = it.boundsInParent().center

                                imageRectangle = Rectangle(center, topLeft, width = size.width)
                            })
                }
                Canvas(
                    Modifier
                        .fillMaxSize()
                        .border(10.dp, Color.Magenta)
                ) {

                    drawLine(
                        Color.Red,
                        strokeWidth = 10f,
                        start = imageRectangle.topLeft,
                        end = imageRectangle.topRight
                    )
                    drawLine(
                        Color.Red,
                        strokeWidth = 10f,
                        start = imageRectangle.topRight,
                        end = imageRectangle.bottomRight
                    )
                    drawLine(
                        Color.Yellow,
                        strokeWidth = 10f,
                        start = imageRectangle.bottomRight,
                        end = imageRectangle.bottomLeft
                    )
                    drawLine(
                        Color.Yellow, strokeWidth = 10f, start = imageRectangle.bottomLeft, end = imageRectangle
                            .topLeft
                    )

                }

            }
        }
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Blue)) {
            Modifier.height(with(LocalDensity.current) { 100.toDp() })
            val (x,y) = with(LocalDensity.current) { cropRect.topLeft.x.toDp() to cropRect.topLeft.y.toDp() }
            val (width,height) = with(LocalDensity.current) { cropRect.width.toDp() to cropRect.height.toDp() }
            val bitm = try {
                test(imageRectangle, bitmap!!, cropRect, offset)
            } catch (e: Exception) {
                null
            }
            Box(
                Modifier
                    .offset(x, y)
                    .size(width, height)
                    .background(Color.Yellow)) {
                bitm?.let {
                    Image(bitmap = it.asImageBitmap(), contentDescription = "")
                }
            }
        }
        Row(
            Modifier
                .height(40.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val coroutineScope = rememberCoroutineScope()
            Button({
                coroutineScope.launch {
                    state.animateRotateBy(90f)
                }
            }) {
                Icon(Icons.Filled.Refresh, "turn")
            }
            Button({
                viewModel.closeCrop()
            }) {
                Icon(Icons.Filled.Close, "close")
            }
            Button({
                plusClicked()
            }) {
                Icon(Icons.Filled.Add, "close")
            }
        }
    }
}
 */