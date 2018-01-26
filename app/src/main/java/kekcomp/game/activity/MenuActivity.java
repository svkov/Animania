package kekcomp.game.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kekcomp.game.R;
import kekcomp.game.utils.AnimationUtils;
import kekcomp.game.utils.Listeners;
import kekcomp.game.kt_activity.GameActivity;

/**
 * Created by svyatoslav on 09.01.2016.
 */
public class MenuActivity extends Activity {

    AnimationUtils animationUtils;

    private LinearLayout linearLayout;

    private Button newGameButton;
    private Button settingsButton;
    private Button statButton;
    private Button highScoreButton;

    private Button backButton;
    private Button resetButton;

    String accepted;
    String wrong;

    @Override
    protected void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        accepted = getString(R.string.accepted);
        wrong = getString(R.string.wrong);
        linearLayout = (LinearLayout) findViewById(R.id.menuLayout);
        newGameButton = (Button) findViewById(R.id.newGameButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);
        newGameButton.setOnTouchListener(Listeners.hoverSmall);
        highScoreButton = (Button) findViewById(R.id.highScoresButton);
        highScoreButton.setOnTouchListener(Listeners.hoverSmall);
        settingsButton.setOnTouchListener(Listeners.hoverSmall);
        statButton = (Button) findViewById(R.id.statisticsButton);
        animationUtils = new AnimationUtils(linearLayout);
        animationUtils.fadeInAllLayoutChildren(linearLayout);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.settings_activity);
                final RelativeLayout resetLayout = (RelativeLayout) findViewById(R.id.resetLayout);
                backButton = (Button) findViewById(R.id.backButton);
                resetButton = (Button) findViewById(R.id.reset);
                animationUtils.fadeInAllLayoutChildren(resetLayout);
                resetButton.setOnTouchListener(Listeners.hoverSmall);
                backButton.setOnTouchListener(Listeners.hoverSmall);
                resetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeDialog();
                    }
                });
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        animationUtils.fadeOutAllLayoutChildren(resetLayout);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onCreate(savedInstanceState);
                            }
                        }, animationUtils.getFadeInDuration()-150);
                    }
                });
            }
        });
        statButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.statistics_activity);
                final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.statisticsLayout);
                TextView accepted = (TextView) findViewById(R.id.okText);
                TextView wrong = (TextView) findViewById(R.id.wrongText);
                animationUtils.fadeInAllLayoutChildren(relativeLayout);
                accepted.setText(generateStatString(accepted));
                wrong.setText(generateStatString(wrong));
                backButton = (Button) findViewById(R.id.backStatButton);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        animationUtils.fadeOutAllLayoutChildren(relativeLayout);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onCreate(savedInstanceState);
                            }
                        }, animationUtils.getFadeInDuration()-150);
                    }
                });
                backButton.setOnTouchListener(Listeners.hoverSmall);
            }
        });
        statButton.setOnTouchListener(Listeners.hoverSmall);
    }

    private String generateStatString(TextView tv){
        String res = (String) tv.getText();
        if(res.equals(accepted)){
            res = "right";
        } else if(res.equals(wrong)){
            res = "wrong";
        } else {
            res = "";
        }
        return tv.getText()+" "+getSharedPreferences("solved_anime", MODE_PRIVATE).getInt(res, 0);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_BACK:
                animationUtils.fadeOutAllLayoutChildren(linearLayout);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, animationUtils.getFadeInDuration()-200);
                return true;
        }

        return super.onKeyDown(keycode, e);
    }

    @Override
    public void onStop(){
        super.onStop();

    }

    public void newGame(View view){
        Intent intent = new Intent(MenuActivity.this, GameActivity.class);
        startActivity(intent);
    }

    public void highScore(View v){
        Intent intent = new Intent(MenuActivity.this, HighScoresActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        System.out.println("Menu destroyed");
    }

    public void reset(){
        SharedPreferences sharedPreferences = getSharedPreferences("solved_anime", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        System.out.println("Score: " + getSharedPreferences("solved_anime", MODE_PRIVATE).getInt("score", 0));
    }

    private void makeDialog() {
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        reset();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to reset your results?")
                .setCancelable(false)
                .setNegativeButton("Nope", clickListener)
                .setPositiveButton("Yep", clickListener).show();
    }
}
