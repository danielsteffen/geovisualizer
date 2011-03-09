/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.ui.vis;

import com.dfki.av.sudplan.camera.Camera;
import java.awt.Component;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public interface VisualisationComponent{
  
  public Component getDnDComponent();
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: should be refactored so that it is independant of Java3D. How to interact with the layer component ?
  public void addContent(final Object scene);
  public void enableDirectedLight(boolean enabled);
  public void removContent(final Object dataObject);
  public void gotoToHome();
  public void setModeZoom();
  public void setModePan();
  public void setModeRotate();
  public void setModeCombined();
  public Camera get3dCamera();
  public Camera get2dCamera();
}
