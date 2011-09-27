/*
 *  VisualizationComponent.java 
 *
 *  Created by DFKI AV on 15.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

import com.dfki.av.sudplan.camera.Camera;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface VisualizationComponent {
    
  public void addLayer(final Object layer);
  public void removeLayer(final Object layer);
  public Camera getCamera();  
  public void setCamera(Camera c);
  public void setModeZoom();
  public void setModePan();
  public void setModeRotate();
  public void setModeCombined();
}
