/*
 *  Camera.java 
 *
 *  Created by DFKI AV on 24.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.camera;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface Camera {

    /**
     * Returns the latitude position of the camera in degrees.
     *
     * @return the latitude in degrees to return.
     */
    public double getLatitude();

    /**
     * Returns the longitude position of the camera in degrees.
     *
     * @return the longitude in degrees to return.
     */
    public double getLongitude();

    /**
     * Returns the altitude position of the camera in degrees.
     *
     * @return the altidue in meter to return.
     */
    public double getAltitude();

    /**
     * Returns the viewing direction of the camera.
     *
     * @return the viewing direction to return.
     */
    public Vector3D getViewingDirection();

    /**
     * Set the viewing direction for the camera.
     *
     * @param direction the viewing direction {@link Vector3D} to set.
     */
    public void setViewingDirection(Vector3D direction);

    /**
     * Return the
     * <code>BoundingVolume</code> of the camera.
     *
     * @return the {@link BoundingVolume} of the camera to return.
     */
    public BoundingVolume getBoundingVolume();
}
