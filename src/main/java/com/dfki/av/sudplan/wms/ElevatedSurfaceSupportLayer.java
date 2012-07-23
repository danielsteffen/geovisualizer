/*
 *  ElevatedSurfaceSupportLayer.java 
 *
 *  Created by DFKI AV on 01.06.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.layers.TextureTile;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support layer for a {@link ElevatedSurfaceLayer} which provides the wms data.
 * 
* @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class ElevatedSurfaceSupportLayer extends WMSTiledImageLayer {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ElevatedSurfaceSupportLayer.class);
    /**
     * List of last known current {@link TextureTile}s
     */
    private ArrayList<TextureTile> lastCurrentTiles;
    /**
     * {@link SwingWorker} to retreive {@link ElevatedSurfaceImage}s
     */
    private ElevatedSurfaceImageRetreiver worker;
    /**
     * Mime type for image retreival Note: default is set to png ("image/png")
     */
    private String image_format = "image/png";
    /**
     * Amount of tiles
     */
    private int tileCount;
    /**
     * List of previous known current {@link TextureTile}s
     */
    private ArrayList<TextureTile> previousCurrentTiles;
    /**
     * {@link ElevatedSurfaceLayer} which needs the wms date from this
     * {@link ElevatedSurfaceSupportLayer}
     */
    private ElevatedSurfaceLayer layer;
    /**
     * Timeout value (ms) for image retreival
     */
    private int timeout = 10000;

    /**
     * Creates a {@link ElevatedSurfaceSupportLayer} with the defined
     * {@link WMSCapabilities} caps, the {@link AVList} params, the mime type as {@link String}
     * image_format and the corresponding
     * {@link ElevatedSurfaceLayer} esl.
     *
     * @param caps the {@link WMSCapabilities}
     * @param params the parameters as {@link AVList}
     * @param image_format the mime type for the image retreival
     * @param esl the corresponding {@link ElevatedSurfaceLayer}
     */
    public ElevatedSurfaceSupportLayer(WMSCapabilities caps, AVList params, String image_format, ElevatedSurfaceLayer esl) {
        super(wmsGetParamsFromCapsDoc(caps, params));
        this.layer = esl;
        this.timeout = (Integer) params.getValue(AVKey.RETRIEVAL_QUEUE_STALE_REQUEST_LIMIT);
        this.setName(esl.getName() + "_support");
        this.addPropertyChangeListener(esl);
        this.setOpacity(0.0);
        this.image_format = image_format;
        this.tileCount = 0;
        this.lastCurrentTiles = new ArrayList<TextureTile>();
        this.previousCurrentTiles = new ArrayList<TextureTile>();
    }

    /**
     * Retreives a {@link BufferedImage} of the {@link TextureTile}
     *
     * @param tile for retreiving the {@link BufferedImage}
     * @return  {@link BufferedImage} corresponding to the given tile
     * @throws {@link Exception} if the retreival failed
     */
    public BufferedImage getImage(TextureTile tile) {
        try {
            BufferedImage img = getImage(tile, image_format, timeout);
            return img;
        } catch (Exception ex) {
            if (!(ex instanceof RuntimeException)) {
                log.error(ex.toString());
            }
        }
        return null;
    }

    @Override
    protected void assembleTiles(DrawContext dc) {
        super.assembleTiles(dc);
        if (!currentTiles.isEmpty()) {
            lastCurrentTiles = new ArrayList<TextureTile>(currentTiles);
            if (isLayerActive(dc) && isLayerInView(dc) && layer != null) {
                if (!lastCurrentTiles.equals(previousCurrentTiles) || previousCurrentTiles.isEmpty()) {
                    if (worker == null || worker.isDone()) {
                        previousCurrentTiles = new ArrayList<TextureTile>(lastCurrentTiles);
                        worker = new ElevatedSurfaceImageRetreiver(this, layer, new ArrayList<TextureTile>(lastCurrentTiles), dc);
                        worker.addPropertyChangeListener(this);
                        worker.execute();
                    } else {
                        previousCurrentTiles = new ArrayList<TextureTile>(lastCurrentTiles);
                        worker.addTiles(new ArrayList<TextureTile>(lastCurrentTiles));
                    }
                }
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PropertyChangeEventHolder.WWD_REDRAW)) {
            firePropertyChange(PropertyChangeEventHolder.WWD_REDRAW, null, null);
        }
        if (evt.getPropertyName().equals(PropertyChangeEventHolder.WMS_DOWNLOAD_ACTIVE)) {
            firePropertyChange(PropertyChangeEventHolder.WMS_DOWNLOAD_ACTIVE, null, null);
        }
        if (evt.getPropertyName().equals(PropertyChangeEventHolder.WMS_DOWNLAOD_COMPLETE)) {
            firePropertyChange(PropertyChangeEventHolder.WMS_DOWNLAOD_COMPLETE, null, null);
        }
    }
}
