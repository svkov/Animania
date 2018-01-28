package kekcomp.game.utils

import android.view.MotionEvent
import android.view.View
import kekcomp.game.R

object KtListeners {
    val hoverBig: View.OnTouchListener = View.OnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.setBackgroundResource(R.drawable.hover)
            }
            MotionEvent.ACTION_MOVE -> {
                v.setBackgroundResource(R.drawable.hover)
            }
            MotionEvent.ACTION_UP -> {
                v.setBackgroundResource(R.drawable.big_button)
            }
            MotionEvent.ACTION_CANCEL -> {
                v.setBackgroundResource(R.drawable.big_button)
            }
        }
        false
    }
    val hoverSmall: View.OnTouchListener = View.OnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.setBackgroundResource(R.drawable.hover_small)
            }
            MotionEvent.ACTION_MOVE -> {
                v.setBackgroundResource(R.drawable.hover_small)
            }
            MotionEvent.ACTION_UP -> {
                v.setBackgroundResource(R.drawable.button)
            }
            MotionEvent.ACTION_CANCEL -> {
                v.setBackgroundResource(R.drawable.button)
            }
        }

        false
    }
}