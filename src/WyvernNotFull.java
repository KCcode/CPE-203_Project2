import processing.core.PImage;
import java.util.List;
import java.util.Optional;

public class WyvernNotFull extends Wyvern {
    int amountAte;

    WyvernNotFull(String id, Point position, List< PImage > images, int actionPeriod, int animationPeriod, int inLimitEat,
               int inAmountAte){
        super(id, position, images, actionPeriod, animationPeriod,inLimitEat);
        this.amountAte = 0;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler){
        Optional<Entity> notFullTarget = world.findNearest(this.getPosition(), MinerFull.class);

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
        System.out.println("transform to not full");
        if (this.amountAte >= this.getWyvernLimit())
        {
            WyvernFull wyvern = new WyvernFull(this.getId(),this.getPosition(), this.getImages(), this.getActionPeriod(),
                    this.getAnimationPeriod(), this.getWyvernLimit(), this.getWyvernLimit());
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);
            world.addEntity(wyvern);
            wyvern.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    //Move to MinerNotFull and make private
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler)
    {

        if (Functions.adjacent(this.getPosition(),target.getPosition()))
        {
            this.amountAte += 1;
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);

            return true;
        }
        else
        {
            Point nextPos = this.nextPositionWyvern(world, target.getPosition());//target.position);

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
