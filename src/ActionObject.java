import processing.core.PImage;
import java.util.List;

abstract class ActionObject extends Entity {
    private int actionPeriod;

    ActionObject(String id, Point position, List<PImage> images, int actionPeriod){
        super(id, position, images);
        this.actionPeriod = actionPeriod;
    }

    public void setActionPeriod(int actionPeriod){this.actionPeriod = actionPeriod;}
    public int getActionPeriod(){return actionPeriod;}

    abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
    //Create scheduleActions for EACH of the following classes: MinerFull, MinerNotFull,Ore, OreBlob, Quake,and Vein
    abstract void scheduleActions(EventScheduler scheduler,WorldModel world, ImageStore imageStore);

}
