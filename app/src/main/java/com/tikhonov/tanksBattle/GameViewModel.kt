package com.tikhonov.tanksBattle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class WhoseTurn {
    FIRST, SECOND
}

class GameViewModel: ViewModel() {

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
    val eventExplosion = MutableLiveData(Event(false))
    val eventGameIsOver = MutableLiveData(Event(false))

    var shotPowerPlayer1 = 0
    var shotPowerPlayer2 = 0
    val shotPower = MutableLiveData(0)

    lateinit var parentActivity: GameActivity

    fun newGame() {

        scorePlayer1.postValue(0)
        scorePlayer2.postValue(0)
        shotPower.postValue(0)
        shotPowerPlayer1 = 0
        shotPowerPlayer2 = 0
        tankGroupPlayer1.clear()
        tankGroupPlayer2.clear()
        whoseTurn = WhoseTurn.FIRST
        for (tankIndex in 0 until gameParameters.numberOfTanks) {
            initTank(whoseTurn)
        }
        tankPlayer1 = tankGroupPlayer1[0]
        whoseTurn = WhoseTurn.SECOND
        for (tankIndex in 0 until gameParameters.numberOfTanks) {
            initTank(whoseTurn)
        }
        tankPlayer2 = tankGroupPlayer2[0]
        when (Random.nextInt(2)) {
            0 -> whoseTurn = WhoseTurn.FIRST
            1 -> whoseTurn = WhoseTurn.SECOND

        }
        initBullet()
    }

    private fun initBullet(){
        bullet.gameParameters = gameParameters
        bullet.radius = gameParameters.bulletRadius

    }

    private fun initTank(whoseTurn: WhoseTurn){
        lateinit var tankGroup: MutableList<Tank>
        val tank = Tank()
        if (whoseTurn == WhoseTurn.FIRST) {
            tank.apply {
                fromLeftToRight = true
                bitmapActual = gameParameters.tank1Bitmap
                bitmapOriginal = gameParameters.tank1Bitmap
            }
            tankGroup = tankGroupPlayer1
        }

        else {
            tank.apply {
                fromLeftToRight = false
                bitmapActual = gameParameters.tank2Bitmap
                bitmapOriginal = gameParameters.tank2Bitmap
            }
            tankGroup = tankGroupPlayer2
        }

        var randomX = 0
        var randomY = 0

        var fitWithGroup = false
        while (!fitWithGroup) {
            fitWithGroup = true
            if (whoseTurn == WhoseTurn.FIRST) {
                randomX = Random.nextInt(gameParameters.tankWidth, gameParameters.boardWidth / 2 - gameParameters.tankWidth)
                randomY = Random.nextInt(gameParameters.tankHeight, gameParameters.boardHeight - gameParameters.tankHeight)
            } else {
                randomX = Random.nextInt(gameParameters.boardWidth / 2 + gameParameters.tankWidth, gameParameters.boardWidth - gameParameters.tankWidth)
                randomY = Random.nextInt(gameParameters.tankHeight, gameParameters.boardHeight - gameParameters.tankHeight)
            }
            for (groupMember in tankGroup){
                if (GeometryUtils.pointIsInsideCircle(randomX, randomY, groupMember.centerPoint.x, groupMember.centerPoint.y, 2*groupMember.width)) {
                    fitWithGroup = false
                    break
                }
            }
        }

        tank.apply {
            coordTank = CoordXY(randomX, randomY)
            width = gameParameters.tankWidth
            height = gameParameters.tankHeight
            val bullet1StartX = coordTank.x + if (whoseTurn == WhoseTurn.FIRST) gameParameters.tankWidth else 0
            val bullet1StartY = coordTank.y + gameParameters.tankHeight /2
            coordBulletInitialOriginal = CoordXY(bullet1StartX, bullet1StartY)
            coordBulletInitialActual = CoordXY(bullet1StartX, bullet1StartY)
            setCenterPoint()
        }
        tankGroup.add(tank)

    }

    fun bang() {

        if (bullet.isVisible()) return

        bullet.assignToTank(
            if (whoseTurn == WhoseTurn.FIRST) tankPlayer1 else tankPlayer2,
            if (whoseTurn == WhoseTurn.FIRST) tankPlayer2 else tankPlayer1,
            whoseTurn,
            if (whoseTurn == WhoseTurn.FIRST) shotPowerPlayer1 else shotPowerPlayer2
        )
        var targets = if (whoseTurn == WhoseTurn.FIRST) tankGroupPlayer2.filter { !it.destroyed } else tankGroupPlayer1.filter { !it.destroyed }
        if (targets.size > 1) targets = targets.drop(1)
        bullet.setVisible(true)
        viewModelScope.launch {
            loop@ while (bullet.coordActual != null) {
                for (target in targets) {
                    if (GeometryUtils.pointIsInsideCircle(bullet.coordActual!!.x, bullet.coordActual!!.y, target.centerPoint.x, target.centerPoint.y, target.height/2)) {
                        eventExplosion.postValue(Event(true))
                        target.destroyed = true
                        if (whoseTurn == WhoseTurn.FIRST) scorePlayer1.postValue(scorePlayer1.value!! + 1)
                        else scorePlayer2.postValue(scorePlayer2.value!! + 1)
                        if (target == tankPlayer1 || target == tankPlayer2) eventGameIsOver.postValue(Event(true))
                        break@loop
                    }
                }
                shouldRedraw.postValue(true)
                delay(5)
                bullet.coordActual = bullet.moveNextStep()
            }
            bullet.setVisible(false)
            whoseTurn = if (whoseTurn == WhoseTurn.FIRST) WhoseTurn.SECOND else WhoseTurn.FIRST
            shotPower.postValue(if (whoseTurn == WhoseTurn.FIRST) shotPowerPlayer1 else shotPowerPlayer2)
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