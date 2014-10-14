/*
 * GeometryType.java
 *
 * Created by DFKI AV on 20.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.io;

/**
 * A {@link Enum} which represents the geometry type which the data is
 * corresponded to.
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public enum GeometryType {

    /**
     * List of points which represents a polyline.
     */
    POLYLINE,
    /**
     * List of points which represents a polygon.
     */
    POLYGON,
    /**
     * Single point.
     */
    POINT,
    /**
     * Undefined geometry type.
     */
    UNDEFINED;
}
