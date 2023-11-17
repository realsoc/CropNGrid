package com.realsoc.cropngrid.ui.icons

import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.collections.List as ____KtList

public object CropNGridIcons

private var __AllIcons: ____KtList<ImageVector>? = null

public val CropNGridIcons.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons = listOf(OutlinedList, FilledList, FilledDownload, FilledGithub, FilledMail, FilledScissors,
      OutlinedInfo, FilledInfo)
    return __AllIcons!!
  }
