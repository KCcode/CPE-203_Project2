import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class MinerNotFull extends Miner {

    private int resourceCount;

    MinerNotFull(String id, Point position,
              List<PImage> images,
                 int resourceLimit, int resourceCount,
              int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod, resourceLimit);
        this.resourceCount = 0;
    }

    public void setResourceCount(int inRC){this.resourceCount = inRC;}
    public int getResourceCount(){return resourceCount;}


    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler){
        Optional<Entity> notFullTarget = world.findNearest(this.getPosition(), Ore.class);

        if (!notFullTarget.isPresent() ||
                !moveTo(world, notFullTarget.get(), scheduler) ||
                !transformNotFull(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        }
    }

    //Move to MinerNotFull Class and make it PRIVATE
    public boolean transformNotFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        if (this.resourceCount >= this.getResourceLimit())
        {
            MinerFull miner = new MinerFull(this.getId(), this.getPosition(), this.getImages(),
                    this.getResourceLimit(), this.getResourceLimit(),
                    this.getActionPeriod(), this.getAnimationPeriod());

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);
            world.addEntity(miner);
            miner.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    //Move to MinerNotFull and make private
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler)
    {

        if (Functions.adjacent(this.getPosition(),target.getPosition()))
        {
            this.resourceCount += 1;
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);

            return true;
        }
        else
        {
            Point nextPos = this.nextPositionMiner(world, target.getPosition());//target.position);

            if (!this.getPosition().equals(nextPos))
            {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent())
                {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }
}
