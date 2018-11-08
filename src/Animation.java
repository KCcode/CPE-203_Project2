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
            if(entity instanceof AnimatedObject){
                scheduler.scheduleEvent(entity, new Animation(entity, Math.max(repeatCount -1, 0)),
                        ((AnimatedObject)entity).getAnimationPeriod());}
            }
    }
}
