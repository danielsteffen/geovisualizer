/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.layer;

import com.sun.j3d.loaders.SceneBase;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public abstract class AbstractLayer implements Layer{

  @Override
  public int getBoundingBox() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getTransparency() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isEnabled() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isVisible() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBoundingBox(int boundingBox) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setEnabled(boolean enabled) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTransparency(int transparency) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setVisible(boolean isVisible) {
    throw new UnsupportedOperationException("Not supported yet.");
  }


}
