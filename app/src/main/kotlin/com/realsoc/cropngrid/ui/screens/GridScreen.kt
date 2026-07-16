package com.realsoc.cropngrid.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.realsoc.cropngrid.R
import com.realsoc.cropngrid.analytics.LocalAnalyticsHelper
import com.realsoc.cropngrid.analytics.TrackDialogDisplayed
import com.realsoc.cropngrid.analytics.TrackScreenViewEvent
import com.realsoc.cropngrid.analytics.buttonClick
import com.realsoc.cropngrid.decode
import com.realsoc.cropngrid.getBitmap
import com.realsoc.cropngrid.models.Grid
import com.realsoc.cropngrid.models.GridUris
import com.realsoc.cropngrid.toUri
import com.realsoc.cropngrid.ui.icons.CropNGridIcons
import com.realsoc.cropngrid.ui.components.DialogButtons
import com.realsoc.cropngrid.ui.components.LoadingView
import com.realsoc.cropngrid.ui.drawTextOverlay
import com.realsoc.cropngrid.ui.icons.FilledDownload
import com.realsoc.cropngrid.viewmodels.GridUiState
import com.realsoc.cropngrid.viewmodels.GridViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private const val SCREEN_NAME = "grid"

fun Context.shareUri(uri: Uri) {
    val type = "image/*"
    val share = Intent(Intent.ACTION_SEND)
    share.type = type
    share.putExtra(Intent.EXTRA_STREAM, uri)
    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    startActivity(Intent.createChooser(share, getString(R.string.share_to)))
}

@Composable
fun GridRoute(
    onGridDeleted: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: GridViewModel = hiltViewModel(),
) {
    val gridUiState: GridUiState by viewModel.gridUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val analyticsHelper = LocalAnalyticsHelper.current

    GridScreen(
        gridUiState = gridUiState,
        onPartClick = {
            analyticsHelper.buttonClick(SCREEN_NAME, "share")
            context.shareUri(it) },
        onBackClick = onBackClick,
        onDeleteGrid = { grid ->
            analyticsHelper.buttonClick(SCREEN_NAME, "delete_confirmed")
            if (viewModel.deleteGrid(grid)) {
                onGridDeleted()
            } else {
                Toast.makeText(context, context.getString(R.string.delete_failure), Toast.LENGTH_SHORT).show()
            }
        },
        onSaveGrid = { grid ->
            analyticsHelper.buttonClick(SCREEN_NAME, "download_confirmed")
            val success = viewModel.saveGrid(grid)
            Toast.makeText(
                context,
                context.getString(if (success) R.string.download_success else R.string.download_failure),
                Toast.LENGTH_SHORT
            ).show()
        }
    )

}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun GridScreen(
    gridUiState: GridUiState,
    onPartClick: (Uri) -> Unit,
    onBackClick: () -> Unit,
    onDeleteGrid: suspend (Grid) -> Unit,
    onSaveGrid: suspend (Grid) -> Unit,
    modifier: Modifier = Modifier
) {

    TrackScreenViewEvent(screenName = SCREEN_NAME)

    val coroutineScope = rememberCoroutineScope()

    Surface(modifier) {
        var requiredAction by remember { mutableStateOf<GridScreenActions?>(null) }

        var loading by remember { mutableStateOf(false) }

        // Writing to public storage requires a runtime permission grant before Android 10
        var pendingDownload by remember { mutableStateOf<Grid?>(null) }
        val writePermissionState = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            rememberPermissionState(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) { granted ->
                pendingDownload?.let { grid ->
                    pendingDownload = null
                    if (granted) {
                        coroutineScope.launch {
                            loading = true
                            onSaveGrid(grid)
                            loading = false
                        }
                    }
                }
            }
        } else {
            null
        }

        if (loading) {
            LoadingView()
        }

        requiredAction?.let { action ->
            val (titleId, textId, callback) = when(action) {
                is GridScreenActions.Delete -> {
                    Triple(R.string.delete, R.string.delete_confirmation_question, onDeleteGrid)
                }
                is GridScreenActions.Download -> {
                    Triple(R.string.download_grid_parts, R.string.confirm_download_parts, onSaveGrid)
                }
            }

            Dialog(onDismissRequest = { requiredAction = null }) {
                val label = when(requiredAction) {
                    is GridScreenActions.Delete -> "delete"
                    is GridScreenActions.Download -> "download"
                    null -> "null"
                }

                TrackDialogDisplayed(dialogName = label)

                Card {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(stringResource(titleId), style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(10.dp))
                        Text(stringResource(id = textId))
                        Spacer(Modifier.height(10.dp))
                        DialogButtons(
                            onDismissRequest = { requiredAction = null },
                            onConfirm = {
                                if (action is GridScreenActions.Download &&
                                    writePermissionState != null &&
                                    !writePermissionState.status.isGranted
                                ) {
                                    pendingDownload = action.grid
                                    requiredAction = null
                                    writePermissionState.launchPermissionRequest()
                                } else {
                                    coroutineScope.launch {
                                        loading = true
                                        callback(action.grid)
                                        requiredAction = null
                                        loading = false
                                    }
                                }
                            },
                            enabled = !loading
                        )
                    }
                }
            }
        }
        Column {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.grid),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                        },
                navigationIcon = { IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.a11y_back)
                    )
                }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme
                    .surfaceColorAtElevation(1.dp)),
                actions = {
                    IconButton(onClick = {
                        requiredAction = (gridUiState as? GridUiState.Success)?.let {
                            GridScreenActions.Delete(gridUiState.grid)
                        }
                    }, enabled = gridUiState is GridUiState.Success) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                    }
                    IconButton(onClick = {
                        requiredAction = (gridUiState as? GridUiState.Success)?.let {
                            GridScreenActions.Download(gridUiState.grid)
                        }
                    }, enabled = gridUiState is GridUiState.Success) {
                        Icon(
                            imageVector = CropNGridIcons.FilledDownload,
                            contentDescription = stringResource(R.string.download_grid_parts)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                },
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.post_on_insta),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(24.dp)
                )

                AnimatedVisibility(
                    visible = gridUiState is GridUiState.Success,
                    enter = fadeIn(tween(500)),
                    exit = fadeOut(tween(500))
                ) {
                    if (gridUiState is GridUiState.Success) {
                        GridPicture(uris = gridUiState.grid.parts, onPartClick = onPartClick,  modifier = Modifier.fillMaxWidth())
                    }
                }

            }

        }
    }
}

