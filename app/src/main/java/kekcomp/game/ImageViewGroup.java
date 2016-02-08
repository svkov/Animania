package kekcomp.game;


/**
 * Created by svyatoslav on 1/12/16.
 */
public class ImageViewGroup {

    int img1, img2, img3, img4;
    private int index;

    public ImageViewGroup(int img1, int img2, int img3, int img4){
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.img4 = img4;
        index = 4;
    }

    public ImageViewGroup(int img[], int index){
        img1 = img[0];
        img2 = img[1];
        img3 = img[2];
        img4 = img[3];
        if(index > 4 | index < 1){
            index = 4;
        }
        this.index = index;
        System.out.println(index);
    }

    public int getIndex() {
        return index;
    }

    public int[] getImages(){
        return new int[] {img1, img2, img3, img4};
    }

    public int getImg1() {
        index = 1;
        System.out.println("index = "+index+"; getImg1()");
        return img1;
    }

    public int getImg2() {
        index = 2;
        System.out.println("index = "+index+"; getImg2()");
        return img2;
    }

    public int getImg3() {
        index = 3;
        System.out.println("index = "+index+"; getImg3()");
        return img3;
    }

    public int getImg4() {
        index = 4;
        System.out.println("index = "+index+"; getImg4()");
        return img4;
    }

    public boolean hasNextImage(){
        if(index == 4){
            return false;
        }
        return true;
    }

    public boolean hasPreviousImage(){
        if(index == 1){
            return false;
        }
        return true;
    }

    public int getNextImage(){
        int img;
        index++;
        switch(index){
            case 1:
                img = getImg1();
                break;
            case 2:
                img = getImg2();
                break;
            case 3:
                img = getImg3();
                break;
            case 4:
                img = getImg4();
                break;
            default:
                return 0;
        }
        System.out.println("index = "+index+"; nextImg()");
        return img;
    }

    public int getPreviousImage(){
        int img;
        index--;
        switch (index){
            case 1:
                img = getImg1();
                break;
            case 2:
                img = getImg2();
                break;
            case 3:
                img = getImg3();
                break;
            case 4:
                img = getImg4();
                break;
            default:
                return 0;
        }
        System.out.println("index = "+index+"; prevImg()");
        return img;
    }

    public int getImageByIndex(){
        switch(index){
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
