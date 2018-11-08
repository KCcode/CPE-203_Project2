import processing.core.PImage;
import java.util.List;

public class Entity {

   private String id;
   private Point position;
   private List<PImage> images;
   private int imageIndex;

   Entity(String id, Point position, List<PImage> images) {
      this.id = id;
      this.position = position;
      this.images = images;
      this.imageIndex = 0;
   }

   public void setId(String inId) {
      id = inId;
   }

   public void setPosition(Point inPos) {
      position = inPos;
   }

   public void setImages(List<PImage> inImages) {
      images = inImages;
   }

   public void setImageIndex(int inImageIndex) {
      imageIndex = inImageIndex;
   }

   public String getId() {
      return id;
   }

   public Point getPosition() {
      return position;
   }

   public List<PImage> getImages() {
      return images;
   }

   public int getImageIndex() {
      return imageIndex;
   }

   //NextImage only for classes that have animations
   public void nextImage() {
      imageIndex = (imageIndex + 1) % images.size();
   }
}


