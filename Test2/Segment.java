public class Segment {
   private int x1, y1, x2, y2;

   public Segment( int px, int py, int qx, int qy ) {
      x1 = px;  y1 = py;
      x2 = qx;  y2 = qy;
   }

   public void draw( Camera cam ) {
      cam.drawLine( x1, y1, x2, y2 );
   }

   public String toString() {
      return "(" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ")";
   }

}
