package com.tikhonov.tanksBattle

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class WhoseTurn {
    FIRST, SECOND
}

class BattleFieldView(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {

    companion object {
        val tankWidth = 200
        val tankHeight = 200
        val bulletRadius = 10f
    }

    val tankPlayer1 = Tank().apply {
        coordTank = CoordXY(200, 500)
        coordBullet = CoordXY(0, 0)
        bulletColor = Color.RED
        bulletIsVisible = false
    }

    val tankPlayer2 = Tank().apply {
        coordTank = CoordXY(600, 1000)
        coordBullet = CoordXY(0, 0)
        bulletColor = Color.BLUE
        bulletIsVisible = false
    }

    val paintPlayer1 = Paint().apply {
        color = tankPlayer1.bulletColor
    }
    val paintPlayer2 = Paint().apply {
        color = tankPlayer2.bulletColor
    }

    var whoseTurn = WhoseTurn.FIRST

    var tankBitmapPlayer1Original = BitmapFactory.decodeResource(context.resources, R.drawable.tank_player1)
    val tankBitmapPlayer1 = Bitmap.createScaledBitmap(tankBitmapPlayer1Original, tankWidth, tankHeight, false)

    var tankBitmapPlayer2Original = BitmapFactory.decodeResource(context.resources, R.drawable.tank_player2)
    val tankBitmapPlayer2 = Bitmap.createScaledBitmap(tankBitmapPlayer2Original, tankWidth, tankHeight, false)

    fun bang(){

        if (tankPlayer1.bulletIsVisible || tankPlayer2.bulletIsVisible) return

        val screenWidth =  resources.displayMetrics.widthPixels
        if (whoseTurn == WhoseTurn.FIRST) {
            val startX = tankPlayer1.coordTank.x + tankBitmapPlayer1.width
            val startY = tankPlayer1.coordTank.y + tankBitmapPlayer1.height * 7/20
            tankPlayer1.bulletIsVisible = true
            tankPlayer1.coordBullet.y = startY
            GlobalScope.launch {
                for (i in startX..screenWidth) {
                    tankPlayer1.coordBullet.x = i
                    postInvalidate()
                    delay(1)
                }
                tankPlayer1.bulletIsVisible = false
                whoseTurn = WhoseTurn.SECOND
            }
        }
        else {
            val startX = tankPlayer2.coordTank.x
            val startY = tankPlayer2.coordTank.y + tankBitmapPlayer2.height * 6/20
            tankPlayer2.bulletIsVisible = true
            tankPlayer2.coordBullet.y = startY
            GlobalScope.launch {
                for (i in startX downTo 0) {
                    tankPlayer2.coordBullet.x = i
                    postInvalidate()
                    delay(1)
                }
                tankPlayer2.bulletIsVisible = false
                whoseTurn = WhoseTurn.FIRST
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawBitmap(tankBitmapPlayer1, tankPlayer1.coordTank.x.toFloat(), tankPlayer1.coordTank.y.toFloat(), paintPlayer1)
        canvas?.drawBitmap(tankBitmapPlayer2, tankPlayer2.coordTank.x.toFloat(), tankPlayer2.coordTank.y.toFloat(), paintPlayer2)


        if (tankPlayer1.bulletIsVisible) {
            canvas?.drawCircle(tankPlayer1.coordBullet.x.toFloat(), tankPlayer1.coordBullet.y.toFloat(), bulletRadius, paintPlayer1)
        }
        if (tankPlayer2.bulletIsVisible) {
            canvas?.drawCircle(tankPlayer2.coordBullet.x.toFloat(), tankPlayer2.coordBullet.y.toFloat(), bulletRadius, paintPlayer2)
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP ->
            {
            }
        }
        return true
    }
}