/*
 *  VisualizationComponent.java 
 *
 *  Created by DFKI AV on 15.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

import com.dfki.av.sudplan.camera.BoundingVolume;
import com.dfki.av.sudplan.camera.Camera;
import com.dfki.av.sudplan.camera.CameraListener;
import java.beans.PropertyChangeListener;

/**
 * The interface to the visualization of the 3-D globe component. 
 * 
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface VisualizationComponent extends PropertyChangeListener{

    /**
     * Adds a layer to the vizualization component. Depending on the
     * implementation which object types are being supported to add.
     * 
     * @param layer the layer to add.
     * @throws IllegalArgumentException If <code>layer</code> is null.
     */
    public void addLayer(final Object layer);

    /**
     * Removes a layer from the visualization component. Depending on 
     * the implementation which layer can be removed. Also depending on the 
     * implementation is which object types are being supported.
     * 
     * @param layer the layer to remove.
     * @throws IllegalArgumentException If <code>layer</code> is null.
     */
    public void removeLayer(final Object layer);

    /**
     * Removes all layers from the visualization component. Depending on
     * the implementation.
     * <p>
     * Note: May be it is important to keep the basic layers.
     */
    public void removeAllLayers();
    
    /**
     * Returns an object of type {@link Camera} containing necessary 
     * information about the current camera view.
     * 
     * @return the camera to return.
     */
    public Camera getCamera();

    /**
     * Sets the camera view of the 3-D visualization component.
     * 
     * @param c the camera to set.
     * @throws IllegalArgumentException If <code>c</code> is null.
     */
    public void setCamera(Camera c);

    /**
     * Adds a {@link CameraListener} to the visualization component.
     * <p>
     * Note: The listener only gets changes of the camera view.
     * 
     * @param cl the {@link CameraListener} to add.
     * @throws IllegalArgumentException If <code>cl</code> is null.
     */
    public void addCameraListener(CameraListener cl);

    /**
     * Removes the {@link CameraListener}.
     * 
     * @param cl the {@link CameraListener} to remove.
     * @throws IllegalArgumentException If <code>cl</code> is null.
     */
    public void removeCameraListener(CameraListener cl);
    
    /**
     * Sets the bounding volume that should be contained in the 
     * camera view.
     * 
     * @param bv the {@link BoundingVolume} to set.
     * @throws IllegalArgumentException If <code>bv</code> is null.
     */
    public void setBoundingVolume(BoundingVolume bv);
    
}
