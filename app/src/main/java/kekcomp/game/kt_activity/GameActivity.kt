package kekcomp.game.kt_activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.*
import kekcomp.game.R
import kekcomp.game.help.GameLogic
import kekcomp.game.utils.Listeners
import kekcomp.game.view.ImageViewGroup
import java.util.ArrayList
import java.util.prefs.Preferences

/**
 * Created by svyatoslav on 26.01.18.
 */

class GameActivity : Activity() {

    private var buttons = arrayOfNulls<Button>(4)
    private var image: ImageView? = null
    private lateinit var imageViewGroup: ImageViewGroup
    private lateinit var scoreView: TextView
    private lateinit var relativeLayout: RelativeLayout
    private lateinit var animationUtils: kekcomp.game.utils.AnimationUtils
    val NUMBER_OF_ANIME = 4
    val NUMBER_OF_ANIME_IN_BASE = 115
    private var score = 0
    private var seed = -1
    private lateinit var names: Array<String>
    private lateinit var winAnime : String
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var right: View.OnClickListener
    private lateinit var wrong:View.OnClickListener

    private var userName: String = ""
        set(value) {
            println("NEW USERNAME $value")
            field = value
        }

    private lateinit var gameLogic : GameLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)
        gameLogic = GameLogic(this)
        initViews()
        initListeners()
        score = getPreferences(Context.MODE_PRIVATE).getInt("score", 0)
        if(savedInstanceState == null){
            createNewGame()
        }
        scoreView.text = score.toString()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        println("onSave()")
        getPreferences(Context.MODE_PRIVATE).edit().putInt("score", score).apply()
        val images = imageViewGroup.images
        for (i in buttons.indices) {
            outState?.putString("button" + i, buttons[i]!!.text as String)
            outState?.putBoolean("clickable" + i, buttons[i]!!.isClickable)
        }
        for (i in images.indices) {
            outState?.putInt("image" + i, images[i])
        }
        outState?.putInt("seed", seed)
        outState?.putInt("current_image", imageViewGroup.index)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        score = getPreferences(Context.MODE_PRIVATE).getInt("score", 0)
        seed = savedInstanceState!!.getInt("seed")
        gameLogic = GameLogic(this)
        gameLogic.seed = seed
        winAnime = gameLogic.winAnime
        for (i in buttons.indices) {
            buttons[i]!!.text = savedInstanceState.getString("button" + i)
            var text = buttons[i]!!.text as String
            text = text.toLowerCase()
            buttons[i]!!.setOnTouchListener(Listeners.hoverBig)
            buttons[i]!!.isClickable = savedInstanceState.getBoolean("clickable" + i)
            val clickable = buttons[i]!!.isClickable
            if ((text == winAnime) and clickable) {
                buttons[i]!!.setOnClickListener(right)
            } else {
                buttons[i]!!.setOnClickListener(wrong)
            }
            if (!clickable) {
                buttons[i]!!.setBackgroundResource(R.drawable.hover)
            }
            buttons[i]!!.isClickable = clickable
        }
        val img = IntArray(4)
        repeat(4, {i -> img[i] = savedInstanceState.getInt("image" + i)})

        val index = savedInstanceState.getInt("current_image")
        imageViewGroup = ImageViewGroup(img, index)
        image!!.setImageResource(imageViewGroup.imageByIndex)
        setSwipe()
    }

    public override fun onDestroy() {
        super.onDestroy()
        getPreferences(Context.MODE_PRIVATE).edit().putInt("score", score).apply()
    }

    override fun onKeyDown(keycode: Int, e: KeyEvent): Boolean {
        when (keycode) {
            KeyEvent.KEYCODE_BACK -> {
                animationUtils.fadeOutAllLayoutChildren(relativeLayout)
                Handler().postDelayed({ finish() }, animationUtils.fadeInDuration - 150)
                return true
            }
        }
        return super.onKeyDown(keycode, e)
    }

    private fun createNewGame() {
        seed = gameLogic.seed
        names = gameLogic.getAnimeNames()
        winAnime = gameLogic.winAnime
        imageViewGroup = generateImageViewGroup()
        image?.setImageResource(imageViewGroup.imageByIndex)
        setSwipe()
        for(button in buttons){
            button?.setOnTouchListener(Listeners.hoverBig)
        }
        setTextAndListenersOnButtons(names)
    }

    private fun setTextAndListenersOnButtons(names : Array<String>) = repeat(names.size, {i -> buttons[i]!!.text = names[i]; if(winAnime == names[i]) buttons[i]!!.setOnClickListener(right) else buttons[i]!!.setOnClickListener(wrong)})

    private fun generateImageViewGroup(): ImageViewGroup {
        val arr = gameLogic.getArrayOfCurrentAnime()
        return ImageViewGroup(IntArray(4, {i -> resources.getIdentifier(arr[i], "drawable", packageName)}), 0)
    }

    private fun initViews() {
        relativeLayout = findViewById(R.id.relativeLayout) as RelativeLayout
        animationUtils = kekcomp.game.utils.AnimationUtils(relativeLayout)
        scoreView = findViewById(R.id.scoreView) as TextView
        image = findViewById(R.id.image) as ImageView
        buttons[0] = findViewById(R.id.rightButton) as Button
        buttons[1] = findViewById(R.id.wrongAnswer1) as Button
        buttons[2] = findViewById(R.id.wrongAnswer2) as Button
        buttons[3] = findViewById(R.id.wrongAnswer3) as Button
        animationUtils.fadeInAllLayoutChildren(relativeLayout)
    }

    private fun initListeners() {
        right = View.OnClickListener {
            for(button in buttons) button!!.isClickable = false
            score += 10
            scoreView.text = score.toString()
            commitAnime(winAnime)
            getSharedPreferences("solved_anime", Context.MODE_PRIVATE).edit().putInt("right", getSharedPreferences("solved_anime", Context.MODE_PRIVATE).getInt("right", 0) + 1).apply()
            it.setBackgroundResource(R.drawable.win)
            mediaPlayer = MediaPlayer.create(this@GameActivity, R.raw.right)
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.start()
            val intent = Intent(this@GameActivity, GameActivity::class.java)
            startActivity(intent)
            finish()
            //add animation
        }
        wrong = View.OnClickListener {
            it.isClickable = false
            it.setBackgroundResource(R.drawable.lose)
            score -= 10
            scoreView.text = score.toString()
            getSharedPreferences("solved_anime", Context.MODE_PRIVATE).edit().putInt("wrong", getPreferences(Context.MODE_PRIVATE).getInt("wrong", 0) + 1).apply()
            mediaPlayer = MediaPlayer.create(this@GameActivity, R.raw.wrong)
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.start()
            //add animation
        }
    }

    public override fun onStop() {
        super.onStop()
        animationUtils.fadeOutAllLayoutChildren(relativeLayout)
    }

    private fun commitAnime(name: String) {
        val editor = getPreferences(Context.MODE_PRIVATE).edit()
        editor.putBoolean(name + "_boolean", true)
        editor.putInt(name + "_int", seed)
        editor.putInt("score", score)
        editor.apply()
    }

    fun newGame(): String {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        val builder = AlertDialog.Builder(this)
        builder.setView(input)
        builder.setMessage(R.string.win)
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    userName = input.text.toString()
                    println(input.text.toString())
                    if (needToUpdateHighScore()) {
                        commitHighScore(userName)
                    }
                    resetGame()
                    finish()
                }
        val alert = builder.create()
        alert.show()
        return userName
    }

    private fun needToUpdateHighScore() : Boolean {
        val sharedPreferences = getSharedPreferences("high_score", Context.MODE_PRIVATE)
        val map = sharedPreferences.all
        val scores = ArrayList<Int>()
        for (key in map.keys) {
            val value = map[key]
            if (value is Int) {
                scores.add(value)
            }
        }
        val scoreArray = scores.toTypedArray()
        if (map.size < 10) {
            return true
        } else {
            for (i in scoreArray) {
                if (score > i) {
                    return true
                }
            }
        }
        return false
    }

    private fun commitHighScore(name: String) {
        getSharedPreferences("high_score", Context.MODE_PRIVATE).edit().putInt(name, score).apply()
        score = 0
    }

    private fun resetGame() {
        getPreferences(Context.MODE_PRIVATE).edit().clear().apply()
        getPreferences(Context.MODE_PRIVATE).edit().putInt("score", 0).apply()
    }

    private fun setSwipe() {
        val delta = 10
        var x1 = 0f
        var x2: Float
        image?.setOnTouchListener({ view: View, motionEvent: MotionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_DOWN){
                x1 = motionEvent.x
                return@setOnTouchListener true
            } else if(motionEvent.action == MotionEvent.ACTION_UP){
                x2 = motionEvent.x
                when {
                    x1 - x2 > delta -> {
                        rightSwipe()
                        return@setOnTouchListener true
                    }
                    x2 - x1 > delta -> {
                        leftSwipe()
                        return@setOnTouchListener true
                    }
                    else -> return@setOnTouchListener false
                }
            }
            return@setOnTouchListener false
        })
    }

    private fun leftSwipe() {
        val img: Int = imageViewGroup.nextImage
        val anim = animationUtils.changeImageAnimation(image, img)
        image?.animation = anim
        image?.setImageResource(img)
    }

    private fun rightSwipe() {
        val img: Int = imageViewGroup.previousImage
        val anim = animationUtils.changeImageAnimation(image, img)
        image?.animation = anim
        image?.setImageResource(img)
    }
}