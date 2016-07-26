package kekcomp.game.view;


import kekcomp.game.help.IterableCounter;

/**
 * Created by svyatoslav on 1/12/16.
 */
public class ImageViewGroup {

    private int img1, img2, img3, img4;
    private IterableCounter index;

    public ImageViewGroup(int img1, int img2, int img3, int img4){
        index = new IterableCounter(4, 1);
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.img4 = img4;
    }

    public ImageViewGroup(int img[], int index){
        img1 = img[0];
        img2 = img[1];
        img3 = img[2];
        img4 = img[3];
        this.index = new IterableCounter(4, 1);
        this.index.setCounter(index);
        System.out.println(index);
    }

    public int getIndex() {
        return index.getCounter();
    }

    public int[] getImages(){
        return new int[] {img1, img2, img3, img4};
    }

    public int getNextImage(){
        int img;
        index.increment();
        switch(index.getCounter()){
            case 1:
                img = img1;
                break;
            case 2:
                img = img2;
                break;
            case 3:
                img = img3;
                break;
            case 4:
                img = img4;
                break;
            default:
                return 0;
        }
        System.out.println("index = "+index+"; nextImg()");
        return img;
    }

    public int getPreviousImage(){
        int img;
        index.decrement();
        switch(index.getCounter()){
            case 1:
                img = img1;
                break;
            case 2:
                img = img2;
                break;
            case 3:
                img = img3;
                break;
            case 4:
                img = img4;
                break;
            default:
                return 0;
        }
        System.out.println("index = "+index+"; prevImg()");
        return img;
    }

    public int getImageByIndex(){
        switch(index.getCounter()){
            case 1:
                return img1;
            case 2:
                return img2;
            case 3:
                return img3;
            case 4:
                return img4;
            default:
                return 0;
        }
    }
}
