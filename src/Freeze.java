import processing.core.PImage;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Freeze extends AnimatedObject{

    public static final String FREEZE_KEY = "freeze";
    public static final int FREEZE_ACTION_PERIOD = 300;
    public static final int FREEZE_ANIMATION_PERIOD = 300;

    PathingStrategy ps = new SingleStepPathingStrategy();

    Freeze(String id, Point position,
           List<PImage> images,int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);

    }

    public void scheduleActions(EventScheduler scheduler,WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this, new Activity(this,world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent(this, new Animation(this,0), this.getAnimationPeriod());
    }


    public void executeActivity(WorldModel world,ImageStore imageStore, EventScheduler scheduler){

        Optional<Entity> fullTarget = world.findNearest(this.getPosition(), OreBlob.class);

        if (fullTarget.isPresent() && moveTo(world, fullTarget.get(), scheduler))
        {
            transformSmith(world, scheduler, imageStore);
        }
        else
        {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        }
    }

    public void transformSmith(WorldModel world,EventScheduler scheduler, ImageStore imageStore){
        BlackSmith blackSmith = new BlackSmith(BlackSmith.SMITH_KEY,
                this.getPosition(), imageStore.getImageList("blacksmith"));
        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);
        world.addEntity(blackSmith);
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler)
    {
        //Kattia - Alert
        if (Functions.adjacent(this.getPosition(), target.getPosition()))
        {
            return true;
        }
        else
        {
            Point nextPos = nextPositionMiner(world, target.getPosition());

            if (!this.getPosition().equals(nextPos))
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


    public Point nextPositionMiner(WorldModel world, Point destPos)
    {
        Predicate<Point> canPassThrough = p ->!world.isOccupied(p) && world.withinBounds(p);
        BiPredicate<Point, Point> withinReach = (Point p1, Point p2)->Functions.adjacent(p1,p2);
        List<Point> path = ps.computePath(this.getPosition(), destPos, canPassThrough, withinReach,
                PathingStrategy.CARDINAL_NEIGHBORS);
        if(path == null || path.size() == 0){return this.getPosition();}
        else {return path.get(0);}

    }


}

