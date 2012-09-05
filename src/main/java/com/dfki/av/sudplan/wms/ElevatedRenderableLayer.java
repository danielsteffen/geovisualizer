/*
 *  ElevatedRenderableLayer.java 
 *
 *  Created by DFKI AV on 15.06.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import gov.nasa.worldwind.View;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.util.TileKey;
import java.beans.PropertyChangeEvent;
import java.util.List;
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
     * Last image_id
     */
    private int image_id;
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
     * {@link ElevatedTileImage}s = opacityLevel * opac.
     */
    private double opacityLevel;
    /**
     * If true the layer corresponse to a {@link WMSControlLayer}.
     */
    private boolean slave;

    /**
     * Constructs a elevated surface layer with the definied capabilities,
     * prameters, elevation and opacity
     *
     * @param caps WMS capabilities for the wms support layer
     * @param params WMS parameter
     * @param elevation The elevation for the {@link ElevatedTileImage}s
     * @param opac The maximum opacity for the {@link ElevatedTileImage}s
     * @param sector The sector of the layer
     */
    public ElevatedRenderableLayer(WMSCapabilities caps, AVList params, Double elevation, Double opac, Sector sector) {
        super();
        this.elevation = elevation;
        this.opac = opac;
        this.opacityLevel = 1.0d;
        this.setPickEnabled(false);
        super.setOpacity(0.0d);
        AVList configParams = params.copy();
        int timeout = 500;
        configParams.setValue(AVKey.URL_CONNECT_TIMEOUT, timeout);
        configParams.setValue(AVKey.URL_READ_TIMEOUT, timeout);
        configParams.setValue(AVKey.RETRIEVAL_QUEUE_STALE_REQUEST_LIMIT, timeout);
        supportLayer = new ElevatedRenderableSupportLayer(caps, configParams, this);
        initialize();
    }

    /**
     * Initializes the support layer and the image id
     */
    private void initialize() {
        image_id = 0;
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
     * Sets the layer slave status. If true the layer corresponse to a
     * {@link WMSControlLayer}.
     *
     * @param slave If true the layer corresponse to a {@link WMSControlLayer}.
     */
    public void setSlave(boolean slave) {
        this.slave = slave;
    }

    /**
     * Returns the layer slave status. If true the layer corresponse to a
     * {@link WMSControlLayer}.
     *
     * @return true if the layer corresponse to a {@link WMSControlLayer}.
     */
    public boolean isSlave() {
        return slave;
    }

    /**
     * Removes all {@link ElevatedTileImage}s from the type
     * {@link ElevatedRenderableLayer}
     *
     * @param renderables list of {@link Renderable}s to remove
     */
    public void removeRenderables(List<Renderable> renderables) {
        for (Renderable renderable : renderables) {
            if (renderable instanceof ElevatedTileImage) {
                ElevatedTileImage image = (ElevatedTileImage) renderable;
                removeRenderable(renderable);
                image.dispose();
            }
        }
    }

    /**
     * Adds a {@link ElevatedTileImage}
     *
     * @param image {@link ElevatedTileImage} to add
     */
    public void addImage(ElevatedTileImage image) {
        image.setElevation(elevation);
        image.setOpacity(opacityLevel * opac);
        image_id++;
        addRenderable(image);
    }

    /**
     * Adds a list of {@link ElevatedTileImage}s
     *
     * @param images List of images which should be added
     */
    public void addImages(List<ElevatedTileImage> images) {
        for (ElevatedTileImage image : images) {
            image.setElevation(elevation);
            image.setOpacity(opacityLevel * opac);
            image_id++;
            addRenderable(image);
        }
    }

    /**
     * Adds a {@link ElevatedTileImage}
     *
     * @param tileKey {@link TileKey} for retreival of the texture
     * @param sector sector of the image
     */
    public void addImage(TileKey tileKey, Sector sector) {
        ElevatedTileImage image = new ElevatedTileImage(tileKey, sector, elevation);
        addImage(image);
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

    public double getMaxOpacity() {
        return opac;
    }

    @Override
    public double getOpacity() {
        return opacityLevel;
    }

    public void cleanUp() {
        if (getRenderables() == null) {
            return;
        }
        for (Renderable renderable1 : getRenderables()) {
            if (renderable1 != null) {
                Sector sector1;
                long updateTime1;
                Sector sector2;
                long updateTime2;
                if (renderable1 instanceof ElevatedTileImage) {
                    sector1 = ((ElevatedTileImage) renderable1).getSector();
                    updateTime1 = ((ElevatedTileImage) renderable1).getUpdateTime();
                } else {
                    continue;
                }
                for (Renderable renderable2 : getRenderables()) {
                    if (renderable2 != null) {
                        if (renderable2 instanceof ElevatedTileImage) {
                            sector2 = ((ElevatedTileImage) renderable2).getSector();
                            updateTime2 = ((ElevatedTileImage) renderable2).getUpdateTime();
                        } else {
                            continue;
                        }
                        if (sector1.contains(sector2)
                                || sector2.contains(sector1)) {
                            if (updateTime1 < updateTime2) {
                                removeRenderable(renderable1);
                            } else if (updateTime1 > updateTime2) {
                                removeRenderable(renderable2);
                            }
                        }
                    }
                }
            } else {
                removeRenderable(renderable1);
            }
        }
    }
}
