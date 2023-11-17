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

public val CropNGridIcons.FilledInfo: ImageVector
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
				moveTo(12f, 2f)
				curveTo(6.4f, 2f, 2f, 6.5f, 2f, 12f)
				curveToRelative(0f, 5.5f, 4.5f, 10f, 10f, 10f)
				curveToRelative(5.5f, 0f, 10f, -4.5f, 10f, -10f)
				curveTo(22f, 6.5f, 17.5f, 2f, 12f, 2f)
				close()
				moveTo(13f, 17f)
				horizontalLineToRelative(-2f)
				verticalLineToRelative(-6f)
				horizontalLineToRelative(2f)
				verticalLineTo(17f)
				close()
				moveTo(12.7f, 8.7f)
				curveTo(12.5f, 8.9f, 12.3f, 9f, 12f, 9f)
				reflectiveCurveToRelative(-0.5f, -0.1f, -0.7f, -0.3f)
				curveTo(11.1f, 8.5f, 11f, 8.3f, 11f, 8f)
				reflectiveCurveToRelative(0.1f, -0.5f, 0.3f, -0.7f)
				curveTo(11.5f, 7.1f, 11.7f, 7f, 12f, 7f)
				reflectiveCurveToRelative(0.5f, 0.1f, 0.7f, 0.3f)
				curveTo(12.9f, 7.5f, 13f, 7.7f, 13f, 8f)
				reflectiveCurveTo(12.9f, 8.5f, 12.7f, 8.7f)
				close()
			}
		}.build()
		return _vector!!
	}

