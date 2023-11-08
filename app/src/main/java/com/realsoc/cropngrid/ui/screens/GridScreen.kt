package com.realsoc.cropngrid.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.realsoc.cropngrid.decode
import com.realsoc.cropngrid.getBitmap
import com.realsoc.cropngrid.models.Grid
import com.realsoc.cropngrid.models.GridUris
import com.realsoc.cropngrid.toUri
import com.realsoc.cropngrid.ui.drawTextOverlay
import com.realsoc.cropngrid.ui.models.GridParameters
import com.realsoc.cropngrid.viewmodels.GridUiState
import com.realsoc.cropngrid.viewmodels.GridViewModel


@Composable
fun GridRoute(
    onGridDeleted: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: GridViewModel = hiltViewModel()
) {
    val gridUiState: GridUiState by viewModel.gridUiState.collectAsStateWithLifecycle()

    GridContent(
        gridUiState = gridUiState,
        onPartClick = {
                      // Todo : generate insta intent to publish
        },
        onBackClick = onBackClick,
        onDeleteGrid = {
            viewModel.deleteGrid(it)
            onGridDeleted()
        }
    )

}
@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun GridContent(
    gridUiState: GridUiState,
    onPartClick: (Uri) -> Unit,
    onBackClick: () -> Unit,
    onDeleteGrid: (Grid) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = gridUiState,
        label = "Animation between screen state",
        modifier = modifier.fillMaxSize()
    ) { state ->
        when(state) {
            GridUiState.Error -> {
                // TOdo : implement

            }
            GridUiState.Loading -> {
                // TOdo : implement
            }
            is GridUiState.Success -> {
                Column {
                    Text("Description de ce qu'il faut faire")

                    GridPicture(uris = state.grid.parts, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }

}

@Composable
fun LoadGridBitmap(uris: GridUris, onLoaded: (List<List<Bitmap>>) -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(uris) {
        with(context) {
            uris.map { row -> row.map { uri -> contentResolver.getBitmap(decode(uri).toUri()) } }
        }.let {
            onLoaded(it)
        }
    }
}

@Composable
fun GridPicture(uris: GridUris, modifier: Modifier) {

    var images: List<List<Bitmap>>? by remember { mutableStateOf(null) }

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

            val maxWidthForItem = maxWidth / columnCount - (columnCount - 1) * offset
            val maxHeightForItem = maxHeight / rowCount - (rowCount - 1) * offset


            Column(modifier = Modifier.background(Color.Red)) {
                images.forEachIndexed { rowNumber, rowItems ->
                    Row {
                        rowItems.forEachIndexed { columnNumber, bitmap ->
                            Card(shape = RectangleShape,
                                border = BorderStroke(1.dp, Color.Gray)
                            ) {
                                val textMeasurer = rememberTextMeasurer()
                                val itemCount = rowCount * columnCount
                                val currentItemCount = rowNumber * columnCount + columnNumber

                                Image(
                                    bitmap.asImageBitmap(),
                                    "",
                                    modifier = Modifier
                                        .padding(offset)
                                        .widthIn(max = maxWidthForItem)
                                        .heightIn(max = maxHeightForItem)
                                        .drawWithContent {
                                            drawContent()
                                            drawTextOverlay(
                                                (itemCount - currentItemCount).toString(),
                                                textMeasurer
                                            )
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }





}