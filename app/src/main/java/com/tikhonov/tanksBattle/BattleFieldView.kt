package com.tikhonov.tanksBattle

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_battle_field.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


enum class WhoseTurn {
    FIRST, SECOND
}

class BattleFieldView(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {

    companion object {
        //const val tankWidth = 200
        //const val tankHeight = 200
        //const val bulletRadius = 10f
        const val angleDelay = 0.2
    }

    private var screenWidth = resources.displayMetrics.widthPixels
    private var screenHeight = (resources.displayMetrics.heightPixels * 0.8).toInt()

    private val tankPlayer1 = Tank()
    private val tankPlayer2 = Tank()

    private lateinit var whoseTurn: WhoseTurn

    private var startCoord = CoordXY()
    private var endCoord = CoordXY()

    private val parentActivity = (context as BattleFieldActivity)

    var scorePlayer1 = 0
    var scorePlayer2 = 0

    init {
        newGame()
        //setFieldBackGround()
    }

    private fun setFieldBackGround() {
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.grass_2)
        val bitmapDrawable = BitmapDrawable(resources, bmp)
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        background = bitmapDrawable
    }

    private fun initTanks(){
        var coord = intArrayOf(0,0)
        getLocationOnScreen(coord)
        val tankWidth = screenHeight/10
        val tankHeight = tankWidth
        val bulletRadius = (tankWidth/20).toFloat()

        val bitmapPlayer1Original = BitmapFactory.decodeResource(context.resources, R.drawable.tank_player1)
        val bitmapPlayer1Scaled = Bitmap.createScaledBitmap(bitmapPlayer1Original, tankWidth, tankHeight, false)

        val randomX1 = Random.nextInt((screenWidth * 0.1).toInt(), (screenWidth * 0.3).toInt())
        val randomY1 = Random.nextInt((coord[0]), ((coord[0]) + screenHeight))
        tankPlayer1.coordTank = CoordXY(randomX1, randomY1)

        val randomX2 = Random.nextInt((screenWidth * 0.6).toInt(), (screenWidth * 0.8).toInt())
        val randomY2 = Random.nextInt((coord[0]), ((coord[0]) + screenHeight))
        tankPlayer2.coordTank = CoordXY(randomX2, randomY2)

        val bullet1StartX = tankPlayer1.coordTank.x + bitmapPlayer1Scaled.width
        val bullet1StartY = tankPlayer1.coordTank.y + bitmapPlayer1Scaled.height * 10/20
        tankPlayer1.apply {
            coordBulletInitialOriginal = CoordXY(bullet1StartX, bullet1StartY)
            coordBulletInitialActual = CoordXY(bullet1StartX, bullet1StartY)
            coordBullet = CoordXY(bullet1StartX, bullet1StartY)
            bulletColor = Color.RED
            fromLeftToRight = true
            bulletStep = 0
            angle = 0.0
            bitmapOriginal = bitmapPlayer1Scaled
            bitmapActual = bitmapPlayer1Scaled
            paint = Paint().apply { color = bulletColor }
            width = tankWidth
            height = tankHeight
            this.bulletRadius = bulletRadius
        }
        val bitmapPlayer2Original = BitmapFactory.decodeResource(context.resources, R.drawable.tank_player2)
        val bitmapPlayer2Scaled = Bitmap.createScaledBitmap(bitmapPlayer2Original, tankWidth, tankHeight, false)
        val bullet2StartX = tankPlayer2.coordTank.x
        val bullet2StartY = tankPlayer2.coordTank.y + bitmapPlayer2Scaled.height * 10/20
        tankPlayer2.apply {
            coordBulletInitialOriginal = CoordXY(bullet2StartX, bullet2StartY)
            coordBulletInitialActual = CoordXY(bullet2StartX, bullet2StartY)
            coordBullet = CoordXY(bullet2StartX, bullet2StartY)
            bulletColor = Color.BLUE
            fromLeftToRight = false
            bulletStep = 0
            bitmapOriginal = bitmapPlayer2Scaled
            angle = 0.0
            bitmapActual = bitmapPlayer2Scaled
            paint = Paint().apply { color = bulletColor }
            width = tankWidth
            height = tankHeight
            this.bulletRadius = bulletRadius
        }

    }

    fun newGame(){
        initTanks()
        whoseTurn = WhoseTurn.FIRST
        postInvalidate()
    }

    private fun bangWithTank(tank: Tank, tankTarget: Tank){
        tank.prepareBullet()
        tank.bulletIsVisible = true
        GlobalScope.launch {
            var bulletCoordXY: CoordXY? = tank.coordBulletInitialActual
            while (bulletCoordXY != null) {
                if (tankTarget.hitWithBullet(bulletCoordXY)) {
                    if (whoseTurn == WhoseTurn.FIRST) parentActivity.scorePlayer1++ else parentActivity.scorePlayer2++
                    parentActivity.runOnUiThread{
                        parentActivity.updateScore()
                    }
                    break
                }
                postInvalidate()
                delay(5)
                bulletCoordXY = tank.moveBullet(screenWidth, screenHeight, (context as BattleFieldActivity).seekBarPower.progress)
            }
            tank.bulletIsVisible = false
            whoseTurn = if (whoseTurn == WhoseTurn.FIRST) WhoseTurn.SECOND else WhoseTurn.FIRST
        }
    }

    fun bang(){

        if (tankPlayer1.bulletIsVisible || tankPlayer2.bulletIsVisible) return

        bangWithTank(
            if (whoseTurn == WhoseTurn.FIRST) tankPlayer1 else tankPlayer2,
            if (whoseTurn == WhoseTurn.FIRST) tankPlayer2 else tankPlayer1
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        with(tankPlayer1) {
            draw(canvas, whoseTurn == WhoseTurn.FIRST)
            drawBullet(canvas)
        }

        with(tankPlayer2) {
            draw(canvas, whoseTurn == WhoseTurn.SECOND)
            drawBullet(canvas)
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenWidth = w
        screenHeight = h
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