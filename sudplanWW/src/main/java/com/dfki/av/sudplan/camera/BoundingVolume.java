/*
 *  BoundingVolume.java 
 *
 *  Created by DFKI AV on 29.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.camera;

import gov.nasa.worldwind.geom.Sector;

/**
 * The interface bouding volume. 
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface BoundingVolume {

    /**
     * The {@link Sector} defining the bounding volume.
     * @return the {@link Sector} to return.
     */
    public Sector getSector();
}
