package com.tikhonov.tanksBattle

import android.graphics.Bitmap
import android.graphics.Color

class Tank {
    var coordTank = CoordXY(0,0)
    var coordBullet = CoordXY(0,0)
    var bulletColor = Color.BLACK
    var bulletIsVisible = false
}