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
        if (entity instanceof MinerFull){((MinerFull) entity).executeActivity(world,imageStore,scheduler);}

        if (entity instanceof MinerNotFull){((MinerNotFull) entity).executeActivity(world,imageStore,scheduler);}

        if (entity instanceof Ore){((Ore) entity).executeActivity(world,imageStore,scheduler);}

        if (entity instanceof OreBlob){((OreBlob) entity).executeActivity(world,imageStore,scheduler);}

        if (entity instanceof Quake){((Quake) entity).executeActivity(world, scheduler);}

        if (entity instanceof Vein){((Vein) entity).executeActivity(world,imageStore,scheduler);}

    }
}
