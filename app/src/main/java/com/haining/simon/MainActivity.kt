package com.haining.simon

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.res.ResourcesCompat
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

const val LIGHT_ON_TIME_MILLIS: Long = 300
const val TIME_BETWEEN_LIGHTS_MILLIS: Long = 500
const val DISPLAY_BEGIN_DELAY_MILLIS: Long = 1000

class SimonButton(val button: Button, private val onColor: Int, private val offColor: Int) {
    init {
        turnOff()
    }

    fun turnOnThenOff() {
        turnOn()
        Handler().postDelayed({turnOff()}, LIGHT_ON_TIME_MILLIS)
    }

    fun turnOff() {
        button.setBackgroundColor(offColor)
    }

    fun turnOn() {
        button.setBackgroundColor(onColor)
    }
}

class MainActivity : AppCompatActivity() {
    private val correctSequence = ArrayList<Int>()
    private var simonButtons: List<SimonButton> = listOf()
    private var currentIndex = 0
    private var acceptPresses = false
    private val random = Random()
    private val score get() = correctSequence.size
    private var highScore = 0

    private enum class LightCommand {
        TURN_ON,
        TURN_OFF,
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        makeSimonButtons()
        newGameButton.setOnClickListener{newGame()}
    }

    private fun makeSimonButtons() {
        simonButtons = listOf(
                SimonButton(button0, lookupColor(R.color.redOn), lookupColor(R.color.redOff)),
                SimonButton(button1, lookupColor(R.color.blueOn), lookupColor(R.color.blueOff)),
                SimonButton(button2, lookupColor(R.color.greenOn), lookupColor(R.color.greenOff)),
                SimonButton(button3, lookupColor(R.color.yellowOn), lookupColor(R.color.yellowOff))
        )

        simonButtons.forEachIndexed{i, b ->
            b.button.setOnClickListener{
                handleButtonPress(i, b)
            }
        }
    }

    private fun lookupColor(colorId: Int) = ResourcesCompat.getColor(getResources(), colorId, null)

    private fun newGame() {
        correctSequence.clear()
        nextRound()
    }

    private fun nextRound() {
        if (score > highScore) {
            highScore = score
        }
        acceptPresses = false
        scoreText.text = "" + score
        highScoreText.text = "" + highScore
        correctSequence.add(random.nextInt(simonButtons.size))
        currentIndex = 0
        displayCorrectSequence()
    }

    private fun gameOver() {
        scoreText.text = "Game Over"
        acceptPresses = false
    }

    private fun handleButtonPress(i: Int, b: SimonButton) {
        if (!acceptPresses) { return }
        b.turnOnThenOff()
        if (i != correctSequence[currentIndex]) {
            gameOver()
        } else {
            ++currentIndex
            if (currentIndex == correctSequence.size) {
                nextRound()
            }
        }
    }

    private fun displayCorrectSequence() {
        Handler().postDelayed({displayImpl(0, LightCommand.TURN_ON)}, DISPLAY_BEGIN_DELAY_MILLIS)
    }

    private fun displayImpl(i: Int, cmd: LightCommand) {
        if (i >= correctSequence.size) {
            acceptPresses = true
            return
        }
        val simonButton = simonButtons[correctSequence[i]]
        if (cmd == LightCommand.TURN_OFF) {
            simonButton.turnOff()
            Handler().postDelayed({displayImpl(i + 1, LightCommand.TURN_ON)}, LIGHT_ON_TIME_MILLIS)
        } else {
            simonButton.turnOn()
            Handler().postDelayed({displayImpl(i, LightCommand.TURN_OFF)}, TIME_BETWEEN_LIGHTS_MILLIS)
        }
    }
}
