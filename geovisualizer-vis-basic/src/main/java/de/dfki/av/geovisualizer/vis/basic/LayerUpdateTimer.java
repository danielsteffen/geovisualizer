/*
 * LayerUpdateTimer.java
 *
 * Created by AG wearHEALTH on 05.05.2015.
 * Copyright (c) 2015 University of Kaiserslautern, Kaiserslautern. 
 * All rights reserved. Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.vis.basic;

import gov.nasa.worldwind.layers.Layer;
import javax.swing.Timer;

/**
 *
 * @author Daniel Steffen
 */
public class LayerUpdateTimer extends Timer {

    public static final int UPDATE_FASTEST = 100;
    public static final int UPDATE_FAST = 500;
    public static final int UPDATE_NORMAL = 1000;
    public static final int UPDATE_SLOW = 500;
    public static final int UPDATE_SLOWEST = 10000;

    public LayerUpdateTimer(Layer layer, IGeometry geometry) {
        this(UPDATE_SLOWEST, layer, geometry);
    }

    public LayerUpdateTimer(int i, Layer layer, IGeometry geometry) {
        super(i, new LayerUpdateListener(layer, geometry));
    }
}
