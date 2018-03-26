package com.haining.simon

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.res.ResourcesCompat
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

enum class LightCommand {
    TURN_ON,
    TURN_OFF,
}

data class SimonButton(val button: Button, val onColor: Int, val offColor: Int)

class MainActivity : AppCompatActivity() {
    val correctSequence = ArrayList<Int>()
    var simonButtons: List<SimonButton> = ArrayList<SimonButton>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        simonButtons = listOf(
                SimonButton(button0, lookupColor(R.color.redOn), lookupColor(R.color.redOff)),
                SimonButton(button1, lookupColor(R.color.blueOn), lookupColor(R.color.blueOff)),
                SimonButton(button2, lookupColor(R.color.greenOn), lookupColor(R.color.greenOff)),
                SimonButton(button3, lookupColor(R.color.yellowOn), lookupColor(R.color.yellowOff))
        )


        for (b in simonButtons) {
            b.button.setBackgroundColor(b.offColor)
        }
        correctSequence.addAll(listOf(0, 1, 2, 3, 3, 2, 1, 0))
        display()
    }

    private fun lookupColor(colorId: Int) = ResourcesCompat.getColor(getResources(), colorId, null)

    private fun display() {
        display_impl(0, LightCommand.TURN_ON)
    }

    private fun display_impl(i: Int, cmd: LightCommand) {
        if (i >= correctSequence.size) {
            return
        }
        val buttonIndex = correctSequence[i]
        assert(buttonIndex < simonButtons.size)
        val simonButton = simonButtons[buttonIndex]
        if (cmd == LightCommand.TURN_OFF) {
            simonButton.button.setBackgroundColor(simonButton.offColor)
            Handler().postDelayed({display_impl(i + 1, LightCommand.TURN_ON)}, 300)
        } else {
            simonButton.button.setBackgroundColor(simonButton.onColor)
            Handler().postDelayed({display_impl(i, LightCommand.TURN_OFF)}, 500)
        }
    }

}
