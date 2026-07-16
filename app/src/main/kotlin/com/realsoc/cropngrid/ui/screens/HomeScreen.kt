package com.realsoc.cropngrid.ui.screens

import android.net.Uri
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
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.realsoc.cropngrid.R
import com.realsoc.cropngrid.analytics.LocalAnalyticsHelper
import com.realsoc.cropngrid.analytics.TrackScreenViewEvent
import com.realsoc.cropngrid.analytics.buttonClick
import com.realsoc.cropngrid.ui.components.CropNGridButton

private const val SCREEN_NAME = "home"
@Composable
internal fun HomeRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onCropRequested: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    HomeScreen(onCropRequested, onShowSnackbar, modifier)
}


@Composable
fun HomeScreen(
    onCropRequested: (Uri) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier
) {

    TrackScreenViewEvent(screenName = SCREEN_NAME)

    Box(modifier.fillMaxSize()) {

        val analyticsHelper = LocalAnalyticsHelper.current

        // The system photo picker requires no permission on any supported API level
        val imagePicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { potentialUri ->
                potentialUri?.let { uri ->
                    onCropRequested(uri)
                }
            }
        )

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
                imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
