import java.util.List;
import java.util.Optional;
import java.util.Random;

import processing.core.PImage;

final class Entity
{
   private EntityKind kind;
   private String id;
   private Point position;
   private List<PImage> images;
   private int imageIndex;
   private int resourceLimit;
   private int resourceCount;
   private int actionPeriod;
   private int animationPeriod; //Already has a getter

   //Private move to Ore and Vein classes
   private static final Random rand = new Random();

   public static final String BLOB_KEY = "blob";
   public static final String BLOB_ID_SUFFIX = " -- blob";
   public static final int BLOB_PERIOD_SCALE = 4;
   public static final int BLOB_ANIMATION_MIN = 50;
   public static final int BLOB_ANIMATION_MAX = 150;

   public static final String ORE_ID_PREFIX = "ore -- ";
   public static final int ORE_CORRUPT_MIN = 20000;
   public static final int ORE_CORRUPT_MAX = 30000;
   public static final int ORE_REACH = 1;

   public static final String QUAKE_KEY = "quake";
   public static final String QUAKE_ID = "quake";
   public static final int QUAKE_ACTION_PERIOD = 1100;
   public static final int QUAKE_ANIMATION_PERIOD = 100;
   public static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;

   /*
   public static final String MINER_KEY = "miner";
   public static final int MINER_NUM_PROPERTIES = 7;
   public static final int MINER_ID = 1;
   public static final int MINER_COL = 2;
   public static final int MINER_ROW = 3;
   public static final int MINER_LIMIT = 4;
   public static final int MINER_ACTION_PERIOD = 5;
   public static final int MINER_ANIMATION_PERIOD = 6;
*/
   public static final String OBSTACLE_KEY = "obstacle";
   public static final int OBSTACLE_NUM_PROPERTIES = 4;
   public static final int OBSTACLE_ID = 1;
   public static final int OBSTACLE_COL = 2;
   public static final int OBSTACLE_ROW = 3;

   public static final String ORE_KEY = "ore";
   public static final int ORE_NUM_PROPERTIES = 5;
   public static final int ORE_ID = 1;
   public static final int ORE_COL = 2;
   public static final int ORE_ROW = 3;
   public static final int ORE_ACTION_PERIOD = 4;

   public static final String SMITH_KEY = "blacksmith";
   public static final int SMITH_NUM_PROPERTIES = 4;
   public static final int SMITH_ID = 1;
   public static final int SMITH_COL = 2;
   public static final int SMITH_ROW = 3;

   public static final String VEIN_KEY = "vein";
   public static final int VEIN_NUM_PROPERTIES = 5;
   public static final int VEIN_ID = 1;
   public static final int VEIN_COL = 2;
   public static final int VEIN_ROW = 3;
   public static final int VEIN_ACTION_PERIOD = 4;


   Entity(EntityKind kind, String id, Point position,
      List<PImage> images, int resourceLimit, int resourceCount,
      int actionPeriod, int animationPeriod)
   {
      this.kind = kind;
      this.id = id;
      this.position = position;
      this.images = images;
      this.imageIndex = 0;
      this.resourceLimit = resourceLimit;
      this.resourceCount = resourceCount;
      this.actionPeriod = actionPeriod;
      this.animationPeriod = animationPeriod;
   }

   public Point getPosition(){return position;}

   public EntityKind getEntityKind(){ return kind;}

   public List<PImage> getImages(){return images;}

   public int getImageIndex(){return imageIndex;}

   public void setPosition(Point inPos) {position = inPos;}


   public int getAnimationPeriod()
   {
      switch (kind)
      {
         case MINER_FULL:
         case MINER_NOT_FULL:
         case ORE_BLOB:
         case QUAKE:
            return animationPeriod;
         default:
            throw new UnsupportedOperationException(
                    String.format("getAnimationPeriod not supported for %s",
                            kind));
      }
   }

   //NextImage only for classes that have animations
   public void nextImage()
   {
      imageIndex = (imageIndex + 1) % images.size();
   }

/*
   //Move to MinerFull Class
   public void executeMinerFullActivity(Entity entity, WorldModel world, ImageStore imageStore, EventScheduler scheduler){
      Optional<Entity> fullTarget = world.findNearest(position,EntityKind.BLACKSMITH);

      if (fullTarget.isPresent() && moveToFull(entity, world, fullTarget.get(), scheduler))
      {
         transformFull(entity, world, scheduler, imageStore);
      }
      else
      {
         scheduler.scheduleEvent(entity, createActivityAction(entity,world,imageStore), actionPeriod);
      }
   }
*/
   //Move to MinerNotFull Class
   public void executeMinerNotFullActivity(Entity entity, WorldModel world, ImageStore imageStore, EventScheduler scheduler){
      Optional<Entity> notFullTarget = world.findNearest(position, EntityKind.ORE);

      if (!notFullTarget.isPresent() ||
              !moveToNotFull(entity, world, notFullTarget.get(), scheduler) ||
              !transformNotFull(entity, world, scheduler, imageStore))
      {
         scheduler.scheduleEvent(entity, createActivityAction(entity, world, imageStore), actionPeriod);
      }
   }

