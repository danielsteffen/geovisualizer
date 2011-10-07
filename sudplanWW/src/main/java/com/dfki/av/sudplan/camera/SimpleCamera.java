/*
 *  SimpleCamera.java 
 *
 *  Created by DFKI AV on 27.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.camera;

import gov.nasa.worldwind.geom.Position;

/**
 * Provides a data container of the most interesting data used by any 
 * other component. This camera is not animated.
 * 
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class SimpleCamera implements Camera {

    /**
     * The <code>latitude</code> position of the camera in degrees.
     */
    private double latitude;
    /**
     * The <code>longitude</code> position of the camera in degrees.
     */
    private double longitude;
    /**
     * The <code>altitude</code> position of the camera in meter.
     */
    private double altitude;
    /**
     * The <code>viewingDirection</code> of the camera.
     */
    private Vector3D viewingDirection;

    /**
     * Creates a <code>SimpleCamera</code> object at {@link Position} <code>(0.0, 0.0, 0.0)</code>
     * and a {@link #viewingDirection} of <code>(0.0, 0.0, 0.0)</code>.
     */
    public SimpleCamera() {
        this(0.0, 0.0, 0.0, new Vector3D());
    }

    /**
     * Creates a <code>SimpleCamera</code> object at {@link Position} <code>p</code>
     * and a {@link #viewingDirection} of <code>(0.0, 0.0, 0.0)</code>.
     * 
     * @param p the {@link Position} to set.
     */
    public SimpleCamera(Position p) {
        this(p.getLatitude().getDegrees(),
                p.getLongitude().getDegrees(),
                p.getAltitude(),
                new Vector3D());
    }

    /**
     * Creates a <code>SimpleCamera</code> object at {@link Position} <code>(lat, lon, alt)</code>
     * and a {@link #viewingDirection} of <code>(0.0, 0.0, 0.0)</code>.
     * 
     * @param lat the latitude position in degrees to set.
     * @param lon the longitude position in degrees to set.
     * @param alt the altitude poisition in meter to set.
     */
    public SimpleCamera(double lat, double lon, double alt) {
        this(lat, lon, alt, new Vector3D());
    }

    /**
     * Creates a <code>SimpleCamera</code> object at {@link Position} <code>p</code>
     * and a {@link #viewingDirection} of <code>vec</code>.
     * 
     * @param p the {@link Position} to set.
     * @param vec the viewing direction to set.
     */
    public SimpleCamera(Position p, Vector3D vec) {
        this(p.getLatitude().getDegrees(),
                p.getLongitude().getDegrees(),
                p.getAltitude(),
                vec);
        // TODO <steffen>: Consider p to be null as well!
    }

    /**
     * Creates a <code>SimpleCamera</code> object at {@link Position} <code>(lat, lon, alt)</code>
     * and a {@link #viewingDirection} of <code>vec</code>.
     * <p>
     * Note: The <code>vec</code> is copied by value not by reference.
     * 
     * @param lat the latitude position in degrees to set.
     * @param lon the longitude position in degrees to set.
     * @param alt the altitude poisition in meter to set.
     * @param vec the viewing direction to set.
     * 
     * @throws IllegalArgumentException If <code>vec</code> is null.
     */
    public SimpleCamera(double lat, double lon, double alt, Vector3D vec) {
        this.latitude = lat;
        this.longitude = lon;
        this.altitude = alt;
        if (vec == null) {
            throw new IllegalArgumentException("Vector3D is null.");
        }
        this.viewingDirection = new Vector3D(vec);
    }

    @Override
    public double getLatitude() {
        return this.latitude;
    }

    @Override
    public double getLongitude() {
        return this.longitude;
    }

    @Override
    public double getAltitude() {
        return this.altitude;
    }

    @Override
    public Vector3D getViewingDirection() {
        return this.viewingDirection;
    }

    @Override
    public BoundingVolume getBoundingVolume() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        String tmp = "lat: " + latitude
                + " lon: " + longitude
                + " alt: " + altitude
                + viewingDirection.toString();
        return tmp;
    }
}
