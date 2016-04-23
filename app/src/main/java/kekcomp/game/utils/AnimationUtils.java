package kekcomp.game.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import kekcomp.game.R;

/**
 * Created by svyatoslav on 4/23/16.
 */
public class AnimationUtils {

    private Context context;
    private Animation fadeIn;
    private Animation fadeOut;

    public AnimationUtils(View v){
        context = v.getContext();
        fadeIn = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.fade_in);
        fadeOut = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.fade_out);
    }

    public void setFadeInDuration(int mills){
        fadeIn.setDuration(mills);
    }

    public long getFadeInDuration(){
        return fadeIn.getDuration();
    }

    public void setFadeOutDuration(int mills){
        fadeOut.setDuration(mills);
    }

    public void fadeInAllLayoutChildren(RelativeLayout relativeLayout){
        int numberOfViews = relativeLayout.getChildCount();
        for(int i = 0; i < numberOfViews; i++){
            View v = relativeLayout.getChildAt(i);
            fadeIn.setDuration(1000);
            v.startAnimation(fadeIn);
        }
    }

    public void fadeOutAllLayoutChildren(RelativeLayout relativeLayout){
        int numberOfViews = relativeLayout.getChildCount();
        for(int i = 0; i < numberOfViews; i++){
            View v = relativeLayout.getChildAt(i);
            fadeOut.setDuration(1000);
            v.startAnimation(fadeOut);
        }
    }

    public void fadeInAllLayoutChildren(LinearLayout relativeLayout){
        int numberOfViews = relativeLayout.getChildCount();
        for(int i = 0; i < numberOfViews; i++){
            View v = relativeLayout.getChildAt(i);
            fadeIn.setDuration(1000);
            v.startAnimation(fadeIn);
        }
    }

    public void fadeOutAllLayoutChildren(LinearLayout relativeLayout){
        int numberOfViews = relativeLayout.getChildCount();
        for(int i = 0; i < numberOfViews; i++){
            View v = relativeLayout.getChildAt(i);
            fadeOut.setDuration(1000);
            v.startAnimation(fadeOut);
        }
    }

    public Animation changeImageAnimation(ImageView view, int newImg){
        final int img = newImg;
        final ImageView v = view;
        Animation anim = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.fade_in);
        anim.setDuration(300);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation anim2 = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.fade_out);
                anim2.setDuration(300);
                v.setImageResource(img);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return anim;
    }
}
