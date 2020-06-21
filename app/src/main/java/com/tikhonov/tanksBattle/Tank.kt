package com.tikhonov.tanksBattle

import android.graphics.*
import android.util.Log
import java.lang.Math.pow
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

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
       set(value) {
           paintIsActive = Paint().apply {
               color = value.color
               strokeWidth = 5f
               style = Paint.Style.STROKE
           }
           paintIsInactive = Paint().apply {
               color = value.color
               strokeWidth = 1f
               style = Paint.Style.STROKE
           }
           field = value
       }

    lateinit var paintIsActive: Paint
    lateinit var paintIsInactive: Paint

    fun draw(canvas: Canvas?, isActive: Boolean){
        paint.style = Paint.Style.STROKE

        canvas?.rotate(this.angle.toFloat(), (coordTank.x + width/2).toFloat(), (coordTank.y + height/2).toFloat())
        canvas?.drawBitmap(bitmapOriginal!!, coordTank.x.toFloat(), coordTank.y.toFloat(), paint)
        canvas?.rotate(-this.angle.toFloat(), (coordTank.x + width/2).toFloat(), (coordTank.y + height/2).toFloat())

        canvas?.drawCircle(
            (coordTank.x + this.width/2).toFloat(),
            (coordTank.y + this.height/2).toFloat(),
            (width/2*1.1).toFloat(),
            if (isActive) paintIsActive else paintIsInactive)
    }

    fun drawBullet(canvas: Canvas?){
        if (bulletIsVisible) {
            paint.style = Paint.Style.FILL
            canvas?.drawCircle(coordBullet.x.toFloat(), coordBullet.y.toFloat(),
                bulletRadius, paint)
        }
    }

    fun rotate(startPoint: CoordXY, endPoint: CoordXY) {
        val coordTankCenter = CoordXY(coordTank.x + this.width/2, coordTank.y + this.height/2)
        angle += GeometryUtils.calculateAngle(coordTankCenter, startPoint, endPoint)
        val matrixPlayer1 = Matrix().apply {
            postRotate(angle.toFloat(), coordTankCenter.x.toFloat(), coordTankCenter.y.toFloat())
        }
        val tankBitmapPlayer1 = Bitmap.createBitmap(bitmapOriginal!!, 0, 0,
            bitmapOriginal!!.width,
            bitmapOriginal!!.height, matrixPlayer1, true)
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

    fun prepareBullet() {
        bulletStep = 0
        coordBullet.x = coordBulletInitialActual.x
        coordBullet.y = coordBulletInitialActual.y
    }

    fun moveBullet(screenWidth: Int, screenHeight: Int, shotPower: Int): CoordXY? {
        val angleRad = GeometryUtils.calculateAngleInRadian(angleGrad = angle.toInt())
        if (fromLeftToRight)
            bulletStep++
        else
            bulletStep--
        if (coordBullet.x > screenWidth || coordBullet.x < 0) return null
        coordBullet = GeometryUtils.calculateCurrentXY(coordBulletInitialActual.x, coordBulletInitialActual.y, bulletStep, angleRad, shotPower)
        if (coordBullet.y > screenHeight || coordBullet.y < 0) return null
        return coordBullet
    }

    fun hitWithBullet(bulletCoordXY: CoordXY): Boolean {
        val circleCenter = CoordXY(coordTank.x + width/2, coordTank.y + height/2)
        val distance = sqrt((circleCenter.x - bulletCoordXY.x).toFloat().pow(2) + (circleCenter.y - bulletCoordXY.y).toFloat().pow(2))
        return distance < width/2
    }
}