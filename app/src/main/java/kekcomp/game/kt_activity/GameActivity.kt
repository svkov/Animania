package kekcomp.game.kt_activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.*
import kekcomp.game.R
import kekcomp.game.help.Constants
import kekcomp.game.help.GameLogic
import kekcomp.game.utils.KtListeners.hoverBig
import kekcomp.game.view.ImageViewGroup
import java.util.*

class GameActivity : Activity(), IInit {

    private var buttons = arrayOfNulls<Button>(4)
    private var image: ImageView? = null
    private lateinit var imageViewGroup: ImageViewGroup
    private lateinit var scoreView: TextView
    private lateinit var relativeLayout: RelativeLayout
    private lateinit var animationUtils: kekcomp.game.utils.AnimationUtils
    fun getNumberOfAnime() = 113 // change for test and debug
    fun getNumberOfAnimeInBase() = 113
    private var score = 0
    private var seed = -1
    private var isCommited = false
    private var isStopped = false
    private var isCreated = false
    private lateinit var names: Array<String>
    private lateinit var winAnime : String
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var right: View.OnClickListener
    private lateinit var wrong:View.OnClickListener
    private lateinit var rightAndWrongSharedPreferences : SharedPreferences
    private lateinit var currentGameSharedPreferences: SharedPreferences
    private lateinit var scoreSharedPreferences : SharedPreferences
    lateinit var animeSharedPreferences: SharedPreferences

    private var userName: String = ""

