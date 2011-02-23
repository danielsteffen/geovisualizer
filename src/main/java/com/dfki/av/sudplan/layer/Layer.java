/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.layer;

import com.dfki.av.sudplan.util.PropertyChangeProvider;
import com.sun.j3d.loaders.Scene;
import javax.media.j3d.BoundingBox;
import javax.swing.Icon;


/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public interface Layer extends PropertyChangeProvider{

  public String getName();
  public void setName(final String name);

  public Icon getIcon();
  public void setIcon(final Icon icon);

  public boolean isVisible();
  public void setVisible(final boolean isVisible);

  public double getTransparency();
  public void setTransparency(final double transparency);
  
  public BoundingBox getBoundingBox();
  public void setBoundingBox(final BoundingBox boundingBox);

  public boolean isEnabled();
  public void setEnabled(final boolean enabled);

  //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: perhaps better to replace by a generic or a concrete super datastructure (independend of Java3D)
  public void setDataObject(final Scene dataObject);
  public Scene getDataObject();
}
