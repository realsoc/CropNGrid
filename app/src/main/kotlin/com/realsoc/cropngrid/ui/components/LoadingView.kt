package com.realsoc.cropngrid.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.realsoc.cropngrid.R

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Dialog(onDismissRequest = {}) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
        val dynamicProperties = rememberLottieDynamicProperties(
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR,
                value = Color.White.toArgb(),
                keyPath = arrayOf(
                    "**",
                )
            ),
        )
        LottieAnimation(
            composition,
            modifier = modifier.heightIn(max = 100.dp)
                .widthIn(max = 100.dp),
            dynamicProperties = dynamicProperties,
            iterations = LottieConstants.IterateForever
        )
    }

}