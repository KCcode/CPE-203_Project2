import processing.core.PImage;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

abstract class Wyvern extends AnimatedObject {

    public static final String WYVERN_KEY = "wyvern";
    public static final int WYVERN_LIMIT = 1;
    public static final int WYVERN_ACTION_PERIOD = 200;
    public static final int WYVERN_ANIMATION_PERIOD = 100;

    int wyvernLimit;
    PathingStrategy ps = new SingleStepPathingStrategy();
    //PathingStrategy ps = new AStar();

    Wyvern(String id, Point position, List< PImage > images, int actionPeriod, int animationPeriod, int inLimitEat){
        super(id, position, images, actionPeriod, animationPeriod);
        wyvernLimit = inLimitEat;
    }

    public void setWyvernLimit(int inRC){ this.wyvernLimit = inRC;}
    public int getWyvernLimit(){return this.wyvernLimit;}

    abstract boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler); //*Full or NotFull

    public void scheduleActions(EventScheduler scheduler,WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this, new Activity(this,world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent(this, new Animation(this,0), this.getAnimationPeriod());
    }

    public Point nextPositionWyvern(WorldModel world, Point destPos)
    {
        Predicate<Point> canPassThrough = p ->!world.isOccupied(p) && world.withinBounds(p);
        BiPredicate<Point, Point> withinReach = (Point p1, Point p2)->Functions.adjacent(p1,p2);
        List<Point> path = ps.computePath(this.getPosition(), destPos, canPassThrough, withinReach,
                PathingStrategy.CARDINAL_NEIGHBORS);
        if(path == null || path.size() == 0){return this.getPosition();}
        else {return path.get(0);}

    }

}
