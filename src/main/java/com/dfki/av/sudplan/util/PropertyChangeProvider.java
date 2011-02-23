/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.util;

import java.beans.PropertyChangeListener;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public interface PropertyChangeProvider {
  public void addPropertyChangeListener(final PropertyChangeListener listener);
  public void removePropertyChangeListener(final PropertyChangeListener listener);
  public void addPropertyChangeListener(final String propertyName,final PropertyChangeListener listener);
  public void removePropertyChangeListener(String propertyName,PropertyChangeListener listener);
}
