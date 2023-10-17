package com.realsoc.image_cropper

import androidx.compose.ui.geometry.Offset
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt

class Rectangle {
    val topLeft: Offset
    val bottomLeft: Offset
    val bottomRight: Offset
    val topRight: Offset

    val a: Offset
        get() = topLeft
    val b: Offset
        get() = bottomLeft
    val c: Offset
        get() = bottomRight
    val d: Offset
        get() = topRight

    val ac: Offset
        get() = c - a
    val ab: Offset
        get() = b - a
    val ad: Offset
        get() = d - a
    val bc: Offset
        get() = c - b
    val bd: Offset
        get() = d - b

    val center: Offset
        get() = a + (ac / 2f)

    val o: Offset
        get() = center

    val oa: Offset
        get() = a - o
    val ob: Offset
        get() = b - o
    val oc: Offset
        get() = c - o
    val od: Offset
        get() = d - o

    val width: Float
        get() = ad.getDistance()
    val height: Float
        get() = ab.getDistance()

    val angle: Float
        get() {
            val abNormed = ab / ab.getDistance()
            // Calculate angle between (0, 1) and normedAB
            // normedAB = (sin(angle), cos(angle))
            val sinAngle = abNormed.x
            val cosAngle = abNormed.y

            val angleOnTopCircle = Math.toDegrees(acos(cosAngle).toDouble()).toFloat()
            val angle = if (sinAngle < 0) {
                180f + angleOnTopCircle
            } else {
                angleOnTopCircle
            }
            return angle
        }
    constructor(topLeft: Offset, bottomLeft: Offset, bottomRight: Offset, topRight: Offset) {
        this.topLeft = topLeft
        this.bottomLeft = bottomLeft
        this.bottomRight = bottomRight
        this.topRight = topRight
    }

    constructor(center: Offset, topLeft: Offset, width: Float) {
        val oa = topLeft - center
        val height = sqrt((oa * 2f).getDistanceSquared() - (width).pow(2))

        val alpha = atan(height / width).toDouble()
        val beta = 180 - 2 * Math.toDegrees(alpha)

        //val ob = oa.rotate(beta.toFloat())
        val oc = -oa
        val od = -ob

        this.topLeft = topLeft
        this.bottomLeft = center + ob
        this.bottomRight = center + oc
        this.topRight = center + od
    }

    constructor() : this(
        Offset(0f, 0f),
        Offset(0f, 0f),
        Offset(0f, 0f),
        Offset(0f, 0f),
    )

    fun applyTransformationState(zoomChange: Float, rotationChange: Float, offsetChange: Offset): Rectangle {
        val newCenter = o + offsetChange
        val newTopLeft = (oa * zoomChange)//.rotate(rotationChange) + newCenter
        val newWidth = width * zoomChange
        return Rectangle(topLeft = newTopLeft, center = newCenter, width = newWidth)
    }
}
