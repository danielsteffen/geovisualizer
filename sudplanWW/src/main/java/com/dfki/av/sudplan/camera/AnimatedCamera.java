/*
 *  AnimatedCamera.java 
 *
 *  Created by DFKI AV on 30.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.camera;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class AnimatedCamera extends SimpleCamera{
    
    /**
     * Creates a <code>AnimatedCamera</code> object at {@link Position} 
     * <code>(lat, lon, alt)</code> and a {@link #viewingDirection} 
     * of <code>(0.0, 0.0, 0.0)</code>.
     * 
     * @param lat the latitude position in degrees to set.
     * @param lon the longitude position in degrees to set.
     * @param alt the altitude poisition in meter to set.
     */
    public AnimatedCamera(double lat, double lon, double alt) {
        super(lat, lon, alt, new Vector3D());
    }
}
