/*
 *  ElevatedWMSTiledImageLayer.java 
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
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class ElevatedSurfaceSupportLayer extends WMSTiledImageLayer {
    /*
     * Logger.
     */

    private static final Logger log = LoggerFactory.getLogger(ElevatedSurfaceSupportLayer.class);
    private ArrayList<TextureTile> lastCurrentTiles;
    private WMSDataRetreiver worker;
    String image_format = "image/png";
    List<Sector> oldTiles = new ArrayList<Sector>();
    List<Sector> newTiles = new ArrayList<Sector>();
    private int tileCount;
    private ArrayList<TextureTile> previousCurrentTiles;
    private ElevatedSurfaceLayer layer = null;

    public ElevatedSurfaceSupportLayer(AVList params) {
        super(params);
    }

    public ElevatedSurfaceSupportLayer(Document dom, AVList params) {
        this(dom.getDocumentElement(), params);
    }

    public ElevatedSurfaceSupportLayer(Element domElement, AVList params) {
        this(wmsGetParamsFromDocument(domElement, params));
    }

    public ElevatedSurfaceSupportLayer(WMSCapabilities caps, AVList params, String image_format, ElevatedSurfaceLayer esl) {
        this(wmsGetParamsFromCapsDoc(caps, params));
        this.layer = esl;
        this.setName(esl.getName() + "_support");
        this.addPropertyChangeListener(esl);
        this.setOpacity(0.0);
        this.image_format = image_format;
        this.tileCount = 0;
        this.lastCurrentTiles = new ArrayList<TextureTile>();
    }

    public ElevatedSurfaceSupportLayer(WMSCapabilities caps, AVList params) {
        this(wmsGetParamsFromCapsDoc(caps, params));
    }

    public ElevatedSurfaceSupportLayer(String stateInXml) {
        super(stateInXml);
    }

    @Override
    protected void addTile(DrawContext dc, TextureTile tile) {
        super.addTile(dc, tile);
        if (isLayerActive(dc) && isLayerInView(dc) && layer != null) {
            updateTiles();
        }
    }

    private void updateTiles() {
        if (currentTiles.size() < tileCount) {
                if (lastCurrentTiles == null || lastCurrentTiles.isEmpty() || !previousCurrentTiles.equals(lastCurrentTiles)) {
                    lastCurrentTiles = new ArrayList<TextureTile>(previousCurrentTiles);

                    if (worker == null || worker.isDone()) {
                        oldTiles = new ArrayList<Sector>(newTiles);
                        newTiles = new ArrayList<Sector>();
                        for (TextureTile textureTile : lastCurrentTiles) {
                            newTiles.add(textureTile.getSector());
                        }
                        worker = new WMSDataRetreiver(image_format, this, new ArrayList<Sector>(oldTiles), new ArrayList<Sector>(newTiles));
                        worker.addPropertyChangeListener(layer);
                        worker.addPropertyChangeListener(this);
                        worker.execute();
                    } else {
                        // TODO
                    }
                }
                tileCount = currentTiles.size();
            } else {
                tileCount = currentTiles.size();
                previousCurrentTiles = new ArrayList<TextureTile>(currentTiles);
            }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("wwd redraw")) {
            firePropertyChange("wwd redraw", null, null);
        }
        if (evt.getPropertyName().equals("wms download")) {
            firePropertyChange("wms download", null, null);
        }
        if (evt.getPropertyName().equals("wms done")) {
            firePropertyChange("wms done", null, null);
        }
        if (evt.getPropertyName().equals("wms done")) {
            updateTiles();
        }
    }

    class WMSDataRetreiver extends SwingWorker<Boolean, Void> {

        protected Iterable<Renderable> images;
        protected ElevatedSurfaceSupportLayer wms;
        private static final int IMAGE_SIZE = 384;
        private final String image_format;
        private List<Sector> oldTiles;
        private List<Sector> newTiles;

        /**
         *
         * @param sul
         * @param elevation
         * @param opacity
         * @param wms
         * @param List<sector>
         */
        public WMSDataRetreiver(String image_format, ElevatedSurfaceSupportLayer wms, List<Sector> oldTiles, List<Sector> newTiles) {
            this.oldTiles = oldTiles;
            this.newTiles = newTiles;
            this.wms = wms;
            this.image_format = image_format;
            this.images = new ArrayList<Renderable>();
        }

        /**
         *
         * @param sul
         * @param elevation
         * @param opacity
         * @param wms
         * @param sector
         */
        protected void addWMSDataToLayer(ElevatedSurfaceSupportLayer wms, Sector sector) {
            firePropertyChange("wms download", null, this);
            double scale = 1d;
            int level = -1;
            BufferedImage img = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
            try {
                wms.composeImageForSector(sector, IMAGE_SIZE, IMAGE_SIZE, scale,
                        level, image_format, true, img, 400000);
                ElevatedSurfaceImage tl = new ElevatedSurfaceImage(img, sector, -1);
                firePropertyChange("compose complete", newTiles, tl);
            } catch (IllegalStateException ex) {
                log.debug("Empty Image Source in Sector: " + sector);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        /**
         *
         * @return true if wms data retreival was successfull
         * @throws URISyntaxException
         * @throws Exception
         */
        @Override
        protected Boolean doInBackground() throws URISyntaxException, Exception {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            if (oldTiles == null || oldTiles.isEmpty()) {
                for (Sector sector : newTiles) {
                    firePropertyChange("wms download", null, this);
                    addWMSDataToLayer(wms, sector);
                    firePropertyChange("toRemove", null, null);
                }
            } else {
                for (Sector sector : newTiles) {
                    Boolean add = true;
                    for (Sector oldSector : oldTiles) {
                        if (sector.equals(oldSector)) {
                            add = false;
                        }
                    }
                    if (add) {
                        firePropertyChange("wms download", null, this);
                        addWMSDataToLayer(wms, sector);
                        firePropertyChange("toRemove", null, null);
                    }
                }
            }
            return true;
        }

        /**
         *
         */
        @Override
        protected void done() {
            firePropertyChange("wms done", null, this);
            try {
                if (get()) {
                    firePropertyChange("wwd redraw", null, this);
                } else {
                    log.warn("Download failed");
                }
            } catch (InterruptedException ex) {
                log.warn("done (InterruptedException)" + ex);
            } catch (ExecutionException ex) {
                log.warn("done (ExecutionException)" + ex);
            } catch (CancellationException ex) {
                log.warn("done (CancellationException)" + ex);
            }
        }
    }
}
