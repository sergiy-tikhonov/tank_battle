package com.tikhonov.tanksBattle

import kotlin.math.*

object GeometryUtils {
    fun calculateAngle(coordCenter: CoordXY, coordStart: CoordXY, coordEnd: CoordXY): Double {
        val ax = (coordStart.x - coordCenter.x).toDouble()
        val ay = (-coordStart.y + coordCenter.y).toDouble()
        val bx = (coordEnd.x - coordCenter.x).toDouble()
        val by = (-coordEnd.y + coordCenter.y).toDouble()
        val cos = (ax * ay + bx * by) / sqrt((ax.pow(2) + ay.pow(2))*(bx.pow(2) + by.pow(2)))
        val angleSign = if (ax * by - ay * bx > 0) -1 else 1
        val angle = acos(cos) * 180 / Math.PI * angleSign
        if (angle.isNaN()) return 0.0

        // TODO replace 0.2 with GameParameters.angleDelay

        return angle * 0.2
    }

    fun calculateCurrentXY(xInitial:Int, yInitial:Int, step: Int, angle: Double, shotPower: Int): CoordXY {

        val g = 0.03

        val currentX =  xInitial + (step * cos(angle) * shotPower).roundToInt()
        val currentY =  (yInitial + step * sin(angle) * shotPower + g * step.toFloat().pow(2) / 2).roundToInt()
        return CoordXY(currentX, currentY)
    }

    fun calculateAngleInRadian(angleGrad: Int): Double {
        return angleGrad * Math.PI / 180
    }

    fun pointIsInsideCircle(pointX: Int, pointY: Int, centerX: Int, centerY: Int, radius: Int): Boolean {
        val distance = sqrt((centerX - pointX).toFloat().pow(2) + (centerY - pointY).toFloat().pow(2))
        return distance < radius
    }
}