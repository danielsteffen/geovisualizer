/*
 *  AnimatedCamera.java 
 *
 *  Created by DFKI AV on 30.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.camera;

import de.dfki.av.geovisualizer.app.vis.VisualizationComponent;
import gov.nasa.worldwind.geom.Position;

/**
 * The <i>AnimatedCamera</i> class is extended from {@link SimpleCamera}. It 
 * provides the same methods and constructors. However, when used in
 * {@link VisualizationComponent#setCamera(de.dfki.av.geovisualizer.app.camera.Camera)}
 * the camera is animated to the specified end position. Using the 
 * {@link #viewingDirection} one can set the Roll-Pitch-Yaw angles in radians 
 * of the camera.
 * <p>
 * 
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class AnimatedCamera extends SimpleCamera{
    
    /**
     * Creates a <code>AnimatedCamera</code> object at {@link Position} <code>(0.0, 0.0, 0.0)</code>
     * and a {@link #viewingDirection} of <code>(0.0, 0.0, 0.0)</code>.
     */
    public AnimatedCamera() {
        super(0.0, 0.0, 0.0, new Vector3D());
    }

    /**
     * Creates a <code>AnimatedCamera</code> object at {@link Position} <code>p</code>
     * and a {@link #viewingDirection} of <code>(0.0, 0.0, 0.0)</code>.
     * 
     * @param p the {@link Position} to set.
     */
    public AnimatedCamera(Position p) {
        super(p.getLatitude().getDegrees(),
                p.getLongitude().getDegrees(),
                p.getAltitude(),
                new Vector3D());
    }

    /**
     * Creates a <code>AnimatedCamera</code> object at {@link Position} 
     * <code>(lat, lon, alt)</code> and a {@link #viewingDirection} 
     * of <code>(0.0, 0.0, 0.0)</code>.
     * 
     * @param lat the latitude position in degrees to set.
     * @param lon the longitude position in degrees to set.
     * @param alt the altitude position in meter to set.
     */
    public AnimatedCamera(double lat, double lon, double alt) {
        super(lat, lon, alt, new Vector3D());
    }
    
    /**
     * Creates a <code>AnimatedCamera</code> object at {@link Position} <code>p</code>
     * and a {@link #viewingDirection} of <code>vec</code>.
     * 
     * @param p the {@link Position} to set.
     * @param vec the viewing direction to set.
     */
    public AnimatedCamera(Position p, Vector3D vec) {
        super(p.getLatitude().getDegrees(),
                p.getLongitude().getDegrees(),
                p.getAltitude(),
                vec);
        // TODO <steffen>: Consider p to be null as well!
    }

    /**
     * Creates a <code>AnimatedCamera</code> object at {@link Position} 
     * <code>(lat, lon, alt)</code> and a {@link #viewingDirection} 
     * of <code>vec</code>.
     * <p>
     * Note: The <code>vec</code> is copied by value not by reference.
     * 
     * @param lat the latitude position in degrees to set.
     * @param lon the longitude position in degrees to set.
     * @param alt the altitude position in meter to set.
     * @param vec the viewing direction to set.
     * 
     * @throws IllegalArgumentException If <code>vec</code> is null.
     */
    public AnimatedCamera(double lat, double lon, double alt, Vector3D vec) {
        super(lat, lon, alt, vec);
    }    
}