   //Move to Ore class
   public void executeOreActivity(Entity entity, WorldModel world, ImageStore imageStore, EventScheduler scheduler){
      Point pos = position;  // store current position before removing

      world.removeEntity(entity);
      scheduler.unscheduleAllEvents(entity);

      Entity blob = Functions.createOreBlob(id + BLOB_ID_SUFFIX,
              pos, actionPeriod / BLOB_PERIOD_SCALE,
              BLOB_ANIMATION_MIN +
                      rand.nextInt(BLOB_ANIMATION_MAX - BLOB_ANIMATION_MIN),
              imageStore.getImageList(BLOB_KEY));

      world.addEntity(blob);
      scheduleActions(blob, scheduler, world, imageStore);
   }

   //Move to OreBlob class
   public void executeOreBlobActivity(Entity entity, WorldModel world,ImageStore imageStore, EventScheduler scheduler){

      Optional<Entity> blobTarget = world.findNearest(position,EntityKind.VEIN);
      long nextPeriod = actionPeriod;

      if (blobTarget.isPresent())
      {
         Point tgtPos = blobTarget.get().position;

         if (moveToOreBlob(entity, world, blobTarget.get(), scheduler))
         {
            //Kattia- Alert
            Entity quake = Functions.createQuake(tgtPos, imageStore.getImageList(QUAKE_KEY));
            world.addEntity(quake);
            nextPeriod += actionPeriod;
            scheduleActions(quake, scheduler, world, imageStore);
         }
      }

      scheduler.scheduleEvent(entity, createActivityAction(entity,world,imageStore), nextPeriod);
   }

   //Move to Quake class
   public void executeQuakeActivity(Entity entity, WorldModel world, EventScheduler scheduler)
   {
      scheduler.unscheduleAllEvents(entity);
      world.removeEntity(entity);
   }

   //Move to Vein class
   public void executeVeinActivity(Entity entity, WorldModel world,ImageStore imageStore, EventScheduler scheduler)
   {
      Optional<Point> openPt = world.findOpenAround(position);

      if (openPt.isPresent())
      {
         Entity ore = Functions.createOre(ORE_ID_PREFIX + id,
                 openPt.get(), ORE_CORRUPT_MIN +
                         rand.nextInt(ORE_CORRUPT_MAX - ORE_CORRUPT_MIN),
                 imageStore.getImageList(ORE_KEY));
         world.addEntity(ore);
         scheduleActions(ore, scheduler, world, imageStore);
      }

      scheduler.scheduleEvent(entity, entity.createActivityAction(entity, world, imageStore), actionPeriod);
   }

   //Create scheduleActions for EACH of the following classes: MinerFull, MinerNotFull,Ore, OreBlob, Quake,and Vein
   //Remove this switch and it defines if each class needs and activity or animation or both
   public void scheduleActions(Entity entity, EventScheduler scheduler,WorldModel world, ImageStore imageStore)
   {
      switch (kind)
      {
         case MINER_FULL:
            scheduler.scheduleEvent(entity, entity.createActivityAction(entity,world, imageStore), actionPeriod);
            scheduler.scheduleEvent(entity,entity.createAnimationAction(entity,0), entity.getAnimationPeriod());

            break;

         case MINER_NOT_FULL:
            scheduler.scheduleEvent(entity, entity.createActivityAction(entity,world, imageStore), actionPeriod);
            scheduler.scheduleEvent(entity,entity.createAnimationAction(entity,0), entity.getAnimationPeriod());

            break;

         case ORE:
            scheduler.scheduleEvent(entity, entity.createActivityAction(entity,world, imageStore), actionPeriod);

            break;

         case ORE_BLOB:
            scheduler.scheduleEvent(entity, entity.createActivityAction(entity,world, imageStore), actionPeriod);
            scheduler.scheduleEvent(entity,entity.createAnimationAction(entity,0), entity.getAnimationPeriod());

            break;

         case QUAKE:
            scheduler.scheduleEvent(entity, entity.createActivityAction(entity,world, imageStore), actionPeriod);
            scheduler.scheduleEvent(entity,entity.createAnimationAction(entity,QUAKE_ANIMATION_REPEAT_COUNT), entity.getAnimationPeriod());

            break;

         case VEIN:
            scheduler.scheduleEvent(entity, entity.createActivityAction(entity,world, imageStore), actionPeriod);
            break;

         default:
      }
   }

