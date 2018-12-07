import processing.core.PImage;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MinerFull extends Miner
{
    private int resourceCount;

    MinerFull(String id, Point position,
           List<PImage> images,
              int resourceLimit, int resourceCount,
           int actionPeriod, int animationPeriod)

    {
        super(id, position, images, actionPeriod, animationPeriod,resourceLimit);
        this.resourceCount = resourceCount;
    }

    public void setResourceCount(int inRC){this.resourceCount = inRC;}
    public int getResourceCount(){return resourceCount;}

    //Move to MinerFull Class
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler){

        Optional<Entity> fullTarget = world.findNearest(this.getPosition(), BlackSmith.class);

        if (fullTarget.isPresent() && moveTo(world, fullTarget.get(), scheduler))
        {
            transformFull(world, scheduler, imageStore);
        }
        else
        {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());//actionPeriod);//createActivityAction(entity,world,imageStore), actionPeriod);
        }
    }

    //Move to MinerFull
    public void transformFull(WorldModel world,EventScheduler scheduler, ImageStore imageStore)
    {
        //Kattia - Alert
        MinerNotFull miner = new MinerNotFull(this.getId(), this.getPosition(), this.getImages(),
                this.getResourceLimit(), 0,
                this.getActionPeriod(), this.getAnimationPeriod());

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);
        world.addEntity(miner);
        miner.scheduleActions(scheduler, world, imageStore);
    }

    //Move to MinerFull and make private
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
}
