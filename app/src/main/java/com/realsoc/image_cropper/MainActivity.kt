package com.realsoc.image_cropper

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.realsoc.image_cropper.ui.components.ImageCropperNavHost
import com.realsoc.image_cropper.ui.theme.Image_cropperTheme


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private var storageResultActivity: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            pickPhoto()
        } else {
            viewModel.storageRecused(startPermissionIntent =  {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            })
        }
    }

    private val singlePhotoPickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        it?.let { viewModel.onPhotoPicked(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val scaffoldState = rememberImageCropperScaffoldState()
            var canPop by remember { mutableStateOf(false) }
            Image_cropperTheme {
                    Scaffold(
                        topBar = {
                            Row(horizontalArrangement = Arrangement.Start) {
                                if (canPop) {
                                    IconButton(onClick = {
                                        navController.popBackStack()
                                    }) {
                                        Icon(Icons.Default.ArrowBack, "Back button")
                                    }
                                }
                        } },
                        snackbarHost = { SnackbarHost(scaffoldState.snackbarHostState) },


                    ) { paddingValues ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues), color = MaterialTheme.colorScheme
                                .background
                        ) {
                            ImageCropperNavHost(
                                navController = navController,
                                fetchPicture = { withStoragePermission { pickPhoto() } },
                                onCanPop = { value -> canPop = value }
                            )
                        }
                    }
            }
        }
    }

    private fun withStoragePermission(block: () -> Unit) {
        if (SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
                block()
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // TODO : show dialog explaining why to accept Confirm / later
                // On confirm
                storageResultActivity.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                // On later drop
                //show user app can't save image for storage permission denied
            } else {
                storageResultActivity.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else {
            block()
        }
    }

    private fun pickPhoto() {
        singlePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}