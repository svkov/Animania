package kekcomp.game.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import kekcomp.game.view.ImageViewGroup;
import kekcomp.game.utils.Listeners;
import kekcomp.game.R;
import kekcomp.game.utils.SetUtils;


/***
 * Created by svyatoslav on 10.01.2016.
 *
 * Upd on 20.01.2016:
 * 448 strings of code in this class
 * 41 anime added
 * READY: pseudo-database, random generated anime on screen, 5 classes (GameActivity, Listeners, MenuActivity, ImageViewGroup, SetUtils),
 * score, swipes, data-save (into bundle and into file)
 *
 * Upd on 26.01.2016
 * 461 strings of code in this class
 * 85 anime added
 * READY: format images, bug fix
 *
 * Upd on 08.02.2016
 * 505 strings of code in this class
 * 85 anime added
 * READY: high score
 *
 * Upd on 18.04.2016
 * Ready to continue work
 * First release is coming
 ***/

public class GameActivity extends Activity {

    Button[] buttons = new Button[4];
    ImageView image;
    ImageViewGroup imageViewGroup;
    TextView scoreView;
    RelativeLayout relativeLayout;
    kekcomp.game.utils.AnimationUtils animationUtils;
    final static int NUMBER_OF_ANIME = 85; //85
    static int score = 0;
    int seed = -1;
    String[] names;
    SharedPreferences sharedPreferences;
    MediaPlayer mediaPlayer;
    View.OnClickListener right, wrong;
    String userName;

    public void setUserName(String newName){
        userName = newName;
        System.out.println("newName = "+newName+" userName = "+userName);
    }

    public String getUserName(){
        return userName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        initViews();
        initListeners();
        score = getSharedPreferences("solved_anime", MODE_PRIVATE).getInt("score", 0);
        if(savedInstanceState == null){
            createNewGame();
        }
        scoreView.setText(getScoreString());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("score", score);
        int[] images = imageViewGroup.getImages();
        for(int i = 0; i < buttons.length; i++){
            outState.putString("button"+i, (String)buttons[i].getText());
            outState.putBoolean("clickable"+i, buttons[i].isClickable());
        }
        for(int i = 0; i < images.length; i++){
            outState.putInt("image" + i, images[i]);
        }
        outState.putInt("seed", seed);
        outState.putInt("current_image", imageViewGroup.getIndex());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        score = savedInstanceState.getInt("score");
        seed = savedInstanceState.getInt("seed");
        for(int i = 0; i < buttons.length; i++){
            buttons[i].setText(savedInstanceState.getString("button" + i));
            String text = (String) buttons[i].getText();
            text = text.toLowerCase();
            buttons[i].setOnTouchListener(Listeners.hoverBig);
            buttons[i].setClickable(savedInstanceState.getBoolean("clickable" + i));
            boolean clickable = buttons[i].isClickable();
            System.out.println("button[" + i + "] is clickable(" + clickable + ")");
            if(text.equals(getWinAnime()) & clickable){
                buttons[i].setOnClickListener(right);
            }else{
                buttons[i].setOnClickListener(wrong);
            }
            if(!clickable){
                buttons[i].setBackgroundResource(R.drawable.hover);
            }
            buttons[i].setClickable(clickable);
        }
        int[] img = new int[4];
        for(int i = 0; i < img.length; i++){
            img[i] = savedInstanceState.getInt("image"+i);
        }
        int index = savedInstanceState.getInt("current_image");
        imageViewGroup = new ImageViewGroup(img, index);
        image.setImageResource(imageViewGroup.getImageByIndex());
        setSwipe();
    }

    public void setAlphaOnScore(View v){
        v.setAlpha(1);
    }

    private String getScoreString(){
        return Integer.toString(score);
    }

