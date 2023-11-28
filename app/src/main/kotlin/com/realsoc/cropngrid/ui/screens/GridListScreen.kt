package com.realsoc.cropngrid.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.realsoc.cropngrid.R
import com.realsoc.cropngrid.analytics.LocalAnalyticsHelper
import com.realsoc.cropngrid.analytics.TrackScreenViewEvent
import com.realsoc.cropngrid.analytics.buttonClick
import com.realsoc.cropngrid.decode
import com.realsoc.cropngrid.viewmodels.GridListUiState
import com.realsoc.cropngrid.viewmodels.GridListViewModel
import org.koin.androidx.compose.koinViewModel

private const val SCREEN_NAME = "grid_list"

@Composable
fun GridListRoute(
    onGridClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GridListViewModel = koinViewModel()
) {
    val gridListUiState: GridListUiState by viewModel.gridListUiState.collectAsStateWithLifecycle()

    val analyticsHelper = LocalAnalyticsHelper.current
    GridListScreen(
        gridListUiState = gridListUiState,
        onGridClicked = {
            analyticsHelper.buttonClick(SCREEN_NAME, "grid")
            onGridClicked(it)
                        },
        modifier = modifier,
    )
}
@Composable
fun GridListScreen(
    gridListUiState: GridListUiState,
    onGridClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    ) {

    TrackScreenViewEvent(screenName = SCREEN_NAME)

    Column(modifier.fillMaxSize()) {
    (gridListUiState as? GridListUiState.Success)?.let { state ->
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
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.camera_animation))

                    Text(
                        stringResource(R.string.have_you_think_about_cropping_an_image_yet),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(CenterHorizontally)
                            .padding(40.dp)
                    )

                    LottieAnimation(
                        composition,
                        modifier = modifier
                            .heightIn(max = 300.dp)
                            .widthIn(max = 300.dp)
                            .align(CenterHorizontally),
                        iterations = LottieConstants.IterateForever
                    )
                }
            }
        }
    }
}