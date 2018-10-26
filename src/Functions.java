import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import processing.core.PImage;
import processing.core.PApplet;

final class Functions
{
   private static final int COLOR_MASK = 0xffffff;
   private static final int KEYED_IMAGE_MIN = 5;
   private static final int KEYED_RED_IDX = 2;
   private static final int KEYED_GREEN_IDX = 3;
   private static final int KEYED_BLUE_IDX = 4;

   private static final int PROPERTY_KEY = 0;

   private static final String BGND_KEY = "background";
   private static final int BGND_NUM_PROPERTIES = 4;
   private static final int BGND_ID = 1;
   private static final int BGND_COL = 2;
   private static final int BGND_ROW = 3;


   public static PImage getCurrentImage(Object entity)
   {
      if (entity instanceof Background)
      {
         return ((Background)entity).getImages().get(((Background)entity).getImageIndex());
      }
      else if (entity instanceof Entity)
      {
         return ((Entity)entity).getImages().get(((Entity)entity).getImageIndex());
         //return ((Entity)entity).images.get(((Entity)entity).imageIndex);
      }
      else
      {
         throw new UnsupportedOperationException(
            String.format("getCurrentImage not supported for %s",
            entity));
      }
   }

   public static boolean adjacent(Point p1, Point p2)
   {
      return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) ||
              (p1.y == p2.y && Math.abs(p1.x - p2.x) == 1);
   }

   public static void loadImages(Scanner in, ImageStore imageStore, PApplet screen)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            processImageLine(imageStore.getImages(), in.nextLine(), screen);
         }
         catch (NumberFormatException e)
         {
            System.out.println(String.format("Image format error on line %d",
                    lineNumber));
         }
         lineNumber++;
      }
   }

   public static void processImageLine(Map<String, List<PImage>> images, String line, PApplet screen)
   {
      String[] attrs = line.split("\\s");
      if (attrs.length >= 2)
      {
         String key = attrs[0];
         PImage img = screen.loadImage(attrs[1]);
         if (img != null && img.width != -1)
         {
            List<PImage> imgs = getImages(images, key);
            imgs.add(img);

            if (attrs.length >= KEYED_IMAGE_MIN)
            {
               int r = Integer.parseInt(attrs[KEYED_RED_IDX]);
               int g = Integer.parseInt(attrs[KEYED_GREEN_IDX]);
               int b = Integer.parseInt(attrs[KEYED_BLUE_IDX]);
               setAlpha(img, screen.color(r, g, b), 0);
            }
         }
      }
   }

   public static List<PImage> getImages(Map<String, List<PImage>> images,
                                        String key)
   {
      List<PImage> imgs = images.get(key);
      if (imgs == null)
      {
         imgs = new LinkedList<>();
         images.put(key, imgs);
      }
      return imgs;
   }

   public static void setAlpha(PImage img, int maskColor, int alpha)
   {
      int alphaValue = alpha << 24;
      int nonAlpha = maskColor & COLOR_MASK;
      img.format = PApplet.ARGB;
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         if ((img.pixels[i] & COLOR_MASK) == nonAlpha)
         {
            img.pixels[i] = alphaValue | nonAlpha;
         }
      }
      img.updatePixels();
   }

   public static void load(Scanner in, WorldModel world, ImageStore imageStore)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            if (!processLine(in.nextLine(), world, imageStore))
            {
               System.err.println(String.format("invalid entry on line %d",
                       lineNumber));
            }
         }
         catch (NumberFormatException e)
         {
            System.err.println(String.format("invalid entry on line %d",
                    lineNumber));
         }
         catch (IllegalArgumentException e)
         {
            System.err.println(String.format("issue on line %d: %s",
                    lineNumber, e.getMessage()));
         }
         lineNumber++;
      }
   }

   public static boolean processLine(String line, WorldModel world,
                                     ImageStore imageStore)
   {
      String[] properties = line.split("\\s");
      if (properties.length > 0)
      {
         switch (properties[PROPERTY_KEY])
         {
            case BGND_KEY:
               return parseBackground(properties, world, imageStore);
            case Miner.MINER_KEY:
            //case MinerFull.MINER_KEY:
               return parseMiner(properties, world, imageStore);
            case Entity.OBSTACLE_KEY:
               return parseObstacle(properties, world, imageStore);
            case Entity.ORE_KEY:
               return parseOre(properties, world, imageStore);
            case Entity.SMITH_KEY:
               return parseSmith(properties, world, imageStore);
            case Entity.VEIN_KEY:
               return parseVein(properties, world, imageStore);
         }
      }

      return false;
   }

   public static boolean parseBackground(String [] properties,
                                         WorldModel world, ImageStore imageStore)
   {
      if (properties.length == BGND_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
                 Integer.parseInt(properties[BGND_ROW]));
         String id = properties[BGND_ID];
         world.setBackground(pt,new Background(id, imageStore.getImageList(id)));
      }

      return properties.length == BGND_NUM_PROPERTIES;
   }

   public static boolean parseMiner(String [] properties, WorldModel world,
                                    ImageStore imageStore)
   {
      if (properties.length == Miner.MINER_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Miner.MINER_COL]),
                 Integer.parseInt(properties[Miner.MINER_ROW]));
         Entity entity = createMinerNotFull(properties[Miner.MINER_ID],
                 Integer.parseInt(properties[Miner.MINER_LIMIT]),
                 pt,
                 Integer.parseInt(properties[Miner.MINER_ACTION_PERIOD]),
                 Integer.parseInt(properties[Miner.MINER_ANIMATION_PERIOD]),
                 imageStore.getImageList(Miner.MINER_KEY));
         world.tryAddEntity(entity);
      }

      return properties.length == Miner.MINER_NUM_PROPERTIES;
   }

   private static boolean parseObstacle(String [] properties, WorldModel world,
                                       ImageStore imageStore)
   {
      if (properties.length == Entity.OBSTACLE_NUM_PROPERTIES)
      {
         Point pt = new Point(
                 Integer.parseInt(properties[Entity.OBSTACLE_COL]),
                 Integer.parseInt(properties[Entity.OBSTACLE_ROW]));
         Entity entity = createObstacle(properties[Entity.OBSTACLE_ID],
                 pt, imageStore.getImageList(Entity.OBSTACLE_KEY));
         world.tryAddEntity(entity);
      }

      return properties.length == Entity.OBSTACLE_NUM_PROPERTIES;
   }

   public static boolean parseOre(String [] properties, WorldModel world,
                                  ImageStore imageStore)
   {
      if (properties.length == Entity.ORE_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Entity.ORE_COL]),
                 Integer.parseInt(properties[Entity.ORE_ROW]));
         Entity entity = createOre(properties[Entity.ORE_ID],
                 pt, Integer.parseInt(properties[Entity.ORE_ACTION_PERIOD]),
                 imageStore.getImageList(Entity.ORE_KEY));
         world.tryAddEntity(entity);
      }

      return properties.length == Entity.ORE_NUM_PROPERTIES;
   }

   public static boolean parseSmith(String [] properties, WorldModel world,
                                    ImageStore imageStore)
   {
      if (properties.length == Entity.SMITH_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Entity.SMITH_COL]),
                 Integer.parseInt(properties[Entity.SMITH_ROW]));
         Entity entity = createBlacksmith(properties[Entity.SMITH_ID],
                 pt, imageStore.getImageList(Entity.SMITH_KEY));
         world.tryAddEntity(entity);
      }

      return properties.length == Entity.SMITH_NUM_PROPERTIES;
   }

   public static boolean parseVein(String [] properties, WorldModel world,
                                   ImageStore imageStore)
   {
      if (properties.length == Entity.VEIN_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Entity.VEIN_COL]),
                 Integer.parseInt(properties[Entity.VEIN_ROW]));
         Entity entity = createVein(properties[Entity.VEIN_ID],
                 pt,
                 Integer.parseInt(properties[Entity.VEIN_ACTION_PERIOD]),
                 imageStore.getImageList(Entity.VEIN_KEY));
         world.tryAddEntity(entity);
      }

      return properties.length == Entity.VEIN_NUM_PROPERTIES;
   }

   public static Optional<Entity> nearestEntity(List<Entity> entities, Point pos){
      if (entities.isEmpty())
      {
         return Optional.empty();
      }
      else
      {
         Entity nearest = entities.get(0);
         int nearestDistance = distanceSquared(nearest.getPosition(), pos);

         for (Entity other : entities)
         {
            int otherDistance = distanceSquared(other.getPosition(), pos);

            if (otherDistance < nearestDistance)
            {
               nearest = other;
               nearestDistance = otherDistance;
            }
         }

         return Optional.of(nearest);
      }
   }

   public static int distanceSquared(Point p1, Point p2)
   {
      int deltaX = p1.x - p2.x;
      int deltaY = p1.y - p2.y;

      return deltaX * deltaX + deltaY * deltaY;
   }

   public static int clamp(int value, int low, int high)
   {
      return Math.min(high, Math.max(value, low));
   }

   //Move to BlackSmith - Constructor
   public static Entity createBlacksmith(String id, Point position,
                                         List<PImage> images)
   {
      return new Entity(EntityKind.BLACKSMITH, id, position, images,
              0, 0, 0, 0);
   }

   //Move to MinerFull - Constructor
   public static Entity createMinerFull(String id, int resourceLimit,
                                        Point position, int actionPeriod, int animationPeriod,
                                        List<PImage> images)
   {
      return new Entity(EntityKind.MINER_FULL, id, position, images,
              resourceLimit, resourceLimit, actionPeriod, animationPeriod);
   }

   //Move to MinerNotFull - Constructor
   public static Entity createMinerNotFull(String id, int resourceLimit,
                                           Point position, int actionPeriod, int animationPeriod,
                                           List<PImage> images)
   {
      return new Entity(EntityKind.MINER_NOT_FULL, id, position, images,
              resourceLimit, 0, actionPeriod, animationPeriod);
   }

   //Move to Obstacle - Constructor
   public static Entity createObstacle(String id, Point position,
                                       List<PImage> images)
   {
      return new Entity(EntityKind.OBSTACLE, id, position, images,
              0, 0, 0, 0);
   }

   //Move to Ore - Constructor
   public static Entity createOre(String id, Point position, int actionPeriod,
                                  List<PImage> images)
   {
      return new Entity(EntityKind.ORE, id, position, images, 0, 0,
              actionPeriod, 0);
   }

   //Move to OreBlob - Constructor
   public static Entity createOreBlob(String id, Point position,
                                      int actionPeriod, int animationPeriod, List<PImage> images)
   {
      return new Entity(EntityKind.ORE_BLOB, id, position, images,
              0, 0, actionPeriod, animationPeriod);
   }

   //Move to Quake - Constructor
   public static Entity createQuake(Point position, List<PImage> images)
   {
      return new Entity(EntityKind.QUAKE, Entity.QUAKE_ID, position, images,
              0, 0, Entity.QUAKE_ACTION_PERIOD, Entity.QUAKE_ANIMATION_PERIOD);
   }

   //Move to Vein - Constructor
   public static Entity createVein(String id, Point position, int actionPeriod,
                                   List<PImage> images)
   {
      return new Entity(EntityKind.VEIN, id, position, images, 0, 0,
              actionPeriod, 0);
   }
}
