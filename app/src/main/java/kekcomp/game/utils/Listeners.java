package kekcomp.game.utils;

import android.view.MotionEvent;
import android.view.View;

import kekcomp.game.R;

/**
 * Created by svyatoslav on 1/15/16.
 */
public class Listeners {
    public static final View.OnTouchListener hoverBig = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    System.out.println("Pressed");
                    v.setBackgroundResource(R.drawable.hover);
                    break;
                case MotionEvent.ACTION_MOVE:
                    v.setBackgroundResource(R.drawable.hover);
                    break;
                case MotionEvent.ACTION_UP:
                    v.setBackgroundResource(R.drawable.big_button);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setBackgroundResource(R.drawable.big_button);
                    break;
            }
            return false;
        }
    };
    public static final View.OnTouchListener hoverSmall = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    System.out.println("Pressed");
                    v.setBackgroundResource(R.drawable.hover_small);
                    break;
                case MotionEvent.ACTION_MOVE:
                    v.setBackgroundResource(R.drawable.hover_small);
                    break;
                case MotionEvent.ACTION_UP:
                    v.setBackgroundResource(R.drawable.button);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setBackgroundResource(R.drawable.button);
                    break;
            }
            return false;
        }
    };
}
