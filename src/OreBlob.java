import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class OreBlob extends AnimatedObject
{
    public static final String BLOB_KEY = "blob";
    public static final String BLOB_ID_SUFFIX = " -- blob";
    public static final int BLOB_PERIOD_SCALE = 4;
    public static final int BLOB_ANIMATION_MIN = 50;
    public static final int BLOB_ANIMATION_MAX = 150;

    private PathingStrategy ps = new SingleStepPathingStrategy();
    //private PathingStrategy ps = new AStar();

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
    public Point nextPositionOreBlob(WorldModel world,Point destPos) {


        Predicate<Point> canPassThrough = p -> !world.isOccupied(p) && world.withinBounds(p);
        BiPredicate<Point, Point> withinReach = (Point p1, Point p2) -> Functions.adjacent(p1, p2);
        List<Point> path = ps.computePath(this.getPosition(), destPos, canPassThrough, withinReach,
                PathingStrategy.CARDINAL_NEIGHBORS);
        if (path == null || path.size() == 0) { return this.getPosition();}
        else {return path.get(0);}

    }

}
