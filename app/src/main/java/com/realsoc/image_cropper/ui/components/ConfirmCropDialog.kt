package com.realsoc.image_cropper.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.realsoc.image_cropper.createBitmapList
import com.realsoc.image_cropper.frame
import com.realsoc.image_cropper.ui.getCropGrid
import com.realsoc.image_cropper.ui.models.CoordinateSystem
import com.realsoc.image_cropper.ui.models.GridParameters

@Composable
fun ConfirmCropDialog(
    source: Bitmap,
    coordinateSystem: CoordinateSystem,
    gridArea: Rect,
    gridParameters: GridParameters,
    onDismissRequest: () -> Unit,
    onConfirmCrop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        var imagePartList by remember { mutableStateOf<List<Bitmap>>(listOf()) }

        LaunchedEffect(source, gridArea, gridParameters, coordinateSystem) {
            val areas = getCropGrid(gridArea, gridParameters)
            imagePartList = createBitmapList(source, areas, coordinateSystem)
        }
        Card(modifier) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Crop the image ?", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                AnimatedContent(
                    modifier = Modifier.aspectRatio(gridParameters.gridRatio),
                    targetState = imagePartList, label = ""
                ) { imagePartList ->
                    when {
                        imagePartList.isEmpty() -> {
                            LoadingView()
                        }
                        else -> {
                            SplittingBoxAnimated(
                                imagePartList,
                                gridParameters.columnNumber,
                                gridParameters.rowNumber,
                                imagePartList.first().frame
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(Modifier.align(Alignment.End)) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Button(onClick = onConfirmCrop) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}


@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier
            .fillMaxSize()
            .padding(100.dp)
    )
}

