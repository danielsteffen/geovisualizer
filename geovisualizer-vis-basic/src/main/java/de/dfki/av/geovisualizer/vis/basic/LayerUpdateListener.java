/*
 * LayerUpdateListener.java
 *
 * Created by AG wearHEALTH on 05.05.2015.
 * Copyright (c) 2015 University of Kaiserslautern, Kaiserslautern. 
 * All rights reserved. Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.vis.basic;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.Layer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Daniel Steffen
 */
public class LayerUpdateListener implements ActionListener {

    /**
     *
     */
    private final Layer layer;
    /**
     *
     */
    private final IGeometry geometry;

    /**
     * 
     * @param layer
     * @param geometry 
     */
    public LayerUpdateListener(final Layer layer, final IGeometry geometry) {
        this.layer = layer;
        this.geometry = geometry;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        geometry.update();
        layer.firePropertyChange(AVKey.LAYER, null, null);
    }
}
