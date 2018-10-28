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

        if (repeatCount != 1) {
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
}
