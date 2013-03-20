/*
 *  BoundingVolume.java 
 *
 *  Created by DFKI AV on 29.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.camera;

import gov.nasa.worldwind.geom.Sector;

/**
 * The interface bouding volume.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface BoundingVolume {

    /**
     * The {@link Sector} defining the bounding volume.
     *
     * @return the {@link Sector} to return.
     */
    public Sector getSector();

    /**
     * Return the minimum latitude.
     *
     * @return the {@link BoundingVolume}s minimum latitude in degrees.
     */
    public double getMinLatitude();

    /**
     * Return the maximum latitude.
     *
     * @return the {@link BoundingVolume}s maximum latitude in degrees.
     */
    public double getMaxLatitude();

    /**
     * Return the minimum longitude.
     *
     * @return the {@link BoundingVolume}s minimum longitude in degrees.
     */
    public double getMinLongitude();

    /**
     * Return the maximum longitude.
     *
     * @return the {@link BoundingVolume}s maximum longitude in degrees.
     */
    public double getMaxLongitude();
}
