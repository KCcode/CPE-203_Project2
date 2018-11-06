import processing.core.PImage;
import java.util.List;
import java.util.Optional;

public class MinerFull implements Miner
{
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod; //Already has a getter


    MinerFull(String id, Point position,
           List<PImage> images, int resourceLimit, int resourceCount,
           int actionPeriod, int animationPeriod)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceLimit;
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

    //Move to MinerFull Class
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler){

        Optional<Entity> fullTarget = world.findNearest(position, BlackSmith.class);//EntityKind.BLACKSMITH);
                                      //moveToFull
        if (fullTarget.isPresent() && moveTo(world, fullTarget.get(), scheduler))
        {
            transformFull(world, scheduler, imageStore);
        }
        else
        {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), actionPeriod);//createActivityAction(entity,world,imageStore), actionPeriod);
        }
    }

    //Move to MinerFull
    public void transformFull(WorldModel world,EventScheduler scheduler, ImageStore imageStore)
    {
        //Kattia - Alert
        MinerNotFull miner = new MinerNotFull(id, position, images, resourceLimit, 0,
                actionPeriod, animationPeriod);

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);
        world.addEntity(miner);
        //this.scheduleActions(scheduler, world, imageStore);
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

    //Create scheduleActions for EACH of the following classes: MinerFull, MinerNotFull,Ore, OreBlob, Quake,and Vein
    public void scheduleActions(EventScheduler scheduler,WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this, new Activity(this,world, imageStore), actionPeriod);
        scheduler.scheduleEvent(this, new Animation(this,0), this.getAnimationPeriod());//.getAnimationPeriod());
    }

}
