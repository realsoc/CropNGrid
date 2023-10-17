package com.realsoc.image_cropper.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlin.math.cos
import kotlin.math.sin

data class Vector(val x: Float, val y: Float) {

    operator fun unaryMinus() = Vector(-x, -y)

    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
    operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)

    companion object {
        val NULL = Vector(0f, 0f)
        val X = Vector(1f, 0f)
        val Y = Vector(0f, 1f)
    }
}

data class Point(val x: Float, val y: Float) {
    companion object {
        val ZERO =  Point(0f, 0f)
    }
}

fun Offset.toPoint(): Point = Point(x, y)
fun Offset.toVector(): Vector = Vector(x, y)

private fun Point.toOffset(): Offset = Offset(x, y)
private fun Vector.toOffset(): Offset = Offset(x, y)

fun Rect.scale(scale: Float, pivot: Point): Rect {
    val vectorToTopLeft = pivot vectorTo topLeft.toPoint()
    val newTopLeft = pivot + (vectorToTopLeft * scale)
    val newSize = size * scale
    return Rect(newTopLeft.toOffset(), newSize)
}


infix fun Point.vectorTo(that: Point): Vector {
    return that - this
}

operator fun Point.minus(other: Point) = Vector(x - other.x, y - other.y)

operator fun Point.plus(vector: Vector) = Point(x + vector.x, y + vector.y)

operator fun Point.minus(vector: Vector) = Point(x - vector.x, y - vector.y)

operator fun Vector.plus(point: Point): Point = point + this

operator fun Vector.times(scalar: Float) =  Vector(x * scalar, y * scalar)
operator fun Float.times(vector: Vector) = Vector(this * vector.x, this * vector.y)
operator fun Vector.div(scalar: Float): Vector = Vector(x / scalar, y / scalar)

private fun Offset.rotate(angle: Float) = Offset(
    x * cosDeg(angle) - y * sinDeg(angle),
    x * sinDeg(angle) + y * cosDeg(angle)
    )

fun cosDeg(angle: Float): Float = cos(angle.toRad()).toFloat()


fun sinDeg(angle: Float): Float = sin(angle.toRad()).toFloat()

fun Point.rotate(pivot: Point, angle: Float) = Point(
        (x - pivot.x) * cosDeg(angle) - (y - pivot.y) * sinDeg(angle) + pivot.x,
        (x - pivot.x) * sinDeg(angle) + (y - pivot.y) * cosDeg(angle) + pivot.y
    )

fun Vector.rotate(angle: Float) = Vector(
        x * cosDeg(angle) - y * sinDeg(angle),
        x * sinDeg(angle) + y * cosDeg(angle)
    )

fun Float.toRad(): Double = Math.toRadians(toDouble())

fun Point.transform(
    pivot: Point,
    translation: Vector = Vector(0f, 0f),
    scale: Float = 1f,
    rotation: Float = 0f
): Point = (pivot vectorTo this).rotate(rotation) * scale + translation + pivot

fun Vector.transform(
    translation: Vector = Vector(0f, 0f),
    scale: Float = 1f,
    rotation: Float = 0f
): Vector = this.rotate(rotation) * scale + translation

fun Offset.transform(pivot: Offset = Offset(0f, 0f), translate: Offset = Offset(0f, 0f), scale: Float = 1f, rotation:
Float = 0f): Offset {
    return (this - pivot).rotate(rotation) * scale + translate + pivot
}

fun Offset.transform(pivot: Offset = Offset(0f, 0f), transformationState: TransformationState = TransformationState()): Offset {
    return transform(pivot, transformationState.translation.toOffset(), transformationState.scale, transformationState
        .rotation)
}
