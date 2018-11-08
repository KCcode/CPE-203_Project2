import processing.core.PImage;

import java.util.List;

public class BlackSmith extends Entity
{
    public static final String SMITH_KEY = "blacksmith";
    public static final int SMITH_NUM_PROPERTIES = 4;
    public static final int SMITH_ID = 1;
    public static final int SMITH_COL = 2;
    public static final int SMITH_ROW = 3;



    BlackSmith(String id, Point position,
           List<PImage> images)
    {
        super(id, position, images);

    }

}
