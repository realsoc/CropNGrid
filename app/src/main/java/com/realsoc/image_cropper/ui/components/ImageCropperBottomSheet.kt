package com.realsoc.image_cropper.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetDragHandle(state: BottomSheetScaffoldState) {
    val coroutineScope = rememberCoroutineScope()
    IconButton(onClick = {
        coroutineScope.launch {
            when (state.bottomSheetState.currentValue) {
                SheetValue.Hidden -> {}
                SheetValue.Expanded -> state.bottomSheetState.partialExpand()
                SheetValue.PartiallyExpanded -> state.bottomSheetState.expand()
            }
        }
    }) {
        AnimatedContent(targetState = state.bottomSheetState.targetValue, label = "") {
            if (it == SheetValue.Expanded) {
                Icon(Icons.Filled.KeyboardArrowDown, "")
            } else {
                Icon(Icons.Filled.KeyboardArrowUp, "")
            }
        }
    }
}