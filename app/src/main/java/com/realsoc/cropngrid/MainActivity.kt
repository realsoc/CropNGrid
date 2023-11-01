package com.realsoc.cropngrid

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import com.realsoc.cropngrid.ui.components.CropNGridNavHost
import com.realsoc.cropngrid.ui.screens.Screen
import com.realsoc.cropngrid.ui.theme.CropNGridTheme

class MainActivity : ComponentActivity(), AndroidActions {

    private val navController = NavHostController(this).apply {
        navigatorProvider.apply {
            // mandatory otherwise navController crash when calling composable(route)
            addNavigator(DialogNavigator())
            addNavigator(ComposeNavigator())
        }
    }

    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory(application, navController, this) }

    private var storageResultActivity: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Kinda ugly but for now it works
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
        enableEdgeToEdge()
        installSplashScreen()

        setContent {
            val screen by viewModel.uiState.collectAsState()

            val scaffoldState = rememberCropNGridScaffoldState()
            CropNGridTheme(dynamicColor = false) {
                ChangeSystemBarsTheme(!isSystemInDarkTheme())
                Scaffold(
                    topBar = { Screen.TopBar(viewModel = viewModel, screen = screen) },
                    snackbarHost = { SnackbarHost(scaffoldState.snackbarHostState) },
                    bottomBar = { Screen.BottomBar(viewModel = viewModel, screen = screen) },
                    ) { paddingValues ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues), color = MaterialTheme.colorScheme
                                .background
                        ) {
                            CropNGridNavHost(navController, viewModel)
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
                // On later, drop
                //show user app can't save image for storage permission denied
            } else {
                storageResultActivity.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else {
            block()
        }
    }

    override fun pickPhotoRequest() {
        withStoragePermission {
            pickPhoto()
        }
    }

    private fun pickPhoto() {
        singlePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // From https://medium.com/@stefanoq21/accompanist-system-ui-controller-deprecated-a3678ba3f244
    @Composable
    private fun ChangeSystemBarsTheme(lightTheme: Boolean) {
        val topBarColor = MaterialTheme.colorScheme.background.toArgb()
        val bottomBarColor = MaterialTheme.colorScheme.surface.toArgb()
        LaunchedEffect(lightTheme) {
            if (lightTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.light(
                        topBarColor, topBarColor,
                    ),
                    navigationBarStyle = SystemBarStyle.light(
                        bottomBarColor, bottomBarColor,
                    ),
                )
            } else {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.dark(
                        topBarColor,
                    ),
                    navigationBarStyle = SystemBarStyle.dark(
                        bottomBarColor,
                    ),
                )
            }
        }
    }
}