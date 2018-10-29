import processing.core.PImage;

import java.util.List;

public class BlackSmith implements Entity
{
    public static final String SMITH_KEY = "blacksmith";
    public static final int SMITH_NUM_PROPERTIES = 4;
    public static final int SMITH_ID = 1;
    public static final int SMITH_COL = 2;
    public static final int SMITH_ROW = 3;

    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    BlackSmith(String id, Point position,
           List<PImage> images)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;

    }

    public Point getPosition(){return position;}

    public List<PImage> getImages(){return images;}

    public int getImageIndex(){return imageIndex;}

    public void setPosition(Point inPos) {position = inPos;}

    public void nextImage()
    {
        imageIndex = (imageIndex + 1) % images.size();
    }


}
