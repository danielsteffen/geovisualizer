/*
 *  ElevatedSurfaceSupportLayer.java 
 *
 *  Created by DFKI AV on 01.06.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.TextureTile;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
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
     * List of last known current {@link Sector}s
     */
    private List<Sector> oldTiles = new ArrayList<Sector>();
    /**
     * List of new current {@link Sector}s
     */
    private List<Sector> newTiles = new ArrayList<Sector>();
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
        this.setName(esl.getName() + "_support");
        this.addPropertyChangeListener(esl);
        this.setOpacity(0.0);
        this.image_format = image_format;
        this.tileCount = 0;
        this.lastCurrentTiles = new ArrayList<TextureTile>();
    }

    /**
     * Checks if a new tile has been added in the {@link ElevatedSurfaceSupportLayer}
     * and starts the {@link ElevatedSurfaceImageRetreiver} if new images must
     * be loaded.
     */
    private void updateTiles() {
        if (currentTiles.size() < tileCount) {
            if (lastCurrentTiles == null || lastCurrentTiles.isEmpty() || !previousCurrentTiles.equals(lastCurrentTiles)) {
                if (worker == null || worker.isDone()) {
                    lastCurrentTiles = new ArrayList<TextureTile>(previousCurrentTiles);
                    oldTiles = new ArrayList<Sector>(newTiles);
                    newTiles = new ArrayList<Sector>();
                    for (TextureTile textureTile : lastCurrentTiles) {
                        newTiles.add(textureTile.getSector());
                    }
                    worker = new ElevatedSurfaceImageRetreiver(image_format, this, new ArrayList<Sector>(oldTiles), new ArrayList<Sector>(newTiles));
                    worker.addPropertyChangeListener(layer);
                    worker.addPropertyChangeListener(this);
                    worker.execute();
                }
            }
            tileCount = currentTiles.size();
        } else {
            tileCount = currentTiles.size();
            previousCurrentTiles = new ArrayList<TextureTile>(currentTiles);
        }
    }

    @Override
    protected void addTile(DrawContext dc, TextureTile tile) {
        super.addTile(dc, tile);
        if (isLayerActive(dc) && isLayerInView(dc) && layer != null) {
            updateTiles();
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
        if (evt.getNewValue() != null && evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
            if (lastCurrentTiles == null || lastCurrentTiles.isEmpty() || !previousCurrentTiles.equals(lastCurrentTiles)) {
                if (worker == null || worker.isDone()) {
                    lastCurrentTiles = new ArrayList<TextureTile>(previousCurrentTiles);
                    oldTiles = new ArrayList<Sector>(newTiles);
                    newTiles = new ArrayList<Sector>();
                    for (TextureTile textureTile : lastCurrentTiles) {
                        newTiles.add(textureTile.getSector());
                    }
                    worker = new ElevatedSurfaceImageRetreiver(image_format, this, new ArrayList<Sector>(oldTiles), new ArrayList<Sector>(newTiles));
                    worker.addPropertyChangeListener(layer);
                    worker.addPropertyChangeListener(this);
                    worker.execute();
                }
            }
        }

    }
}
