public class Activity implements Action{

    private Entity entity;
    private WorldModel world;
    private ImageStore imageStore;


    Activity(Entity entity, WorldModel world,
           ImageStore imageStore)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    public void executeAction(EventScheduler scheduler)
    {
        if(entity instanceof ActionObject){((ActionObject)entity).executeActivity(world,imageStore,scheduler);}

    }
}
