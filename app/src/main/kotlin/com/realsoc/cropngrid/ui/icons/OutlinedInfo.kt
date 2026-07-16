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

public val CropNGridIcons.OutlinedInfo: ImageVector
		get() {
			if (_vector != null) {
				return _vector!!
			}
_vector = ImageVector.Builder(
                name = "vector",
                defaultWidth = 23.dp,
                defaultHeight = 23.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
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
					moveTo(11.016f, 9f)
					verticalLineToRelative(-2.016f)
					horizontalLineToRelative(1.969f)
					verticalLineToRelative(2.016f)
					horizontalLineToRelative(-1.969f)
					close()
					moveTo(12f, 20.016f)
					curveToRelative(4.406f, 0f, 8.016f, -3.609f, 8.016f, -8.016f)
					reflectiveCurveToRelative(-3.609f, -8.016f, -8.016f, -8.016f)
					reflectiveCurveToRelative(-8.016f, 3.609f, -8.016f, 8.016f)
					reflectiveCurveToRelative(3.609f, 8.016f, 8.016f, 8.016f)
					close()
					moveTo(12f, 2.016f)
					curveToRelative(5.531f, 0f, 9.984f, 4.453f, 9.984f, 9.984f)
					reflectiveCurveToRelative(-4.453f, 9.984f, -9.984f, 9.984f)
					reflectiveCurveToRelative(-9.984f, -4.453f, -9.984f, -9.984f)
					reflectiveCurveToRelative(4.453f, -9.984f, 9.984f, -9.984f)
					close()
					moveTo(11.016f, 17.016f)
					verticalLineToRelative(-6f)
					horizontalLineToRelative(1.969f)
					verticalLineToRelative(6f)
					horizontalLineToRelative(-1.969f)
					close()
}
}.build()
return _vector!!
		}

