import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class OreBlob extends AnimatedObject
{
    public static final String BLOB_KEY = "blob";
    public static final String BLOB_ID_SUFFIX = " -- blob";
    public static final int BLOB_PERIOD_SCALE = 4;
    public static final int BLOB_ANIMATION_MIN = 50;
    public static final int BLOB_ANIMATION_MAX = 150;



    OreBlob(String id, Point position,
           List<PImage> images,
           int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);

    }

    public void executeActivity(WorldModel world,ImageStore imageStore, EventScheduler scheduler){

        Optional<Entity> blobTarget = world.findNearest(this.getPosition(),Vein.class);//EntityKind.VEIN);
        long nextPeriod = this.getActionPeriod();

        if (blobTarget.isPresent())
        {
            Point tgtPos = blobTarget.get().getPosition();

            if (moveToOreBlob(world, blobTarget.get(), scheduler))
            {
                Quake quake = new Quake(tgtPos, imageStore.getImageList(Quake.QUAKE_KEY));
                world.addEntity(quake);
                nextPeriod += this.getActionPeriod();
                quake.scheduleActions(scheduler, world, imageStore);

            }
        }

        scheduler.scheduleEvent(this, new Activity(this,world,imageStore), nextPeriod);
    }

    //Create scheduleActions for EACH of the following classes: MinerFull, MinerNotFull,Ore, OreBlob, Quake,and Vein
    public void scheduleActions(EventScheduler scheduler,WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this, new Activity(this,world, imageStore), this.getActionPeriod());//actionPeriod);
        scheduler.scheduleEvent(this, new Animation(this,0), this.getAnimationPeriod());
    }

    //Move to OreBlob and make private
    public boolean moveToOreBlob(WorldModel world,Entity target, EventScheduler scheduler)
    {
        //Kattia - Alert
        if (Functions.adjacent(this.getPosition(), target.getPosition()))
        {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else
        {
            Point nextPos = nextPositionOreBlob(world, target.getPosition());

            if (!(this.getPosition().equals(nextPos)))
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
        int horiz = Integer.signum(destPos.x - this.getPosition().x);
        Point newPos = new Point(this.getPosition().x + horiz,
                this.getPosition().y);

        Optional<Entity> occupant = world.getOccupant(newPos);

        if (horiz == 0 || (occupant.isPresent() && !(occupant.get().getClass() == Ore.class)))
        {
            int vert = Integer.signum(destPos.y - this.getPosition().y);
            newPos = new Point(this.getPosition().x, this.getPosition().y + vert);
            occupant = world.getOccupant(newPos);

            if (vert == 0 ||
                    (occupant.isPresent() && !(occupant.get().getClass() == Ore.class)))
            {
                newPos = this.getPosition();//this.setPosition(newPos);//newPos = position;
            }
        }
        return newPos;
    }

}
