package com.realsoc.image_cropper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.tooling.preview.Preview
import com.realsoc.image_cropper.ui.MainScreen
import com.realsoc.image_cropper.ui.theme.Image_cropperTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Image_cropperTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen(::pickPhoto)
                }
            }
        }
    }


    private val singlePhotoPickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        it?.let { viewModel.onPhotoPicked(it) }
    }

    private fun pickPhoto() {
        singlePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun testBitmap(): Bitmap {
        val imageStream = resources.assets.open("test_image.png")
        return BitmapFactory.decodeStream(imageStream)
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Image_cropperTheme {
        // Greeting("Android", block = {}, uri =  state<Uri?>(null))
    }
}

fun Rect.toCoordinatesString(): String {
    val tL = left to top
    val tR = right to top
    val bL = left to bottom
    val bR = right to bottom

    return "tL : $tL tR : $tR bL: $bL bR : $bR"
}
