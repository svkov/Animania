package kekcomp.game;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * Created by svyatoslav on 1/20/16.
 */
public class AlphaView extends TextView {

    private static final int cost = 5;

    public AlphaView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        setText("-"+cost+" scores!");
        setTextSize(20);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.piu_piu);
        animation.setDuration(2000);
        animation.setFillAfter(true);
        setAnimation(animation);
        startAnimation(animation);
    }
}
