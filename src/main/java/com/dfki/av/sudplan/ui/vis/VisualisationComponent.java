/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.ui.vis;

import com.sun.j3d.loaders.Scene;
import java.awt.Component;
import javax.media.j3d.BoundingBox;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public interface VisualisationComponent{

  public void gotoToHome();
  public Component getDnDComponent();
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: should be refactored so that it is independant of Java3D. How to interact with the layer component ?
  public void addContent(final Scene scene);
  public void enableDirectedLight(boolean enabled);
  public void removContent(final Scene dataObject);
  public void gotoBoundingBox(final BoundingBox boundingBox);
  public void gotoPoint(final Tuple3f point);
  public void gotoPoint(final Tuple3d point);
  public void setModeZoom();
  public void setModePan();
  public void setModeRotate();
  public void setModeCombined();
}
