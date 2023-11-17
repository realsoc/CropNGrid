package com.realsoc.cropngrid.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp


private var _vector: ImageVector? = null

public val CropNGridIcons.FilledScissors: ImageVector
    get() {
        if (_vector != null) {
            return _vector!!
        }
        _vector = ImageVector.Builder(
            name = "vector",
            defaultWidth = 400.dp,
            defaultHeight = 400.dp,
            viewportWidth = 480f,
            viewportHeight = 480f
        ).apply {
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
                moveTo(259.59f, 245.8f)
                moveToRelative(0f, 0f)
                curveToRelative(-8.33f, 15.46f, -16.86f, 30.81f, -25.3f, 46.21f)
                curveToRelative(-0.83f, 1.51f, -1.48f, 3.13f, -2.21f, 4.69f)
                curveToRelative(1.7f, 0.13f, 3.4f, 0.38f, 5.09f, 0.37f)
                curveToRelative(38.56f, -0.18f, 69.27f, 30.24f, 69f, 69.78f)
                curveToRelative(-0.12f, 1.6f, -0.15f, 4.62f, -0.58f, 7.58f)
                curveToRelative(-7f, 48.37f, -59.73f, 73.75f, -102.24f, 49.59f)
                arcToRelative(62.65f, 62.65f, 0f, isMoreThanHalf = false, isPositiveArc = true, -14f, -11.19f)
                curveToRelative(-16.8f, -17.25f, -23.18f, -37.67f, -18.44f, -61.43f)
                curveToRelative(3.11f, -15.62f, 10.3f, -29.43f, 18.6f, -42.81f)
                curveToRelative(7.83f, -12.61f, 15.4f, -25.37f, 23f, -38.1f)
                arcToRelative(27.64f, 27.64f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.6f, -3.93f)
                curveToRelative(2.21f, -3.55f, 26.93f, -48.58f, 28.33f, -51.41f)
                quadTo(284.84f, 130f, 327.34f, 44.86f)
                curveToRelative(0.73f, -1.46f, 1.37f, -3f, 2.22f, -4.36f)
                arcToRelative(14.56f, 14.56f, 0f, isMoreThanHalf = false, isPositiveArc = true, 16.65f, -6.4f)
                curveToRelative(6.5f, 1.94f, 10.9f, 7.61f, 10.51f, 14.32f)
                arcToRelative(22.07f, 22.07f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.3f, 8.13f)
                quadTo(320.9f, 124.7f, 287.23f, 192.77f)
                curveToRelative(-0.51f, 1f, -27.28f, 51.92f, -27.64f, 53f)
                moveToRelative(17f, 119.25f)
                arcToRelative(38.54f, 38.54f, 0f, isMoreThanHalf = true, isPositiveArc = false, -38.65f, 38.59f)
                arcTo(38.3f, 38.3f, 0f, isMoreThanHalf = false, isPositiveArc = false, 276.58f, 365.05f)
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
                moveTo(234.36f, 220.71f)
                moveToRelative(0f, 0f)
                curveToRelative(1.1f, -0.37f, 52f, -27.17f, 53f, -27.68f)
                quadToRelative(68.08f, -33.66f, 136.17f, -67.3f)
                arcToRelative(22f, 22f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8.13f, -2.31f)
                curveToRelative(6.71f, -0.39f, 12.38f, 4f, 14.33f, 10.5f)
                arcToRelative(14.54f, 14.54f, 0f, isMoreThanHalf = false, isPositiveArc = true, -6.39f, 16.65f)
                curveToRelative(-1.39f, 0.85f, -2.9f, 1.5f, -4.36f, 2.23f)
                quadToRelative(-85.1f, 42.52f, -170.22f, 85f)
                curveToRelative(-2.83f, 1.41f, -47.84f, 26.15f, -51.39f, 28.37f)
                arcToRelative(27.68f, 27.68f, 0f, isMoreThanHalf = false, isPositiveArc = false, -3.93f, 1.61f)
                curveTo(197f, 275.44f, 184.22f, 283f, 171.62f, 290.85f)
                curveToRelative(-13.38f, 8.31f, -27.18f, 15.51f, -42.8f, 18.64f)
                curveToRelative(-23.75f, 4.75f, -44.18f, -1.62f, -61.44f, -18.4f)
                arcToRelative(62.63f, 62.63f, 0f, isMoreThanHalf = false, isPositiveArc = true, -11.2f, -14f)
                curveTo(32f, 234.6f, 57.32f, 181.87f, 105.69f, 174.82f)
                curveToRelative(3f, -0.44f, 6f, -0.47f, 7.58f, -0.59f)
                curveToRelative(39.53f, -0.31f, 70f, 30.37f, 69.83f, 68.93f)
                curveToRelative(0f, 1.7f, 0.24f, 3.39f, 0.37f, 5.09f)
                curveToRelative(1.57f, -0.73f, 3.18f, -1.38f, 4.69f, -2.21f)
                curveToRelative(15.4f, -8.45f, 30.74f, -17f, 46.2f, -25.33f)
                moveTo(76.53f, 242.48f)
                arcToRelative(38.53f, 38.53f, 0f, isMoreThanHalf = true, isPositiveArc = false, 38.57f, -38.67f)
                arcTo(38.26f, 38.26f, 0f, isMoreThanHalf = false, isPositiveArc = false, 76.53f, 242.48f)
                close()
            }
        }.build()
        return _vector!!
    }

