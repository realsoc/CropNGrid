package com.realsoc.cropngrid.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.realsoc.cropngrid.R
import com.realsoc.cropngrid.ui.components.CropNGridButton
import kotlinx.coroutines.launch

@Composable
internal fun HomeRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onCropRequested: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
   HomeContent(onCropRequested, onShowSnackbar, modifier)
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeContent(
    onCropRequested: (Uri) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier
) {

    val coroutineScope = rememberCoroutineScope()

    val context: Context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { potentialUri ->
            potentialUri?.let { uri ->
                onCropRequested(uri)
            } ?: println("No picture selected")
        }
    )

    val writeExternalStorageState = rememberPermissionState(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
        if (it) {
            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            coroutineScope.launch {
                val snackBarResult = onShowSnackbar("The application cannot work without storage permission", "Act")
                if (snackBarResult) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                } else {
                    println("Recused STRONG")
                    // TODO : here
                }
            }

        }
    }



    var shouldDisplayRational by remember { mutableStateOf(false) }

    LaunchedEffect(shouldDisplayRational) {
        if (shouldDisplayRational) {
            val snackBarResult = onShowSnackbar("Please accept rationnaly", "Accept")
            if (snackBarResult) {
                writeExternalStorageState.launchPermissionRequest()
            } else {
                println("Recused")
                // todo : here
            }
            shouldDisplayRational = false
        }
    }

    Box(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(50.dp),
            horizontalAlignment = CenterHorizontally
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.logo_crop),
                contentDescription = "Application logo",
                modifier = Modifier
                    .width(100.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.crop_the_img),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.app_description_home),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium)
            )
            Spacer(modifier = Modifier.height(20.dp))
            val onStartButtonClicked = {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P
                    && !writeExternalStorageState.status.isGranted) {
                    if (writeExternalStorageState.status.shouldShowRationale) {
                        shouldDisplayRational = true
                    } else {
                        writeExternalStorageState.launchPermissionRequest()
                    }
                } else {
                    imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }
            CropNGridButton(
                textId = R.string.start,
                onClick = onStartButtonClicked,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.hand_with_camera),
            contentDescription = "Hand taking a picture",
            modifier = Modifier
                .align(CenterStart)
                .padding(top = 40.dp)
        )
    }
}

fun Offset.toIntOffset(): IntOffset {
    return IntOffset(x.toInt(), y.toInt())
}
