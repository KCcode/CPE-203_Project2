import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Vein implements Entity
{
    public static final String VEIN_KEY = "vein";
    public static final int VEIN_NUM_PROPERTIES = 5;
    public static final int VEIN_ID = 1;
    public static final int VEIN_COL = 2;
    public static final int VEIN_ROW = 3;
    public static final int VEIN_ACTION_PERIOD = 4;

    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int actionPeriod;

    Vein(String id, Point position,
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

    //Move to Vein class
    public void executeActivity(WorldModel world,ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Point> openPt = world.findOpenAround(position);

        if (openPt.isPresent())
        {
            Ore ore = new Ore(Ore.ORE_ID_PREFIX + id, openPt.get(),imageStore.getImageList(Ore.ORE_KEY),
                    Ore.ORE_CORRUPT_MIN + rand.nextInt(Ore.ORE_CORRUPT_MAX - Ore.ORE_CORRUPT_MIN));

            world.addEntity(ore);
            ore.scheduleActions(scheduler, world, imageStore);
        }

        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), actionPeriod);
    }

    //Create scheduleActions for EACH of the following classes: MinerFull, MinerNotFull,Ore, OreBlob, Quake,and Vein
    public void scheduleActions(EventScheduler scheduler,WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this, new Activity(this,world, imageStore), actionPeriod);
    }
}