    private lateinit var gameLogic : GameLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)
        init()
    }

    override fun onRestart() {
        super.onRestart()
        if(isStopped){
            isStopped = false
            recreate()
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if(isStopped) {
            isStopped = false
            recreate()
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        isStopped = true
        animationUtils.fadeOutAllLayoutChildren(relativeLayout)
    }

    override fun onPause() {
        super.onPause()
        saveScore()
        if(!isCommited) saveData()
    }

    override fun onDestroy() {
        super.onDestroy()
        isStopped = false
    }

    override fun init() {
        getSharedPreferences(Constants.GAME_IS_RUNNING, Context.MODE_PRIVATE).edit().putBoolean(Constants.GAME_IS_RUNNING, true).apply()
        currentGameSharedPreferences = getSharedPreferences(Constants.CURRENT, Context.MODE_PRIVATE)
        animeSharedPreferences = getSharedPreferences(Constants.ANIME, Context.MODE_PRIVATE)
        scoreSharedPreferences = getSharedPreferences(Constants.SCORE, Context.MODE_PRIVATE)
        gameLogic = GameLogic(this)
        score = scoreSharedPreferences.getInt(Constants.SCORE, 0)
        initViews()
        initListeners()
        scoreView.text = score.toString()
        if(currentGameSharedPreferences.getInt(Constants.SEED, -1) != -1){
            restoreData()
        } else {
            gameLogic.seed = currentGameSharedPreferences.getInt(Constants.SEED, -1)
            createNewGame()
            setSwipe()
        }

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        saveData()
    }

    fun saveScore(){
        scoreSharedPreferences.edit().putInt(Constants.SCORE, score).apply()
    }

    private fun saveData(){
        val outState = currentGameSharedPreferences.edit()
        saveScore()
        val images = imageViewGroup.images
        for (i in buttons.indices) {
            outState.putString("button" + i, buttons[i]!!.text as String)
            outState.putBoolean("clickable" + i, buttons[i]!!.isClickable)
        }
        for (i in images.indices) {
            outState.putInt("image" + i, images[i])
        }
        outState.putInt(Constants.SEED, seed)
        outState.putInt("current_image", imageViewGroup.index)
        outState.apply()
    }

    fun restoreData(){
        score = scoreSharedPreferences.getInt(Constants.SCORE, 0)
        seed = currentGameSharedPreferences.getInt(Constants.SEED, -1)
        gameLogic = GameLogic(this)
        gameLogic.seed = seed
        winAnime = gameLogic.winAnime
        for (i in buttons.indices) {
            buttons[i]!!.text = currentGameSharedPreferences.getString("button" + i, "")
            var text = buttons[i]!!.text as String
            text = text.toLowerCase()
            buttons[i]!!.setOnTouchListener(hoverBig)
            buttons[i]!!.isClickable = currentGameSharedPreferences.getBoolean("clickable" + i, true)
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
        repeat(4, {i -> img[i] = currentGameSharedPreferences.getInt("image" + i, 0)})

        val index = currentGameSharedPreferences.getInt("current_image", 0)
        imageViewGroup = ImageViewGroup(img, index)
        image!!.setImageResource(imageViewGroup.imageByIndex)
        setSwipe()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        restoreData()
    }

    private fun createNewGame() {
        seed = gameLogic.seed
        names = gameLogic.getAnimeNames()
        winAnime = gameLogic.winAnime
        imageViewGroup = generateImageViewGroup()
        image?.setImageResource(imageViewGroup.imageByIndex)
        for(button in buttons){
            button?.setOnTouchListener(hoverBig)
        }
        setTextAndListenersOnButtons(names)
    }

    private fun setTextAndListenersOnButtons(names : Array<String>) = repeat(names.size, {i -> buttons[i]!!.text = names[i]; if(winAnime == names[i]) buttons[i]!!.setOnClickListener(right) else buttons[i]!!.setOnClickListener(wrong)})

    private fun generateImageViewGroup(): ImageViewGroup {
        val arr = gameLogic.getArrayOfCurrentAnime()
        return ImageViewGroup(IntArray(4, {i -> resources.getIdentifier(arr[i], "drawable", packageName)}), 0)
    }

    private fun initViews() {
        relativeLayout = findViewById(R.id.relativeLayout)
        animationUtils = kekcomp.game.utils.AnimationUtils(relativeLayout)
        scoreView = findViewById(R.id.scoreView)
        image = findViewById(R.id.image)
        buttons[0] = findViewById(R.id.rightButton)
        buttons[1] = findViewById(R.id.wrongAnswer1)
        buttons[2] = findViewById(R.id.wrongAnswer2)
        buttons[3] = findViewById(R.id.wrongAnswer3)
        animationUtils.fadeInAllLayoutChildren(relativeLayout)
    }

    private fun initListeners() {
        rightAndWrongSharedPreferences = getSharedPreferences(Constants.SOLVED_ANIME, Context.MODE_PRIVATE)
        right = View.OnClickListener {
            for(button in buttons) button!!.isClickable = false
            score += 10
            scoreView.text = score.toString()
            commitAnime(winAnime)
            rightAndWrongSharedPreferences.edit().putInt(Constants.RIGHT, rightAndWrongSharedPreferences.getInt(Constants.RIGHT, 0) + 1).apply()
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
            rightAndWrongSharedPreferences.edit().putInt(Constants.WRONG, rightAndWrongSharedPreferences.getInt(Constants.WRONG, 0) + 1).apply()
            mediaPlayer = MediaPlayer.create(this@GameActivity, R.raw.wrong)
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.start()
            //add animation
        }
    }

    private fun commitAnime(name: String) {
        isCommited = true
        val editor = animeSharedPreferences.edit()
        editor.putBoolean(name + "_boolean", true)
        editor.putInt(name + "_int", seed)
        editor.apply()
        saveData()
        currentGameSharedPreferences.edit().clear().apply()
    }

    fun newGame(): String {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        val builder = AlertDialog.Builder(this)
        builder.setView(input)
        builder.setMessage(R.string.win)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
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
        val sharedPreferences = getSharedPreferences(Constants.HIGH_SCORE, Context.MODE_PRIVATE)
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
        getSharedPreferences(Constants.HIGH_SCORE, Context.MODE_PRIVATE).edit().putInt(name, score).apply()
        score = 0
    }

    private fun resetGame() {
        getSharedPreferences(Constants.GAME_IS_RUNNING, Context.MODE_PRIVATE).edit().putBoolean(Constants.GAME_IS_RUNNING, false).apply()
        animeSharedPreferences.edit().clear().apply()
        currentGameSharedPreferences.edit().clear().apply()
        scoreSharedPreferences.edit().clear().apply()
    }

    override fun onKeyDown(keycode: Int, e: KeyEvent): Boolean {
        when (keycode) {
            KeyEvent.KEYCODE_BACK -> {
                animationUtils.fadeOutAllLayoutChildren(relativeLayout)
                Handler().postDelayed({ goToMenu() }, animationUtils.fadeInDuration - 150)
                return true
            }
        }
        return super.onKeyDown(keycode, e)
    }

    private fun goToMenu() {
        //val intent = Intent(this@GameActivity, MenuActivity::class.java)
        //startActivity(intent)
        getSharedPreferences(Constants.GAME_IS_RUNNING, Context.MODE_PRIVATE).edit().putBoolean(Constants.GAME_IS_RUNNING, false).apply()
        finish()
    }

    private fun setSwipe() {
        val delta = 10
        var x1 = 0f
        var x2: Float
        image?.setOnTouchListener({ view: View, motionEvent: MotionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_DOWN){
                x1 = motionEvent.x
                view.performClick()
                return@setOnTouchListener true
            } else if(motionEvent.action == MotionEvent.ACTION_UP){
                x2 = motionEvent.x
                when {
                    x1 - x2 > delta -> {
                        rightSwipe()
                        view.performClick()
                        return@setOnTouchListener true
                    }
                    x2 - x1 > delta -> {
                        leftSwipe()
                        view.performClick()
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