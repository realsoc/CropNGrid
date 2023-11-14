package com.realsoc.cropngrid.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InfoRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier
) {
    InfoScreen(modifier)
}

@Composable
fun InfoScreen(modifier: Modifier = Modifier) {

    Box(modifier.fillMaxSize()) {
        Column(
            Modifier
                .padding(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }

}