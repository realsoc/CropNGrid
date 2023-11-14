package com.realsoc.cropngrid.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.realsoc.cropngrid.R
import com.realsoc.cropngrid.decode
import com.realsoc.cropngrid.viewmodels.GridListUiState
import com.realsoc.cropngrid.viewmodels.GridListViewModel

@Composable
fun GridListRoute(
    onGridClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GridListViewModel = hiltViewModel()
) {
    val gridListUiState: GridListUiState by viewModel.gridListUiState.collectAsStateWithLifecycle()

    GridListScreen(
        gridListUiState = gridListUiState,
        onGridClicked = onGridClicked,
        modifier = modifier
    )
}
@Composable
fun GridListScreen(
    gridListUiState: GridListUiState,
    onGridClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    (gridListUiState as? GridListUiState.Success)?.let { state ->
        Column(modifier.fillMaxSize()) {
            if (state.gridList.isNotEmpty()) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    verticalItemSpacing = 4.dp,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    content = {
                        items(gridListUiState.gridList, key = { it.id }) { item ->
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(decode(item.miniatureUriEncoded)).build(),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .clickable {
                                        onGridClicked(item.id)
                                    }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // todo : resolve this
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize()
                ) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.desert_rolling))
                    LottieAnimation(
                        composition,
                        Modifier
                            .padding(20.dp)
                            .background(Color.Yellow),
                        contentScale = ContentScale.FillWidth,
                    )
                    Text("Have you think about cropping an image yet?", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }
}