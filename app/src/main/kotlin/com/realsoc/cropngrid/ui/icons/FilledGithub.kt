package com.realsoc.cropngrid.ui.icons

import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp


private var _vector: ImageVector? = null

public val CropNGridIcons.FilledGithub: ImageVector
		get() {
			if (_vector != null) {
				return _vector!!
			}
_vector = ImageVector.Builder(
                name = "vector",
                defaultWidth = 33.dp,
                defaultHeight = 31.dp,
                viewportWidth = 33f,
                viewportHeight = 31f
            ).apply {
				group {
					path(
    					fill = SolidColor(Color(0xFFFFFF00)),
    					fillAlpha = 1.0f,
    					stroke = null,
    					strokeAlpha = 1.0f,
    					strokeLineWidth = 1.0f,
    					strokeLineCap = StrokeCap.Butt,
    					strokeLineJoin = StrokeJoin.Miter,
    					strokeLineMiter = 1.0f,
    					pathFillType = PathFillType.EvenOdd
					) {
						moveTo(16.2648f, 0f)
						curveTo(7.4436f, 0f, 0.3125f, 7.1042f, 0.3125f, 15.893f)
						curveTo(0.3125f, 22.9184f, 4.8816f, 28.8652f, 11.2203f, 30.97f)
						curveTo(12.0127f, 31.1282f, 12.303f, 30.628f, 12.303f, 30.2072f)
						curveTo(12.303f, 29.8388f, 12.2769f, 28.5759f, 12.2769f, 27.26f)
						curveTo(7.8394f, 28.2074f, 6.9153f, 25.3654f, 6.9153f, 25.3654f)
						curveTo(6.2021f, 23.5235f, 5.1455f, 23.0501f, 5.1455f, 23.0501f)
						curveTo(3.6931f, 22.0765f, 5.2513f, 22.0765f, 5.2513f, 22.0765f)
						curveTo(6.8624f, 22.1818f, 7.7078f, 23.7079f, 7.7078f, 23.7079f)
						curveTo(9.1337f, 26.1285f, 11.4315f, 25.4445f, 12.3559f, 25.0235f)
						curveTo(12.4878f, 23.9972f, 12.9107f, 23.2868f, 13.3597f, 22.8922f)
						curveTo(9.8204f, 22.5238f, 6.0967f, 21.1556f, 6.0967f, 15.0508f)
						curveTo(6.0967f, 13.3142f, 6.7301f, 11.8933f, 7.7339f, 10.7883f)
						curveTo(7.5755f, 10.3937f, 7.0207f, 8.762f, 7.8926f, 6.5781f)
						curveTo(7.8926f, 6.5781f, 9.2395f, 6.157f, 12.2766f, 8.2095f)
						curveTo(13.5769f, 7.8616f, 14.9178f, 7.6847f, 16.2648f, 7.6832f)
						curveTo(17.6118f, 7.6832f, 18.9848f, 7.8675f, 20.2527f, 8.2095f)
						curveTo(23.2901f, 6.157f, 24.6371f, 6.5781f, 24.6371f, 6.5781f)
						curveTo(25.5089f, 8.762f, 24.9538f, 10.3937f, 24.7954f, 10.7883f)
						curveTo(25.8256f, 11.8933f, 26.433f, 13.3142f, 26.433f, 15.0508f)
						curveTo(26.433f, 21.1556f, 22.7092f, 22.4973f, 19.1435f, 22.8922f)
						curveTo(19.7247f, 23.3921f, 20.2263f, 24.3392f, 20.2263f, 25.8391f)
						curveTo(20.2263f, 27.9704f, 20.2002f, 29.6809f, 20.2002f, 30.2069f)
						curveTo(20.2002f, 30.628f, 20.4908f, 31.1282f, 21.283f, 30.9703f)
						curveTo(27.6216f, 28.8649f, 32.1907f, 22.9184f, 32.1907f, 15.893f)
						curveTo(32.2168f, 7.1042f, 25.0596f, 0f, 16.2648f, 0f)
						close()
}
}
}.build()
return _vector!!
		}

