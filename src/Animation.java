public class Animation implements Action{

    private Entity entity;
    private int repeatCount;

    Animation(Entity entity, int repeatCount)
    {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }


    public void executeAction(EventScheduler scheduler) {
        entity.nextImage();
/*
        if (entity.getClass().isInstance(MinerFull.class)){((MinerFull)entity).nextImage();}

        if (entity.getClass().isInstance(MinerNotFull.class)){((MinerNotFull)entity).nextImage();}

        if (entity.getClass().isInstance(OreBlob.class)){((OreBlob)entity).nextImage();}

        if (entity.getClass().isInstance(Quake.class)){((Quake)entity).nextImage();}
  */

        /*
        if (entity.getClass().isInstance(MinerFull.class)){((MinerFull)entity).nextImage();}
        * case MINER_FULL:
         case MINER_NOT_FULL:
         case ORE_BLOB:
         case QUAKE:
        * */


        if (repeatCount != 1) {
//           scheduler.scheduleEvent(entity, new Animation(entity, Math.max(repeatCount -1, 0)), entity.getAnimationPeriod());

            if (entity instanceof MinerFull){
                scheduler.scheduleEvent(entity, new Animation(entity, Math.max(repeatCount -1, 0)),((MinerFull)entity).getAnimationPeriod());}

            if (entity instanceof MinerNotFull){
                scheduler.scheduleEvent(entity, new Animation(entity, Math.max(repeatCount -1, 0)),((MinerNotFull)entity).getAnimationPeriod());}
            }

            if (entity instanceof OreBlob){
                scheduler.scheduleEvent(entity, new Animation(entity, Math.max(repeatCount -1, 0)),((OreBlob)entity).getAnimationPeriod());
            }

            if (entity instanceof Quake){
                scheduler.scheduleEvent(entity, new Animation(entity, Math.max(repeatCount -1, 0)),((Quake)entity).getAnimationPeriod());
            }
    }

            //scheduler.scheduleEvent(entity, new Animation(entity, Math.max(repeatCount -1, 0)), ((MinerFull)entity).getAnimationPeriod());
}
