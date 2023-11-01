package com.realsoc.cropngrid.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.realsoc.cropngrid.MainViewModel
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun EndContent(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val uiState: Screen.End by viewModel.uiState.filterIsInstance<Screen.End>().collectAsState(Screen.End(true))

    AnimatedContent(targetState = uiState.loading, label = "Transition on end screen") { loading ->
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

@Composable
fun EndAppBar(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val uiState: Screen.End by viewModel.uiState.filterIsInstance<Screen.End>().collectAsState(Screen.End(true))

}

@Composable
fun EndBottomBar(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val uiState: Screen.End by viewModel.uiState.filterIsInstance<Screen.End>().collectAsState(Screen.End(true))

}