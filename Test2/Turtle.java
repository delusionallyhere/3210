import java.util.ArrayList;

public class Turtle {

   private int x, y;
   private int angle;
   private boolean penDown;

   private ArrayList<Segment> list;

   public Turtle() {
      x = 0;  y = 0;
      angle = 0;
      penDown = true;

      list = new ArrayList<Segment>();
   }

   public void up() {
      penDown = false;
   }

   public void down() {
      penDown = true;
   }

   public void move() {
      int oldX = x, oldY = y;
      if ( angle == 0 ) x++;
      else if ( angle == 90 ) y++;
      else if ( angle == 180 ) x--;
      else if ( angle == 270 ) y--;
      else {
         System.out.println("illegal angle");
         System.exit(1);
      }

      if ( penDown )
         list.add( new Segment( oldX, oldY, x, y ) );
   }

   public void left() {
      angle = angle + 90;
      if ( angle == 360 ) angle = 0;
   }

   public void right() {
      angle = angle - 90;
      if ( angle == -90 ) angle = 270;
   }

   public void draw( Camera cam ) {
      for (int k=0; k<list.size(); k++) {
         list.get(k).draw( cam );
      }
   }

}
