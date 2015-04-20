/*
 * VisualizationComponent.java
 *
 * Created by DFKI AV on 15.09.2011.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.vis;

import de.dfki.av.geovisualizer.app.camera.BoundingVolume;
import de.dfki.av.geovisualizer.app.camera.Camera;
import de.dfki.av.geovisualizer.app.camera.CameraListener;
import java.beans.PropertyChangeListener;

/**
 * The interface to the visualization of the 3-D globe component.
 */
public interface VisualizationComponent extends PropertyChangeListener {

    /**
     * Adds a layer to the visualization component. Depending on the
     * implementation which object types are being supported to add.
     *
     * @param layer the layer to add.
     * @throws IllegalArgumentException If <code>layer</code> is null.
     */
    void addLayer(final Object layer);

    /**
     * Removes a layer from the visualization component. Depending on the
     * implementation which layer can be removed. Also depending on the
     * implementation is which object types are being supported.
     *
     * @param layer the layer to remove.
     * @throws IllegalArgumentException If <code>layer</code> is null.
     */
    void removeLayer(final Object layer);

    /**
     * Removes all layers from the visualization component. Depending on the
     * implementation. <p> Note: May be it is important to keep the basic
     * layers.
     */
    void removeAllLayers();

    /**
     * Returns an object of type {@link Camera} containing necessary information
     * about the current camera view.
     *
     * @return the camera to return.
     */
    Camera getCamera();

    /**
     * Sets the camera view of the 3-D visualization component.
     *
     * @param c the camera to set.
     * @throws IllegalArgumentException If <code>c</code> is null.
     */
    void setCamera(Camera c);

    /**
     * Adds a {@link CameraListener} to the visualization component. <p> Note:
     * The listener only gets changes of the camera view.
     *
     * @param cl the {@link CameraListener} to add.
     * @throws IllegalArgumentException If <code>cl</code> is null.
     */
    void addCameraListener(CameraListener cl);

    /**
     * Removes the {@link CameraListener}.
     *
     * @param cl the {@link CameraListener} to remove.
     * @throws IllegalArgumentException If <code>cl</code> is null.
     */
    void removeCameraListener(CameraListener cl);

    /**
     * Add a listener for changes of type.
     * {@link de.dfki.av.geovisualizer.core.IVisAlgorithm#PROGRESS_PROPERTY}
     *
     * @param listener the {@link PropertyChangeListener} to add.
     */
    void addProgressListener(PropertyChangeListener listener);

    /**
     * Remove a {@link PropertyChangeListener} for property
     * {@link de.dfki.av.geovisualizer.core.IVisAlgorithm#PROGRESS_PROPERTY}.
     *
     * @param listener the {@link PropertyChangeListener} to remove.
     */
    void removeProgressListener(PropertyChangeListener listener);

    /**
     * Sets the bounding volume that should be contained in the camera view and
     * animates the view to the desired area.
     *
     * @param bv the {@link BoundingVolume} to set.
     * @throws IllegalArgumentException If <code>bv</code> is null.
     */
    void setBoundingVolume(BoundingVolume bv);

    /**
     * Returns the {@link BoundingVolume} contained by the camera view.
     *
     * @return the bounding volume to return.
     */
    BoundingVolume getBoundingVolume();
}
