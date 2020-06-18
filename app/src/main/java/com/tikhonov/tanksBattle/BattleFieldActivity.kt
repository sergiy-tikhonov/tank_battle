package com.tikhonov.tanksBattle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_battle_field.*

class BattleFieldActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battle_field)
        battleFieldView.newGame()

        buttonBang.setOnClickListener {
            battleFieldView.bang()
        }

        buttonNewGame.setOnClickListener {
            battleFieldView.newGame()
        }

    }
}