   //Move to MinerNotFull Class and make it PRIVATE
   public boolean transformNotFull(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore)
   {
      if (resourceCount >= resourceLimit)
      {
         //Kattia - Alert
         Entity miner = Functions.createMinerFull(id, resourceLimit, position, actionPeriod, animationPeriod, images);

         world.removeEntity(entity);
         scheduler.unscheduleAllEvents(entity);
         world.addEntity(miner);
         scheduleActions(miner, scheduler, world, imageStore);

         return true;
      }

      return false;
   }

   /*
   //Move to MinerFull
   public void transformFull(Entity entity, WorldModel world,EventScheduler scheduler, ImageStore imageStore)
   {
      //Kattia - Alert
      Entity miner = Functions.createMinerNotFull(id, resourceLimit, position, actionPeriod, animationPeriod, images);

      world.removeEntity(entity);
      scheduler.unscheduleAllEvents(entity);
      world.addEntity(miner);
      scheduleActions(miner, scheduler, world, imageStore);
   }
*/
   //Move to MinerNotFull and make private
   public boolean moveToNotFull(Entity miner, WorldModel world, Entity target, EventScheduler scheduler)
   {
      //Kattia - Alert
      if (Functions.adjacent(miner.position, target.position))
      {
         miner.resourceCount += 1;
         world.removeEntity(target);
         scheduler.unscheduleAllEvents(target);

         return true;
      }
      else
      {
         Point nextPos = nextPositionMiner(world, target.position);

         if (!miner.position.equals(nextPos))
         {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            world.moveEntity(miner, nextPos);
         }
         return false;
      }
   }

   /*
   //Move to MinerFull and make private
   public boolean moveToFull(Entity miner, WorldModel world, Entity target, EventScheduler scheduler)
   {
      //Kattia - Alert
      if (Functions.adjacent(miner.position, target.position))
      {
         return true;
      }
      else
      {
         Point nextPos = nextPositionMiner(world, target.position);

         if (!miner.position.equals(nextPos))
         {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            world.moveEntity(miner,nextPos);
         }
         return false;
      }
   }
*/

   //Move to OreBlob and make private
   public boolean moveToOreBlob(Entity blob, WorldModel world,Entity target, EventScheduler scheduler)
   {
      //Kattia - Alert
      if (Functions.adjacent(blob.position, target.position))
      {
         world.removeEntity(target);
         scheduler.unscheduleAllEvents(target);
         return true;
      }
      else
      {
         Point nextPos = nextPositionOreBlob(world, target.position);

         if (!blob.position.equals(nextPos))
         {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            world.moveEntity(blob,nextPos);
         }
         return false;
      }
   }

   //MinerFull AND MinerNotFull uses this send to Miner interface?
    // Called by the move functions this should be PRIVATE
   public Point nextPositionMiner(WorldModel world, Point destPos)
   {
      int horiz = Integer.signum(destPos.x - this.position.x);
      Point newPos = new Point(this.position.x + horiz,
              this.position.y);

      if (horiz == 0 || world.isOccupied(newPos))
      {
         int vert = Integer.signum(destPos.y - this.position.y);
         newPos = new Point(this.position.x,
                 this.position.y + vert);

         if (vert == 0 || world.isOccupied(newPos))
         {
            newPos = position;
         }
      }

      return newPos;
   }

   //OreBlob class and make private
   public Point nextPositionOreBlob(WorldModel world,Point destPos)
   {
      int horiz = Integer.signum(destPos.x - this.position.x);
      Point newPos = new Point(this.position.x + horiz,
              this.position.y);

      Optional<Entity> occupant = world.getOccupant(newPos);

      if (horiz == 0 ||
              (occupant.isPresent() && !(occupant.get().kind == EntityKind.ORE)))
      {
         int vert = Integer.signum(destPos.y - this.position.y);
         newPos = new Point(this.position.x, this.position.y + vert);
         occupant = world.getOccupant(newPos);

         if (vert == 0 ||
                 (occupant.isPresent() && !(occupant.get().kind == EntityKind.ORE)))
         {
            newPos = position;
         }
      }

      return newPos;
   }


   //Constructor for Animation - change and move to Aniamtion
   public Action createAnimationAction(Entity entity, int repeatCount)
   {
       return new Animation(entity,repeatCount);
      //return new Action(ActionKind.ANIMATION, entity, null, null, repeatCount);
   }

   //Constructor for Activity - change and move to Activity
   public Action createActivityAction(Entity entity, WorldModel world, ImageStore imageStore)
   {
       return new Activity(entity, world, imageStore);
      //return new Action(ActionKind.ACTIVITY, entity, world, imageStore, 0);
   }

}
