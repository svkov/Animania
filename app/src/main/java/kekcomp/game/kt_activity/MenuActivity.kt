package kekcomp.game.kt_activity

import android.app.Activity
import android.app.AlertDialog
import android.app.usage.UsageEvents
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import kekcomp.game.R
import kekcomp.game.activity.HighScoresActivity
import kekcomp.game.utils.AnimationUtils
import kekcomp.game.utils.Listeners

/**
 * Created by svyatoslav on 28.01.18.
 */

class MenuActivity : Activity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var animationUtils: AnimationUtils

    private var linearLayout: LinearLayout? = null

    private var newGameButton: Button? = null
    private var settingsButton: Button? = null
    private var statButton: Button? = null
    private var highScoreButton: Button? = null

    private var backButton: Button? = null
    private var resetButton: Button? = null

    private lateinit var accepted: String
    private lateinit var wrong: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)
        init()
        animationUtils.fadeInAllLayoutChildren(linearLayout)
        settingsButton?.setOnClickListener({v -> onSettingsClick(v, savedInstanceState)})
        statButton?.setOnClickListener({v -> onStatClick(v, savedInstanceState)})

    }

    private fun init(){
        sharedPreferences = getSharedPreferences("solved_anime", Context.MODE_PRIVATE)
        accepted = getString(R.string.accepted)
        wrong = getString(R.string.wrong)
        linearLayout = findViewById(R.id.menuLayout) as LinearLayout
        newGameButton = findViewById(R.id.newGameButton) as Button
        settingsButton = findViewById(R.id.settingsButton) as Button
        newGameButton?.setOnTouchListener(Listeners.hoverSmall)
        highScoreButton = findViewById(R.id.highScoresButton) as Button
        highScoreButton?.setOnTouchListener(Listeners.hoverSmall)
        settingsButton?.setOnTouchListener(Listeners.hoverSmall)
        statButton = findViewById(R.id.statisticsButton) as Button
        statButton?.setOnTouchListener(Listeners.hoverSmall)
        animationUtils = AnimationUtils(linearLayout)
    }

    private fun onStatClick(v: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.statistics_activity)
        val relativeLayout = findViewById(R.id.statisticsLayout) as RelativeLayout
        val accepted = findViewById(R.id.okText) as TextView
        val wrong = findViewById(R.id.wrongText) as TextView
        animationUtils.fadeInAllLayoutChildren(relativeLayout)
        accepted.setText(generateStatString(accepted))
        wrong.setText(generateStatString(wrong))
        backButton = findViewById(R.id.backStatButton) as Button
        backButton?.setOnClickListener(View.OnClickListener {
            animationUtils.fadeOutAllLayoutChildren(relativeLayout)
            Handler().postDelayed({ onCreate(savedInstanceState) }, animationUtils.fadeInDuration - 150)
        })
        backButton?.setOnTouchListener(Listeners.hoverSmall)
    }

    private fun generateStatString(tv: TextView): String {
        var res = tv.text as String
        if (res == accepted) {
            res = "right"
        } else if (res == wrong) {
            res = "wrong"
        } else {
            res = ""
        }
        return "${tv.text} ${sharedPreferences.getInt(res, 0)}"
    }

    private fun onSettingsClick(v: View, savedInstanceState: Bundle?){
        setContentView(R.layout.settings_activity)
        val resetLayout = findViewById(R.id.resetLayout) as RelativeLayout
        backButton = findViewById(R.id.backButton) as Button
        resetButton = findViewById(R.id.reset) as Button
        animationUtils.fadeInAllLayoutChildren(resetLayout)
        resetButton?.setOnTouchListener(Listeners.hoverSmall)
        backButton?.setOnTouchListener(Listeners.hoverSmall)
        resetButton?.setOnClickListener({ makeDialog() })
        backButton?.setOnClickListener({
            animationUtils.fadeOutAllLayoutChildren(resetLayout)
            Handler().postDelayed({ onCreate(savedInstanceState) }, animationUtils.fadeInDuration - 150)
        })
    }

    private fun makeDialog() {
        val clickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> reset()
                DialogInterface.BUTTON_NEGATIVE -> finish()
                else -> {
                }
            }
        }
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you really want to reset your results?")
                .setCancelable(false)
                .setNegativeButton("Nope", clickListener)
                .setPositiveButton("Yep", clickListener).show()
    }

    private fun reset() {
        getSharedPreferences("anime", Context.MODE_PRIVATE).edit().clear().apply()
        sharedPreferences.edit().clear().apply()
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        getSharedPreferences("score", Context.MODE_PRIVATE).edit().putInt("score", 0).apply()
        println("Score: " + sharedPreferences.getInt("score", 0))
    }

    fun newGame(view: View) {
        val intent = Intent(this@MenuActivity, GameActivity::class.java)
        startActivity(intent)
    }

    fun highScore(v: View) {
        val intent = Intent(this@MenuActivity, HighScoresActivity::class.java)
        startActivity(intent)
    }

    override fun onKeyDown(keycode: Int, e: KeyEvent): Boolean {
        when (keycode) {
            KeyEvent.KEYCODE_BACK -> {
                animationUtils.fadeOutAllLayoutChildren(linearLayout)
                Handler().postDelayed({ finish() }, animationUtils.fadeInDuration - 200)
                return true
            }
        }

        return super.onKeyDown(keycode, e)
    }

}