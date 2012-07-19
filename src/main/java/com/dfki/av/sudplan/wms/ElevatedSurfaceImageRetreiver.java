/*
 *  ElevatedSurfaceImageRetreiver.java 
 *
 *  Created by DFKI AV on 12.07.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import com.sun.j3d.utils.behaviors.interpolators.KBCubicSplineCurve;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.TextureTile;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import java.awt.image.BufferedImage;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;
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
     * The support layer with the wms data
     */
    protected ElevatedSurfaceSupportLayer supportLayer;
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
     * time to sleep after requesting new image
     */
    private static final int SLEEPTIME = 500;
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
    public ElevatedSurfaceImageRetreiver(ElevatedSurfaceSupportLayer supportLayer, ElevatedSurfaceLayer layer, List<TextureTile> tiles, DrawContext dc) {
        this.supportLayer = supportLayer;
        this.layer = layer;
        this.tiles = tiles;
        this.dc = dc;
    }

    /**
     * Generates a {@link ElevatedSurfaceImage} for the given bounding box from
     * the date of the {@link ElevatedSurfaceSupportLayer}
     *
     * @param suppertLayer support layer with the wms data
     * @param sector sector for the {@link ElevatedSurfaceImage} creation
     * @throws {@link InterruptedException}
     */
    protected ElevatedSurfaceImage createElevatedSurfaceImage(BufferedImage img, Sector sector) {
        if (img != null) {
            return new ElevatedSurfaceImage(img, sector);
        }
        return null;
    }

    private void addElevatedSurfaceImage(ElevatedSurfaceImage image) {
        try {
            layer.addImage(image);
            Thread.sleep(SLEEPTIME);
            check();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     *
     * @return
     */
    private Boolean check() {
        if (layer == null) {
            return false;
        }
        List<Renderable> toRemove = new ArrayList<Renderable>();
        for (Renderable renderable : layer.getRenderables()) {
            if (renderable != null && renderable instanceof ElevatedSurfaceImage) {
                ElevatedSurfaceImage image = (ElevatedSurfaceImage) renderable;
                if (image != null && dc != null && image.getSector() != null && dc.getVisibleSector() != null) {
                    if (dc.getVisibleSector().equals(Sector.EMPTY_SECTOR) || image.getSector().intersects(dc.getVisibleSector())) {
                        for (Renderable renderable2 : layer.getRenderables()) {
                            if (renderable2 != null && renderable2 instanceof ElevatedSurfaceImage) {
                                ElevatedSurfaceImage image2 = (ElevatedSurfaceImage) renderable2;
                                if (image != null && image2 != null && image.getSector().contains(image2.getSector())
                                        || image2.getSector().contains(image.getSector())) {
                                    if (image != null && image2 != null && image.getId() < image2.getId()) {
                                        if (!toRemove.contains(renderable)) {
                                            toRemove.add(renderable);
                                        }
                                    } else if (image != null && image2 != null && image.getId() > image2.getId()) {
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
     * Adds a new list of {@link TextureTiles}
     * @param tiles tiles to add
     */
    public void addTiles(List<TextureTile> tiles) {
        newTiles = tiles;
    }

    @Override
    protected Boolean doInBackground() throws URISyntaxException, Exception {
        firePropertyChange(PropertyChangeEventHolder.WMS_DOWNLOAD_ACTIVE, null, this);
        List<ElevatedSurfaceImage> images = new ArrayList<ElevatedSurfaceImage>();

        for (TextureTile tile : tiles) {
            images.add(createElevatedSurfaceImage(supportLayer.getImage(tile), tile.getSector()));
        }
        if (!newTiles.isEmpty()) {
            tiles = new ArrayList<TextureTile>(newTiles);
            images.clear();
            newTiles.clear();
            doInBackground();
        }
        for (ElevatedSurfaceImage image : images) {
            if (!newTiles.isEmpty()) {
                continue;
            }
            addElevatedSurfaceImage(image);
        }
        if (!newTiles.isEmpty()) {
            tiles = new ArrayList<TextureTile>(newTiles);
            images.clear();
            newTiles.clear();
            doInBackground();
        }
        return true;
    }

    @Override
    protected void done() {
        firePropertyChange(PropertyChangeEventHolder.WMS_DOWNLAOD_COMPLETE, null, this);
        layer.refresh();
        try {
            if (get()) {
                firePropertyChange(PropertyChangeEventHolder.WWD_REDRAW, null, this);
            } else {
                log.warn("Download failed");
            }
        } catch (InterruptedException ex) {
            log.warn("done (InterruptedException)" + ex);
        } catch (ExecutionException ex) {
            log.warn("done (ExecutionException)" + ex);
            ex.printStackTrace();
        } catch (CancellationException ex) {
            log.warn("done (CancellationException)" + ex);
        }
    }
}
