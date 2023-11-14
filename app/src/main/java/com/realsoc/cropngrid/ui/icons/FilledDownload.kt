package com.realsoc.cropngrid.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.realsoc.cropngrid.ui.CropNGridIcons


private var _vector: ImageVector? = null

public val CropNGridIcons.FilledDownload: ImageVector
    get() {
        if (_vector != null) {
            return _vector!!
        }
        _vector = ImageVector.Builder(
            name = "vector",
            defaultWidth = 28.dp,
            defaultHeight = 28.dp,
            viewportWidth = 48f,
            viewportHeight = 48f
        ).apply {
            group {
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(4.93f, 32.06f)
                    curveToRelative(0f, -0.12f, 0.09f, -0.23f, 0.14f, -0.35f)
                    arcToRelative(2.12f, 2.12f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.1f, 0.75f)
                    curveToRelative(0f, 0.93f, 0f, 1.87f, 0.08f, 2.79f)
                    arcToRelative(4.15f, 4.15f, 0f, isMoreThanHalf = false, isPositiveArc = false, 4.11f, 3.58f)
                    curveToRelative(2.14f, 0f, 4.27f, 0f, 6.41f, 0f)
                    horizontalLineToRelative(14.6f)
                    arcToRelative(4.12f, 4.12f, 0f, isMoreThanHalf = false, isPositiveArc = false, 4.05f, -2.48f)
                    arcToRelative(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.39f, -1.72f)
                    curveToRelative(0.07f, -0.72f, 0f, -1.44f, 0f, -2.16f)
                    arcTo(2.14f, 2.14f, 0f, isMoreThanHalf = false, isPositiveArc = true, 41f, 30.35f)
                    arcToRelative(2.11f, 2.11f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.1f, 2.12f)
                    arcTo(31.66f, 31.66f, 0f, isMoreThanHalf = false, isPositiveArc = true, 43f, 35.7f)
                    arcToRelative(8.4f, 8.4f, 0f, isMoreThanHalf = false, isPositiveArc = true, -6.4f, 7.15f)
                    lineToRelative(-1f, 0.23f)
                    horizontalLineTo(12.45f)
                    arcToRelative(1.11f, 1.11f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.16f, -0.07f)
                    arcToRelative(8.47f, 8.47f, 0f, isMoreThanHalf = false, isPositiveArc = true, -7.07f, -6.17f)
                    curveToRelative(-0.11f, -0.42f, -0.2f, -0.85f, -0.29f, -1.28f)
                    close()
                }
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(21.86f, 27.31f)
                    verticalLineToRelative(-20f)
                    arcTo(2.19f, 2.19f, 0f, isMoreThanHalf = false, isPositiveArc = true, 23f, 5.2f)
                    arcToRelative(2.13f, 2.13f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3.17f, 1.86f)
                    curveToRelative(0f, 0.15f, 0f, 0.3f, 0f, 0.45f)
                    verticalLineToRelative(19.8f)
                    curveToRelative(0.16f, -0.15f, 0.26f, -0.23f, 0.35f, -0.33f)
                    curveToRelative(1.5f, -1.49f, 3f, -3f, 4.49f, -4.47f)
                    arcToRelative(2.11f, 2.11f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3.56f, 1f)
                    arcToRelative(2.15f, 2.15f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.66f, 2.13f)
                    quadToRelative(-2.75f, 2.73f, -5.48f, 5.48f)
                    lineToRelative(-2.74f, 2.73f)
                    arcToRelative(2.15f, 2.15f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3.31f, 0f)
                    lineToRelative(-8.22f, -8.22f)
                    arcToRelative(2.16f, 2.16f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, -3.2f)
                    arcToRelative(2.12f, 2.12f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3f, 0.1f)
                    curveToRelative(1.49f, 1.47f, 3f, 3f, 4.45f, 4.45f)
                    close()
                }
            }
        }.build()
        return _vector!!
    }

