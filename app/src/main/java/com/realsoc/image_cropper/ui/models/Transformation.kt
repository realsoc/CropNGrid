package com.realsoc.image_cropper.ui.models

import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import com.realsoc.image_cropper.ui.Vector
import com.realsoc.image_cropper.ui.times

data class Transformation(
    val rotation: Float = 0f,
    val translation: Vector = Vector(0f, 0f),
    val scale: Float = 1f
) {
    fun applyChange(zoomChange: Float, translationChange: Vector, rotationChange: Float, minScale: Float):
            Transformation {
        return copy(
            scale = java.lang.Float.max(scale * zoomChange, minScale),
            rotation = (rotation + rotationChange) % 360,
            translation = translation + translationChange
        )
    }

    fun scale(newScale: Float): Transformation {
        return copy(scale = scale * newScale, rotation = rotation, translation = translation * newScale)
    }

    companion object TransformationStateConverter: TwoWayConverter<Transformation, AnimationVector4D> {
        override val convertFromVector: (AnimationVector4D) -> Transformation
            get() = { animationVector4d: AnimationVector4D ->
                Transformation(
                    animationVector4d.v1,
                    Vector(animationVector4d.v2, animationVector4d.v3),
                    animationVector4d.v4
                )
            }
        override val convertToVector: (Transformation) -> AnimationVector4D
            get() = { transformationState ->
                AnimationVector4D(
                    transformationState.rotation,
                    transformationState.translation.x,
                    transformationState.translation.y,
                    transformationState.scale
                )
            }
    }

}
