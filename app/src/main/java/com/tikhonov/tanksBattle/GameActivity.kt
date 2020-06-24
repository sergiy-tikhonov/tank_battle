package com.tikhonov.tanksBattle

import android.graphics.BitmapFactory
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_battle_field.*

class GameActivity: AppCompatActivity() {

    var scorePlayer1 = 0
    var scorePlayer2 = 0

    companion object {
        const val keyScorePlayer1 = "scorePlayer1"
        const val keyScorePlayer2 = "scorePlayer2"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battle_field)

        savedInstanceState?.let {
            scorePlayer1 = it.getInt(keyScorePlayer1)
            scorePlayer2 = it.getInt(keyScorePlayer2)
        }

        setFieldBackGround()

        buttonBang.setOnClickListener {
            it.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.button_shot))
            battleFieldView.bang()
        }

        buttonNewGame.setOnClickListener {
            battleFieldView.newGame()
        }

    }

    private fun setFieldBackGround() {
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.ground)
        val bitmapDrawable = BitmapDrawable(resources, bmp)
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        rootLayout.background = bitmapDrawable
    }

    fun updateScore(scorePlayer1: Int, scorePlayer2: Int) {
        textViewScore.text = resources.getString(R.string.score, scorePlayer1, scorePlayer2)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState?.putInt(keyScorePlayer1, scorePlayer1)
        outState?.putInt(keyScorePlayer2, scorePlayer2)
    }
}