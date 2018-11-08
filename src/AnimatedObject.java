import processing.core.PImage;
import java.util.List;

abstract class AnimatedObject extends ActionObject {
    private int animationPeriod; //Already has a getter

    AnimatedObject(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod){
        super(id, position, images, actionPeriod);
        this.animationPeriod = animationPeriod;

    }
    public void setAnimationPeriod(int animationPeriod){this.animationPeriod = animationPeriod;}
    public int getAnimationPeriod(){return animationPeriod;}
}
