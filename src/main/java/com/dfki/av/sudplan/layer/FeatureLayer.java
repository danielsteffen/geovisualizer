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
public interface FeatureLayer extends Layer{

  //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: perhaps better to replace by a generic or a concrete super datastructure (independend of Java3D)
  public void setDataObject(final Object dataObject);
  public Object getDataObject();

}
