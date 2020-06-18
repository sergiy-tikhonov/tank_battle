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
        return angle * BattleFieldView.angleDelay
    }



    fun calculateCurrentXY(xInitial:Int, yInitial:Int, step: Int, angle: Double): CoordXY {
        val currentX =  xInitial + (step * cos(angle)).roundToInt()
        val currentY =  yInitial + (step * sin(angle)).roundToInt()
        return CoordXY(currentX, currentY)
    }

    fun calculateAngleInRadian(angleGrad: Int): Double {
        return angleGrad * Math.PI / 180
    }
}