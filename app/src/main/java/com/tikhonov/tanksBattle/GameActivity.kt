package com.tikhonov.tanksBattle

import android.graphics.BitmapFactory
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.animation.AnimationUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_battle_field.*

class GameActivity: AppCompatActivity() {

    var gameViewModel = GameViewModel()

    private lateinit var playerExplosion: MediaPlayer
    lateinit var playerCannon : MediaPlayer
    private lateinit var playerCheers: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battle_field)

        playerExplosion = MediaPlayer.create(this, R.raw.explosion)
        playerCannon = MediaPlayer.create(this, R.raw.cannon)
        playerCheers = MediaPlayer.create(this, R.raw.cheers)

        setFieldBackGround()

        buttonBang.setOnClickListener {
            it.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.button_shot))
            battleFieldView.bang()
        }

        buttonNewGame.setOnClickListener {
            battleFieldView.newGame()
        }

        seekBarPower.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (gameViewModel.whoseTurn == WhoseTurn.FIRST)
                    gameViewModel.shotPowerPlayer1 = progress
                else
                    gameViewModel.shotPowerPlayer2 = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        observeGameViewModel()

    }

    private fun observeGameViewModel() {
        gameViewModel.scorePlayer1.observe(this,  Observer{ scorePlayer1 ->
            updateScore(scorePlayer1, gameViewModel.scorePlayer2.value!!)
        })
        gameViewModel.scorePlayer2.observe(this,  Observer{ scorePlayer2 ->
            updateScore(gameViewModel.scorePlayer1.value!!, scorePlayer2)
        })
        gameViewModel.shouldRedraw.observe(this, Observer {
            battleFieldView.invalidate()
        })
        gameViewModel.eventExplosion.observe(this, EventObserver {
            if (it) playerExplosion.start()
        })

        gameViewModel.shotPower.observe(this, Observer {
            setShotPower(it)
        })

        gameViewModel.eventGameIsOver.observe(this, EventObserver {
            if (it) {
                playerCheers.start()
                Handler().postDelayed({
                    val score1 = gameViewModel.scorePlayer1.value!!
                    val score2 = gameViewModel.scorePlayer2.value!!
                    val message =
                        if (score1 > score2) getString(R.string.firstPlayerWon, score1, score2)
                        else getString(R.string.secondPlayerWon, score1, score2)
                    val builder = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                        .setTitle(getString(R.string.gameIsOver))
                        .setMessage(message)
                        .setCancelable(false)
                    builder.setPositiveButton(getString(R.string.startNextGame)
                    ) { dialog, _ ->
                        battleFieldView.newGame()
                        battleFieldView.postInvalidate()
                        dialog.cancel()
                    }
                    builder.setNegativeButton(getString(R.string.leaveTheApp)
                    ) { dialog, _ ->
                        finish()
                    }
                    builder.create().show()
                }, 500)
            }
        })
    }

    private fun setFieldBackGround() {
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.ground)
        val bitmapDrawable = BitmapDrawable(resources, bmp)
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        rootLayout.background = bitmapDrawable
    }

    private fun updateScore(scorePlayer1: Int, scorePlayer2: Int) {
        textViewScore.text = resources.getString(R.string.score, scorePlayer1, scorePlayer2)
    }

    private fun setShotPower(shotPower: Int) {
        seekBarPower.progress = shotPower
    }

}