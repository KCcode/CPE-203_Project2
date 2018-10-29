import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Ore implements Entity
{
    public static final String ORE_ID_PREFIX = "ore -- ";
    public static final int ORE_CORRUPT_MIN = 20000;
    public static final int ORE_CORRUPT_MAX = 30000;
    public static final int ORE_REACH = 1;

    public static final String ORE_KEY = "ore";
    public static final int ORE_NUM_PROPERTIES = 5;
    public static final int ORE_ID = 1;
    public static final int ORE_COL = 2;
    public static final int ORE_ROW = 3;
    public static final int ORE_ACTION_PERIOD = 4;

    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int actionPeriod;

    Ore(String id, Point position,
           List<PImage> images,
           int actionPeriod)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;

        this.actionPeriod = actionPeriod;

    }

    //Private move to Ore and Vein classes
    private static final Random rand = new Random();

    public Point getPosition(){return position;}

    public List<PImage> getImages(){return images;}

    public int getImageIndex(){return imageIndex;}

    public void setPosition(Point inPos) {position = inPos;}

    public void nextImage()
    {
        imageIndex = (imageIndex + 1) % images.size();
    }


    //Move to Ore class
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler){
        Point pos = position;  // store current position before removing

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        OreBlob blob = new OreBlob(id + OreBlob.BLOB_ID_SUFFIX, pos, imageStore.getImageList(OreBlob.BLOB_KEY),
                actionPeriod/OreBlob.BLOB_PERIOD_SCALE,
                OreBlob.BLOB_ANIMATION_MIN + rand.nextInt(OreBlob.BLOB_ANIMATION_MAX - OreBlob.BLOB_ANIMATION_MIN));

        world.addEntity(blob);
        blob.scheduleActions(scheduler, world, imageStore);

    }

    //Create scheduleActions for EACH of the following classes: MinerFull, MinerNotFull,Ore, OreBlob, Quake,and Vein
    public void scheduleActions(EventScheduler scheduler,WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this, new Activity(this,world, imageStore), actionPeriod);

    }
}
