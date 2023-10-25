package com.realsoc.image_cropper.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.realsoc.image_cropper.MainViewModel

@Composable
fun EndScreen(loading: Boolean = false) {
    val viewModel: MainViewModel = viewModel()
    AnimatedContent(targetState = loading, label = "Transition on end screen") { loading ->
        if (loading) {
            CircularProgressIndicator()
        } else {
            Column {
                Image(imageVector = Icons.Default.Check, "Check")
                TextButton(onClick = { viewModel.terminate() }) {
                    Text("Click ici")
                }
            }
        }
    }

}