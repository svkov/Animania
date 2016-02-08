package kekcomp.game;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by svyatoslav on 1/26/16.
 */
public class HighScoresActivity extends Activity {
    HashMap<String, Integer> hs;
    TableLayout tableLayout;
    final static int rows = 10;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.high_scores);
        tableLayout = (TableLayout) findViewById(R.id.table);
        hs = getNamesAndScores();
        for(int i = 0; i < rows; i++){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            if(hs.size() >= 1){
                int score = findMax();
                String name = findKeyByValue(score);
                hs.remove(name);
                TextView id = new TextView(this);
                id.setText(i + 1 + ". ");
                id.setTextSize(40);
                id.setTextColor(Color.WHITE);
                tableRow.addView(id, 0);
                TextView scoreView = new TextView(this);
                scoreView.setText(score+" ");
                scoreView.setTextSize(40);
                scoreView.setTextColor(Color.WHITE);
                tableRow.addView(scoreView, 1);
                TextView nameView = new TextView(this);
                nameView.setText(name);
                nameView.setTextColor(Color.WHITE);
                nameView.setTextSize(40);
                tableRow.addView(nameView, 2);
                tableLayout.addView(tableRow, i);
            }else{
                break;
            }
        }
    }

    private int findMax(){
        int max = Integer.MIN_VALUE;
        for(String key : hs.keySet()){
            int value = hs.get(key);
            if(max < value){
                max = value;
            }
        }
        return max;
    }

    private String findKeyByValue(int value){
        for(String name : hs.keySet()){
            int number = hs.get(name);
            if(value == number){
                return name;
            }
        }
        System.out.println("Can't find key by value");
        return null;
    }

    private HashMap<String, Integer> getNamesAndScores(){
        SharedPreferences sharedPreferences = getSharedPreferences("high_score", MODE_PRIVATE);
        Map<String, ?> map = sharedPreferences.getAll();
        System.out.println("map.size() = "+map.size());
        HashMap<String, Integer> scores = new HashMap<>();
        for(String name: map.keySet()){
            //System.out.println("key = " + name);
            int score = (Integer) map.get(name);
            //System.out.println("value = " + score);
            scores.put(name, score);
        }
        System.out.println("scores.size() = "+scores.size());
        return scores;
    }
}
