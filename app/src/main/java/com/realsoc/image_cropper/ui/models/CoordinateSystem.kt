package com.realsoc.image_cropper.ui.models

import com.realsoc.image_cropper.ui.Point
import com.realsoc.image_cropper.ui.Vector


data class CoordinateSystem(
    val transformation: Transformation = Transformation(),
    val pivot: Point = Point.ZERO,
    val minScale: Float = 1f
) {
    fun withChange(zoomChange: Float, translationChange: Vector, rotationChange: Float): CoordinateSystem {
        return copy(transformation = transformation.applyChange(zoomChange, translationChange,
            rotationChange, minScale))
    }

    fun withTransformation(newTransformation: Transformation): CoordinateSystem {
        return copy(transformation = newTransformation)
    }

    fun withPivot(newPivot: Point): CoordinateSystem {
        return copy(pivot = newPivot)
    }

    fun withMinScale(newMinScale: Float): CoordinateSystem {
        return copy(minScale = newMinScale)
    }
}
