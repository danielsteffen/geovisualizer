/*
 *  VisualisationComponent.java 
 *
 *  Created by DFKI AV on 15.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface VisualisationComponent {
    
  //public Component getDnDComponent();
  public void addContent(final Object scene);
  public void enableDirectedLight(boolean enabled);
  public void removeContent(final Object dataObject);
  public void goToHome();
  public void goTo(double latitude, double longitude, double elevation);
  public void setModeZoom();
  public void setModePan();
  public void setModeRotate();
  public void setModeCombined();
  //public Camera get3dCamera();
  //public Camera get2dCamera();
  //public Camera getGeographicCamera();  
  
}
