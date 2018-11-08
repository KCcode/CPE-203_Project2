import processing.core.PImage;

import java.util.List;

abstract class Miner extends AnimatedObject{
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
