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
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class SimpleCamera implements Camera{
    
    private double latitude;
    private double longitude;
    private double altitude;
    
    public SimpleCamera(){
        this(0.0, 0.0, 0.0);
    }
    
    public SimpleCamera(Position p){
        this(p.getLatitude().getDegrees(), p.getLongitude().getDegrees(), p.getAltitude());
    }
    
    public SimpleCamera(double lat, double lon, double alt){
        this.latitude = lat;
        this.longitude = lon;
        this.altitude = alt;
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
}
