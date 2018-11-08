import processing.core.PImage;

import java.util.List;

abstract class Miner extends AnimatedObject{
    String MINER_KEY = "miner";
    int MINER_NUM_PROPERTIES = 7;
    int MINER_ID = 1;
    int MINER_COL = 2;
    int MINER_ROW = 3;
    int MINER_LIMIT = 4;
    int MINER_ACTION_PERIOD = 5;
    int MINER_ANIMATION_PERIOD = 6;

    private int resourceLimit;

    Miner(String id, Point position, List< PImage > images, int actionPeriod, int animationPeriod, int resourceLimit){
        super(id, position, images, actionPeriod, animationPeriod);
            this.resourceLimit = resourceLimit;
    }

    public void setResourceLimit(int inRC){ this.resourceLimit = inRC;}
    public int getResourceLimit(){return this.resourceLimit;}

    abstract boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler); //*Full or NotFull

    public void scheduleActions(EventScheduler scheduler,WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this, new Activity(this,world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent(this, new Animation(this,0), this.getAnimationPeriod());
    }


    public Point nextPositionMiner(WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - this.getPosition().x);//this.position.x);
        Point newPos = new Point(this.getPosition().x + horiz,
                this.getPosition().y);

        if (horiz == 0 || world.isOccupied(newPos))
        {
            int vert = Integer.signum(destPos.y - this.getPosition().y);
            newPos = new Point(this.getPosition().x,
                    this.getPosition().y + vert);

            if (vert == 0 || world.isOccupied(newPos))
            {
                newPos = this.getPosition();
            }
        }

        return newPos;
    }

}
