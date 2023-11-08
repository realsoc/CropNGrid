package com.realsoc.cropngrid.navigation

import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.realsoc.cropngrid.decode
import com.realsoc.cropngrid.encode
import com.realsoc.cropngrid.toUri
import com.realsoc.cropngrid.ui.screens.CropperRoute
import kotlinx.coroutines.CoroutineScope


@VisibleForTesting
internal const val encodedUriArg = "encodedUri"
internal class CropperArgs(val uri: Uri) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(decode(checkNotNull(savedStateHandle[encodedUriArg])).toUri())
}

fun NavController.navigateToCropper(uri: Uri) {
    this.navigate("cropper/${encode(uri.toString())}") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.cropperScreen(onCropComplete: (String) -> Unit, coroutineScope: CoroutineScope, onBackClick: ()
-> Unit) {
    composable(
        route = "cropper/{$encodedUriArg}",
    ) {
        CropperRoute(
            onCropComplete = onCropComplete,
            coroutineScope = coroutineScope,
            onBackClick = onBackClick
        )
    }
}