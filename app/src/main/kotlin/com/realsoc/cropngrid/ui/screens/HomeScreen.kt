package com.realsoc.cropngrid.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.ExperimentalTransitionApi
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
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.realsoc.cropngrid.R
import com.realsoc.cropngrid.analytics.LocalAnalyticsHelper
import com.realsoc.cropngrid.analytics.TrackScreenViewEvent
import com.realsoc.cropngrid.analytics.buttonClick
import com.realsoc.cropngrid.analytics.logLogGranted
import com.realsoc.cropngrid.analytics.logPermissionAsked
import com.realsoc.cropngrid.analytics.logPermissionGranted
import com.realsoc.cropngrid.analytics.logPermissionIntentLaunched
import com.realsoc.cropngrid.analytics.logPermissionRecused
import com.realsoc.cropngrid.ui.components.CropNGridButton
import kotlinx.coroutines.launch

private const val SCREEN_NAME = "home"
@Composable
internal fun HomeRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onCropRequested: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    HomeScreen(onCropRequested, onShowSnackbar, modifier)
}


@OptIn(ExperimentalPermissionsApi::class, ExperimentalTransitionApi::class)
@Composable
fun HomeScreen(
    onCropRequested: (Uri) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier
) {

    TrackScreenViewEvent(screenName = SCREEN_NAME)

    Box(modifier.fillMaxSize()) {

        val coroutineScope = rememberCoroutineScope()

        val context: Context = LocalContext.current

        val analyticsHelper = LocalAnalyticsHelper.current

        val imagePicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { potentialUri ->
                potentialUri?.let { uri ->
                    onCropRequested(uri)
                } ?: println("No picture selected")
            }
        )

        val fixString = stringResource(id = R.string.fix)
        val permissionRecusedString = stringResource(id = R.string.permission_recused)

        val writeExternalStorageState = rememberPermissionState(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            analyticsHelper.logPermissionAsked("system")
            if (it) {
                analyticsHelper.logPermissionGranted("system")
                imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                analyticsHelper.logPermissionRecused("system")
                coroutineScope.launch {
                    analyticsHelper.logPermissionAsked("snackbar")
                    val snackBarResult = onShowSnackbar(permissionRecusedString, fixString)
                    if (snackBarResult) {
                        analyticsHelper.logPermissionIntentLaunched()
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", context.packageName, null)
                        intent.data = uri
                        context.startActivity(intent)
                    } else {
                        analyticsHelper.logPermissionRecused("snackbar")
                    }
                }

            }
        }


        var shouldDisplayRational by remember { mutableStateOf(false) }

        val acceptRationalText = stringResource(id = R.string.please_accept_rationnaly)
        val acceptText = stringResource(id = R.string.accept)

        LaunchedEffect(shouldDisplayRational) {
            if (shouldDisplayRational) {
                analyticsHelper.logPermissionAsked("rational")
                val snackBarResult = onShowSnackbar(acceptRationalText, acceptText)
                if (snackBarResult) {
                    analyticsHelper.logPermissionGranted("rational")
                    writeExternalStorageState.launchPermissionRequest()
                } else {
                    analyticsHelper.logPermissionRecused("rational")
                }
                shouldDisplayRational = false
            }
        }

        Column(
            horizontalAlignment = CenterHorizontally
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.logo_crop),
                contentDescription = "Application logo",
                modifier = Modifier
                    .width(100.dp)
                    .padding(top = 32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = stringResource(R.string.app_description_home),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.weight(1f))

            val onStartButtonClicked = {
                analyticsHelper.buttonClick(SCREEN_NAME, "start_button")
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P
                    && !writeExternalStorageState.status.isGranted
                ) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(56.dp)
            )
        }
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.hand_with_camera),
            contentDescription = "Hand taking a picture",
            modifier = Modifier
                .align(BottomStart)
                .padding(bottom = 24.dp)
        )
    }
}
