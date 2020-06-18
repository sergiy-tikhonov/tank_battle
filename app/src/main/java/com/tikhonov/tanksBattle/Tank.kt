package com.tikhonov.tanksBattle

import android.graphics.*

class Tank {
    var coordTank = CoordXY(0,0)
    var width: Int = 0
    var height: Int = 0
    var fromLeftToRight = true
    var bulletRadius: Float = 0.0f
    var coordBullet = CoordXY(0,0)
    var coordBulletInitialOriginal= CoordXY(0,0)
    var coordBulletInitialActual= CoordXY(0,0)
    var bulletColor = Color.BLACK
    var bulletIsVisible = false
    var angle: Double = 0.0
    var bulletStep = 0
    var bitmapOriginal: Bitmap? = null
    var bitmapActual: Bitmap? = null
    var paint: Paint = Paint()

    fun draw(canvas: Canvas?){
        canvas?.drawBitmap(bitmapActual!!, coordTank.x.toFloat(), coordTank.y.toFloat(), paint)
    }

    fun drawBullet(canvas: Canvas?){
        if (bulletIsVisible) {
            canvas?.drawCircle(coordBullet.x.toFloat(), coordBullet.y.toFloat(),
                bulletRadius, paint)
        }
    }

    fun rotate(startPoint: CoordXY, endPoint: CoordXY) {
        angle += GeometryUtils.calculateAngle(coordTank, startPoint, endPoint)
        val matrixPlayer1 = Matrix().apply {
            postRotate(angle.toFloat())
        }
        val tankBitmapPlayer1 = Bitmap.createBitmap(bitmapOriginal!!, 0, 0,
            BattleFieldView.tankWidth,
            BattleFieldView.tankHeight, matrixPlayer1, false)
        bitmapActual = tankBitmapPlayer1

        val matrixBullet = Matrix().apply {
            postRotate(angle.toFloat(),
                (coordTank.x + BattleFieldView.tankWidth /2).toFloat(),
                (coordTank.y + BattleFieldView.tankHeight /2).toFloat())
        }
        val pointsArray = floatArrayOf(coordBulletInitialOriginal.x.toFloat(), coordBulletInitialOriginal.y.toFloat())
        matrixBullet.mapPoints(pointsArray)
        coordBulletInitialActual = CoordXY(pointsArray[0].toInt(), pointsArray[1].toInt())
    }

    fun prepareBullet() {
        bulletStep = 0
        coordBullet.x = coordBulletInitialActual.x
        coordBullet.y = coordBulletInitialActual.y
    }

    fun moveBullet(screenWidth: Int, screenHeight: Int): Boolean {
        val angleRad = GeometryUtils.calculateAngleInRadian(angleGrad = angle.toInt())
        if (fromLeftToRight)
            bulletStep++
        else
            bulletStep--
        if (coordBullet.x > screenWidth || coordBullet.x < 0) return false
        coordBullet = GeometryUtils.calculateCurrentXY(coordBulletInitialActual.x, coordBulletInitialActual.y, bulletStep, angleRad)
        if (coordBullet.y > screenHeight || coordBullet.y < 0) return false
        return true
    }
}