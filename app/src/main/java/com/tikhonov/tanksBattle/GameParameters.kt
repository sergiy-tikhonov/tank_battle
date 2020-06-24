package com.tikhonov.tanksBattle

import android.graphics.Bitmap
import android.graphics.Paint

class GameParameters {
    var boardHeight = 0
    var boardWidth = 0
    var bulletRadius = 0
    var tankHeight = 0
    var tankWidth = 0
    var pointBoardTopLeft = CoordXY(0,0)
    lateinit var tank1Bitmap: Bitmap
    lateinit var tank2Bitmap: Bitmap
    lateinit var fireBitmap: Bitmap
    var angleDelay = 0.0
    var numberOfTanks = 5
    lateinit var paintTank1Active: Paint
    lateinit var paintTank1Inactive: Paint
    lateinit var paintTankDestroyed: Paint
    lateinit var paintTank2Active: Paint
    lateinit var paintTank2Inactive: Paint
    lateinit var paintBullet1: Paint
    lateinit var paintBullet2: Paint

}