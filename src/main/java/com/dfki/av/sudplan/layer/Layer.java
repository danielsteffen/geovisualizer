/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.layer;

import com.sun.j3d.loaders.Scene;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public interface Layer {

  public boolean isVisible();
  public void setVisible(final boolean isVisible);

  public int getTransparency();
  public void setTransparency(final int transparency);

  //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: right datatype
  public int getBoundingBox();
  public void setBoundingBox(final int boundingBox);

  public boolean isEnabled();
  public void setEnabled(final boolean enabled);

  //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: perhaps better to replace by a generic or a concrete super datastructure (independend of Java3D)
  public void setDataObject(final Scene dataObject);
  public Scene getDataObject();
}
