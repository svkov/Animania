package kekcomp.game.kt_activity

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import kekcomp.game.R
import kekcomp.game.help.Constants
import kekcomp.game.utils.AnimationUtils
import kekcomp.game.utils.KtListeners

class StatisticActivity : Activity(), IInit {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var animationUtils: AnimationUtils

    private var backButton: Button? = null
    private var relativeLayout : RelativeLayout? = null
    private var acceptedTextView: TextView? = null
    private var wrongTextView: TextView? = null

    private lateinit var acceptedString: String
    private lateinit var wrongString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistics_activity)
        init()
    }

    override fun init() {
        sharedPreferences = getSharedPreferences(Constants.SOLVED_ANIME, Context.MODE_PRIVATE)
        acceptedString = getString(R.string.accepted)
        wrongString = getString(R.string.wrong)
        relativeLayout = findViewById(R.id.statisticsLayout)
        acceptedTextView = findViewById(R.id.okText)
        wrongTextView = findViewById(R.id.wrongText)
        backButton = findViewById(R.id.backButton)
        animationUtils = AnimationUtils(relativeLayout)

        acceptedTextView?.text = generateStatString(acceptedTextView)
        wrongTextView?.text = generateStatString(wrongTextView)
        backButton?.setOnTouchListener(KtListeners.hoverSmall)
        backButton?.setOnClickListener({
            animationUtils.fadeOutAllLayoutChildren(relativeLayout)
            Handler().postDelayed({ finish() }, animationUtils.fadeInDuration - 150)
        })

        animationUtils.fadeInAllLayoutChildren(relativeLayout)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        init()
    }

    private fun generateStatString(tv: TextView?): String {
        var res = tv?.text as String
        when (res) {
            acceptedString -> res = Constants.RIGHT
            wrongString -> res = Constants.WRONG
            else -> res = ""
        }
        return "${tv.text} ${sharedPreferences.getInt(res, 0)}"
    }

}