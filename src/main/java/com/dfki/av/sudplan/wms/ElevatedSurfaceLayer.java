/*
 *  ElevatedSurfaceLayer.java 
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
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.render.Renderable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extended {@link SurfaceImageLayer} which receives {@link PropertyChangeEvent}
 * from a {@link ElevatedSurfaceSupportLayer} to add and remove
 * {@link ElevatedSurfaceImage} if needed for the current {@link View}
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class ElevatedSurfaceLayer extends SurfaceImageLayer {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ElevatedSurfaceLayer.class);
    /**
     * Last image_id
     */
    private int image_id;
    /**
     * Support layer for the {@link ElevatedSurfaceLayer}
     */
    private final ElevatedSurfaceSupportLayer supportLayer;
    /**
     * The elevation for the {@link ElevatedSurfaceImage}s
     */
    private final Double elevation;
    /**
     * The maximum opacity for the {@link ElevatedSurfaceImage}s
     */
    private final Double opac;
    /**
     * Timeout value (ms) for image retreival and url request
     */
    private int timeout;
    /**
     * Represents the percentage of the maximal opacity. Opacity of the
     * {@link ElevatedSurfaceImage}s = opacityLevel * opac.
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
     * @param caps WMSCapabilities for the support layer
     * @param params WMS parameter for the support layer
     * @param elevation Elevation for the ElevatedSurfaceImages
     * @param opac Opacity for the ElevatedSurfaceImages
     * @param sector Bounding box of the ElevatedSurfaceLayer
     */
    public ElevatedSurfaceLayer(WMSCapabilities caps, AVList params, Double elevation, Double opac, Sector sector) {
        super();
        timeout = 10000;
        this.elevation = elevation;
        this.opac = opac;
        this.opacityLevel = 1.0d;
        this.setPickEnabled(false);
        super.setOpacity(0.0d);

        AVList configParams = params.copy();
        String image_format = "";
        for (String s : caps.getImageFormats()) {
            if (s.contains("dds")) {
                image_format = s;
                break;
            }
        }
        for (String s : caps.getImageFormats()) {
            if (s.endsWith("gif") || s.endsWith("png")) {
                image_format = s;
                break;
            }
        }
        if (image_format.equals("")) {
            log.warn("No supported image format available.");
        }
        image_id = 0;
        // Some wms servers are slow, so increase the timeouts and limits used by world wind's retrievers.
        configParams.setValue(AVKey.URL_CONNECT_TIMEOUT, timeout);
        configParams.setValue(AVKey.URL_READ_TIMEOUT, timeout);
        configParams.setValue(AVKey.RETRIEVAL_QUEUE_STALE_REQUEST_LIMIT, timeout);
        ElevatedSurfaceSupportLayer tmp = new ElevatedSurfaceSupportLayer(caps,
                configParams, image_format, this);
        this.supportLayer = tmp;
    }

    /**
     * Returns the support layer
     *
     * @return The support layer ({@link ElevatedSurfaceSupportLayer})
     */
    public ElevatedSurfaceSupportLayer getSupportLayer() {
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
     * Removes all {@link ElevatedSurfaceImage}s from the type
     * {@link ElevatedSurfaceLayer}
     *
     * @param renderables list of {@link Renderable}s to remove
     */
    public void removeRenderables(List<Renderable> renderables) {
        for (Renderable renderable : renderables) {
            if (renderable instanceof ElevatedSurfaceImage) {
                ElevatedSurfaceImage image = (ElevatedSurfaceImage) renderable;
                removeRenderable(renderable);
                image.dispose();
            }
        }
    }

    /**
     * Forces all {@link Renderable} from type {@link ElevatedSurfaceImage} to
     * refresh.
     */
    public void refresh() {
        for (Renderable renderable : getRenderables()) {
            if (renderable instanceof ElevatedSurfaceImage) {
                ((ElevatedSurfaceImage) renderable).refresh();
            }
        }
    }

    /**
     * Adds a {@link ElevatedSurfaceImage}
     *
     * @param image {@link ElevatedSurfaceImage} to add
     */
    public void addImage(ElevatedSurfaceImage image) {
        image.setElevation(elevation);
        image.setOpacity(opacityLevel * opac);
        image.setFloating(true);
        image.setId(image_id);
        image_id++;
        addRenderable(image);
    }

    /**
     * Adds a list of {@link ElevatedSurfaceImage}s
     *
     * @param images List of images which should be added
     */
    public void addImages(List<ElevatedSurfaceImage> images) {
        for (ElevatedSurfaceImage image : images) {
            image.setElevation(elevation);
            image.setOpacity(opacityLevel * opac);
            image.setFloating(true);
            image.setId(image_id);
            image_id++;
            addRenderable(image);
        }
    }

    /**
     * Checks if a image for the given sector exists
     *
     * @param sector Sector to check
     * @return true if an image exists for the given sector
     */
    public Boolean hasSector(Sector sector) {
        for (Renderable renderable : getRenderables()) {
            if (renderable instanceof ElevatedSurfaceImage) {
                ElevatedSurfaceImage image = (ElevatedSurfaceImage) renderable;
                if (image.getSector().equals(sector)) {
                    return true;
                }
            }
        }
        return false;
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
            if (renderable instanceof ElevatedSurfaceImage) {
                ((ElevatedSurfaceImage) renderable).setOpacity(interplate(opacity) * opac);
            }
        }
    }
    
    public double interplate(double value){
//        return Math.sqrt(value);
//        return Math.pow(value, 10);
//        return Math.exp(value);
        return value;
    }

    public double getMaxOpacity() {
        return opac;
    }

    @Override
    public double getOpacity() {
        return opacityLevel;
    }

    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener pl) {
        super.addPropertyChangeListener(pl);
        supportLayer.addPropertyChangeListener(pl);
    }
}
