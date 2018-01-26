package kekcomp.game.kt_activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import kekcomp.game.R
import kekcomp.game.help.GameLogic
import kekcomp.game.utils.Listeners
import kekcomp.game.view.ImageViewGroup

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
    val NUMBER_OF_ANIME = 115 //115
    private var score = 0
    private var seed = -1
    private lateinit var names: Array<String>
    private lateinit var winAnime : String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var right: View.OnClickListener;
    internal lateinit var wrong:View.OnClickListener
    private lateinit var userName: String

    private lateinit var gameLogic : GameLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)
        gameLogic = GameLogic(this)
        initViews()
        initListeners()
        score = getSharedPreferences("solved_anime", Context.MODE_PRIVATE).getInt("score", 0)
        if(savedInstanceState == null){
            createNewGame()
        }
        scoreView.text = score.toString()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putInt("score", score)
        val images = imageViewGroup.images
        for (i in buttons.indices) {
            outState.putString("button" + i, buttons[i]!!.text as String)
            outState.putBoolean("clickable" + i, buttons[i]!!.isClickable)
        }
        for (i in images.indices) {
            outState.putInt("image" + i, images[i])
        }
        outState.putInt("seed", seed)
        outState.putInt("current_image", imageViewGroup.index)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        score = savedInstanceState!!.getInt("score")
        seed = savedInstanceState.getInt("seed")
        gameLogic = GameLogic(this)
        gameLogic.seed = seed
        for (i in buttons.indices) {
            buttons[i]!!.text = savedInstanceState.getString("button" + i)
            var text = buttons[i]!!.text as String
            text = text.toLowerCase()
            buttons[i]!!.setOnTouchListener(Listeners.hoverBig)
            buttons[i]!!.isClickable = savedInstanceState.getBoolean("clickable" + i)
            val clickable = buttons[i]!!.isClickable
            println("button[$i] is clickable($clickable)")
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

    private fun setTextAndListenersOnButtons(names : Array<String>) {
        repeat(names.size, {i -> buttons[i]!!.text = names[i]; if(winAnime == names[i]) buttons[i]!!.setOnClickListener(right) else buttons[i]!!.setOnClickListener(wrong)})
    }

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
            it.setBackgroundResource(R.drawable.win)

            val intent = Intent(this@GameActivity, GameActivity::class.java)
            startActivity(intent)
            finish()
            //add animation and sound
        }
        wrong = View.OnClickListener {
            it.isClickable = false
            it.setBackgroundResource(R.drawable.lose)
            score -= 10
            scoreView.text = score.toString()
            getSharedPreferences("solved_anime", Context.MODE_PRIVATE).edit().putInt("wrong", getSharedPreferences("solved_anime", Context.MODE_PRIVATE).getInt("wrong", 0) + 1).apply()
            //add animation and sound
        }
    }

    private fun commitAnime(name: String) {
        sharedPreferences = getSharedPreferences("solved_anime", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(name + "_boolean", true)
        editor.putInt(name + "_int", seed)
        editor.putInt("right", sharedPreferences.getInt("right", 0) + 1)
        editor.putInt("score", score)
        editor.apply()
    }

    fun newGame() {

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
                if (x1 - x2 > delta) {
                    rightSwipe()
                    return@setOnTouchListener true
                } else if (x2 - x1 > delta) {
                    leftSwipe()
                    return@setOnTouchListener true
                } else {
                    return@setOnTouchListener false
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