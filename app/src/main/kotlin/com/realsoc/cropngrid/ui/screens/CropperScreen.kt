package com.realsoc.cropngrid.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.realsoc.cropngrid.R
import com.realsoc.cropngrid.analytics.LocalAnalyticsHelper
import com.realsoc.cropngrid.analytics.TrackScreenViewEvent
import com.realsoc.cropngrid.analytics.buttonClick
import com.realsoc.cropngrid.analytics.gridCropped
import com.realsoc.cropngrid.frame
import com.realsoc.cropngrid.getBitmap
import com.realsoc.cropngrid.minus
import com.realsoc.cropngrid.ui.Point
import com.realsoc.cropngrid.ui.calculateGridArea
import com.realsoc.cropngrid.ui.canvasTransformation
import com.realsoc.cropngrid.ui.components.ConfirmCropDialog
import com.realsoc.cropngrid.ui.components.CropNGridButton
import com.realsoc.cropngrid.ui.components.DimensionLayout
import com.realsoc.cropngrid.ui.div
import com.realsoc.cropngrid.ui.drawGridArea
import com.realsoc.cropngrid.ui.getCropGrid
import com.realsoc.cropngrid.ui.minus
import com.realsoc.cropngrid.ui.models.CoordinateSystem
import com.realsoc.cropngrid.ui.models.GridParameters
import com.realsoc.cropngrid.ui.models.Transformation
import com.realsoc.cropngrid.ui.scale
import com.realsoc.cropngrid.ui.toPoint
import com.realsoc.cropngrid.ui.toVector
import com.realsoc.cropngrid.ui.vectorTo
import com.realsoc.cropngrid.viewmodels.CropperViewModel
import com.realsoc.cropngrid.viewmodels.CroppingUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Float.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val SCREEN_NAME = "cropper"

