package com.tikhonov.tanksBattle

import android.graphics.*

class Tank {
    var coordTank = CoordXY(0,0)
    var width: Int = 0
    var height: Int = 0
    var fromLeftToRight = true
    var destroyed = false
    var coordBulletInitialOriginal= CoordXY(0,0)
    var coordBulletInitialActual= CoordXY(0,0)
    var angle: Double = 0.0
    lateinit var bitmapOriginal: Bitmap
    lateinit var bitmapActual: Bitmap
    lateinit var centerPoint: CoordXY

    fun rotate(startPoint: CoordXY, endPoint: CoordXY) {
        val coordTankCenter = CoordXY(coordTank.x + this.width/2, coordTank.y + this.height/2)
        angle += GeometryUtils.calculateAngle(coordTankCenter, startPoint, endPoint)
        val matrixPlayer1 = Matrix().apply {
            postRotate(angle.toFloat(), coordTankCenter.x.toFloat(), coordTankCenter.y.toFloat())
        }
        val tankBitmapPlayer1 = Bitmap.createBitmap(bitmapOriginal, 0, 0,
            bitmapOriginal.width,
            bitmapOriginal.height, matrixPlayer1, true)
        bitmapActual = tankBitmapPlayer1

        val matrixBullet = Matrix().apply {
            postRotate(angle.toFloat(),
                (coordTank.x + width /2).toFloat(),
                (coordTank.y + height /2).toFloat())
        }
        val pointsArray = floatArrayOf(coordBulletInitialOriginal.x.toFloat(), coordBulletInitialOriginal.y.toFloat())
        matrixBullet.mapPoints(pointsArray)
        coordBulletInitialActual = CoordXY(pointsArray[0].toInt(), pointsArray[1].toInt())
    }

    fun setCenterPoint() {
        centerPoint = CoordXY(coordTank.x + width/2, coordTank.y + height/2)
    }

}