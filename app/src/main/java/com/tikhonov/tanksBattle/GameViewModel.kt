package com.tikhonov.tanksBattle

import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.android.synthetic.main.activity_battle_field.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class WhoseTurn {
    FIRST, SECOND
}

class Game: ViewModel() {

    lateinit var gameParameters: GameParameters
    lateinit var tankPlayer1: Tank
    lateinit var tankPlayer2: Tank
    val tankGroupPlayer1 = mutableListOf<Tank>()

    val tankGroupPlayer2 = mutableListOf<Tank>()
    val bullet = Bullet()
    var whoseTurn = WhoseTurn.FIRST

    val scorePlayer1 = MutableLiveData(0)
    val scorePlayer2 = MutableLiveData(0)
    val shouldRedraw = MutableLiveData(false)

    lateinit var parentActivity: GameActivity

    fun newGame() {
        whoseTurn = WhoseTurn.FIRST
        for (tankIndex in 0 until gameParameters.numberOfTanks) {
            initTankPlayer1()
        }
        tankPlayer1 = tankGroupPlayer1[0]
        for (tankIndex in 0 until gameParameters.numberOfTanks) {
            initTankPlayer2()
        }
        tankPlayer2 = tankGroupPlayer2[0]

        initBullet()
    }

    private fun initBullet(){
        bullet.gameParameters = gameParameters
        bullet.radius = gameParameters.bulletRadius

    }

    private fun initTankPlayer1() {
        val tank = Tank()
        var randomX1 = 0
        var randomY1 = 0
        var fitWithGroup = false
        while (!fitWithGroup) {
            fitWithGroup = true
            randomX1 = Random.nextInt((gameParameters.boardWidth * 0.1).toInt(), (gameParameters.boardWidth * 0.3).toInt())
            randomY1 = Random.nextInt(gameParameters.tankHeight, gameParameters.boardHeight - gameParameters.tankHeight)
            for (groupMember in tankGroupPlayer1)
                if (GeometryUtils.pointIsInsideCircle(CoordXY(randomX1, randomY1), groupMember.centerPoint(), 2 * groupMember.width)) {
                    fitWithGroup = false
                    break
                }
        }

        tank.apply {
            coordTank = CoordXY(randomX1, randomY1)
            val bullet1StartX = coordTank.x + gameParameters.tankWidth
            val bullet1StartY = coordTank.y + gameParameters.tankHeight /2
            coordBulletInitialOriginal = CoordXY(bullet1StartX, bullet1StartY)
            coordBulletInitialActual = CoordXY(bullet1StartX, bullet1StartY)
            fromLeftToRight = true
            angle = 0.0
            bitmapOriginal = gameParameters.tank1Bitmap
            bitmapActual = gameParameters.tank1Bitmap
            width = gameParameters.tankWidth
            height = gameParameters.tankHeight
        }
        tankGroupPlayer1.add(tank)
    }

    private fun initTankPlayer2() {
        val tank = Tank()
        var randomX2 = 0
        var randomY2 = 0
        var fitWithGroup = false

        while (!fitWithGroup) {
            fitWithGroup = true
            randomX2 = Random.nextInt((gameParameters.boardWidth * 0.6).toInt(), (gameParameters.boardWidth * 0.8).toInt())
            randomY2 = Random.nextInt(gameParameters.tankHeight, gameParameters.boardHeight - gameParameters.tankHeight)
            for (groupMember in tankGroupPlayer2)
                if (GeometryUtils.pointIsInsideCircle(CoordXY(randomX2, randomY2), groupMember.centerPoint(), 2 * groupMember.width)) {
                    fitWithGroup = false
                    break
                }
        }

        tank.apply {
            coordTank = CoordXY(randomX2, randomY2)
            val bullet2StartX = coordTank.x
            val bullet2StartY = coordTank.y + gameParameters.tankHeight /2
            coordBulletInitialOriginal = CoordXY(bullet2StartX, bullet2StartY)
            coordBulletInitialActual = CoordXY(bullet2StartX, bullet2StartY)
            fromLeftToRight = false
            angle = 0.0
            bitmapOriginal = gameParameters.tank2Bitmap
            bitmapActual = gameParameters.tank2Bitmap
            width = gameParameters.tankWidth
            height = gameParameters.tankHeight
        }
        tankGroupPlayer2.add(tank)
    }

    fun bang() {

        if (bullet.isVisible()) return

        bullet.assignToTank(
            if (whoseTurn == WhoseTurn.FIRST) tankPlayer1 else tankPlayer2,
            if (whoseTurn == WhoseTurn.FIRST) tankPlayer2 else tankPlayer1,
            whoseTurn,
            parentActivity.seekBarPower.progress

        )
        var targets = if (whoseTurn == WhoseTurn.FIRST) tankGroupPlayer2.filter { !it.destroyed } else tankGroupPlayer1.filter { !it.destroyed }
        if (targets.size > 1) targets = targets.drop(1)
        bullet.setVisible(true)
        GlobalScope.launch {
            loop@ while (bullet.coordActual != null) {
                for (target in targets) {
                    if (GeometryUtils.pointIsInsideCircle(bullet.coordActual!!, target.centerPoint(),target.height/2)) {
                        target.destroyed = true
                        if (whoseTurn == WhoseTurn.FIRST) scorePlayer1.postValue(scorePlayer1.value!! + 1)
                        else scorePlayer2.postValue(scorePlayer2.value!! + 1)
                        break@loop
                    }
                }
                shouldRedraw.postValue(true)
                delay(5)
                bullet.coordActual = bullet.moveNextStep()
            }
            bullet.setVisible(false)
            whoseTurn = if (whoseTurn == WhoseTurn.FIRST) WhoseTurn.SECOND else WhoseTurn.FIRST
            shouldRedraw.postValue(true)

        }
    }

    fun rotateTank(startPoint: CoordXY, endPoint: CoordXY) {
        when (whoseTurn) {
            WhoseTurn.FIRST -> tankPlayer1.rotate(startPoint, endPoint)
            WhoseTurn.SECOND -> tankPlayer2.rotate(startPoint, endPoint)
        }
    }

}