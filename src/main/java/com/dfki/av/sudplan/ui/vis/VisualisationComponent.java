/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.ui.vis;

import com.sun.j3d.loaders.Scene;
import java.awt.Component;

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
  public void addContent(Scene scene);
  public void enableDirectedLight(boolean enabled);
}
