import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class OreBlob implements Entity
{
    public static final String BLOB_KEY = "blob";
    public static final String BLOB_ID_SUFFIX = " -- blob";
    public static final int BLOB_PERIOD_SCALE = 4;
    public static final int BLOB_ANIMATION_MIN = 50;
    public static final int BLOB_ANIMATION_MAX = 150;

    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int actionPeriod;
    private int animationPeriod; //Already has a getter

    OreBlob(String id, Point position,
           List<PImage> images,
           int actionPeriod, int animationPeriod)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public Point getPosition(){return position;}

    public List<PImage> getImages(){return images;}

    public int getImageIndex(){return imageIndex;}

    public int getAnimationPeriod() {return animationPeriod;}

    public void nextImage()
    {
        imageIndex = (imageIndex + 1) % images.size();
    }

    public void setPosition(Point inPos) {position = inPos;}

    public void executeActivity(WorldModel world,ImageStore imageStore, EventScheduler scheduler){

        Optional<Entity> blobTarget = world.findNearest(this.position,Vein.class);//EntityKind.VEIN);
        long nextPeriod = actionPeriod;

        if (blobTarget.isPresent())
        {
            Point tgtPos = blobTarget.get().getPosition();

            if (moveToOreBlob(world, blobTarget.get(), scheduler))
            {
                Quake quake = new Quake(tgtPos, imageStore.getImageList(Quake.QUAKE_KEY));
                world.addEntity(quake);
                nextPeriod += actionPeriod;
                quake.scheduleActions(scheduler, world, imageStore);

            }
        }

        scheduler.scheduleEvent(this, new Activity(this,world,imageStore), nextPeriod);
    }

    //Create scheduleActions for EACH of the following classes: MinerFull, MinerNotFull,Ore, OreBlob, Quake,and Vein
    public void scheduleActions(EventScheduler scheduler,WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this, new Activity(this,world, imageStore), actionPeriod);
        scheduler.scheduleEvent(this, new Animation(this,0), this.getAnimationPeriod());
    }

    //Move to OreBlob and make private
    public boolean moveToOreBlob(WorldModel world,Entity target, EventScheduler scheduler)
    {
        //Kattia - Alert
        if (Functions.adjacent(this.position, target.getPosition()))
        {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else
        {
            Point nextPos = nextPositionOreBlob(world, target.getPosition());

            if (!(this.position.equals(nextPos)))
            {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent())
                {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this,nextPos);
            }
            return false;
        }
    }

    //OreBlob class and make private
    public Point nextPositionOreBlob(WorldModel world,Point destPos)
    {
        int horiz = Integer.signum(destPos.x - this.position.x);
        Point newPos = new Point(this.position.x + horiz,
                this.position.y);

        Optional<Entity> occupant = world.getOccupant(newPos);

        if (horiz == 0 || (occupant.isPresent() && !(occupant.get().getClass() == Ore.class)))
        {
            int vert = Integer.signum(destPos.y - this.position.y);
            newPos = new Point(this.position.x, this.position.y + vert);
            occupant = world.getOccupant(newPos);

            if (vert == 0 ||
                    (occupant.isPresent() && !(occupant.get().getClass() == Ore.class)))
            {
                newPos = position;
            }
        }
        return newPos;
    }

}
