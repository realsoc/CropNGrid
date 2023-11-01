package com.realsoc.cropngrid.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realsoc.cropngrid.MainViewModel
import com.realsoc.cropngrid.R
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun HomeContent(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val uiState: Screen.Home? by viewModel.uiState.filterIsInstance<Screen.Home>().collectAsState(null)

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
            Spacer(modifier = Modifier.height(50.dp))
            Button(onClick = viewModel::startButtonClickedHome, Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.start))
            }
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

@Composable
fun CropHistoryContent() {
    Column {
        Text(text = "Tatata")
    }
}

@Composable
fun HomeAppBar(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val uiState: Screen.Home? by viewModel.uiState.filterIsInstance<Screen.Home>().collectAsState(null)
}

@Composable
fun HomeBottomBar(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val uiState: Screen.Home? by viewModel.uiState.filterIsInstance<Screen.Home>().collectAsState(null)
    BottomAppBar(
        actions = {
            Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                IconButton(onClick = viewModel::homeClicked) {
                    Icon(Icons.Default.Home, "Home")
                }
                IconButton(onClick = viewModel::cropListClicked) {
                    Icon(Icons.Default.List, "Previous crop list")
                }
            }

        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 3.dp
    )
}