package kekcomp.game.help;

/**
 * Created by svyatoslav on 4/23/16.
 */
public class IterableCounter {

    private int counter;
    private int start;
    private int limit;

    public IterableCounter(int limit){
        counter = start;
        this.limit = limit;
        start = 0;
    }

    public IterableCounter(int limit, int start){
        counter = start;
        this.start = start;
        this.limit = limit;
    }

    public void setCounter(int newCounter){
        if(newCounter > limit){
            counter = limit;
        }else if(newCounter < start){
            counter = start;
        }else{
            counter = newCounter;
        }
    }

    public int getCounter(){
        return counter;
    }

    public void increment(){
        counter++;
        refresh();
    }

    public void decrement(){
        counter--;
        refresh();
    }

    private void refresh(){
        System.out.println("counter "+counter);
        if(counter == limit+1){
            counter = start;
        }else if(counter == start-1){
            counter = limit;
        }
    }
}
