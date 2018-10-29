public interface Miner extends Entity{
    String MINER_KEY = "miner";
    int MINER_NUM_PROPERTIES = 7;
    int MINER_ID = 1;
    int MINER_COL = 2;
    int MINER_ROW = 3;
    int MINER_LIMIT = 4;
    int MINER_ACTION_PERIOD = 5;
    int MINER_ANIMATION_PERIOD = 6;

    void setPosition(Point inPos);
    int getAnimationPeriod();
    void nextImage();
    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
    void scheduleActions(EventScheduler scheduler,WorldModel world, ImageStore imageStore);
    boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler); //*Full or NotFull
    Point nextPositionMiner(WorldModel world, Point destPos);

}
