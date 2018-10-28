import processing.core.PImage;
import java.util.List;

public interface Entity{
   Point getPosition();
   List<PImage> getImages();
   int getImageIndex();
   void setPosition(Point inPos);
   void nextImage();
}
