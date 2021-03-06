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


   private static final String MINER_KEY = "miner";
   private static final int MINER_NUM_PROPERTIES = 7;
   private static final int MINER_ID = 1;
   private static final int MINER_COL = 2;
   private static final int MINER_ROW = 3;
   private static final int MINER_LIMIT = 4;
   private static final int MINER_ACTION_PERIOD = 5;
   private static final int MINER_ANIMATION_PERIOD = 6;


   public static PImage getCurrentImage(Object entity)
   {
      if (entity instanceof Background)
      {
         return ((Background)entity).getImages().get(((Background)entity).getImageIndex());
      }
      else if (entity instanceof Entity)
      {
         return ((Entity)entity).getImages().get(((Entity)entity).getImageIndex());
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
            case MINER_KEY:
               return parseMiner(properties, world, imageStore);
            case Obstacle.OBSTACLE_KEY:
               return parseObstacle(properties, world, imageStore);
            case Ore.ORE_KEY:
               return parseOre(properties, world, imageStore);
            case BlackSmith.SMITH_KEY:
               return parseSmith(properties, world, imageStore);
            case Vein.VEIN_KEY:
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
      if (properties.length == MINER_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[MINER_COL]),
                 Integer.parseInt(properties[MINER_ROW]));

         MinerNotFull entity = new MinerNotFull(properties[MINER_ID], pt, imageStore.getImageList(MINER_KEY),
                 Integer.parseInt(properties[MINER_LIMIT]),0,
                 Integer.parseInt(properties[MINER_ACTION_PERIOD]),
                 Integer.parseInt(properties[MINER_ANIMATION_PERIOD]));

         world.tryAddEntity(entity);
      }

      return properties.length == MINER_NUM_PROPERTIES;
   }

   private static boolean parseObstacle(String [] properties, WorldModel world,
                                       ImageStore imageStore)
   {
      if (properties.length == Obstacle.OBSTACLE_NUM_PROPERTIES)
      {
         Point pt = new Point(
                 Integer.parseInt(properties[Obstacle.OBSTACLE_COL]),
                 Integer.parseInt(properties[Obstacle.OBSTACLE_ROW]));

         Obstacle entity = new Obstacle(properties[Obstacle.OBSTACLE_ID],
                 pt, imageStore.getImageList(Obstacle.OBSTACLE_KEY));


         world.tryAddEntity(entity);
      }

      return properties.length == Obstacle.OBSTACLE_NUM_PROPERTIES;
   }

   public static boolean parseOre(String [] properties, WorldModel world,
                                  ImageStore imageStore)
   {
      if (properties.length == Ore.ORE_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Ore.ORE_COL]),
                 Integer.parseInt(properties[Ore.ORE_ROW]));

         Ore entity = new Ore(properties[Ore.ORE_ID], pt, imageStore.getImageList(Ore.ORE_KEY),
                 Integer.parseInt(properties[Ore.ORE_ACTION_PERIOD]));

         world.tryAddEntity(entity);
      }

      return properties.length == Ore.ORE_NUM_PROPERTIES;
   }

   public static boolean parseSmith(String [] properties, WorldModel world,
                                    ImageStore imageStore)
   {
      if (properties.length == BlackSmith.SMITH_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[BlackSmith.SMITH_COL]),
                 Integer.parseInt(properties[BlackSmith.SMITH_ROW]));

         BlackSmith entity = new BlackSmith(properties[BlackSmith.SMITH_ID],
                 pt, imageStore.getImageList(BlackSmith.SMITH_KEY));

         world.tryAddEntity(entity);
      }

      return properties.length == BlackSmith.SMITH_NUM_PROPERTIES;
   }

   public static boolean parseVein(String [] properties, WorldModel world,
                                   ImageStore imageStore)
   {
      if (properties.length == Vein.VEIN_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Vein.VEIN_COL]),
                 Integer.parseInt(properties[Vein.VEIN_ROW]));

         Vein entity = new Vein(properties[Vein.VEIN_ID],
                 pt,imageStore.getImageList(Vein.VEIN_KEY),Integer.parseInt(properties[Vein.VEIN_ACTION_PERIOD]));

         world.tryAddEntity(entity);
      }

      return properties.length == Vein.VEIN_NUM_PROPERTIES;
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

}
