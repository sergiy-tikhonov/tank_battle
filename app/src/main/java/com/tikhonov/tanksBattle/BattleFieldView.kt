package com.tikhonov.tanksBattle

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class BattleFieldView(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {

    private var battleFieldHeight = 0
    private var battleFieldWidth = 0

    private lateinit var game: GameViewModel

    private var startCoord = CoordXY()
    private var endCoord = CoordXY()

    private val parentActivity = (context as GameActivity)

    private fun initGameParameters(): GameParameters {
        val gameParameters = GameParameters()
        val colorPlayer1 = Color.RED
        val colorPlayer2 = Color.BLUE
        gameParameters.boardWidth = battleFieldWidth
        gameParameters.boardHeight = battleFieldHeight
        val coord = intArrayOf(0,0)
        getLocationOnScreen(coord)
        gameParameters.pointBoardTopLeft = CoordXY(coord[0], coord[1])
        gameParameters.tankHeight = minOf(battleFieldWidth, battleFieldHeight) /8
        gameParameters.tankWidth = gameParameters.tankHeight
        gameParameters.bulletRadius = gameParameters.tankHeight/20
        val bitmapPlayer1Original = BitmapFactory.decodeResource(context.resources, R.drawable.tank_player1)
        val bitmapPlayer1Scaled = Bitmap.createScaledBitmap(bitmapPlayer1Original, gameParameters.tankWidth, gameParameters.tankHeight, false)
        gameParameters.tank1Bitmap = bitmapPlayer1Scaled

        val bitmapPlayer2Original = BitmapFactory.decodeResource(context.resources, R.drawable.tank_player2)
        val bitmapPlayer2Scaled = Bitmap.createScaledBitmap(bitmapPlayer2Original, gameParameters.tankWidth, gameParameters.tankHeight, false)
        gameParameters.tank2Bitmap = bitmapPlayer2Scaled

        val bitmapFireOriginal = BitmapFactory.decodeResource(context.resources, R.drawable.fire)
        val bitmapFireScaled = Bitmap.createScaledBitmap(bitmapFireOriginal, gameParameters.tankWidth/2, gameParameters.tankHeight/2, false)
        gameParameters.fireBitmap = bitmapFireScaled

        gameParameters.angleDelay = 0.2
        gameParameters.numberOfTanks = 5
        gameParameters.paintTank1Active = Paint().apply {
            color = colorPlayer1
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        gameParameters.paintTank1Inactive = Paint().apply {
            color = colorPlayer1
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        gameParameters.paintTank2Active = Paint().apply {
            color = colorPlayer2
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        gameParameters.paintTank2Inactive = Paint().apply {
            color = colorPlayer2
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        gameParameters.paintBullet1 = Paint().apply {
            color = colorPlayer1
            style = Paint.Style.FILL
        }
        gameParameters.paintBullet2 = Paint().apply {
            color = colorPlayer2
            style = Paint.Style.FILL
        }
        return gameParameters
    }

    private fun initNewGame(){
        val gameParameters = initGameParameters()
        game = parentActivity.gameViewModel
        game.parentActivity = (context as GameActivity)
        game.gameParameters = gameParameters
        game.newGame()
    }

    fun newGame(){
        initNewGame()
        postInvalidate()
    }

    fun bang(){
        parentActivity.playerCannon.start()
        game.bang()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (tankIndex in game.tankGroupPlayer1.indices)
            drawTank(canvas, game.tankGroupPlayer1[tankIndex])
        for (tankIndex in game.tankGroupPlayer2.indices)
            drawTank(canvas, game.tankGroupPlayer2[tankIndex])
        drawBullet(canvas, game.bullet)
    }

    private fun drawTank(canvas: Canvas?, tank: Tank) {

        with(tank) {
            if (angle != 0.0) canvas?.rotate(angle.toFloat(), (coordTank.x + width/2).toFloat(), (coordTank.y + height/2).toFloat())

            canvas?.drawBitmap(tank.bitmapOriginal, coordTank.x.toFloat(), coordTank.y.toFloat(), game.gameParameters.paintTank1Inactive)

            if (tank.destroyed) {
                canvas?.drawBitmap(game.gameParameters.fireBitmap, coordTank.x.toFloat() + width/4, coordTank.y.toFloat(), game.gameParameters.paintTank1Inactive)
            }
            if (angle != 0.0) canvas?.rotate(-this.angle.toFloat(), (coordTank.x + width/2).toFloat(), (coordTank.y + height/2).toFloat())

            if (tank == game.tankPlayer1 || tank == game.tankPlayer2)
                canvas?.drawCircle(
                    (coordTank.x + this.width/2).toFloat(),
                    (coordTank.y + this.height/2).toFloat(),
                    (width/2*1.1).toFloat(),
                    getPaint(tank))
        }

    }

    private fun getPaint(tank: Tank): Paint{
        return when {
            game.whoseTurn == WhoseTurn.FIRST && tank == game.tankPlayer1 -> game.gameParameters.paintTank1Active
            game.whoseTurn == WhoseTurn.FIRST && tank == game.tankPlayer2 -> game.gameParameters.paintTank2Inactive
            game.whoseTurn == WhoseTurn.SECOND && tank == game.tankPlayer2 -> game.gameParameters.paintTank2Active
            else -> game.gameParameters.paintTank1Inactive
        }
    }

    private fun drawBullet(canvas: Canvas?, bullet: Bullet) {
        if (!bullet.isVisible()) return

        bullet.coordActual?.let {
            canvas?.drawCircle(it.x.toFloat(), it.y.toFloat(),
                bullet.radius.toFloat(), if (game.whoseTurn == WhoseTurn.FIRST) game.gameParameters.paintBullet1 else game.gameParameters.paintBullet2)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        battleFieldHeight = h
        battleFieldWidth = w
        initNewGame()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP ->
            {
                endCoord.x = event.x.toInt()
                endCoord.y = event.y.toInt()

                game.rotateTank(startCoord, endCoord)

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