@Composable
internal fun CropperRoute(
    onCropComplete: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CropperViewModel = hiltViewModel(),
) {
    val croppingUiState by viewModel.croppingUiState.collectAsStateWithLifecycle()
    val gridParameters by viewModel.gridParametersState.collectAsStateWithLifecycle()

    CropperScreen(
        uri = viewModel.pictureUri,
        gridParameters = gridParameters,
        onGridParameters = { viewModel.updateGridParameters(it) },
        onBackClick = onBackClick,
        croppingUiState = croppingUiState,
        onCrop = viewModel::makeGrid,
        onCropDialogDismissed = viewModel::resetCroppingState,
        onCropComplete = onCropComplete,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropperScreen(
    uri: Uri,
    gridParameters: GridParameters,
    onGridParameters: (GridParameters) -> Unit,
    onBackClick: () -> Unit,
    croppingUiState: CroppingUiState?,
    onCrop: (Bitmap, Rect, List<List<Rect>>, CoordinateSystem, String?) -> Unit,
    onCropDialogDismissed: () -> Unit,
    onCropComplete: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    TrackScreenViewEvent(screenName = SCREEN_NAME)

    val analyticsHelper = LocalAnalyticsHelper.current
    val coroutineScope = rememberCoroutineScope()

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var name by remember { mutableStateOf<String?>(null) }

    var showControls by remember { mutableStateOf(false) }
    // Holder survives recompositions; a plain var would reset to the first remembered Job
    val gridControlsVisibilityJob = remember { mutableStateOf<Job?>(null) }

    var showCropDialog by remember { mutableStateOf(false) }

    var coordinateSystem by remember { mutableStateOf(CoordinateSystem()) }

    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val gridArea = remember(gridParameters, canvasSize) {
        if (canvasSize == Size.Zero) Rect.Zero
        else calculateGridArea(gridParameters, canvasSize.width, canvasSize.height)
    }

    val gridPartAreas: List<List<Rect>> = remember(gridArea, gridParameters) {
        getCropGrid(gridArea, gridParameters)
    }

    val restartHideControlsTimer = {
        gridControlsVisibilityJob.value?.cancel()
        gridControlsVisibilityJob.value = coroutineScope.launch {
            showControls = true
            delay(1500)
            showControls = false
        }
    }

    val controlsInUse = {
        gridControlsVisibilityJob.value?.cancel()
        showControls = true
    }

    LoadBitmap(uri = uri, onError = onBackClick) {
        bitmap = it
        coordinateSystem = coordinateSystem.withPivot(it.frame.center.toPoint())
        restartHideControlsTimer()
    }

    LoadName(uri = uri) {
        name = it
    }

    // Fit the image into the grid once both the bitmap and the measured grid area are known,
    // and keep the minimum zoom in sync when the grid area changes
    var initialFitDone by remember(bitmap) { mutableStateOf(false) }
    LaunchedEffect(bitmap, gridArea) {
        val loadedBitmap = bitmap ?: return@LaunchedEffect
        if (gridArea == Rect.Zero) return@LaunchedEffect
        // The last scale allowing the image to fit entirely in the grid
        val minScale = min(gridArea.width / loadedBitmap.width, gridArea.height / loadedBitmap.height)
        coordinateSystem = coordinateSystem.withMinScale(minScale)
        if (!initialFitDone) {
            initialFitDone = true
            animateToInitialState(
                loadedBitmap.frame,
                gridArea,
                coordinateSystem.pivot,
                coordinateSystem.transformation
            ) { state, _ ->
                coordinateSystem = coordinateSystem.withTransformation(state)
            }
        }
    }

    if (showCropDialog) {
        bitmap?.let {
            ConfirmCropDialog(
                source = it,
                coordinateSystem = coordinateSystem,
                gridArea = gridArea,
                gridParameters = gridParameters,
                onCropComplete,
                croppingUiState,
                onDismissRequest = {
                    showCropDialog = false
                    onCropDialogDismissed()
                },
                onConfirmCrop = {
                    analyticsHelper.gridCropped(gridParameters.columnNumber, gridParameters.rowNumber)
                    onCrop(it, gridArea, gridPartAreas, coordinateSystem, name)
                }
            )
        }
    }
    Column(
        modifier
            .fillMaxSize()) {
        val transformationState = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
            coordinateSystem = coordinateSystem.withChange(
                zoomChange,
                offsetChange.toVector(),
                rotationChange
            )
            restartHideControlsTimer()
        }

        TopAppBar(
            title = { Text(
                stringResource(R.string.grid_creator),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium)
            ) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back arrow")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme
                .surfaceColorAtElevation(1.dp))
        )

        DimensionLayout(
            Modifier
                .weight(1f)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .onSizeChanged { canvasSize = it.toSize() }
                    .background(MaterialTheme.colorScheme.surface)
                    .drawWithContent {
                        drawContent()
                        // Calculate grid area was here before. But was recalculated on every frame
                        drawGridArea(gridArea, gridParameters)
                    },
                contentAlignment = Alignment.Center
            ) {
                BoxWithConstraints(
                    Modifier
                        .transformable(state = transformationState)
                        .pointerInput(Unit) {
                            this.detectTapGestures(
                                onPress = {
                                    controlsInUse()
                                    awaitRelease()
                                    restartHideControlsTimer()
                                },
                                onDoubleTap = {
                                    bitmap?.let { loadedBitmap ->
                                        coroutineScope.launch {
                                            animateToInitialState(
                                                loadedBitmap.frame,
                                                gridArea,
                                                coordinateSystem.pivot,
                                                coordinateSystem.transformation
                                            ) { state, _ ->
                                                coordinateSystem = coordinateSystem.withTransformation(state)
                                            }
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
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    with(LocalDensity.current) {
                        val verticalMargin = (size.height * 0.10f).toDp()
                        val horizontalMargin = (size.width * 0.10f).toDp()
                        val sliderPadding = 30.dp
                        var columnSliderPosition by remember { mutableFloatStateOf(gridParameters.columnNumber.toFloat()) }
                        val rowSliderWidth = 400.dp
                        val columnSliderWidth = rowSliderWidth * 3/5 + sliderPadding
                        val columnInteractionSource = remember { MutableInteractionSource() }
                        val rowInteractionSource = remember { MutableInteractionSource() }
                        Slider(
                            value = gridParameters.columnNumber.toFloat(),
                            onValueChange = { newValue ->
                                controlsInUse()
                                columnSliderPosition = newValue
                                val columnNumber = newValue.roundToInt()
                                onGridParameters(gridParameters.copy(columnNumber = columnNumber))
                            },
                            onValueChangeFinished = { restartHideControlsTimer() },
                            steps = 1,
                            valueRange = 1f..3f,
                            modifier = Modifier
                                .width(columnSliderWidth)
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = sliderPadding)
                                .padding(bottom = verticalMargin),
                            interactionSource = columnInteractionSource,
                            thumb = {
                                SliderDefaults.Thumb(
                                    interactionSource = columnInteractionSource,
                                    thumbSize = DpSize(80.dp, 80.dp),
                                    modifier = Modifier.scale(0.3f)
                                )
                            }
                        )
                        var rowSliderPosition by remember { mutableFloatStateOf(gridParameters.rowNumber.toFloat()) }
                        Slider(
                            value = gridParameters.rowNumber.toFloat(),
                            onValueChange = { newValue ->
                                controlsInUse()
                                rowSliderPosition = newValue
                                val rowNumber = rowSliderPosition.roundToInt()
                                onGridParameters(gridParameters.copy(rowNumber = rowNumber))
                            },
                            onValueChangeFinished = { restartHideControlsTimer() },
                            steps = 3,
                            valueRange = 1f..5f,
                            modifier = Modifier
                                .width(rowSliderWidth)
                                .padding(horizontal = sliderPadding)
                                .rotate(90f)
                                .align(Alignment.Center)
                                .offset(y = -(size.width / 2).toDp() + horizontalMargin),
                            interactionSource = rowInteractionSource,
                            thumb = {
                                SliderDefaults.Thumb(
                                    interactionSource = rowInteractionSource,
                                    thumbSize = DpSize(80.dp, 80.dp),
                                    modifier = Modifier.scale(0.3f)
                                )
                            }
                        )
                    }

                }
            }

        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))) {
            CropNGridButton(
                textId = R.string.make_the_grid,
                onClick = { showCropDialog = true },
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 32.dp)
                )
        }

    }

}


@Composable
fun LoadBitmap(uri: Uri, onError: () -> Unit = {}, onLoaded: (Bitmap) -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(uri) {
        try {
            onLoaded(context.contentResolver.getBitmap(uri))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("CropNGrid", "Failed to load image $uri", e)
            onError()
        }
    }
}

@Composable
fun LoadName(uri: Uri, onLoaded: (String?) -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(uri) {
        with(context) {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex).let {
                    it.substring(0, it.lastIndexOf("."));
                }
            }
        }.let {
            onLoaded(it)
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


fun Rect.getScaleToFit(other: Rect): Float = max(other.width / width, other.height / height)