    private void createNewGame(){
        seed = generateSeed();
        int[] abc = generateABC();
        names = getAnimeNames(abc);
        names = unjubmle(names);
        imageViewGroup = generateImageViewGroup();
        image.setImageResource(imageViewGroup.getImageByIndex());
        setSwipe();
        for(int i = 0; i < buttons.length; i++){
            buttons[i].setOnTouchListener(Listeners.hoverBig);
        }
        setTextAndListenersOnButtons(names, getWinAnime(), right, wrong);
        setValidNames(names);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_BACK:
                animationUtils.fadeOutAllLayoutChildren(relativeLayout);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, animationUtils.getFadeInDuration()-150);
                return true;
        }

        return super.onKeyDown(keycode, e);
    }

    private void initListeners(){
        right = new View.OnClickListener() {
            Animation anim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.fade_out);
            @Override
            public void onClick(final View v) {
                for(int i = 0; i < buttons.length; i++){
                    buttons[i].setClickable(false);
                }
                System.out.println("Clicked on win!");
                score += 10;
                scoreView.setText(getScoreString());
                commitAnime(getWinAnime());
                mediaPlayer = MediaPlayer.create(GameActivity.this, R.raw.right);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.start();
                v.setBackgroundResource(R.drawable.win);
                anim.setDuration(750);
                anim.setFillAfter(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.clearAnimation();
                        Intent intent = new Intent(GameActivity.this, GameActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, anim.getDuration());
                v.startAnimation(anim);
            }
        };

        wrong = new View.OnClickListener() {
            Animation anim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.fade_out);
            @Override
            public void onClick(final View v) {
                v.setClickable(false);
                v.setBackgroundResource(R.drawable.lose);
                score -= 10;
                scoreView.setText(getScoreString());
                getSharedPreferences("solved_anime", MODE_PRIVATE).edit().putInt("wrong", getSharedPreferences("solved_anime", MODE_PRIVATE).getInt("wrong", 0)+1).commit();
                mediaPlayer = MediaPlayer.create(GameActivity.this, R.raw.wrong);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.start();
                anim.setRepeatCount(3);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setBackgroundResource(R.drawable.hover);
                    }
                }, anim.getDuration());
                v.startAnimation(anim);
            }
        };
    }

    private void initViews(){
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        animationUtils = new kekcomp.game.utils.AnimationUtils(relativeLayout);
        scoreView = (TextView) findViewById(R.id.scoreView);
        image = (ImageView) findViewById(R.id.image);
        buttons[0] = (Button) findViewById(R.id.rightButton);
        buttons[1] =  (Button) findViewById(R.id.wrongAnswer1);
        buttons[2] = (Button) findViewById(R.id.wrongAnswer2);
        buttons[3] = (Button) findViewById(R.id.wrongAnswer3);
        animationUtils.fadeInAllLayoutChildren(relativeLayout);
    }

    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
        animationUtils.fadeOutAllLayoutChildren(relativeLayout);
        System.out.println("Activity has been stoped and destroyed");
    }

    @Override
    public void onStart(){
        super.onStart();
        System.out.println("Activity has been started");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        image = null;
        buttons[0] = null;
        buttons[1] = null;
        buttons[2] = null;
        buttons[3] = null;
        sharedPreferences = getSharedPreferences("solved_anime", MODE_PRIVATE);
        int sc = sharedPreferences.getInt("score", 0);
        System.out.println("score"+sc);
        if(sc != 0)
            getSharedPreferences("solved_anime", MODE_PRIVATE).edit().putInt("score", score).commit();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        System.out.println("Activity has been restarted");
    }

    @Override
    public void onResume(){
        super.onResume();
    }
    private void newGame(){
        System.out.println("newGame()");
        makeDialog();
    }

    private void commitHighScore(String name){
        sharedPreferences = getSharedPreferences("high_score", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(name, score);
        editor.apply();
        System.out.println("SCORE: " + sharedPreferences.getInt("score", -1));
    }

    private void resetGame(){
        System.out.println("resetGame()");
        sharedPreferences = getSharedPreferences("solved_anime", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private boolean checkHighScore(){
        sharedPreferences = getSharedPreferences("high_score", MODE_PRIVATE);
        Map<String, ?> map = sharedPreferences.getAll();
        List<Integer> scores = new ArrayList<>();
        for(String key : map.keySet()){
            Object value = map.get(key);
            if(value.getClass().equals(Integer.class)){
                scores.add((int) value);
            }
        }
        Object[] scoreArray = scores.toArray();
        System.out.println("checkHighScore(): map.size() = " + map.size());
        if(map.size() < 10) {
            return true;
        }else{
            for(int i = 0; i < scoreArray.length; i++) {
                if (score > (int) scoreArray[i]) {
                    return true;
                }
            }
        }
        return false;
    }

    private String makeDialog(){

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(input);
        builder.setMessage("You solve all anime!\nYou can play again!\nWrite your name and press OK to continue")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setUserName(input.getText().toString());
                        System.out.println(input.getText().toString());
                        if(checkHighScore()){
                            commitHighScore(getUserName());
                        }
                        resetGame();
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        return getUserName();
    }

    private void commitAnime(String name){
        sharedPreferences = getSharedPreferences("solved_anime", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(name + "_boolean", true);
        editor.putInt(name + "_int", seed);
        editor.putInt("right", sharedPreferences.getInt("right", 0)+1);
        editor.putInt("score", score);
        editor.apply();
    }

    private String getWinAnime(){
        if(seed == -1)
            return null;
        return getAnimeName(seed);
    }

    private String[] unjubmle(String[] arr){
        String[] n = new String[arr.length];
        boolean[] a = new boolean[arr.length];
        Random r = new Random();
        for(int i = 0; i < n.length; i++){
            while(true){
                int number = r.nextInt(arr.length);
                if(!a[number]){
                    a[number] = true;
                    n[i] = arr[number];
                    break;
                }
            }
        }
        return n;
    }

    private void setTextAndListenersOnButtons(String[] names,
                                              String winName,
                                              View.OnClickListener right,
                                              View.OnClickListener wrong){
        for(int i = 0; i < 4; i++){
            buttons[i].setText(names[i]);
            if(winName.equals(names[i])){
                buttons[i].setOnClickListener(right);
            }else{
                buttons[i].setOnClickListener(wrong);
            }
        }
    }

    private int[] generateABC(){
        int[] abc = new int[3];
        Random r = new Random();
        int n = r.nextInt(NUMBER_OF_ANIME - 1);
        while(n == seed){
            n = r.nextInt(NUMBER_OF_ANIME-1);
        }
        int a = n, b, c;
        while(n == a || n == seed){
            n = r.nextInt(NUMBER_OF_ANIME-1);
        }
        b = n;
        while(n == b || n == a || n == seed){
            n = r.nextInt(NUMBER_OF_ANIME-1);
        }
        c = n;
        abc[0] = a;
        abc[1] = b;
        abc[2] = c;
        return abc;
    }

    private int generateSeed(){
        int seed;
        sharedPreferences = getSharedPreferences("solved_anime", MODE_PRIVATE);
        Map<String, ?> animeList = sharedPreferences.getAll();
        List<Integer> ints = new ArrayList<>();
        for(String key : animeList.keySet()){
            Object value = animeList.get(key);
            if(value.getClass() == Integer.class){
                ints.add((int) value);
            }
        }
        Set<Integer> mySet = new HashSet<>(ints);
        System.out.println("Solved set: "+mySet);
        List<Integer> fullList = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_ANIME - 1; i++){
            fullList.add(i);
        }
        Set<Integer> fullSet = new HashSet<>(fullList);
        HashSet<Integer> usableSet =(HashSet) SetUtils.complement(fullSet, mySet);
        if(usableSet.size() == 0){
            System.out.println("call newGame()");
            newGame();
            return 0;
        }
        Random r = new Random();
        int n = r.nextInt(usableSet.size());
        System.out.println(n);
        System.out.println("Usable set "+usableSet);
        int[] array = SetUtils.toInt(usableSet);
        seed = array[n];

        System.out.println("Seed: "+seed);
        return seed;
    }

    private String[] getAnimeNames(int[] abc){
        return new String[]{
                getAnimeName(seed),
                getAnimeName(abc[0]),
                getAnimeName(abc[1]),
                getAnimeName(abc[2]),
        };
    }

    private String getAnimeName(int n){
        String[] strArr = getResources().getStringArray(R.array.anime_names);
        strArr = strArr[n].split(" ");
        String animeName = strArr[2]; //2 because there is a string like "   abc abc abc abc"

        return animeName.substring(0, animeName.length()-1).replace("_", " ");
    }

    private ImageViewGroup generateImageViewGroup(){
        String[] strArr = getStrArr();
        int img1, img2, img3, img4;
        img1 = getResources().getIdentifier(strArr[2], "drawable", this.getPackageName());
        img2 = getResources().getIdentifier(strArr[3], "drawable", this.getPackageName());
        img3 = getResources().getIdentifier(strArr[4], "drawable", this.getPackageName());
        img4 = getResources().getIdentifier(strArr[5], "drawable", this.getPackageName());
        return new ImageViewGroup(img1, img2, img3, img4);
    }

    private String[] getStrArr(){
        String[] strArr = getResources().getStringArray(R.array.anime_names);
        return strArr[seed].split(" ");
    }

    private void setValidNames(String[] in){
        String[] out = new String[in.length];
        for(int i = 0; i < out.length; i++) {
            char[] c = in[i].toCharArray();
            c[0] = Character.toTitleCase(c[0]);
            out[i] = new String(c);
        }
        for(int i = 0; i < buttons.length; i++){
            buttons[i].setText(out[i]);
        }
    }

    private void setSwipe(){
        final int delta = 10;
        image.setOnTouchListener(new View.OnTouchListener() {
            float x1 = 0, y1 = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x2;
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        if(x1 - x2 > delta){
                            rightSwipe();
                            return true;
                        } else if(x2 - x1 > delta){
                            leftSwipe();
                            return true;
                        }
                        break;
                    default:
                        return false;
                }
                return false;
            }
        });
    }

    private void rightSwipe(){
        int img;
        img = imageViewGroup.getNextImage();
        Animation anim = animationUtils.changeImageAnimation(image, img);
        image.setAnimation(anim);
        image.setImageResource(img);
    }

    private void leftSwipe(){
        int img;
        img = imageViewGroup.getPreviousImage();
        Animation anim = animationUtils.changeImageAnimation(image, img);
        image.setAnimation(anim);
        image.setImageResource(img);
    }
}
