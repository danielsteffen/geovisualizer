/*
 * Vector3D.java 
 *
 * Created by DFKI AV on 29.09.2011.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.camera;

import gov.nasa.worldwind.geom.Vec4;

/**
 * A data structure for a 3-D vector. Another one ;)
 */
public class Vector3D {

    /**
     * The
     * <code>x</code> component of the {@link Vector3D}.
     */
    private double x;
    /**
     * The
     * <code>y</code> component of the {@link Vector3D}.
     */
    private double y;
    /**
     * The
     * <code>z</code> component of the {@link Vector3D}.
     */
    private double z;

    /**
     * Constructor. Sets all components to zero.
     */
    public Vector3D() {
        this(0.0, 0.0, 0.0);
    }

    /**
     * Constructor.
     *
     * @param vec the {@link Vec4} to set.
     * @throws IllegalArgumentException if <code>vec</code> is null.
     */
    public Vector3D(Vec4 vec) {
        if (vec == null) {
            throw new IllegalArgumentException("Vec4 is null.");
        }
        this.x = vec.getX();
        this.y = vec.getY();
        this.z = vec.getZ();
    }

    /**
     * Copy constructor.
     *
     * @param vec the {@link Vector3D} to set.
     * @throws IllegalArgumentException if <code>vec</code> is null.
     */
    public Vector3D(Vector3D vec) {
        if (vec == null) {
            throw new IllegalArgumentException("Vector3D is null.");
        }
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    /**
     * Constructor.
     *
     * @param x the x component to set.
     * @param y the y component to set.
     * @param z the z component to set.
     */
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Returns the x component.
     *
     * @return the x component to return.
     */
    public double getX() {
        return x;
    }

    /**
     * Set the x component.
     *
     * @param x the value to set for the x component.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Returns the y component of the vector.
     *
     * @return the y component to return.
     */
    public double getY() {
        return y;
    }

    /**
     * Set the y component of the vector.
     *
     * @param y the y to set.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Returns the z component of the vector.
     *
     * @return the z component to return.
     */
    public double getZ() {
        return z;
    }

    /**
     * The z component to set for this vector.
     *
     * @param z the z to set
     */
    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "Vector3D( " + x + ", "
                + y + ", "
                + z + ")";
    }
}
