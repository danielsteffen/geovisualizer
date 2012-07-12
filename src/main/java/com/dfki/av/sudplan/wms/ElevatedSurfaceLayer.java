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
import javax.swing.SwingWorker;
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

    /**
     * Last image_id
     */
    private int image_id;
    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ElevatedSurfaceLayer.class);
    /**
     * Support layer for the {@link ElevatedSurfaceLayer}
     */
    private final ElevatedSurfaceSupportLayer supportLayer;
    /**
     * The elevation for the {@link ElevatedSurfaceImage}s
     */
    private final Double elevation;
    /**
     * The opacity for the {@link ElevatedSurfaceImage}s
     */
    private final Double opac;
    /**
     * {@link SwingWorker} to check if {@link ElevatedSurfaceImage} must be
     * removed
     */
    private ElevatedSurfaceImageRemover remover;

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
        this.elevation = elevation;
        this.opac = opac;
        this.setPickEnabled(false);

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
        configParams.setValue(AVKey.URL_CONNECT_TIMEOUT, 30000);
        configParams.setValue(AVKey.URL_READ_TIMEOUT, 30000);
        configParams.setValue(AVKey.RETRIEVAL_QUEUE_STALE_REQUEST_LIMIT, 60000);
        ElevatedSurfaceSupportLayer tmp = new ElevatedSurfaceSupportLayer(caps, configParams, image_format, this);
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
     * Forces all {@link Renderable} from type
     * {@link ElevatedSurfaceImage} to refresh.
     */
    void refresh() {
        for (Renderable renderable : getRenderables()) {
            if (renderable instanceof ElevatedSurfaceImage) {
                ((ElevatedSurfaceImage) renderable).refresh();
            }
        }
    }

    @Override
    public void setEnabled(boolean bln) {
        super.setEnabled(bln);
        getSupportLayer().setEnabled(bln);
    }

    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener pl) {
        super.addPropertyChangeListener(pl);
        supportLayer.addPropertyChangeListener(pl);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PropertyChangeEventHolder.IMAGE_REMOVAL)) {
            if (evt.getNewValue() instanceof List<?>) {
                List<Renderable> toRemove = (List<Renderable>) evt.getNewValue();
                removeRenderables(toRemove);
            } else if(evt.getOldValue() instanceof List<?>) {
                List<Sector> toRemove = (List<Sector>) evt.getOldValue();
                ElevatedSurfaceImageRemover removeOld = new ElevatedSurfaceImageRemover(getRenderables(), toRemove);
                removeOld.addPropertyChangeListener(this);
                removeOld.execute();
            } else if (evt.getNewValue() == null && evt.getOldValue() == null 
                    && (remover == null || remover.isDone())) {
                remover = new ElevatedSurfaceImageRemover(getRenderables());
                remover.addPropertyChangeListener(this);
                remover.execute();
            }
        }
        if (evt.getPropertyName().equals(PropertyChangeEventHolder.IMAGE_REMOVAL_COMPLETE)) {
            removeAllRenderables();
        }
        if (evt.getPropertyName().equals(PropertyChangeEventHolder.IMAGE_CREATION_COMPLETE)) {
            if (evt.getNewValue() instanceof ElevatedSurfaceImage && evt.getOldValue() instanceof List<?>) {
                ElevatedSurfaceImage image = (ElevatedSurfaceImage) evt.getNewValue();
                image.setElevation(elevation);
                image.setOpacity(opac);
                image.setFloating(true);
                image.setId(image_id);
                image_id++;
                addRenderable(image);
            } else {
                log.warn("No ElevatedSurfaceImage");
            }
        }
    }
}