private sealed interface GridScreenActions {

    val grid: Grid
    data class Delete(override val grid: Grid): GridScreenActions
    data class Download(override val grid: Grid): GridScreenActions

}

@Composable
fun LoadGridBitmap(uris: GridUris, onLoaded: (List<List<Pair<Uri, Bitmap>>>) -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(uris) {
        try {
            uris.map { row ->
                row.map { uri ->
                    decode(uri).toUri().let { it to context.contentResolver.getBitmap(it) }
                }
            }.let { onLoaded(it) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("CropNGrid", "Failed to load grid images", e)
        }
    }
}

@Suppress("NAME_SHADOWING")
@Composable
fun GridPicture(
    uris: GridUris,
    onPartClick: (Uri) -> Unit,
    modifier: Modifier
) {

    var images: List<List<Pair<Uri, Bitmap>>>? by remember { mutableStateOf(null) }

    LoadGridBitmap(uris = uris) {
        images = it
    }

    images?.let { images ->
        BoxWithConstraints(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            val offset = 2.dp

            val maxWidth = LocalDensity.current.run { constraints.maxWidth.toDp() }
            val maxHeight = LocalDensity.current.run { constraints.maxHeight.toDp() }

            val rowCount = images.size
            val columnCount = images.first().size

            val maxWidthForItem = maxWidth / columnCount - columnCount * offset
            val maxHeightForItem = maxHeight / rowCount - rowCount * offset

            val primaryColor = MaterialTheme.colorScheme.primary
            val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

            Column {
                images.forEachIndexed { rowNumber, rowItems ->
                    Row {
                        rowItems.forEachIndexed { columnNumber, pair ->
                            Card(shape = RectangleShape, border = null) {
                                val textMeasurer = rememberTextMeasurer()
                                val itemCount = rowCount * columnCount
                                val currentItemCount = rowNumber * columnCount + columnNumber
                                Image(
                                    pair.second.asImageBitmap(),
                                    stringResource(R.string.a11y_grid_part, itemCount - currentItemCount),
                                    modifier = Modifier
                                        .padding(offset)
                                        .widthIn(max = maxWidthForItem)
                                        .heightIn(max = maxHeightForItem)
                                        .drawWithContent {
                                            drawContent()
                                            drawTextOverlay(
                                                (itemCount - currentItemCount).toString(),
                                                textMeasurer,
                                                onPrimaryColor,
                                                primaryColor
                                            )
                                        }
                                        .clickable { onPartClick(pair.first) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }





}