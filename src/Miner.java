import processing.core.PImage;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

abstract class Miner extends AnimatedObject{
    private int resourceLimit;
    //private PathingStrategy ps = new SingleStepPathingStrategy();
    private PathingStrategy ps = new AStar();

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
        Predicate<Point> canPassThrough = p ->!world.isOccupied(p) && world.withinBounds(p);
        BiPredicate<Point, Point> withinReach = (Point p1, Point p2)->Functions.adjacent(p1,p2);
        List<Point> path = ps.computePath(this.getPosition(), destPos, canPassThrough, withinReach,
                PathingStrategy.CARDINAL_NEIGHBORS);
        if(path == null || path.size() == 0){return this.getPosition();}
        else {return path.get(0);}

    }

}
