import processing.core.PImage;

import java.util.List;

public class Quake implements Entity
{
    public static final String QUAKE_KEY = "quake";
    public static final String QUAKE_ID = "quake";
    public static final int QUAKE_ACTION_PERIOD = 1100;
    public static final int QUAKE_ANIMATION_PERIOD = 100;
    public static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;

    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int actionPeriod;
    private int animationPeriod;

    Quake(Point position,
           List<PImage> images)
    {
        this.id = QUAKE_ID;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = QUAKE_ACTION_PERIOD;
        this.animationPeriod = QUAKE_ANIMATION_PERIOD;
    }

    public Point getPosition(){return position;}

    public List<PImage> getImages(){return images;}

    public int getImageIndex(){return imageIndex;}

    public int getAnimationPeriod(){return animationPeriod;}

    public void setPosition(Point inPos) {position = inPos;}

    //NextImage only for classes that have animations
    public void nextImage()
    {
        imageIndex = (imageIndex + 1) % images.size();
    }

    //Move to Quake class
    public void executeActivity(WorldModel world, EventScheduler scheduler)
    {
        scheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }

    //Create scheduleActions for EACH of the following classes: MinerFull, MinerNotFull,Ore, OreBlob, Quake,and Vein
    public void scheduleActions(EventScheduler scheduler,WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this, new Activity(this,world, imageStore), actionPeriod);
        scheduler.scheduleEvent(this,new Animation(this,QUAKE_ANIMATION_REPEAT_COUNT), this.getAnimationPeriod());
    }

}
