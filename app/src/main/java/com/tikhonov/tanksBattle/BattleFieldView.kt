package com.tikhonov.tanksBattle

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class WhoseTurn {
    FIRST, SECOND
}

class BattleFieldView(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {

    companion object {
        const val tankWidth = 200
        const val tankHeight = 200
        const val bulletRadius = 10f
        const val angleDelay = 0.2
    }

    private val screenWidth = resources.displayMetrics.widthPixels
    private val screenHeight = resources.displayMetrics.heightPixels

    private val tankPlayer1 = Tank()
    private val tankPlayer2 = Tank()

    private lateinit var whoseTurn: WhoseTurn

    private var startCoord = CoordXY()
    private var endCoord = CoordXY()

    init {
        newGame()
    }

    private fun initTanks(){

        val bitmapPlayer1Original = BitmapFactory.decodeResource(context.resources, R.drawable.tank_player1)
        val bitmapPlayer1Scaled = Bitmap.createScaledBitmap(bitmapPlayer1Original, tankWidth, tankHeight, false)

        val randomX1 = Random.nextInt((screenWidth * 0.1).toInt(), (screenWidth * 0.3).toInt())
        val randomY1 = Random.nextInt((screenHeight * 0.3).toInt(), (screenHeight * 0.8).toInt())
        tankPlayer1.coordTank = CoordXY(randomX1, randomY1)

        val randomX2 = Random.nextInt((screenWidth * 0.6).toInt(), (screenWidth * 0.8).toInt())
        val randomY2 = Random.nextInt((screenHeight * 0.3).toInt(), (screenHeight * 0.8).toInt())
        tankPlayer2.coordTank = CoordXY(randomX2, randomY2)

        val bullet1StartX = tankPlayer1.coordTank.x + bitmapPlayer1Scaled.width
        val bullet1StartY = tankPlayer1.coordTank.y + bitmapPlayer1Scaled.height * 7/20
        tankPlayer1.apply {
            coordBulletInitialOriginal = CoordXY(bullet1StartX, bullet1StartY)
            coordBulletInitialActual = CoordXY(bullet1StartX, bullet1StartY)
            coordBullet = CoordXY(bullet1StartX, bullet1StartY)
            bulletColor = Color.RED
            fromLeftToRight = true
            bulletStep = 0
            bitmapOriginal = bitmapPlayer1Scaled
            bitmapActual = bitmapPlayer1Scaled
            paint = Paint().apply { color = bulletColor }
            width = tankWidth
            height = tankHeight
            this.bulletRadius = BattleFieldView.bulletRadius
        }
        val bitmapPlayer2Original = BitmapFactory.decodeResource(context.resources, R.drawable.tank_player2)
        val bitmapPlayer2Scaled = Bitmap.createScaledBitmap(bitmapPlayer2Original, tankWidth, tankHeight, false)
        val bullet2StartX = tankPlayer2.coordTank.x
        val bullet2StartY = tankPlayer2.coordTank.y + bitmapPlayer2Scaled.height * 6/20
        tankPlayer2.apply {
            coordBulletInitialOriginal = CoordXY(bullet2StartX, bullet2StartY)
            coordBulletInitialActual = CoordXY(bullet2StartX, bullet2StartY)
            coordBullet = CoordXY(bullet2StartX, bullet2StartY)
            bulletColor = Color.BLUE
            fromLeftToRight = false
            bulletStep = 0
            bitmapOriginal = bitmapPlayer2Scaled
            bitmapActual = bitmapPlayer2Scaled
            paint = Paint().apply { color = bulletColor }
            width = tankWidth
            height = tankHeight
            this.bulletRadius = BattleFieldView.bulletRadius
        }

    }

    fun newGame(){
        initTanks()
        whoseTurn = WhoseTurn.FIRST
        postInvalidate()
    }

    fun bang(){

        if (tankPlayer1.bulletIsVisible || tankPlayer2.bulletIsVisible) return

        if (whoseTurn == WhoseTurn.FIRST) {
            tankPlayer1.prepareBullet()
            tankPlayer1.bulletIsVisible = true
            GlobalScope.launch {
                while (tankPlayer1.moveBullet(screenWidth, screenHeight)) {
                    postInvalidate()
                    delay(1)
                }
                tankPlayer1.bulletIsVisible = false
                whoseTurn = WhoseTurn.SECOND
            }
        }
        else {
            tankPlayer2.prepareBullet()
            tankPlayer2.bulletIsVisible = true
            GlobalScope.launch {
                while (tankPlayer2.moveBullet(screenWidth, screenHeight)) {
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

        with(tankPlayer1) {
            draw(canvas)
            drawBullet(canvas)
        }

        with(tankPlayer2) {
            draw(canvas)
            drawBullet(canvas)
        }

    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP ->
            {
                endCoord.x = event.x.toInt()
                endCoord.y = event.y.toInt()
                when (whoseTurn) {
                    WhoseTurn.FIRST -> tankPlayer1.rotate(startCoord, endCoord)
                    WhoseTurn.SECOND -> tankPlayer2.rotate(startCoord, endCoord)
                }
                postInvalidate()
            }
            MotionEvent.ACTION_DOWN ->
            {
                startCoord.x = event.x.toInt()
                startCoord.y = event.y.toInt()
            }
        }
        return true
    }
}