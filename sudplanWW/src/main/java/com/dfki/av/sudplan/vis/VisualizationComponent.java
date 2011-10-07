/*
 *  VisualizationComponent.java 
 *
 *  Created by DFKI AV on 15.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

import com.dfki.av.sudplan.camera.Camera;
import com.dfki.av.sudplan.camera.CameraListener;
import java.beans.PropertyChangeListener;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface VisualizationComponent extends PropertyChangeListener{

    /**
     * 
     * @param layer 
     * @throws IllegalArgumentException If <code>layer</code> is null.
     */
    public void addLayer(final Object layer);

    /**
     * 
     * @param layer 
     * @throws IllegalArgumentException If <code>layer</code> is null.
     */
    public void removeLayer(final Object layer);

    /**
     * 
     * @return 
     */
    public Camera getCamera();

    /**
     * 
     * @param c 
     * @throws IllegalArgumentException If <code>c</code> is null.
     */
    public void setCamera(Camera c);

    /**
     * 
     * @param cl 
     * @throws IllegalArgumentException If <code>cl</code> is null.
     */
    public void addCameraListener(CameraListener cl);

    /**
     * 
     * @param cl 
     * @throws IllegalArgumentException If <code>cl</code> is null.
     */
    public void removeCameraListener(CameraListener cl);
}
