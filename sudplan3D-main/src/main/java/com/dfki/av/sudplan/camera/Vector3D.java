/*
 *  Vector3D.java 
 *
 *  Created by DFKI AV on 29.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.camera;

import gov.nasa.worldwind.geom.Vec4;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class Vector3D {

    /**
     * The <code>x</code> component of the {@link Vector3D}.
     */
    private double x;
    /**
     * The <code>y</code> component of the {@link Vector3D}.
     */
    private double y;
    /**
     * The <code>z</code> component of the {@link Vector3D}.
     */
    private double z;

    /**
     * 
     */
    public Vector3D() {
        this(0.0, 0.0, 0.0);
    }

    /**
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
     * 
     * @param x
     * @param y
     * @param z 
     */
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return the z
     */
    public double getZ() {
        return z;
    }

    /**
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
