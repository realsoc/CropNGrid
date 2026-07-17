package com.realsoc.cropandgrid.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
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
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.realsoc.cropandgrid.R
import com.realsoc.cropandgrid.analytics.LocalAnalyticsHelper
import com.realsoc.cropandgrid.analytics.TrackScreenViewEvent
import com.realsoc.cropandgrid.analytics.gridCropped
import com.realsoc.cropandgrid.frame
import com.realsoc.cropandgrid.getBitmap
import com.realsoc.cropandgrid.minus
import com.realsoc.cropandgrid.ui.Point
import com.realsoc.cropandgrid.ui.calculateGridArea
import com.realsoc.cropandgrid.ui.canvasTransformation
import com.realsoc.cropandgrid.ui.components.ConfirmCropDialog
import com.realsoc.cropandgrid.ui.components.CropNGridButton
import com.realsoc.cropandgrid.ui.components.DimensionLayout
import com.realsoc.cropandgrid.ui.div
import com.realsoc.cropandgrid.ui.drawGridArea
import com.realsoc.cropandgrid.ui.getCropGrid
import com.realsoc.cropandgrid.ui.minus
import com.realsoc.cropandgrid.ui.models.CoordinateSystem
import com.realsoc.cropandgrid.ui.models.GridParameters
import com.realsoc.cropandgrid.ui.models.Transformation
import com.realsoc.cropandgrid.ui.scale
import com.realsoc.cropandgrid.ui.toPoint
import com.realsoc.cropandgrid.ui.toVector
import com.realsoc.cropandgrid.ui.vectorTo
import com.realsoc.cropandgrid.viewmodels.CropperViewModel
import com.realsoc.cropandgrid.viewmodels.CroppingUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import java.lang.Float.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val SCREEN_NAME = "cropper"

