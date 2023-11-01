package com.realsoc.cropngrid.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.realsoc.cropngrid.MainViewModel
import com.realsoc.cropngrid.frame
import com.realsoc.cropngrid.getBitmap
import com.realsoc.cropngrid.minus
import com.realsoc.cropngrid.toUri
import com.realsoc.cropngrid.ui.Point
import com.realsoc.cropngrid.ui.calculateGridArea
import com.realsoc.cropngrid.ui.canvasTransformation
import com.realsoc.cropngrid.ui.components.ConfirmCropDialog
import com.realsoc.cropngrid.ui.components.DimensionLayout
import com.realsoc.cropngrid.ui.components.GridParametersLayout
import com.realsoc.cropngrid.ui.components.SheetDragHandle
import com.realsoc.cropngrid.ui.div
import com.realsoc.cropngrid.ui.drawGrid
import com.realsoc.cropngrid.ui.drawGridArea
import com.realsoc.cropngrid.ui.getCropGrid
import com.realsoc.cropngrid.ui.minus
import com.realsoc.cropngrid.ui.models.CoordinateSystem
import com.realsoc.cropngrid.ui.models.GridParameters
import com.realsoc.cropngrid.ui.models.Transformation
import com.realsoc.cropngrid.ui.scale
import com.realsoc.cropngrid.ui.toPoint
import com.realsoc.cropngrid.ui.toVector
import com.realsoc.cropngrid.ui.transform
import com.realsoc.cropngrid.ui.translate
import com.realsoc.cropngrid.ui.vectorTo
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import java.lang.Float.max

// TODO problem for 2columns 5 row
@Composable
fun LoadBitmap(encodedUri: String, onLoaded: (Bitmap) -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(encodedUri) {
        with(context) {
            contentResolver.getBitmap(encodedUri.toUri())
        }.let {
            onLoaded(it)
        }
    }
}

@Composable
fun CropAppBar(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val uiState: Screen.Crop by viewModel.uiState.filterIsInstance<Screen.Crop>().collectAsState(Screen.Crop(""))

}

@Composable
fun CropBottomBar(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val uiState: Screen.Crop by viewModel.uiState.filterIsInstance<Screen.Crop>().collectAsState(Screen.Crop(""))

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropContent(viewModel: MainViewModel, modifier: Modifier = Modifier) {

    val uiState: Screen.Crop? by viewModel.uiState.filterIsInstance<Screen.Crop?>().collectAsState(null)

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    var pictureParts by remember { mutableStateOf<List<Rect>>(listOf()) }

    var coordinateSystem by remember { mutableStateOf(CoordinateSystem()) }

    var gridArea by remember { mutableStateOf(Rect(0f, 0f, 100f, 100f)) }

    var gridParameters by remember { mutableStateOf(GridParameters()) }

    var showDialog by remember { mutableStateOf(false) }

    uiState?.let { uiState ->
        LoadBitmap(encodedUri = uiState.encodedUri) {
            bitmap = it
            coordinateSystem = coordinateSystem.withPivot(it.frame.center.toPoint())
        }
    }

    // Setup initial state when bitmap and grid area are loaded
    LaunchedEffect(bitmap, gridArea) {
        bitmap?.let { bitmap ->
            // The last scale allowing the image to fit entirely in the grid
            val minScale = max(gridArea.width / bitmap.width, gridArea.height / bitmap.height)
            coordinateSystem = coordinateSystem.withMinScale(minScale)
            animateToInitialState(
                bitmap.frame,
                gridArea,
                coordinateSystem.pivot,
                coordinateSystem.transformation
            ) { state, _ ->
                coordinateSystem = coordinateSystem.withTransformation(state)
            }
        }
    }

    LaunchedEffect(gridArea, gridParameters) {
        pictureParts = getCropGrid(gridArea, gridParameters)
    }

    if (showDialog) {
        bitmap?.let {
            ConfirmCropDialog(
                source = it,
                coordinateSystem = coordinateSystem,
                gridArea = gridArea,
                gridParameters = gridParameters,
                onDismissRequest = { showDialog = false },
                onConfirmCrop = {
                    showDialog = false
                    // Todo : base name
                    viewModel.cropImageInParts(it, pictureParts, coordinateSystem, "Toto")
                }
            )
        }
    }
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        sheetDragHandle = { SheetDragHandle(state = bottomSheetScaffoldState) },
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            GridParametersLayout(
                gridParameters = gridParameters,
                onGridParameters = { callback -> gridParameters = callback(gridParameters) },
                modifier = Modifier.padding(20.dp)
            )
        }
    ) {
        Column(modifier.fillMaxSize()) {
            val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                coordinateSystem = coordinateSystem.withChange(
                    zoomChange,
                    offsetChange.toVector(),
                    rotationChange
                )
            }

            DimensionLayout(
                Modifier
                    .weight(1f)
                    .clipToBounds()
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .drawWithContent {
                            drawContent()
                            // Calculate grid area was here before. But was recalculated on every frame
                            drawGridArea(gridArea, gridParameters)
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
                                                coordinateSystem.pivot,
                                                coordinateSystem.transformation
                                            ) { state, _ ->
                                                coordinateSystem = coordinateSystem.withTransformation(state)
                                            }
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
                                    withTransform(canvasTransformation(coordinateSystem)) {
                                        drawImage(bitmap.asImageBitmap())
                                    }
                                }
                            }
                        }
                    }
                }
                // Calculate grid was isolated here to calculate only when it's required
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    gridArea = calculateGridArea(
                        gridParameters,
                        constraints.maxWidth.toFloat(),
                        constraints.maxHeight.toFloat()
                    )
                }
            }
            val offset = LocalDensity.current.run { bottomSheetScaffoldState.bottomSheetState.requireOffset().toDp() }
            FloatingActionButton(modifier = Modifier
                .offset(y = (-100).dp)
                .align(Alignment.End),
                onClick = { showDialog = true }) {
                Icon(Icons.Filled.Check, "")
            }
        }
    }

}


