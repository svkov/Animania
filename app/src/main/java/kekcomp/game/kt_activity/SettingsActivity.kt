package kekcomp.game.kt_activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.RelativeLayout
import kekcomp.game.R
import kekcomp.game.help.Constants
import kekcomp.game.utils.AnimationUtils
import kekcomp.game.utils.KtListeners

class SettingsActivity : Activity(), IInit {

    private lateinit var animationUtils: AnimationUtils

    private var resetLayout : RelativeLayout? = null

    private var backButton: Button? = null
    private var resetButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        init()
    }

    override fun init() {
        resetLayout = findViewById(R.id.resetLayout)
        backButton = findViewById(R.id.backButton)
        resetButton = findViewById(R.id.reset)
        animationUtils = AnimationUtils(resetLayout)

        animationUtils.fadeInAllLayoutChildren(resetLayout)
        resetButton?.setOnTouchListener(KtListeners.hoverSmall)
        backButton?.setOnTouchListener(KtListeners.hoverSmall)
        resetButton?.setOnClickListener({ makeDialog() })
        backButton?.setOnClickListener({
            animationUtils.fadeOutAllLayoutChildren(resetLayout)
            Handler().postDelayed({ finish() }, animationUtils.fadeInDuration - 150)
        })
    }

    private fun makeDialog() {
        val clickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> reset()
                DialogInterface.BUTTON_NEGATIVE -> finish()
                else -> {
                }
            }
        }
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you really want to reset your current results?")
                .setCancelable(false)
                .setNegativeButton("Nope", clickListener)
                .setPositiveButton("Yep", clickListener).show()
    }

    private fun reset() {
        getSharedPreferences(Constants.ANIME, Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences(Constants.SCORE, Context.MODE_PRIVATE).edit().clear().apply()
    }


}