import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class MinerNotFull implements Miner {

    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod; //Already has a getter

    MinerNotFull(String id, Point position,
              List<PImage> images, int resourceLimit, //int resourceCount,
              int actionPeriod, int animationPeriod)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public void setPosition(Point inPos) {position = inPos;}
    public Point getPosition(){return position;}
    public int getAnimationPeriod() {return animationPeriod;}


    //NextImage only for classes that have animations
    public void nextImage()
    {
        imageIndex = (imageIndex + 1) % images.size();
    }

    public List<PImage> getImages(){return images;}

    public int getImageIndex(){return imageIndex;}

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler){
        Optional<Entity> notFullTarget = world.findNearest(position, Ore.class);

        if (!notFullTarget.isPresent() ||
                !moveTo(world, notFullTarget.get(), scheduler) ||
                !transformNotFull(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), actionPeriod);
        }
    }

    public void scheduleActions(EventScheduler scheduler,WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this, new Activity(this,world, imageStore), actionPeriod);
        scheduler.scheduleEvent(this, new Animation(this,0), this.getAnimationPeriod());
    }

    //Move to MinerNotFull Class and make it PRIVATE
    public boolean transformNotFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        if (this.resourceCount >= this.resourceLimit)
        {
            MinerFull miner = new MinerFull(id, position, images, resourceLimit, //resourceCount,
                    actionPeriod, animationPeriod);

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);
            world.addEntity(miner);
            this.scheduleActions(scheduler, world, imageStore);

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
            Point nextPos = nextPositionMiner(world, target.getPosition());//target.position);

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

    //MinerFull AND MinerNotFull uses this send to Miner interface?
    // Called by the move functions this should be PRIVATE
    public Point nextPositionMiner(WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - this.position.x);
        Point newPos = new Point(this.position.x + horiz,
                this.position.y);

        if (horiz == 0 || world.isOccupied(newPos))
        {
            int vert = Integer.signum(destPos.y - this.position.y);
            newPos = new Point(this.position.x,
                    this.position.y + vert);

            if (vert == 0 || world.isOccupied(newPos))
            {
                newPos = position;
            }
        }

        return newPos;
    }

}
