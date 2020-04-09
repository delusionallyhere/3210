/*  this class allows
    drawing of simple line
    segments
*/

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.io.*;

public class TurtleViewer extends Basic
{
  public TurtleViewer( String title, int ulx, int uly, int pw, int ph ) {
    super(title,ulx,uly,pw,ph);

    setBackgroundColor( new Color( 128, 128, 200 ) );

    // tree display
    cameras.add( new Camera( 25, 45, pw-55, ph-75,
                             -50, 50, -50, 50,
                             new Color( 200, 200, 200 )  ) );

    super.start();
  }

  public void step()
  {
    // display
    Camera cam = cameras.get(0);
    cam.activate();
    cam.setColor( Color.black );

    Node.turtle.draw( cam );

  }// step

}// TurtleViewer
