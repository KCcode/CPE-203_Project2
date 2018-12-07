import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class WyvernFull extends Wyvern{
    int amountAte;

    WyvernFull(String id, Point position, List< PImage > images, int actionPeriod, int animationPeriod, int inLimitEat,
               int inAmountAte){
        super(id, position, images, actionPeriod, animationPeriod,inLimitEat);
        this.amountAte = inAmountAte;
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
            Point nextPos = nextPositionWyvern(world, target.getPosition());

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

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler){

        System.out.println("wyvernfull");
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

    public void transformFull(WorldModel world,EventScheduler scheduler, ImageStore imageStore)
    {
        WyvernNotFull wyvern = new WyvernNotFull(this.getId(), this.getPosition(), this.getImages(),
                this.getActionPeriod(), this.getAnimationPeriod(), this.getWyvernLimit(), 0);

        System.out.println("transform to wyvern not full");
        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);
        world.addEntity(wyvern);
        wyvern.scheduleActions(scheduler, world, imageStore);
    }


}
