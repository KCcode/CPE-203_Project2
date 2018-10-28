import processing.core.PImage;
import java.util.List;
import java.util.Random;
import java.util.Optional;

public class MinerFull implements Miner //extends Entity
{
    /*
    public static final String MINER_KEY = "miner";
    public static final int MINER_NUM_PROPERTIES = 7;
    public static final int MINER_ID = 1;
    public static final int MINER_COL = 2;
    public static final int MINER_ROW = 3;
    public static final int MINER_LIMIT = 4;
    public static final int MINER_ACTION_PERIOD = 5;
    public static final int MINER_ANIMATION_PERIOD = 6;
*/
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod; //Already has a getter


    MinerFull(String id, Point position,
           List<PImage> images, int resourceLimit, //int resourceCount,
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
    //public void executeMinerFullActivity(Entity entity, WorldModel world, ImageStore imageStore, EventScheduler scheduler){
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
        //Entity miner = Functions.createMinerNotFull(id, resourceLimit, position, actionPeriod, animationPeriod, images);
        MinerNotFull miner = new MinerNotFull(id, position, images, resourceLimit, //resourceCount,
                actionPeriod, animationPeriod);

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);
        world.addEntity(miner);
        this.scheduleActions(scheduler, world, imageStore);
    }

    //Move to MinerFull and make private
    //private boolean moveToFull(Entity miner, WorldModel world, Entity target, EventScheduler scheduler)
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
    //Remove this switch and it defines if each class needs and activity or animation or both
    public void scheduleActions(EventScheduler scheduler,WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this, new Activity(this,world, imageStore), actionPeriod);
        scheduler.scheduleEvent(this, new Animation(this,0), this.getAnimationPeriod());//.getAnimationPeriod());

        //scheduler.scheduleEvent(entity, entity.createActivityAction(entity,world, imageStore), actionPeriod);
        //scheduler.scheduleEvent(entity,entity.createAnimationAction(entity,0), entity.getAnimationPeriod());
        /*
        switch (kind)
        {
            case MINER_FULL:
                scheduler.scheduleEvent(entity, entity.createActivityAction(entity,world, imageStore), actionPeriod);
                scheduler.scheduleEvent(entity,entity.createAnimationAction(entity,0), entity.getAnimationPeriod());

                break;

            case MINER_NOT_FULL:
                scheduler.scheduleEvent(entity, entity.createActivityAction(entity,world, imageStore), actionPeriod);
                scheduler.scheduleEvent(entity,entity.createAnimationAction(entity,0), entity.getAnimationPeriod());

                break;

            case ORE:
                scheduler.scheduleEvent(entity, entity.createActivityAction(entity,world, imageStore), actionPeriod);

                break;

            case ORE_BLOB:
                scheduler.scheduleEvent(entity, entity.createActivityAction(entity,world, imageStore), actionPeriod);
                scheduler.scheduleEvent(entity,entity.createAnimationAction(entity,0), entity.getAnimationPeriod());

                break;

            case QUAKE:
                scheduler.scheduleEvent(entity, entity.createActivityAction(entity,world, imageStore), actionPeriod);
                scheduler.scheduleEvent(entity,entity.createAnimationAction(entity,QUAKE_ANIMATION_REPEAT_COUNT), entity.getAnimationPeriod());

                break;

            case VEIN:
                scheduler.scheduleEvent(entity, entity.createActivityAction(entity,world, imageStore), actionPeriod);
                break;

            default:
        }*/
    }
}
