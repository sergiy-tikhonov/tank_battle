package com.tikhonov.tanksBattle

import android.graphics.Color
import android.graphics.Paint

class Bullet {
    private var isVisible = false
    lateinit var gameParameters: GameParameters
    var angle = 0.0
    var shotPower = 0
    var radius = 0
    lateinit var whoseTurn: WhoseTurn
    var color: Int = Color.BLACK
    var fromLeftToRight = true
    var coordActual: CoordXY? = CoordXY(0, 0)
    var coordInitial = CoordXY(0, 0)
    var step = 0

    fun assignToTank(tank: Tank, target: Tank, whoseTurn: WhoseTurn, shotPower: Int) {
        coordInitial = CoordXY(tank.coordBulletInitialActual.x, tank.coordBulletInitialActual.y)
        coordActual = CoordXY(tank.coordBulletInitialActual.x, tank.coordBulletInitialActual.y)
        angle = tank.angle
        fromLeftToRight = tank.fromLeftToRight
        step = 0
        isVisible = true
        this.whoseTurn = whoseTurn
        this.shotPower = shotPower
    }

    fun setVisible(visible: Boolean) {
        isVisible = visible
    }

    fun isVisible(): Boolean {
        return isVisible
    }

    fun moveNextStep(): CoordXY? {
        val angleRad = GeometryUtils.calculateAngleInRadian(angleGrad = angle.toInt())
        if (fromLeftToRight)
            step++
        else
            step--
        if (coordActual!!.x > gameParameters.boardWidth || coordActual!!.x < 0) return null
        coordActual = GeometryUtils.calculateCurrentXY(coordInitial.x, coordInitial.y, step, angleRad, shotPower)
        if (coordActual!!.y > gameParameters.boardHeight || coordActual!!.y < 0) return null
        return coordActual
    }

}