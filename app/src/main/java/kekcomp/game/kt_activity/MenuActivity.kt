package kekcomp.game.kt_activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import kekcomp.game.R
import kekcomp.game.activity.HighScoresActivity
import kekcomp.game.help.Constants
import kekcomp.game.utils.AnimationUtils
import kekcomp.game.utils.KtListeners.hoverSmall

class MenuActivity : Activity(), IInit {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var animationUtils: AnimationUtils

    private var linearLayout: LinearLayout? = null

    private var newGameButton: Button? = null
    private var settingsButton: Button? = null
    private var statButton: Button? = null
    private var highScoreButton: Button? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)
        init()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        init()
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    override fun onRestart() {
        super.onRestart()
        init()
    }

    override fun init(){
        println(getSharedPreferences(Constants.GAME_IS_RUNNING, Context.MODE_PRIVATE).getBoolean(Constants.GAME_IS_RUNNING, false))
        if(getSharedPreferences(Constants.GAME_IS_RUNNING, Context.MODE_PRIVATE).getBoolean(Constants.GAME_IS_RUNNING, false)){
            val intent = Intent(this@MenuActivity, GameActivity::class.java)
            startActivity(intent)
        }
        sharedPreferences = getSharedPreferences(Constants.SOLVED_ANIME, Context.MODE_PRIVATE)
        linearLayout = findViewById(R.id.menuLayout)
        newGameButton = findViewById(R.id.newGameButton)
        settingsButton = findViewById(R.id.settingsButton)
        highScoreButton = findViewById(R.id.highScoresButton)
        statButton = findViewById(R.id.statisticsButton)
        highScoreButton?.setOnTouchListener(hoverSmall)
        settingsButton?.setOnTouchListener(hoverSmall)
        newGameButton?.setOnTouchListener(hoverSmall)
        statButton?.setOnTouchListener(hoverSmall)
        animationUtils = AnimationUtils(linearLayout)
        animationUtils.fadeInAllLayoutChildren(linearLayout)
    }

    fun newGame(v: View) {
        val intent = Intent(this@MenuActivity, GameActivity::class.java)
        startActivity(intent)
    }

    fun highScore(v: View) {
        val intent = Intent(this@MenuActivity, HighScoresActivity::class.java)
        startActivity(intent)
    }

    fun settings(v: View){
        val intent = Intent(this@MenuActivity, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun statistics(v: View){
        val intent = Intent(this@MenuActivity, StatisticActivity::class.java)
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