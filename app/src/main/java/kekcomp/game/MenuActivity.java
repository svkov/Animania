package kekcomp.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by svyatoslav on 09.01.2016.
 */
public class MenuActivity extends Activity {

    private Button newGameButton;
    private Button settingsButton;
    private Button statButton;
    private Button highScoreButton;

    private Button backButton;
    private Button resetButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        newGameButton = (Button) findViewById(R.id.newGameButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);
        newGameButton.setOnTouchListener(Listeners.hoverSmall);
        highScoreButton = (Button) findViewById(R.id.highScoresButton);
        settingsButton.setOnTouchListener(Listeners.hoverSmall);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.settings_activity);
                backButton = (Button) findViewById(R.id.backButton);
                resetButton = (Button) findViewById(R.id.reset);
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
                        onCreate(savedInstanceState);
                    }
                });
            }
        });
        statButton = (Button) findViewById(R.id.statisticsButton);
        statButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.statistics_activity);
                TextView accepted = (TextView) findViewById(R.id.okText);
                TextView wrong = (TextView) findViewById(R.id.wrongText);
                accepted.setText(generateStatString(accepted));
                wrong.setText(generateStatString(wrong));
                backButton = (Button) findViewById(R.id.backStatButton);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCreate(savedInstanceState);
                    }
                });
                backButton.setOnTouchListener(Listeners.hoverSmall);
            }
        });
        statButton.setOnTouchListener(Listeners.hoverSmall);
    }

    private String generateStatString(TextView tv){
        String res = (String) tv.getText();
        switch (res){
            case "Accepted:":
                res = "right";
                break;
            case "Wrong:":
                res = "wrong";
                break;
            default:
                res = null;
        }
        return tv.getText()+" "+getSharedPreferences("solved_anime", MODE_PRIVATE).getInt(res, 0);
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
        sharedPreferences.edit().clear().commit();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
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
