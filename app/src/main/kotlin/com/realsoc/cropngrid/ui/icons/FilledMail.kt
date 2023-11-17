package com.realsoc.cropngrid.ui.icons

import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp


private var _vector: ImageVector? = null

public val CropNGridIcons.FilledMail: ImageVector
		get() {
			if (_vector != null) {
				return _vector!!
			}
_vector = ImageVector.Builder(
                name = "vector",
                defaultWidth = 37.dp,
                defaultHeight = 30.dp,
                viewportWidth = 37f,
                viewportHeight = 30f
            ).apply {
				path(
    				fill = SolidColor(Color(0xFFFFFF00)),
    				fillAlpha = 1.0f,
    				stroke = null,
    				strokeAlpha = 1.0f,
    				strokeLineWidth = 1.0f,
    				strokeLineCap = StrokeCap.Butt,
    				strokeLineJoin = StrokeJoin.Miter,
    				strokeLineMiter = 1.0f,
    				pathFillType = PathFillType.NonZero
				) {
					moveTo(31.1802f, 0.336426f)
					horizontalLineTo(5.50238f)
					curveTo(4.0431f, 0.3364f, 2.6435f, 0.9161f, 1.6116f, 1.948f)
					curveTo(0.5797f, 2.9799f, 0f, 4.3795f, 0f, 5.8388f)
					verticalLineTo(24.1801f)
					curveTo(0f, 25.6394f, 0.5797f, 27.039f, 1.6116f, 28.0709f)
					curveTo(2.6435f, 29.1027f, 4.0431f, 29.6825f, 5.5024f, 29.6825f)
					horizontalLineTo(31.1802f)
					curveTo(32.6395f, 29.6825f, 34.039f, 29.1027f, 35.0709f, 28.0709f)
					curveTo(36.1028f, 27.039f, 36.6825f, 25.6394f, 36.6825f, 24.1801f)
					verticalLineTo(5.83881f)
					curveTo(36.6825f, 4.3795f, 36.1028f, 2.9799f, 35.0709f, 1.948f)
					curveTo(34.039f, 0.9161f, 32.6395f, 0.3364f, 31.1802f, 0.3364f)
					close()
					moveTo(29.9513f, 4.00468f)
					lineTo(18.3413f, 12.7168f)
					lineTo(6.73125f, 4.00468f)
					horizontalLineTo(29.9513f)
					close()
					moveTo(31.1802f, 26.0142f)
					horizontalLineTo(5.50238f)
					curveTo(5.0159f, 26.0142f, 4.5494f, 25.821f, 4.2055f, 25.477f)
					curveTo(3.8615f, 25.133f, 3.6683f, 24.6665f, 3.6683f, 24.1801f)
					verticalLineTo(6.29734f)
					lineTo(17.2408f, 16.4767f)
					curveTo(17.5583f, 16.7149f, 17.9444f, 16.8436f, 18.3413f, 16.8436f)
					curveTo(18.7381f, 16.8436f, 19.1243f, 16.7149f, 19.4417f, 16.4767f)
					lineTo(33.0143f, 6.29734f)
					verticalLineTo(24.1801f)
					curveTo(33.0143f, 24.6665f, 32.8211f, 25.133f, 32.4771f, 25.477f)
					curveTo(32.1331f, 25.821f, 31.6666f, 26.0142f, 31.1802f, 26.0142f)
					close()
}
}.build()
return _vector!!
		}

