/*
 *  ElevatedRenderableLayer.java 
 *
 *  Created by DFKI AV on 15.06.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import gov.nasa.worldwind.View;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.render.Renderable;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extended {@link SurfaceImageLayer} which receives {@link PropertyChangeEvent}
 * from a {@link ElevatedRenderableSupportLayer} to add and remove
 * {@link ElevatedTileImage} if needed for the current {@link View}
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class ElevatedRenderableLayer extends RenderableLayer {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ElevatedRenderableLayer.class);
    /**
     * Support layer for the {@link ElevatedRenderableLayer}
     */
    private final ElevatedRenderableSupportLayer supportLayer;
    /**
     * The elevation for the {@link ElevatedTileImage}s
     */
    private final Double elevation;
    /**
     * The maximum opacity for the {@link ElevatedTileImage}s
     */
    private final Double opac;
    /**
     * Represents the percentage of the maximal opacity. Opacity of the
     * {@link ElevatedTileImage}s = {@link #opacityLevel} * {@link #opac}.
     */
    private double opacityLevel;
    /**
     * If true the layer corresponds to a {@link WMSControlLayer}.
     */
    private boolean slave;
    /**
     * The {@link Collection} of {@link Renderable} to clean.
     */
    private Collection<Renderable> toCleanup;

    /**
     * Constructs a elevated surface layer with the defined capabilities,
     * parameters, elevation and opacity
     *
     * @param caps WMS capabilities for the WMS support layer
     * @param params WMS parameter
     * @param elevation The elevation for the {@link ElevatedTileImage}s
     * @param opac The maximum opacity for the {@link ElevatedTileImage}s
     */
    public ElevatedRenderableLayer(WMSCapabilities caps, AVList params, 
            Double elevation, Double opac) {
        super();
        this.elevation = elevation;
        this.toCleanup = new ConcurrentLinkedQueue<Renderable>();
        this.opac = opac;
        this.opacityLevel = 1.0d;
        this.setPickEnabled(false);
        super.setOpacity(0.0d);
        AVList configParams = params.copy();
        supportLayer = new ElevatedRenderableSupportLayer(caps, configParams, this);
        initialize();
    }

    /**
     * Initializes the support layer and the image id
     */
    private void initialize() {
        supportLayer.setName(getName() + "_support");
        supportLayer.addPropertyChangeListener(this);
    }

    /**
     * Returns the support layer
     *
     * @return The support layer ({@link ElevatedRenderableSupportLayer})
     */
    public ElevatedRenderableSupportLayer getSupportLayer() {
        return supportLayer;
    }

    /**
     * Sets the layer slave status. If true the layer corresponds to a
     * {@link WMSControlLayer}.
     *
     * @param slave If true the layer corresponds to a {@link WMSControlLayer}.
     */
    public void setSlave(boolean slave) {
        this.slave = slave;
    }

    /**
     * Returns the layer slave status. If true the layer corresponds to a
     * {@link WMSControlLayer}.
     *
     * @return true if the layer corresponds to a {@link WMSControlLayer}.
     */
    public boolean isSlave() {
        return slave;
    }

    /**
     * Adds a {@link ElevatedTileImage}
     *
     * @param image {@link ElevatedTileImage} to add
     */
    public void addImage(ElevatedTileImage image) {
        image.setElevation(elevation);
        image.setOpacity(opacityLevel * opac);
        addRenderable(image);
    }

    /**
     * Returns the pre-defined maximal opacity for the
     * {@link ElevatedTileImage}s
     *
     * @return the pre-defined maximal opacity for the
     * {@link ElevatedTileImage}s
     */
    public double getMaxOpacity() {
        return opac;
    }

    /**
     * Returns the elevation {@link ElevatedTileImage}s
     *
     * @return the elevation for the {@link ElevatedTileImage}s
     */
    double getElevation() {
        return elevation;
    }

    /**
     * Cleans up unneeded {@link ElevatedTileImage}s
     */
    public void cleanUp() {
        if (toCleanup != null) {
            for (Renderable renderable : toCleanup) {
                if (renderable instanceof ElevatedTileImage) {
                    ElevatedTileImage image = (ElevatedTileImage) renderable;
                    image.clear();
                }
            }
            toCleanup.clear();
        }
    }

    @Override
    public void setEnabled(boolean bln) {
        super.setEnabled(bln);
        getSupportLayer().setEnabled(bln);
    }

    @Override
    public void setOpacity(double opacity) {
        this.opacityLevel = opacity;
        for (Renderable renderable : getRenderables()) {
            if (renderable instanceof ElevatedTileImage) {
                ((ElevatedTileImage) renderable).setOpacity(opacity * opac);
            }
        }
        firePropertyChange(EventHolder.WWD_REDRAW, log, log);
    }

    @Override
    public double getOpacity() {
        return opacityLevel;
    }

    @Override
    public void removeAllRenderables() {
        toCleanup = new ConcurrentLinkedQueue<Renderable>(renderables);
        super.removeAllRenderables();
    }
}