@Composable
internal fun CropperRoute(
    coroutineScope: CoroutineScope,
    onCropComplete: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CropperViewModel = hiltViewModel(),
) {
    val croppingUiState by viewModel.croppingUiState.collectAsStateWithLifecycle()
    val gridParameters by viewModel.gridParametersState.collectAsStateWithLifecycle()

    CropperScreen(
        uri = viewModel.pictureUri,
        coroutineScope = coroutineScope,
        gridParameters = gridParameters,
        onGridParameters = { viewModel.updateGridParameters(it) },
        onBackClick = onBackClick,
        croppingUiState = croppingUiState,
        onCrop = viewModel::makeGrid,
        onCropComplete = onCropComplete,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropperScreen(
    uri: Uri,
    coroutineScope: CoroutineScope,
    gridParameters: GridParameters,
    onGridParameters: (GridParameters) -> Unit,
    onBackClick: () -> Unit,
    croppingUiState: CroppingUiState?,
    onCrop: suspend (Bitmap, Rect, List<List<Rect>>, CoordinateSystem, String?) -> Unit,
    onCropComplete: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    TrackScreenViewEvent(screenName = SCREEN_NAME)

    val analyticsHelper = LocalAnalyticsHelper.current

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var name by remember { mutableStateOf<String?>(null) }

    var showControls by remember { mutableStateOf(false) }
    var gridControlsVisibilityJob: Job = remember { Job() }

    var showCropDialog by remember { mutableStateOf(false) }

    var coordinateSystem by remember { mutableStateOf(CoordinateSystem()) }
    var gridArea by remember { mutableStateOf(Rect(0f, 0f, 100f, 100f)) }

    var gridPartAreas: List<List<Rect>> by remember { mutableStateOf(listOf()) }

    LaunchedEffect(gridArea, gridParameters) {
        gridPartAreas = getCropGrid(gridArea, gridParameters)
    }

    val controlsChannel = remember { Channel<ControlsAction>(Channel.UNLIMITED) }
    val coroutineScope = rememberCoroutineScope()

    // Single job that handles all control actions
    LaunchedEffect(Unit) {
        var hideJob: Job? = null

        controlsChannel.consumeAsFlow().collect { action ->
            when (action) {
                is ControlsAction.InUse -> {
                    hideJob?.cancel()
                    showControls = true
                }
                is ControlsAction.RestartTimer -> {
                    hideJob?.cancel()
                    showControls = true
                    hideJob = launch {
                        delay(1500)
                        showControls = false
                    }
                }
            }
        }
    }

    fun restartHideControlsTimer() {
        controlsChannel.trySend(ControlsAction.RestartTimer)
    }

    fun controlsInUse() {
        controlsChannel.trySend(ControlsAction.InUse)
    }

    LoadBitmap(uri = uri) {
        bitmap = it
        coordinateSystem = coordinateSystem.withPivot(it.frame.center.toPoint())
        restartHideControlsTimer()
    }

    LoadName(uri = uri) {
        name = it
    }

    // Setup initial state when bitmap and grid area are loaded
    LaunchedEffect(bitmap) {
        bitmap?.let { bitmap ->
            // The last scale allowing the image to fit entirely in the grid
            val minScale = min(gridArea.width / bitmap.width, gridArea.height / bitmap.height)
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

    if (showCropDialog) {
        bitmap?.let {
            ConfirmCropDialog(
                source = it,
                coordinateSystem = coordinateSystem,
                gridArea = gridArea,
                gridParameters = gridParameters,
                onCropComplete,
                croppingUiState,
                onDismissRequest = { showCropDialog = false },
                onConfirmCrop = {
                    analyticsHelper.gridCropped(gridParameters.columnNumber, gridParameters.rowNumber)
                    coroutineScope.launch { onCrop(it, gridArea, gridPartAreas, coordinateSystem, name) }
                }
            )
        }
    }
    // Screen start
    Column(
        modifier
            .fillMaxSize()) {
        // onTransformation (Zoom, Translate, Rotate)
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


        // Body
        DimensionLayout(
            Modifier
                .weight(1f)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .drawWithContent {
                        // On every frame
                        drawContent()
                        drawGridArea(gridArea, gridParameters)
                    },
                contentAlignment = Alignment.Center
            ) {

                // Draw transformed image (zoom, translate, rotate)
                // on press show controls
                // on double tap transform to initial state
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
                            clipRect(0f, 0f, this@BoxWithConstraints.constraints.maxWidth.toFloat(),
                                this@BoxWithConstraints.constraints
                                .maxHeight.toFloat()) {
                                withTransform(canvasTransformation(coordinateSystem)) {
                                    drawImage(bitmap.asImageBitmap())
                                }
                            }
                        }
                    }
                }
            }
            // Calculate grid was isolated here to calculate only when it's required, before it was in parent's
            // drawWithContent
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                gridArea = calculateGridArea(
                    gridParameters,
                    this.constraints.maxWidth.toFloat(),
                    this.constraints.maxHeight.toFloat()
                )
            }
            // Controls
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    with(LocalDensity.current) {
                        val verticalMargin = (size.height * 0.10f).toDp()
                        val horizontalMargin = (size.width * 0.10f).toDp()
                        val sliderPadding = 30.dp
                        var columnSliderPosition by remember { mutableFloatStateOf(gridParameters.columnNumber.toFloat()) }
                        val rowSliderWidth = 400.dp
                        val columnSliderWidth = rowSliderWidth * 3/5 + sliderPadding
                        val columnInteractionSource = remember { MutableInteractionSource() }
                        val rowInteractionSource = remember { MutableInteractionSource() }
                        //
                        Slider(
                            value = gridParameters.columnNumber.toFloat(),
                            onValueChange = { newValue ->
                                controlsInUse()
                                restartHideControlsTimer()
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
                        )
                        var rowSliderPosition by remember { mutableFloatStateOf(gridParameters.rowNumber.toFloat()) }
                        // Vertical
                        Slider(
                            value = gridParameters.rowNumber.toFloat(),
                            onValueChange = { newValue ->
                                controlsInUse()
                                restartHideControlsTimer()
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
                                .offset(y = -(size.width / 2).toDp() + horizontalMargin)
                                .minimumInteractiveComponentSize(),
                            interactionSource = rowInteractionSource,

                        )
                    }

                }
            }
        }
        // Button create grid
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
fun LoadBitmap(uri: Uri, onLoaded: (Bitmap) -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(uri) {
        with(context) {
            contentResolver.getBitmap(uri)
        }.let {
            onLoaded(it)
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

sealed class ControlsAction {
    object InUse : ControlsAction()
    object RestartTimer : ControlsAction()
}