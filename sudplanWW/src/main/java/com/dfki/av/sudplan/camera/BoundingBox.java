/*
 *  BoundingBox.java 
 *
 *  Created by DFKI AV on 29.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.camera;

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
     * Creates a new <code>BoundingBox</code>.
     * <p>
     * Note: The angles are assumed to be normalized to +/- 90 degrees latitude 
     * and +/- 180 degrees longitude.

     * @param minLat the bounding box's minimum latitude in degrees.
     * @param maxLat the bounding box's maximum latitude in degrees.
     * @param minLon the bounding box's minimum longitude in degrees.
     * @param maxLon the bounding box's minimum longitude in degrees.
     */
    public BoundingBox(double minLat, double maxLat, double minLon, double maxLon) {
        this.sector = Sector.fromDegrees(minLat, maxLat, minLon, maxLon);
    }

    @Override
    public Sector getSector() {
        return this.sector;
    }
}