/**
 * Animate to the initial state
 */
suspend fun animateToInitialState(
    imageFrame: Rect,
    gridArea: Rect,
    pivot: Point,
    currentValue: Transformation,
    onNewStateAndVelocity: (Transformation, Transformation) -> Unit
) {
    val fitAroundTransformation = getFitAroundTransformation(imageFrame, gridArea, pivot)
    animate(
        Transformation.TransformationStateConverter,
        currentValue,
        targetValue = fitAroundTransformation,
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
fun getFitAroundTransformation(imageFrame: Rect, gridArea: Rect, pivot: Point): Transformation {
    val scaleToFit = imageFrame.getScaleToFit(gridArea)

    val newImageFrame = imageFrame.scale(scaleToFit, pivot)

    val deltaSize = newImageFrame.size - gridArea.size

    val topLeftOfCenteredImage = gridArea.topLeft.toPoint() - deltaSize.toVector() / 2f

    val centeringTranslation = newImageFrame.topLeft.toPoint() vectorTo topLeftOfCenteredImage

    return Transformation(0f, centeringTranslation, scaleToFit)
}


// BS after here

// TOdo : use it to create an answer to stackoverflow
@Composable
fun BoxWithConstraintsScope.ShowCropped(
    modifier: Modifier = Modifier,
    bitmap: Bitmap,
    transformation: Transformation,
    origin: Offset,
    gridParameters: GridParameters,
    pivot: Offset
) {
    val size = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
    val zero = Offset(0f, 0f)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .align(Alignment.Center)
    ) {

        clipRect(0f, 0f, size.width, size.height) {
            drawRect(Color.Black, zero, size)
            //println("bottom transformation state : $transformationState, pivot $pivot ")
            withTransform({
                translate(transformation.translation)
                translate(-origin.x, -origin.y)

                rotate(transformation.rotation, pivot)
                scale(transformation.scale, pivot)
            }) {
                drawImage(bitmap.asImageBitmap())
            }
            drawGrid(Rect(zero, size), gridParameters = gridParameters)

            val topLeft = Offset(0f, 0f).transform(pivot, transformation) - origin
            val topRight = Offset(bitmap.width.toFloat(), 0f).transform(pivot, transformation) - origin
            val bottomLeft = Offset(0f, bitmap.height.toFloat()).transform(pivot, transformation) -
                    origin
            val bottomRight = Offset(bitmap.width.toFloat(), bitmap.height.toFloat()).transform(
                pivot,
                transformation
            ) - origin

            drawLine(Color.Red, start = topLeft, end = topRight, strokeWidth = 5f)
            drawLine(Color.Red, start = bottomRight, end = topRight, strokeWidth = 5f)
            drawLine(Color.Red, start = bottomRight, end = bottomLeft, strokeWidth = 5f)
            drawLine(Color.Red, start = topLeft, end = bottomLeft, strokeWidth = 5f)
        }
    }
}

fun Rect.getScaleToFit(other: Rect): Float = max(other.width / width, other.height / height)
