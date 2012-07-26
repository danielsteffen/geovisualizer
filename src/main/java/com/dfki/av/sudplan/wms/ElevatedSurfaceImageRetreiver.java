/*
 *  ElevatedSurfaceImageRetreiver.java 
 *
 *  Created by DFKI AV on 12.07.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.TextureTile;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import java.awt.image.BufferedImage;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SwingWorker for the retreival of the ElevatedSurfaceImages for the
 * ElevatedSurfaceLayer
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class ElevatedSurfaceImageRetreiver extends SwingWorker<Boolean, Void> {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ElevatedSurfaceImageRetreiver.class);
    /**
     * time to sleep after requesting new image
     */
    private static final int SLEEPTIME = 250;
    /**
     * The support layer with the wms data
     */
    private ElevatedSurfaceSupportLayer supportLayer;
    /**
     * {@link ElevatedSurfaceLayer} layer which the {@link ElevatedSurfaceImage}
     * will be added
     */
    private final ElevatedSurfaceLayer layer;
    /**
     * {@link TextureTile} which holds the information of the image position
     */
    private List<TextureTile> tiles;
    /**
     * {@link DrawContext} which holds the information about the current view
     */
    private final DrawContext dc;
    /**
     * list of tiles to download
     */
    private List<TextureTile> newTiles = new ArrayList<TextureTile>();

    /**
     * Creates a {@link SwingWorker} for the retreival of the
     * {@link ElevatedSurfaceImage}s for the {@link ElevatedSurfaceLayer} layer
     *
     * @param supportLayer the support layer which holds the wms informations
     * @param layer the {@link ElevatedSurfaceLayer} which the {@link ElevatedSurfaceImage}s
     * will be added
     * @param tiles a list of tiles for which {@link ElevatedSurfaceImage}s
     * should be created
     * @param dc current {@link DrawContext}
     */
    public ElevatedSurfaceImageRetreiver(ElevatedSurfaceSupportLayer supportLayer,
            ElevatedSurfaceLayer layer, List<TextureTile> tiles, DrawContext dc) {
        this.supportLayer = supportLayer;
        this.layer = layer;
        this.tiles = tiles;
        this.dc = dc;
    }

    /**
     * Removes all {@link ElevatedSurfaceImage}s which are not needed for the
     * current view.
     *
     * @return true if cleanup of {@link ElevatedSurfaceImage}s was successfull
     */
    private Boolean cleanup() {
        if (layer == null) {
            return false;
        }
        List<Renderable> toRemove = new ArrayList<Renderable>();
        for (Renderable renderable : layer.getRenderables()) {
            if (renderable != null) {
                Sector sector = Sector.EMPTY_SECTOR;
                int id = 0;
                Sector sector2 = Sector.EMPTY_SECTOR;
                int id2 = 0;
                if (renderable instanceof ElevatedSurfaceImage) {
                    ElevatedSurfaceImage image = (ElevatedSurfaceImage) renderable;
                    sector = image.getSector();
                    id = image.getId();
                }
                if (dc != null && sector != null && dc.getVisibleSector() != null) {
                    if (dc.getVisibleSector().equals(Sector.EMPTY_SECTOR)
                            || sector.intersects(dc.getVisibleSector())) {
                        for (Renderable renderable2 : layer.getRenderables()) {
                            if (renderable2 != null) {
                                if (renderable2 instanceof ElevatedSurfaceImage) {
                                    ElevatedSurfaceImage image = (ElevatedSurfaceImage) renderable2;
                                    sector2 = image.getSector();
                                    id2 = image.getId();
                                }
                                if (sector.contains(sector2)
                                        || sector2.contains(sector)) {
                                    if (id < id2) {
                                        if (!toRemove.contains(renderable)) {
                                            toRemove.add(renderable);
                                        }
                                    } else if (id > id2) {
                                        if (!toRemove.contains(renderable2)) {
                                            toRemove.add(renderable2);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (!toRemove.contains(renderable) && renderable != null) {
                            toRemove.add(renderable);
                        }
                    }
                }
            }
        }
        for (Renderable renderable : toRemove) {
            if (renderable != null) {
                layer.removeRenderable(renderable);
            }
        }
        return true;
    }

    /**
     * Adds a new list of {@link TextureTile}
     *
     * @param tiles tiles to add
     */
    public void addTiles(List<TextureTile> tiles) {
        newTiles = tiles;
    }

    /**
     * Downloads the requiered {@link BufferedImage}s and creates the {@link ElevatedSurfaceImage}s.
     *
     * @return true of retreival was succesfull
     * @throws InterruptedException if the retreival was interuppted during
     * sleep
     */
    private Boolean download() throws InterruptedException {
        firePropertyChange(EventHolder.WMS_DOWNLOAD_ACTIVE, null, this);
        Thread.sleep(SLEEPTIME);
        if (!newTiles.isEmpty()) {
            tiles = new ArrayList<TextureTile>(newTiles);
            newTiles.clear();
            cleanup();
            return download();
        }
        for (TextureTile tile : tiles) {
            Sector s = tile.getSector();
            if (!layer.hasSector(s)) {
                BufferedImage img = supportLayer.getImage(tile);
                if (img != null) {
                    layer.addImage(new ElevatedSurfaceImage(img, s));
                    Thread.sleep(SLEEPTIME);
                    firePropertyChange(EventHolder.WWD_REDRAW, null, this);
                    if (!newTiles.isEmpty()) {
                        tiles = new ArrayList<TextureTile>(newTiles);
                        newTiles.clear();
                        cleanup();
                        return download();
                    }
                    cleanup();
                }
            }
        }
        cleanup();
        return true;
    }

    @Override
    protected Boolean doInBackground() throws URISyntaxException, Exception {
        Thread.currentThread().setName(layer.getName() + "_retreiver");
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        return download();
    }

    @Override
    protected void done() {
        firePropertyChange(EventHolder.WMS_DOWNLAOD_COMPLETE, null, this);
        layer.refresh();
        try {
            if (get()) {
                firePropertyChange(EventHolder.WWD_REDRAW, null, this);
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
