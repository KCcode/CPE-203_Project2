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

        //When well written this will change to
        //entity.executeActivity(entity, world, imageStore, scheduler);
        //((MinerFull)entity).executeActivity(entity, world, imageStore, scheduler)
        //switch will be removed

        if (entity instanceof MinerFull){((MinerFull) entity).executeActivity(world,imageStore,scheduler);}

        if (entity instanceof MinerNotFull){((MinerNotFull) entity).executeActivity(world,imageStore,scheduler);}

        if (entity instanceof Ore){((Ore) entity).executeActivity(world,imageStore,scheduler);}

        if (entity instanceof OreBlob){((OreBlob) entity).executeActivity(world,imageStore,scheduler);}

        if (entity instanceof Quake){((Quake) entity).executeActivity(world, scheduler);}

        if (entity instanceof Vein){((Vein) entity).executeActivity(world,imageStore,scheduler);}
        /*
        switch (entity.getEntityKind())
        {
            case MINER_FULL:
                entity.executeMinerFullActivity(entity, world, imageStore,scheduler);
                break;

            case MINER_NOT_FULL:
                entity.executeMinerNotFullActivity(entity, world, imageStore, scheduler);
                break;

            case ORE:
                entity.executeOreActivity(entity, world, imageStore,scheduler);
                break;

            case ORE_BLOB:
                entity.executeOreBlobActivity(entity, world, imageStore, scheduler);
                break;

            case QUAKE:
                entity.executeQuakeActivity(entity, world,scheduler);
                break;

            case VEIN:
                entity.executeVeinActivity(entity, world, imageStore,scheduler);
                break;

            default:
                throw new UnsupportedOperationException(
                        String.format("executeActivityAction not supported for %s", entity.getEntityKind()));
        }*/
    }
}
