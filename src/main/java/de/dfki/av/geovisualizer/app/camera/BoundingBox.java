/*
 *  BoundingBox.java 
 *
 *  Created by DFKI AV on 29.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.camera;

import gov.nasa.worldwind.geom.Sector;

/**
 * The bounding box to set.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class BoundingBox implements BoundingVolume {

    /*
     * The sector defining the bounding box.
     */
    private Sector sector;

    /**
     * Creates a new
     * <code>BoundingBox</code>. <p> Note: The angles are assumed to be
     * normalized to +/- 90 degrees latitude and +/- 180 degrees longitude.
     *
     * @param minLat the bounding box's minimum latitude in degrees.
     * @param maxLat the bounding box's maximum latitude in degrees.
     * @param minLon the bounding box's minimum longitude in degrees.
     * @param maxLon the bounding box's minimum longitude in degrees.
     */
    public BoundingBox(double minLat, double maxLat, double minLon, double maxLon) {
        this(Sector.fromDegrees(minLat, maxLat, minLon, maxLon));
    }

    /**
     * Creates a new
     * <code>BoundingBox</code>.
     *
     * @param s the {@link Sector} of the {@link BoundingBox} to set.
     * @throws IllegalArgumentException if <code>s</code> is set      * to <code>null</code>
     */
    public BoundingBox(Sector s) {
        if (s == null) {
            throw new IllegalArgumentException("Parameter Sector is null.");
        }
        this.sector = new Sector(s);
    }

    @Override
    public Sector getSector() {
        return this.sector;
    }

    @Override
    public double getMinLatitude() {
        return this.sector.getMinLatitude().getDegrees();
    }

    @Override
    public double getMaxLatitude() {
        return this.sector.getMaxLatitude().getDegrees();
    }

    @Override
    public double getMinLongitude() {
        return this.sector.getMinLongitude().getDegrees();
    }

    @Override
    public double getMaxLongitude() {
        return this.sector.getMaxLongitude().getDegrees();
    }
}
