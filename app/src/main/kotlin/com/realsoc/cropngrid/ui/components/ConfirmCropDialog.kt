package com.realsoc.cropngrid.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.realsoc.cropngrid.R
import com.realsoc.cropngrid.analytics.TrackDialogDisplayed
import com.realsoc.cropngrid.analytics.TrackScreenViewEvent
import com.realsoc.cropngrid.createBitmapList
import com.realsoc.cropngrid.ui.getCropGrid
import com.realsoc.cropngrid.ui.models.CoordinateSystem
import com.realsoc.cropngrid.ui.models.GridParameters
import com.realsoc.cropngrid.viewmodels.CroppingUiState

@Composable
fun ConfirmCropDialog(
    source: Bitmap,
    coordinateSystem: CoordinateSystem,
    gridArea: Rect,
    gridParameters: GridParameters,
    onCropComplete: (String) -> Unit,
    croppingUiState: CroppingUiState?,
    onDismissRequest: () -> Unit,
    onConfirmCrop: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentDismissRequest by remember { mutableStateOf(onDismissRequest) }

    if (croppingUiState is CroppingUiState.Loading) {
        LoadingView()
    }

    TrackDialogDisplayed(dialogName = "confirm_crop")

    Dialog(
        onDismissRequest = currentDismissRequest
    ) {
        var imagePartList by remember { mutableStateOf<List<List<Bitmap>>>(listOf()) }

        LaunchedEffect(source, gridArea, gridParameters, coordinateSystem) {
            val areas = getCropGrid(gridArea, gridParameters)
            imagePartList = createBitmapList(source, areas, coordinateSystem)
        }
        val maxHeight = LocalConfiguration.current.screenHeightDp.dp - 100.dp
        Card(modifier.heightIn(max = maxHeight)) {
            Column(modifier = Modifier.padding(20.dp)) {
                if (croppingUiState == null || croppingUiState is CroppingUiState.Loading) {
                    Text(
                        stringResource(R.string.crop_the_image_question),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Spacer(Modifier.height(10.dp))
                    AnimatedContent(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .aspectRatio(gridParameters.gridRatio)
                            .align(Alignment.CenterHorizontally),
                        targetState = imagePartList, label = ""
                    ) { imagePartList ->
                        if (imagePartList.isEmpty()) {
                            LoadingView()
                        }
                        SplittingBoxAnimated(
                            imagePartList
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    DialogButtons(onDismissRequest, onConfirmCrop)
                } else {
                    if (croppingUiState is CroppingUiState.Success) {
                        currentDismissRequest = { onCropComplete(croppingUiState.gridId) }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(stringResource(R.string.success_cropping))
                            Spacer(Modifier.height(20.dp))
                            Button({ onCropComplete(croppingUiState.gridId) }) {
                                Text(stringResource(R.string.go_to_grid))
                            }
                        }
                    }
                }
            }
        }
    }
}
